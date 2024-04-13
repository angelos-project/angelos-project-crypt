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

import org.angproj.aux.num.*

public infix fun BigInt.shl(n: Int): BigInt = shiftLeft(n)

public fun BigInt.shiftLeft(n: Int): BigInt = when {
    sigNum.isZero() -> BigInt.zero
    n > 0 -> BigInt(MutableBigInt.shiftLeftBits(mag, n).toList(), sigNum)
    n == 0 -> this
    else -> asMutableBigInt().shiftRightBits(-n)
}

internal fun MutableBigInt.Companion.shiftLeftBits(mag: List<Int>, count: Int): IntArray {
    val bigShift = count.floorDiv(Int.SIZE_BITS)
    val tinyShift = count.mod(Int.SIZE_BITS)
    val tinyShiftOpposite = Int.SIZE_BITS - tinyShift

    return when(tinyShift) {
        0 -> IntArray(mag.size + bigShift).also {
            mag.toIntArray().copyInto(it, 0, 0, mag.size) }
        else -> {
            val extra = if (mag[0].countLeadingZeroBits() <= tinyShift) 1 else 0
            val result = IntArray(mag.size + bigShift + extra)

            (result.size - bigShift until result.size).forEach { result[it] = 0 }
            if (extra == 1) result[0] = mag.first() ushr tinyShiftOpposite
            (0 until mag.lastIndex).forEach {
                result[it + extra] = (mag[it] shl tinyShift) or (mag[it + 1] ushr tinyShiftOpposite)
            }
            result[result.lastIndex - bigShift] = mag.last() shl tinyShift
            result
        }
    }
}