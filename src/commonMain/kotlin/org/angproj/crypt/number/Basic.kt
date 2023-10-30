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

internal val one: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.POSITIVE) }
internal val zero: BigInt by lazy { BigInt(intArrayOf(0), BigSigned.ZERO) }
internal val minusOne: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.NEGATIVE) }

public fun compare2sign(sigNum: BigSigned, cmp: BigCompare): BigSigned = when (cmp.state == sigNum.state) {
    true -> BigSigned.POSITIVE
    else -> BigSigned.NEGATIVE
}

public fun packMagSigNum(mag: IntArray, sigNum: BigSigned, cmp: BigCompare): BigInt = BigInt(
    BigInt.trustedStripLeadingZeroInts1(mag), compare2sign(sigNum, cmp)
)

internal fun biggerFirst(x: IntArray, y: IntArray, block: (x: IntArray, y: IntArray) -> IntArray): IntArray =
    when (x.size < y.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

public operator fun BigInt.plus(other: BigInt): BigInt = add(other)

public fun BigInt.add(value: BigInt): BigInt = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value
    value.sigNum == sigNum -> BigInt(BigInt.add(mag, value.mag), sigNum)
    else -> {
        when (val cmp = compareMagnitude(value)) {
            BigCompare.GREATER -> packMagSigNum(BigInt.subtract(mag, value.mag), sigNum, cmp)
            BigCompare.LESSER -> packMagSigNum(BigInt.subtract(value.mag, mag), sigNum, cmp)
            BigCompare.EQUAL -> zero
        }
    }
}

internal fun BigInt.Companion.add(x: IntArray, y: IntArray): IntArray = biggerFirst(x, y) { big, little ->
    val result = IntArray(big.size)
    val sizeDiff = big.size - little.size
    var index = if (little.size == 1) big.lastIndex else sizeDiff
    var sum: Long = 0

    when (little.size) {
        1 -> {
            sum = (big.last().toLong() and 0xffffffffL) + (little.first().toLong() and 0xffffffffL)
            result[big.lastIndex] = sum.toInt()
        }
        else -> (little.lastIndex downTo 0).forEach {
            val idx = it + sizeDiff
            sum = (big[idx].toLong() and 0xffffffffL
                    ) + (little[it].toLong() and 0xffffffffL
                    ) + (sum ushr Int.SIZE_BITS)
            result[idx] = sum.toInt()
        }
    }

    var carry = sum ushr Int.SIZE_BITS != 0L
    carry.takeIf { index > 0 }.let {
        while (index > 0 && carry) {
            result[--index] = big[index] + 1
            carry = result[index] == 0
        }
    }
    takeIf { index > 0 }.let { (index - 1 downTo 0).forEach { result[it] = big[it] } }

    return@biggerFirst when (carry) {
        true -> intArrayOf(1) + result
        else -> result
    }
}

public operator fun BigInt.minus(other: BigInt): BigInt = subtract(other)

public fun BigInt.subtract(value: BigInt): BigInt = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> -value
    value.sigNum != sigNum -> BigInt(BigInt.add(mag, value.mag), sigNum)
    else -> {
        when (val cmp = compareMagnitude(value)) {
            BigCompare.GREATER -> packMagSigNum(BigInt.subtract(mag, value.mag), sigNum, cmp)
            BigCompare.LESSER -> packMagSigNum(BigInt.subtract(value.mag, mag), sigNum, cmp)
            BigCompare.EQUAL -> zero
        }
    }
}

internal fun BigInt.Companion.subtract(big: IntArray, little: IntArray): IntArray {
    val result = IntArray(big.size)
    val sizeDiff = big.size - little.size
    var index = sizeDiff
    var difference: Long = 0

    (little.lastIndex downTo 0).forEach {
        val idx = it + sizeDiff
        difference = (big[idx].toLong() and 0xffffffffL
                ) - (little[it].toLong() and 0xffffffffL
                ) + (difference shr Int.SIZE_BITS)
        result[idx] = difference.toInt()
    }

    index.takeIf { it > 0 && difference shr Int.SIZE_BITS != 0L}?.let {
        do result[--index] = big[index] - 1
        while (index > 1 && result[index] == -1)
    }
    index.takeIf { it > 0 }.let { (index - 1 downTo 0).forEach { result[it] = big[it] } }

    return result
}

public operator fun BigInt.unaryMinus(): BigInt = BigInt.fromIntArrayAndSigNum(mag, sigNum.negate())

public fun BigInt.abs(): BigInt = when (sigNum) {
    BigSigned.NEGATIVE -> -this
    else -> this
}

private fun BigInt.Companion.trustedStripLeadingZeroInts1(value: IntArray): IntArray {
    /*val vlen = value.size
    var keep: Int

    // Find first nonzero byte
    keep = 0
    while (keep < vlen && value[keep] == 0) {
        keep++
    }
    //value.indexOfFirst { it != 0 }
    return if (keep == 0) value else value.copyOfRange(keep, vlen)*/
    val vlen: Int = value.size
    var keep: Int

    keep = 0
    while (keep < vlen && value[keep] == 0) {
        keep++
    }

    return if (keep == 0) value else value.copyOfRange(keep, vlen)
}

internal fun keep(value: IntArray, sigNum: BigSigned): Int {
    val keep = value.indexOfFirst { it != sigNum.signed }
    return when (keep) {
        -1 -> value.size
        else -> keep
    }
}

internal fun keepStripZero(value: IntArray): Int = keep(value, BigSigned.ZERO)
internal fun keepMakePositive(value: IntArray): Int = keep(value, BigSigned.NEGATIVE)

private fun BigInt.Companion.makePositive1(a: IntArray): IntArray {
    var keep: Int = keepMakePositive(a)
    var j: Int

    // Find first non-sign (0xffffffff) int of input
    /*keep = 0
    while (keep < a.size && a[keep] == -1) {
        keep++
    }*/

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

public fun BigInt.Companion.fromIntArray0(value: IntArray): BigInt {
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

public fun BigInt.Companion.fromIntArray2(value: IntArray): BigInt {
    if (value.size == 0) error("Zero length BigInteger")
    var sigNum: BigSigned

    val magnitude = if (value[0] < 0) {
        sigNum = BigSigned.NEGATIVE
        makePositive1(value)
    } else {
        val newMag = trustedStripLeadingZeroInts1(value)
        sigNum = if (newMag.size == 0) BigSigned.ZERO else BigSigned.POSITIVE
        newMag
    }
    //return BigInt(magnitude, sigNum)
    return fromIntArrayAndSigNum2(magnitude, sigNum)
}

public fun BigInt.Companion.fromIntArrayAndSigNum(magnitude: IntArray, signum: BigSigned): BigInt {
    val newSigNum = if (magnitude.isEmpty()) BigSigned.ZERO else signum
    return BigInt(magnitude, newSigNum)
}

/*
BigInteger(int[] magnitude, int signum) {
        this.signum = (magnitude.length == 0 ? 0 : signum);
        this.mag = magnitude;
        if (mag.length >= MAX_MAG_LENGTH) {
            checkRange();
        }
    }
* */

public fun BigInt.Companion.fromIntArrayAndSigNum2(magnitude: IntArray, signum: BigSigned): BigInt {
    val newSigNum = if (magnitude.isEmpty()) BigSigned.ZERO else signum
    return BigInt(magnitude, newSigNum)
}

// return (val[0] > 0 ? new BigInteger(val, 1) : new BigInteger(val));
public fun BigInt.valueOf(value: IntArray): BigInt = when {
    value.first() > 0 -> BigInt(value, BigSigned.POSITIVE)
    else -> BigInt.fromIntArray2(value)
}

/*{
    return if (value[0] > 0) BigInt(value, BigSigned.POSITIVE) else {
        BigInt.fromIntArray(value)
    }
}*/

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

public fun BigInt.longValueExact(): Long =
    if (mag.size <= 2 && bitLength <= 63) longValue() else error("BigInteger out of long range")


public fun BigInt.Companion.fromIntArray3(value: IntArray): BigInt {
    checkZeroSize(value)
    val sigNum = sigNumFromValue(value)
    val mag = magFromValue(value)
    return BigInt(mag, sigNumZeroAdjust(mag, sigNum))
}

public fun checkZeroSize(mag: IntArray): Unit = check(mag.isEmpty()) { "Zero length" }

public fun sigNumFromValue(value: IntArray): BigSigned = when(value.first() < 0) {
    true -> BigSigned.NEGATIVE
    else -> BigSigned.POSITIVE
}

public fun BigInt.Companion.magFromValue(value: IntArray): IntArray = when(value.first() < 0) {
    true -> makePositive1(value)
    else -> trustedStripLeadingZeroInts1(value)
}

