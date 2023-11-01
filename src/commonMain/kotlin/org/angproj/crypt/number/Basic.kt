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

public operator fun BigInt.unaryMinus(): BigInt = negate()

public operator fun BigInt.plus(other: BigInt): BigInt = add(other)

public fun BigInt.add(value: BigInt): BigInt = when {
    sigNum.isZero() -> value
    value.sigNum.isZero() -> this
    else -> biggerFirst(this, value) { big, little ->
        return@biggerFirst BigInt.fromIntArray(BigInt.add(big, little))
    }
}

internal fun BigInt.Companion.add(x: BigInt, y: BigInt): IntArray {
    val result = IntArray(x.mag.size+1)

    var carry: Long = 0
    result.indices.forEach { idx ->
        carry += ((x.getIdx(idx).toLong() and 0xffffffffL)
                + (y.getIdx(idx).toLong() and 0xffffffffL))
        result.revSet(idx, carry.toInt())
        carry = carry ushr Int.SIZE_BITS
    }
    return result
}


public operator fun BigInt.minus(other: BigInt): BigInt = subtract(other)

public fun BigInt.subtract(value: BigInt): BigInt = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value.negate()
    else -> BigInt.fromIntArray(BigInt.subtract(this, value))
}

internal fun BigInt.Companion.subtract(x: BigInt, y: BigInt): IntArray {
    val result = maxOfArrays(x.mag, y.mag)
    var carry = 0
    result.indices.forEach { idr ->
        var yNum = y.getIdx(idr) + carry
        val xNum = x.getIdx(idr)
        carry = if (yNum xor -0x80000000 < carry xor -0x80000000) 1 else 0
        yNum = xNum - yNum
        carry += if (yNum xor -0x80000000 > xNum xor -0x80000000) 1 else 0
        result.revSet(idr, yNum)
    }
    return result
}

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
    val result = IntArray(x.mag.size + y.mag.size)
    result.revSet(x.mag.size, multiply(result, x, y.getIdx(0)))
    (1 until y.mag.size).forEach { idy ->
        val num = y.getIdx(idy).toLong() and 0xffffffffL
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += ((
                    x.getIdx(idx).toLong() and 0xffffffffL) * num + (
                    result.revGet(idy + idx).toLong() and 0xffffffffL))
            result.revSet(idy + idx, carry.toInt())
            carry = carry ushr Int.SIZE_BITS
        }
        result.revSet(idy + x.mag.size, carry.toInt())
    }
    return result
}

internal fun BigInt.Companion.multiply(result: IntArray, x: BigInt, y: Int): Int {
    val first = y.toLong() and 0xffffffffL
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += (x.getIdx(idx).toLong() and 0xffffffffL) * first
        result.revSet(idx, carry.toInt())
        carry = carry ushr Int.SIZE_BITS
    }
    return carry.toInt()
}