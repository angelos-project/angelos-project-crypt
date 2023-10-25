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

import org.angproj.crypt.dsa.BigInt
import kotlin.math.max
import kotlin.math.min


public inline fun maxOfArrays(a: IntArray, b: IntArray, extra: Int = 1): IntArray =
    IntArray(max(a.size, b.size) + extra)

public inline fun revIdx(idx: Int, arr: IntArray): Int = arr.lastIndex - idx

public inline fun BigInt.getIdx(idx: Int): Int = when {
    idx < 0 -> 0
    idx >= mag.size -> sigNum.signed
    else -> {
        val num = mag[revIdx(idx, mag)]
        when {
            sigNum.isNonNegative() -> num
            idx <= firstNonZero -> -num
            else -> num.inv()
        }
    }
}

public inline fun BigInt.getNum(idx: Int): Int = when {
    idx < 0 -> 0
    idx >= mag.size -> sigNum.signed
    else -> mag[revIdx(idx, mag)]
}

public inline fun BigInt.adjNum(idx: Int, num: Int): Int = when {
    sigNum.isNonNegative() -> num
    idx <= firstNonZero -> -num
    else -> num.inv()
}

public infix fun BigInt.and(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@and.getIdx(idx) and value.getIdx(idx)
        }
    }
    return valueOf(result)
}

public infix fun BigInt.or(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@or.getIdx(idx) or value.getIdx(idx)
        }
    }
    return valueOf(result)
}

public infix fun BigInt.xor(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@xor.getIdx(idx) xor value.getIdx(idx)
        }
    }
    return valueOf(result)
}

public fun BigInt.not(): BigInt {
    val result = IntArray(size + 1).apply {
        indices.forEach { this[it] = this@not[revIdx(it, this)].inv() }
    }
    return valueOf(result)
}

public fun BigInt.andNot(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@andNot.getIdx(idx) and value.getIdx(idx).inv()
        }
    }
    return valueOf(result)
}

public fun BigInt.Companion.shiftLeft(mag: IntArray, count: Int): IntArray {
    val bigShift = count.floorDiv(Int.SIZE_BITS)
    val tinyShift = count.mod(Int.SIZE_BITS)
    val tinyShiftOpposite = Int.SIZE_BITS - tinyShift

    val extra = if (mag[0].countLeadingZeroBits() <= tinyShift) 1 else 0
    val result = IntArray(mag.size + bigShift + extra)

    (result.size - bigShift until result.size).forEach { result[it] = 0 }
    if (extra == 1) result[0] = mag.first() ushr tinyShiftOpposite
    (0 until mag.lastIndex).forEach {
        result[it + extra] = (mag[it] shl tinyShift) or (mag[it + 1] ushr tinyShiftOpposite)
    }
    result[result.lastIndex - bigShift] = mag.last() shl tinyShift

    return result
}

public fun BigInt.shiftRight(count: Int): BigInt {
    val bigShift: Int = count.floorDiv(Int.SIZE_BITS)
    val tinyShift: Int = count.mod(Int.SIZE_BITS)

    if (bigShift >= mag.size) return if (sigNum.isNonNegative()) zero else minusOne

    var result = when (tinyShift) {
        0 -> mag.copyOf(mag.size - bigShift)
        else -> {
            val highBits = mag[0] ushr tinyShift
            val extra = min(highBits, 1)
            val remove = if (extra == 1) 0 else 1
            when (highBits) {
                0 -> IntArray(mag.lastIndex - bigShift)
                else -> IntArray(mag.size - bigShift).also { it[0] = highBits }
            }.also { result ->
                val tinyshiftOpposite = Int.SIZE_BITS - tinyShift
                (mag.lastIndex - bigShift - remove downTo extra).forEach {
                    val idx = it + remove
                    result[it] = mag[idx] ushr tinyShift or (mag[idx - 1] shl tinyshiftOpposite)
                }
            }
        }
    }

    if (sigNum.isNegative()) when (tinyShift != 0) {
        true -> mag[mag.lastIndex - bigShift] shl Int.SIZE_BITS - tinyShift != 0
        else -> (mag.lastIndex downTo mag.size - bigShift).all { mag[it] == 0 }.not()
    }.takeIf { it }?.let {
        (result.lastIndex downTo 0).indexOfFirst {
            result[it] += 1
            result[it] != 0
        }
    }.takeIf { it == -1 }?.let { return BigInt(intArrayOf(1) + IntArray(result.size), sigNum) }

    return BigInt(result, sigNum)
}