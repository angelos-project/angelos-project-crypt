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

    override fun nextBits(bitCount: Int): Int {
        counter = (counter + 1).inv().rotateLeft(2) xor -counter.rotateRight(23)
        return (counter shr (32 - bitCount + 32)).toInt()
    }
}