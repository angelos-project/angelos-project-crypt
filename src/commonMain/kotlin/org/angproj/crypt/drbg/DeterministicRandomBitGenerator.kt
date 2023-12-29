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

/**
 * A DRBG mechanism requires instantiate, uninstantiate, generate, and health testing functions.
 *
 * A DRBG shall be instantiated prior to the generation of output by the DRBG.
 */
public interface DeterministicRandomBitGenerator {

    /**
     * The instantiate function acquires entropy input and may combine it with a nonce and a
     * personalization string to create a seed from which the initial internal state is created.
     * */
    public fun instantiate(
        personalization: ByteArray,
        nonce: ByteArray = byteArrayOf(),
        entropy: ByteArray = byteArrayOf()
    )

    /**
     * The reseed function acquires new entropy input and combines it with the current internal
     * state and any additional input that is provided to create a new seed and a new internal
     * state.
     *
     * A DRBG mechanism includes an optional reseed function.
     * */
    public fun reseed(entropy: ByteArray, additional: ByteArray)

    /**
     * The generate function generates pseudorandom bits upon request, using the current
     * internal state and possibly additional input; a new internal state for the next request is also
     * generated.
     * */
    public fun generate(additional: ByteArray): ByteArray

    /**
     * The uninstantiate function zeroizes (i.e., erases) the internal state.
     */
    public fun uninstantiate()

    /**
     * The health test function determines that the DRBG mechanism continues to function
     * correctly.
     * */
    public fun healthTest()
}