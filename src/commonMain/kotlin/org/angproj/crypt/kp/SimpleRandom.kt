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

import kotlin.random.Random

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class SimpleRandom(salt: Long = 0): Random() {

    private var counter: Long = 4165467438104248319 + salt

    public fun reseed(salt: Long) {
        counter += salt
    }

    // 31, 43  ->  For Random without bit manipulation
    // 23, 53  ->  For Kotlin bits
    private fun cycle() { counter = (counter + 1).inv().rotateLeft(31) xor -counter.rotateRight(43) }

    override fun nextBits(bitCount: Int): Int {
        cycle()
        return (counter shr (32 - bitCount + 32)).toInt()
    }

    override fun nextLong(): Long {
        cycle()
        return counter
    }
}

/**
 * left right
 * 23	53
 * 5	23
 * 41	5
 * 47	61
 * 2	19
 * 43	2
 * 53	29
 * 37	13
 * 59	11
 * 11	31
 * 31	43
 * 29	61
 * 19	59
 * 13	17
 * 17	37
 * 7	47
 */


