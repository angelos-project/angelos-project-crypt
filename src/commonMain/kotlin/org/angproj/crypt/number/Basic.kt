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
package org.angproj.crypt.number

import org.angproj.crypt.dsa.*


internal val zero: BigInt = BigInt(intArrayOf(0), BigSigned.ZERO)
internal val minusOne: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.NEGATIVE) }

public operator fun BigInt.unaryMinus(): BigInt = BigInt.fromIntArrayAndSigNum(mag, sigNum.negate())

public inline fun BigInt.abs(): BigInt = when(sigNum) {
    BigSigned.NEGATIVE -> -this
    else -> this
}

private fun BigInt.Companion.trustedStripLeadingZeroInts1(value: IntArray): IntArray {
    val vlen = value.size
    var keep: Int

    // Find first nonzero byte
    keep = 0
    while (keep < vlen && value[keep] == 0) {
        keep++
    }
    //value.indexOfFirst { it != 0 }
    return if (keep == 0) value else value.copyOfRange(keep, vlen)
}

private fun BigInt.Companion.makePositive1(a: IntArray): IntArray {
    var keep: Int
    var j: Int

    // Find first non-sign (0xffffffff) int of input
    keep = 0
    while (keep < a.size && a[keep] == -1) {
        keep++
    }

    /* Allocate output array.  If all non-sign ints are 0x00, we must
         * allocate space for one extra output int. */j = keep
    while (j < a.size && a[j] == 0) {
        j++
    }
    val extraInt = if (j == a.size) 1 else 0
    val result = IntArray(a.size - keep + extraInt)

    /* Copy one's complement of input into output, leaving extra
         * int (if it exists) == 0x00 */
    for (i in keep until a.size)
        result[i - keep + extraInt] = a[i].inv()

    // Add one to one's complement to generate two's complement
    var i = result.size - 1
    while (++result[i] == 0) {
        i--
    }
    return result
}

public fun bitLengthForInt(n: Int): Int {
    return 32 - n.countLeadingZeroBits()
}

public fun BigInt.intLength(): Int {
    return (bitLength ushr 5) + 1
}

public fun BigInt.Companion.fromIntArray(value: IntArray): BigInt {
    if (value.size == 0) error("Zero length BigInteger")
    var signum: BigSigned

    val magnitude = if (value[0] < 0) {
        signum = BigSigned.NEGATIVE
        makePositive1(value)
    } else {
        val newMag = trustedStripLeadingZeroInts1(value)
        signum = if (newMag.size == 0) BigSigned.ZERO else BigSigned.POSITIVE
        newMag
    }
    return fromIntArrayAndSigNum(magnitude, signum)
}

public fun BigInt.Companion.fromIntArrayAndSigNum(magnitude: IntArray, signum: BigSigned): BigInt {
    val newSigNum = if (magnitude.size == 0) BigSigned.ZERO else signum
    return BigInt(magnitude, newSigNum)
}

public fun BigInt.valueOf(value: IntArray): BigInt {
    return if (value[0] > 0) BigInt(value, BigSigned.POSITIVE) else {
        BigInt.fromIntArray(value)
    }
}

public fun BigInt.getInt(n: Int): Int {
    if (n < 0) return 0
    if (n >= mag.size) return sigNum.signed
    val magInt: Int = mag.get(mag.size - n - 1)
    return if (sigNum.state >= 0) magInt else if (n <= firstNonZero) -magInt else magInt.inv()
}

public fun BigInt.longValue(): Long {
    var result: Long = 0
    for (i in 1 downTo 0) result = (result shl 32) + (getInt(i).toLong() and 0xffffffffL)
    return result
}

public fun BigInt.longValueExact(): Long = if (mag.size <= 2 && bitLength <= 63) longValue() else error("BigInteger out of long range")