/**
 * Copyright (c) 2023 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.util.Epoch
import org.angproj.aux.util.Nonce
import org.angproj.aux.util.toByteArray
import org.angproj.crypt.drbg.DeterministicRandomBitGenerator.Companion.ERROR_FLAG
import org.angproj.crypt.drbg.DeterministicRandomBitGenerator.Companion.SUCCESS

public abstract class AbstractDrbgEngine<E>: DeterministicRandomBitGenerator {

    protected abstract var initialWorkingState: E

    private var _securityStrength: Int = 0
    public override val securityStrength: Int
        get() = _securityStrength

    private var _predictionResistanceFlag: Boolean = false
    public val predictionResistanceFlag: Boolean
        get() = _predictionResistanceFlag

    /*public val securityStrength: Int
    public val workingState: Any*/

    /**
     * A count of the number of requests produced since the instantiation was seeded or
     * reseeded.
     */
    public val requestCount: Long = 0

    protected fun getEntropyInput(
        minEntropy: Int,
        minLength: Int,
        maxLength: Int,
        predictionResistanceRequest: Boolean
    ): Pair<Int, ByteArray> {
        if(minEntropy.mod(Byte.SIZE_BITS) != 0) return Pair(ERROR_FLAG, byteArrayOf())
        if(minEntropy !in minLength..maxLength) return Pair(ERROR_FLAG, byteArrayOf())
        return Pair(SUCCESS, ByteArray(minEntropy / Byte.SIZE_BITS) { Epoch.entropy().toByte() })
    }

    protected abstract fun initializeAlgorithm(
        entropyInput: ByteArray,
        nonce: ByteArray,
        personalizationString: ByteArray,
        securityStrength: Int
    ): E

    override fun initialize(
        requestedInstantiationSecurityStrength: Int,
        predictionResistanceFlag: Boolean,
        personalizationString: ByteArray
    ): Pair<Int, Int> {
        if (requestedInstantiationSecurityStrength > highestSupportedSecurityStrength)
            return Pair(ERROR_FLAG, 0)
        //if (predictionResistanceFlag)
        //    return Pair(ERROR_FLAG, 0)
        if (personalizationString.size > maxPersonalizationStringLength)
            return Pair(ERROR_FLAG, 0)

        val strength = when(requestedInstantiationSecurityStrength) {
            in 0..112 -> 112
            in 113..128 -> 128
            in 129..192 -> 192
            in 193..256 -> 256
            else -> error("Unspecified use of DRBG.")
        }

        val (entropyStatus, entropyInput) = getEntropyInput(
            strength, 112, 256, predictionResistanceFlag)
        if(entropyStatus != SUCCESS) return Pair(entropyStatus, 0)

        val nonce = Nonce.someEntropy().toByteArray()
        initialWorkingState = initializeAlgorithm(entropyInput, nonce, personalizationString, securityStrength)

        _predictionResistanceFlag = predictionResistanceFlag
        _securityStrength = strength

        return Pair(SUCCESS, 0)
    }

    override fun reseed(
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ) {
        TODO("Not yet implemented")
    }

    override fun generate(
        requestedNumberOfBits: Int,
        requestedSecurityStrength: Int,
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ): ByteArray {
        TODO("Not yet implemented")
    }

    override fun finalize(): Int {
        TODO("Not yet implemented")
    }

    override fun checkHealth() {
        TODO("Not yet implemented")
    }
}
