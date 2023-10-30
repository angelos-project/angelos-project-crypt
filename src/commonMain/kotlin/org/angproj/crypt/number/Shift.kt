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

public infix fun BigInt.shl(count: Int): BigInt = shiftLeft(count)

public fun BigInt.shiftLeft(n: Int): BigInt = when {
    sigNum.isZero() -> zero
    n > 0 -> BigInt(BigInt.shiftLeftBits(mag, n), sigNum)
    n == 0 -> this
    else -> shiftRightBits(-n)
}

internal fun BigInt.Companion.shiftLeftBits(mag: IntArray, count: Int): IntArray {
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

public infix fun BigInt.shr(count: Int): BigInt = shiftRight(count)

public fun BigInt.shiftRight(n: Int): BigInt = when {
    sigNum.isZero() -> zero
    n > 0 -> shiftRightBits(n)
    n == 0 -> this
    else -> BigInt(BigInt.shiftLeftBits(mag, -n), sigNum)
}

internal fun BigInt.shiftRightBits(count: Int): BigInt {
    val bigShift: Int = count.floorDiv(Int.SIZE_BITS)
    val tinyShift: Int = count.mod(Int.SIZE_BITS)
    val tinyShiftOpposite = Int.SIZE_BITS - tinyShift

    if (bigShift >= mag.size) return if (sigNum.isNonNegative()) zero else minusOne

    val highBits = mag[0] ushr tinyShift
    val extra = if(highBits == 0) 0 else 1
    val remove = if (extra == 1) 0 else 1
    val result = when (highBits) {
        0 -> IntArray(mag.lastIndex - bigShift)
        else -> IntArray(mag.size - bigShift).also { it[0] = highBits }
    }
    (mag.lastIndex - bigShift - remove downTo extra).forEach {
        val idx = it + remove
        result[it] = mag[idx] ushr tinyShift or (mag[idx - 1] shl tinyShiftOpposite)
    }

    if (sigNum.isNegative()) {
        var onesLost = (mag.lastIndex downTo mag.size - bigShift).all { mag[it] == 0 }.not()
        if (!onesLost && tinyShift != 0) onesLost = mag[mag.size - bigShift - 1] shl 32 - tinyShift != 0
        if (onesLost) {
            (result.lastIndex downTo 0).indexOfFirst {
                result[it] += 1
                result[it] != 0
            }.takeIf { it == -1 }?.let {
                return BigInt(intArrayOf(1) + IntArray(result.size), sigNum) }
        }
    }

    return BigInt(result, sigNum)
}