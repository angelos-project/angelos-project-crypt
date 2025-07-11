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

import org.angproj.aux.reg.RegistryItem
import org.angproj.aux.util.BinHex
import org.angproj.crypt.Hash
import org.angproj.crypt.hmac.KeyHashedMac
import org.angproj.sec.SecureEntropy
import org.angproj.sec.SecureRandom

public class HmacDrbgEngine(
    protected val algorithm: Hash,
    requestedInstantiationSecurityStrength: Int,
    predictionResistanceFlag: Boolean,
    private var personalizationString: ByteArray
): HmacDrbg, RegistryItem {

    private lateinit var state: HmacDrbgState

    private var _securityStrength: Int = 0
    public override val securityStrength: Int
        get() = _securityStrength

    private var _predictionResistanceFlag: Boolean = false
    public override val predictionResistanceFlag: Boolean
        get() = _predictionResistanceFlag

    private var _maxNumberOfBitsPerRequest: Int = 1024 * 32 * 8
    public override val maxNumberOfBitsPerRequest: Int
        get() = _maxNumberOfBitsPerRequest

    private var _maxAdditionalInputLength: Int = 8192
    override val maxAdditionalInputLength: Int
        get() = _maxAdditionalInputLength

    private var _reseedRequiredFlag: Boolean = false
    override val reseedRequiredFlag: Boolean
        get() = _reseedRequiredFlag

    private var _reseedInterval: Int = 100_000
    override val reseedInterval: Int
        get() = _reseedInterval

    init {
        require (requestedInstantiationSecurityStrength <= highestSupportedSecurityStrength) {
            "Requested security strength is higher than highest supported security strength." }
        _predictionResistanceFlag = predictionResistanceFlag
        require (personalizationString.size <= maxPersonalizationStringLength) {
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
        val entropy = ByteArray(minEntropy)
        when(predictionResistanceRequest) {
            true -> SecureEntropy.exportBytes(entropy, 0, entropy.size) { index, value ->
                entropy[index] = value
            }
            else -> SecureRandom.readBytes(entropy)
        }
        return entropy
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
        val nonce = ByteArray(512)
        SecureRandom.readBytes(nonce)
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
        _reseedRequiredFlag = false
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
        println("Before reseed: " + BinHex.encodeToHex(state.first) + " : " + BinHex.encodeToHex(state.second))
        state = reseedAlgorithm(state, entropyInput, additionalInput)
        println("After reseed: " + BinHex.encodeToHex(state.first) + " : " + BinHex.encodeToHex(state.second))
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
        ) else Pair(workingState.first, workingState.second)

        var temp = byteArrayOf()
        while(temp.size < requestedNumberOfBits / Byte.SIZE_BITS) {
            v = KeyHashedMac.create(key, algorithm).let {
                it.update(v)
                it.final()
            }
            temp += v
        }

        val returnedBits = temp.copyOf(requestedNumberOfBits / Byte.SIZE_BITS)
        val kv = update(additionalInput, key, v)
        val reseedCounter = workingState.third + 1
        if(reseedCounter > reseedInterval) _reseedRequiredFlag = true
        return Pair(returnedBits, HmacDrbgState(kv.first, kv.second, reseedCounter))
    }

    override fun generate(
        requestedNumberOfBits: Int,
        requestedSecurityStrength: Int,
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ): ByteArray {
        var predictionResistanceRequest_ = predictionResistanceRequest
        check(requestedNumberOfBits <= maxNumberOfBitsPerRequest) {
            "The requested number of bits are higher than maximum possible." }
        check(requestedSecurityStrength <= securityStrength) {
            "Requested security strength is higher than currently possible." }
        check(additionalInput.size <= maxAdditionalInputLength) {
            "Additional input too long to be accepted currently." }
        if(predictionResistanceRequest_)
            check(predictionResistanceRequest_ == predictionResistanceFlag) {
                "Prediction resistance is not granted." }

        val output = generateFromAlgorithm(state, requestedNumberOfBits, additionalInput)

        state = output.second
        return output.first
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