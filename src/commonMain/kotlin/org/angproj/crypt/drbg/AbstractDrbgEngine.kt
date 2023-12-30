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

public abstract class AbstractDrbgEngine: DeterministicRandomBitGenerator {
    /*public val securityStrength: Int
    public val workingState: Any*/

    /**
     * A count of the number of requests produced since the instantiation was seeded or
     * reseeded.
     */
    public val requestCount: Long = 0

    protected fun getEntropyInput( // Remove minEntropy?
        minEntropy: Int,
        minLength: Int,
        maxLength: Int,
        predictionResistanceRequest: Boolean
    ): Pair<Int, ByteArray> {
        return Pair(0, byteArrayOf())
    }

    protected fun initializeAlgorithm() {

    }

    override fun initialize(
        requestedInstantiationSecurityStrength: Int,
        predictionResistanceFlag: Boolean,
        personalizationString: ByteArray
    ) {
        TODO("Not yet implemented")
        //personalization: ByteArray, nonce: ByteArray, entropy: ByteArray

        /**
         * 1. Checks the validity of the input parameters,
         * 2. Determines the security strength for the DRBG instantiation,
         * 3. Obtains entropy input with entropy sufficient to support the security strength,
         * 4. Obtains the nonce (if required),
         * 5. Determines the initial internal state using the instantiate algorithm, and
         * 6. If an implementation supports multiple simultaneous instantiations of the same DRBG, a
         * state_handle for the internal state is returned to the consuming application (see below).
         * */
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
