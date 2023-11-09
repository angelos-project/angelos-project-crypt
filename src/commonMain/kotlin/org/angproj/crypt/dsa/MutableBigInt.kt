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

public class MutableBigInt internal constructor(magnitude: MutableList<Int>, sigNum: BigSigned): AbstractBigInt<MutableList<Int>>(magnitude, sigNum) {
    public constructor(magnitude: IntArray, sigNum: BigSigned): this(magnitude.toMutableList(), sigNum)

    override fun negate(): MutableBigInt = MutableBigInt(mag, sigNum.negate())

    override fun copyOf(): MutableBigInt = MutableBigInt(mag, sigNum)
    override fun of(value: IntArray): MutableBigInt = mutableBigIntOf(value)
    override fun of(value: IntArray, sigNum: BigSigned): MutableBigInt = MutableBigInt(value, sigNum)
}

public fun mutableBigIntOf(value: IntArray): MutableBigInt = AbstractBigInt.fromIntArray(value) { a, b -> MutableBigInt(a, b) }
public fun mutableBigIntOf(value: ByteArray): MutableBigInt = AbstractBigInt.fromByteArray(value) { a, b -> MutableBigInt(a, b) }