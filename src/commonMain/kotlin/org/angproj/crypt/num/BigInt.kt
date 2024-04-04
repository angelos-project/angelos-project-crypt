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
package org.angproj.crypt.num

import org.angproj.aux.util.NullObject

public class BigInt internal constructor(magnitude: List<Int>, sigNum: BigSigned) :
    AbstractBigInt<List<Int>>(magnitude, sigNum) {

    public constructor(magnitude: IntArray, sigNum: BigSigned) : this(
        magnitude.asList(),
        sigNumZeroAdjust(magnitude, sigNum)
    )

    override fun negate(): BigInt = BigInt(mag, sigNum.negate())

    override fun copyOf(): BigInt = BigInt(mag, sigNum)
    override fun of(value: IntArray): BigInt = bigIntOf(value)
    override fun of(value: IntArray, sigNum: BigSigned): BigInt = BigInt(value, sigNum)
    override fun of(value: Long): BigInt = bigIntOf(value)

    public override fun toMutableBigInt(): MutableBigInt = MutableBigInt(mag.toIntArray(), sigNum)
    public override fun toBigInt(): BigInt = this

    public companion object {
        public val two: BigInt by lazy { MutableBigInt.two.toBigInt() }
        public val one: BigInt by lazy { MutableBigInt.one.toBigInt() }
        public val zero: BigInt by lazy { MutableBigInt.zero.toBigInt() }
        public val minusOne: BigInt by lazy { MutableBigInt.minusOne.toBigInt() }
    }
}

public fun unsignedBigIntOf(value: ByteArray): BigInt =
    BigInt(
        AbstractBigInt.stripLeadingZeros(value),
        BigSigned.POSITIVE
    )

public fun bigIntOf(value: IntArray): BigInt =
    AbstractBigInt.fromIntArray(value) { a, b -> BigInt(a, b) }

public fun bigIntOf(value: ByteArray): BigInt =
    AbstractBigInt.fromByteArray(value) { a, b -> BigInt(a, b) }

public fun bigIntOf(value: Long): BigInt = AbstractBigInt.fromLong(value) { a, b ->
    BigInt(
        a,
        b
    )
}

public fun BigInt.isNull(): Boolean = NullObject.bigInt === this

private val nullBigInt = BigInt(NullObject.intArray, BigSigned.ZERO)
public val NullObject.bigInt: BigInt
    get() = nullBigInt