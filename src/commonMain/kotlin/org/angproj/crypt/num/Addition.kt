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

public operator fun BigInt.plus(other: BigInt): BigInt = add(other)

public operator fun BigInt.inc(): BigInt = add(BigInt.one)

public fun BigInt.add(value: BigMath<*>): BigInt = when {
    sigNum.isZero() -> value.toBigInt()
    value.sigNum.isZero() -> this
    else -> {
        val out = biggerFirst(this, value) { big, little ->
            return@biggerFirst MutableBigInt.add(big, little)
        }
        ofIntArray(out.mag.toIntArray())
    }
}

internal fun MutableBigInt.Companion.add(x: BigMath<*>, y: BigMath<*>): MutableBigInt {
    val result = emptyMutableBigIntOf(IntArray(x.mag.size + 1))
    var carry: Long = 0

    result.mag.indices.forEach { idx ->
        carry += x.getIdxL(idx) + y.getIdxL(idx)
        result.setIdxL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return result
}