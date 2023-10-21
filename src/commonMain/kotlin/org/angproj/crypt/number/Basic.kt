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

public operator fun BigInt.unaryMinus(): BigInt = fromIntArrayAndSigNum(mag, sigNum.negate())

public inline fun BigInt.abs(): BigInt = when(sigNum) {
    BigSigned.NEGATIVE -> -this
    else -> this
}

public operator fun BigInt.plus(value: BigInt): BigInt {
    if (value.sigNum.state == 0) return this
    if (sigNum.state == 0) return value
    if (value.sigNum.state == sigNum.state) return BigInt(BigInt.add(mag, value.mag), sigNum)
    val cmp: BigCompare = compareMagnitude(value)
    if (cmp.state == 0) return zero
    var resultMag: IntArray = if (cmp.state > 0) BigInt.subtract(mag, value.mag) else BigInt.subtract(value.mag, mag)
    resultMag = trustedStripLeadingZeroInts1(resultMag)
    // if(resultMag.size == 0) BigSignedInt.ZERO else
    return BigInt(resultMag, if (cmp.state == sigNum.state) BigSigned.POSITIVE else BigSigned.NEGATIVE)
}

internal fun BigInt.Companion.add(x0: IntArray, y0: IntArray): IntArray {
    // If x is shorter, swap the two arrays
    var x = x0
    var y = y0
    if (x.size < y.size) {
        val tmp = x
        x = y
        y = tmp
    }
    var xIndex = x.size
    var yIndex = y.size
    val result = IntArray(xIndex)
    var sum: Long = 0

    // Add common parts of both numbers
    while (yIndex > 0) {
        sum = (x[--xIndex].toLong() and 0xffffffffL) +
                (y[--yIndex].toLong() and 0xffffffffL) + (sum ushr 32)
        result[xIndex] = sum.toInt()
    }

    // Copy remainder of longer number while carry propagation is required
    var carry = sum ushr 32 != 0L
    while (xIndex > 0 && carry) carry = (x[xIndex] + 1).also { result[--xIndex] = it } == 0

    // Copy remainder of longer number
    while (xIndex > 0) result[--xIndex] = x[xIndex]

    // Grow result if necessary
    if (carry) {
        //val bigger = IntArray(result.size + 1)
        val bigger = IntArray(result.size)
        //result.copyInto(bigger, 1, 0, result.size)
        result.copyInto(bigger, 0, 0, result.size)
        //bigger[0] = 0x01
        bigger[0] = Int.MIN_VALUE
        return bigger
    }
    return result
}

internal fun BigInt.Companion.add0(x0: IntArray, y0: IntArray): IntArray {
    // If x is shorter, swap the two arrays
    var x = x0
    var y = y0
    if (x.size < y.size) {
        val tmp = x
        x = y
        y = tmp
    }
    var xIndex = x.size
    var yIndex = y.size
    val result = IntArray(xIndex)
    var sum: Long = 0
    if (yIndex == 1) {
        sum = ((x[--xIndex].toLong() and 0xffffffffL) + (y[0].toLong() and 0xffffffffL))
        result[xIndex] = sum.toInt()
    } else {
        // Add common parts of both numbers
        while (yIndex > 0) {
            sum = (x[--xIndex].toLong() and 0xffffffffL) + (y[--yIndex].toLong() and 0xffffffffL) + (sum ushr 32)
            result[xIndex] = sum.toInt()
        }
    }
    // Copy remainder of longer number while carry propagation is required
    var carry = sum ushr 32 != 0L
    while (xIndex > 0 && carry) carry = (x[xIndex] + 1).also { result[--xIndex] = it } == 0

    // Copy remainder of longer number
    while (xIndex > 0) result[--xIndex] = x[xIndex]

    // Grow result if necessary
    if (carry) {
        val bigger = IntArray(result.size) // IntArray(result.size + 1)
        //result.copyInto(bigger, 1, 0, result.size)
        result.copyInto(bigger, 0, 0, result.size)
        //java.lang.System.arraycopy(result, 0, bigger, 1, result.size)
        // bigger[0] = 0x01
        bigger[0] = Int.MIN_VALUE
        return bigger
    }
    return result
}

public operator fun BigInt.minus(value: BigInt): BigInt {
    if (value.sigNum.state == 0) return this
    if (sigNum.state == 0) return -value
    if (value.sigNum != sigNum) return BigInt(BigInt.add(mag, value.mag), sigNum)
    val cmp: BigCompare = compareMagnitude(value)
    if (cmp.state == 0) return zero
    var resultMag: IntArray = if (cmp.state > 0) BigInt.subtract(mag, value.mag) else BigInt.subtract(value.mag, mag)
    resultMag = trustedStripLeadingZeroInts1(resultMag)
    return BigInt(resultMag, if (cmp.state == sigNum.state) BigSigned.POSITIVE else BigSigned.NEGATIVE)
}

public fun BigInt.Companion.subtract(big: IntArray, little: IntArray): IntArray {
    var bigIndex = big.size
    val result = IntArray(bigIndex)
    var littleIndex = little.size
    var difference: Long = 0

    // Subtract common parts of both numbers
    while (littleIndex > 0) {
        difference = (big[--bigIndex].toLong() and 0xffffffffL) -
                (little[--littleIndex].toLong() and 0xffffffffL) +
                (difference shr 32)
        result[bigIndex] = difference.toInt()
    }

    // Subtract remainder of longer number while borrow propagates
    var borrow = difference shr 32 != 0L
    while (bigIndex > 0 && borrow) borrow = (big[bigIndex] - 1).also { result[--bigIndex] = it } == -1

    // Copy remainder of longer number
    while (bigIndex > 0) result[--bigIndex] = big[bigIndex]
    return result
}

private fun BigInt.trustedStripLeadingZeroInts1(value: IntArray): IntArray {
    val vlen = value.size
    var keep: Int

    // Find first nonzero byte
    keep = 0
    while (keep < vlen && value[keep] == 0) {
        keep++
    }
    return if (keep == 0) value else value.copyOfRange(keep, vlen)
}

private fun BigInt.makePositive1(a: IntArray): IntArray {
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

public fun BigInt.fromIntArray(value: IntArray): BigInt {
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

public fun BigInt.fromIntArrayAndSigNum(magnitude: IntArray, signum: BigSigned): BigInt {
    val newSigNum = if (magnitude.size == 0) BigSigned.ZERO else signum
    return BigInt(magnitude, newSigNum)
}

public fun BigInt.valueOf(value: IntArray): BigInt {
    return if (value[0] > 0) BigInt(value, BigSigned.POSITIVE) else {
        fromIntArray(value)
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