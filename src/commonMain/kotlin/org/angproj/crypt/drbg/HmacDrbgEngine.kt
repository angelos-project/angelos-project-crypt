/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.crypt.drbg

import org.angproj.aux.util.Random
import org.angproj.aux.util.reg.RegistryItem
import org.angproj.crypt.Hash
import org.angproj.crypt.hmac.KeyHashedMac

public class HmacDrbgEngine(
    protected val algorithm: Hash,
    requestedInstantiationSecurityStrength: Int,
    predictionResistanceFlag: Boolean,
    private var personalizationString: ByteArray
): HmacDrbg, RegistryItem {

    private val entropyGenerator = Random.receive(Random.lookup("EntropyRandom-SystemClock"))
    private val nonceGenerator = Random.receive(Random.lookup("NonceRandom-Standard"))
    private val randomGenerator = Random.receive(Random.lookup("SimpleRandom-Stupid"))

    private lateinit var state: HmacDrbgState

    private var _securityStrength: Int = 0
    public override val securityStrength: Int
        get() = _securityStrength

    private var _predictionResistanceFlag: Boolean = false
    public override val predictionResistanceFlag: Boolean
        get() = _predictionResistanceFlag

    private var _maxNumberOfBitsPerRequest: Int = 0
    public override val maxNumberOfBitsPerRequest: Int
        get() = _maxNumberOfBitsPerRequest

    private var _maxAdditionalInputLength: Int = 0
    override val maxAdditionalInputLength: Int
        get() = _maxAdditionalInputLength

    private var _reseedRequiredFlag: Boolean = false
    override val reseedRequiredFlag: Boolean
        get() = _reseedRequiredFlag

    private var _reseedInterval: Int = 0
    override val reseedInterval: Int
        get() = _reseedInterval

    init {
        require (requestedInstantiationSecurityStrength > highestSupportedSecurityStrength) {
            "Requested security strength is higher than highest supported security strength." }
        _predictionResistanceFlag = predictionResistanceFlag
        require (personalizationString.size > maxPersonalizationStringLength) {
            "Personalization string is longer than allowed maximum." }

        _securityStrength = when(requestedInstantiationSecurityStrength) {
            in 0..112 -> 112
            in 113..128 -> 128
            in 129..192 -> 192
            in 193..256 -> 256
            else -> error("Unspecified use of DRBG.")
        }
    }

    override val identifier: String
        get() = "$name-${algorithm.name}_$_securityStrength"

    private var _instantiated: Boolean = false
    override val instantiated: Boolean
        get() = _instantiated

    protected fun update(providedData: ByteArray, k_: ByteArray, v_: ByteArray): Pair<ByteArray, ByteArray> {
        var k = KeyHashedMac.create(k_, algorithm).let {
            it.update(v_ + byteArrayOf(0x00) + providedData)
            it.final()
        }
        var v = KeyHashedMac.create(k, algorithm).let {
            it.update(v_)
            it.final()
        }

        if(providedData.isEmpty()) return Pair(k, v)

        k = KeyHashedMac.create(k, algorithm).let {
            it.update(v + byteArrayOf(0x01) + providedData)
            it.final()
        }
        v = KeyHashedMac.create(k, algorithm).let {
            it.update(v)
            it.final()
        }

        return Pair(k, v)
    }

    protected fun getEntropyInput(
        minEntropy: Int,
        minLength: Int,
        maxLength: Int,
        predictionResistanceRequest: Boolean
    ): ByteArray {
        check(minEntropy in minLength..maxLength) {
            "Minimum entropy out of bounds between $minLength and $maxLength." }
        return when(predictionResistanceRequest) {
            true -> entropyGenerator.getByteArray(minEntropy)
            else -> randomGenerator.getByteArray(minEntropy)
        }
    }

    protected fun initializeAlgorithm(
        entropyInput: ByteArray,
        nonce: ByteArray,
        personalizationString: ByteArray,
        securityStrength: Int
    ): HmacDrbgState {
        val seedMaterial = entropyInput + nonce + personalizationString
        val key = ByteArray(algorithm.messageDigestSize) { 0x00 }
        val v = ByteArray(algorithm.messageDigestSize) { 0x01 }
        val kv = update(seedMaterial, key, v)
        val reseedCounter = 1
        return HmacDrbgState(kv.first, kv.second, reseedCounter)
    }

    override fun initialize() {
        val entropyInput = getEntropyInput(
            securityStrength, securityStrength,
            highestSupportedSecurityStrength, predictionResistanceFlag
        )
        val nonce = nonceGenerator.getByteArray(512)
        state = initializeAlgorithm(entropyInput, nonce, personalizationString, securityStrength)
        personalizationString = byteArrayOf()
        _instantiated = true
    }

    override fun finalize() {
        state = HmacDrbgState(byteArrayOf(), byteArrayOf(), -1)
        _instantiated = false
    }

    protected fun reseedAlgorithm(
        workingState: HmacDrbgState,
        entropyInput: ByteArray,
        additionalInput: ByteArray
    ): HmacDrbgState {
        val reseedMaterial = entropyInput + additionalInput
        val kv = update(reseedMaterial, workingState.first, workingState.second)
        val reseedCounter = 1
        return HmacDrbgState(kv.first, kv.second, reseedCounter)
    }

    override fun reseed(
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ) {
        if(predictionResistanceRequest)
            check(predictionResistanceRequest == predictionResistanceFlag) {
                "Prediction resistance is not granted." }
        check(additionalInput.size <= maxAdditionalInputLength) {
            "Additional input too long to be accepted currently." }

        val entropyInput = getEntropyInput(
            securityStrength, securityStrength,
            highestSupportedSecurityStrength, predictionResistanceFlag
        )
        state = reseedAlgorithm(state, entropyInput, additionalInput)
    }

    protected fun generateFromAlgorithm(
        workingState: HmacDrbgState,
        requestedNumberOfBits: Int,
        additionalInput: ByteArray
    ): Pair<ByteArray, HmacDrbgState> {
        check(workingState.third <= reseedInterval) { "Reseeding is required." }

        var (key, v) = if(additionalInput.isNotEmpty()) update(
            additionalInput,
            workingState.first,
            workingState.second
        ) else Pair(byteArrayOf(), byteArrayOf())

        var temp = byteArrayOf()
        while(temp.size * Byte.SIZE_BITS < requestedNumberOfBits) {
            v = KeyHashedMac.create(key, algorithm).let {
                it.update(v)
                it.final()
            }
            temp += v
        }

        val returnedBits = temp.copyOf(requestedNumberOfBits / Byte.SIZE_BITS)
        val kv = update(additionalInput, key, v)
        val reseedCounter = workingState.third + 1
        return Pair(returnedBits, HmacDrbgState(kv.first, kv.second, reseedCounter))
    }

    override fun generate(
        requestedNumberOfBits: Int,
        requestedSecurityStrength: Int,
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ): ByteArray {
        check(requestedNumberOfBits <= maxNumberOfBitsPerRequest) {
            "The requested number of bits are higher than maximum possible." }
        check(requestedSecurityStrength <= securityStrength) {
            "Requested security strength is higher than currently possible." }
        check(additionalInput.size <= maxAdditionalInputLength) {
            "Additional input too long to be accepted currently." }
        if(predictionResistanceRequest)
            check(predictionResistanceRequest == predictionResistanceFlag) {
                "Prediction resistance is not granted." }

        _reseedRequiredFlag = false
        if(reseedRequiredFlag or predictionResistanceFlag) {
            reseed(predictionResistanceRequest, additionalInput)
            _reseedRequiredFlag = false
        }

        TODO("Fix the last stuff")
        return byteArrayOf()
        //return generateFromAlgorithm(requestedNumberOfBits)
    }

    public override fun checkHealth() {
        TODO("Not yet implemented")
    }
    
    public companion object {
        public val name: String = "HMAC_DRBG"

        public const val highestSupportedSecurityStrength: Int = 256
        public const val maxPersonalizationStringLength: Int = 1024
    }
}