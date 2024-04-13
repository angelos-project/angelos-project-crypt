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

public operator fun BigInt.times(other: BigInt): BigInt = multiply(other)

public fun BigInt.multiply(value: BigMath<*>): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value.toBigInt()) { big, little ->
        val negative = big.sigNum.isNegative().let { if (little.sigNum.isNegative()) !it else it }
        val product = MutableBigInt.multiply(
            big.abs().asMutableBigInt(),
            little.abs().asMutableBigInt()
        )
        val result = BigInt(product.mag.toList(), BigSigned.POSITIVE)
        return@biggerFirst if (negative) result.negate() else result
    }
}

internal fun MutableBigInt.Companion.multiply(x: BigMath<*>, y: BigMath<*>): MutableBigInt {
    val result = emptyMutableBigIntOf(IntArray(x.mag.size + y.mag.size))

    result.setIdx(x.mag.size, multiply(result, x, y.getIdx(0)))
    (1 until y.mag.size).forEach { idy ->
        val num = y.getIdxL(idy)
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += x.getIdxL(idx) * num + result.getIdxL(idy + idx)
            result.setIdxL(idy + idx, carry)
            carry = carry ushr Int.SIZE_BITS
        }
        result.setIdxL(idy + x.mag.size, carry)
    }
    return result
}

internal fun MutableBigInt.Companion.multiply(result: MutableBigInt, x: BigMath<*>, y: Int): Int {
    val first = y.getL()
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += x.getIdxL(idx) * first
        result.setIdxL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return carry.toInt()
}