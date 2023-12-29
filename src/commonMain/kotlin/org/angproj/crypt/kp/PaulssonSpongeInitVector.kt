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
package org.angproj.crypt.kp

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class PaulssonSpongeInitVector {

    protected val iv: LongArray = LongArray(16)

    public fun setKey(key: LongArray) {
        key.copyInto(iv, 0, 0, 8) }

    public fun setSalt(salt: LongArray) {
        salt.copyInto(iv, 8, 0, 4) }

    public fun setEntropy(entropy: Long) { iv[13] = entropy }

    public fun setDomain(domain: Long) { iv[14] = domain }

    public fun setOffset(offset: Long) { iv[15] = offset }

    public fun increase() { iv[15]++ }

    public fun toSponge(): PaulssonSponge = PaulssonSponge(iv)
    public fun toScrambledSponge():PaulssonSponge = PaulssonSponge(iv, true)
}