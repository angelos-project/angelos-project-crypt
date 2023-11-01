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

public operator fun BigInt.times(other: BigInt): BigInt = multiply(other)

private fun BigInt.multiply(value: BigInt): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value) { big, little ->
        val negative = big.sigNum.isNegative().let { if(little.sigNum.isNegative()) !it else it }
        val result = BigInt(BigInt.multiply(big.abs(), little.abs()), BigSigned.POSITIVE)
        return@biggerFirst if(negative) result.negate() else result
    }
}

internal fun BigInt.Companion.multiply(x: BigInt, y: BigInt): IntArray {
    val dest = IntArray(x.mag.size + y.mag.size)
    dest.revSet(x.mag.size, multiply(dest, x, y.getIdx(0)))
    (1 until y.mag.size).forEach { idy ->
        val num = y.getIdx(idy).toLong() and 0xffffffffL
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += ((
                    x.getIdx(idx).toLong() and 0xffffffffL) * num + (
                    dest.revGet(idy + idx).toLong() and 0xffffffffL))
            dest.revSet(idy + idx, carry.toInt())
            carry = carry ushr Int.SIZE_BITS
        }
        dest.revSet(idy + x.mag.size, carry.toInt())
    }
    return dest
}

internal fun BigInt.Companion.multiply(dest: IntArray, x: BigInt, y: Int): Int {
    val first = y.toLong() and 0xffffffffL
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += (x.getIdx(idx).toLong() and 0xffffffffL) * first
        dest.revSet(idx, carry.toInt())
        carry = carry ushr Int.SIZE_BITS
    }
    return carry.toInt()
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
            BigCompare.EQUAL -> BigInt.zero
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
    sigNum.isZero() -> value.negate()
    value.sigNum != sigNum -> BigInt(BigInt.add(mag, value.mag), sigNum)
    else -> {
        when (val cmp = compareMagnitude(value)) {
            BigCompare.GREATER -> packMagSigNum(BigInt.subtract(mag, value.mag), sigNum, cmp)
            BigCompare.LESSER -> packMagSigNum(BigInt.subtract(value.mag, mag), sigNum, cmp)
            BigCompare.EQUAL -> BigInt.zero
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

    index.takeIf { it > 0 && difference shr Int.SIZE_BITS != 0L }?.let {
        do result[--index] = big[index] - 1
        while (index > 1 && result[index] == -1)
    }
    index.takeIf { it > 0 }.let { (index - 1 downTo 0).forEach { result[it] = big[it] } }

    return result
}

public operator fun BigInt.unaryMinus(): BigInt = negate()