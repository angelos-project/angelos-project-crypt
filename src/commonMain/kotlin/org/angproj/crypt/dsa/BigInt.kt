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
package org.angproj.crypt.dsa

/**
 * pow mod log sqrt abs ceil floor lt gt le ge ne odd even
 * pow2 log2 sqrt2
 * addition subtraction multiplication division
 *
 * mod !
 * add
 * multiply
 * compareTo
 * divide !
 * subtract
 * pow !
 * equals
 */

public class BigInt internal constructor(magnitude: List<Int>, sigNum: BigSigned): AbstractBigInt<List<Int>>(magnitude, sigNum) {
    public constructor(magnitude: IntArray, sigNum: BigSigned): this(magnitude.asList(), sigNum)

    override fun negate(): BigInt = BigInt(mag, sigNum.negate())

    override fun copyOf(): BigInt = BigInt(mag, sigNum)
    override fun of(value: IntArray): BigInt = bigIntOf(value)
    override fun of(value: IntArray, sigNum: BigSigned): BigInt = BigInt(value, sigNum)

    public companion object {
        public val one: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.POSITIVE) }
        public val zero: BigInt by lazy { BigInt(intArrayOf(0), BigSigned.ZERO) }
        public val minusOne: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.NEGATIVE) }
    }
}

public fun bigIntOf(value: IntArray): BigInt = AbstractBigInt.fromIntArray(value) { a, b -> BigInt(a, b) }
public fun bigIntOf(value: ByteArray): BigInt = AbstractBigInt.fromByteArray(value) { a, b -> BigInt(a, b) }
