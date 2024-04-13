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
import kotlin.math.*

/**
 * Not ready to be used
 * Check unittest for a brief explanation.
 * */
internal fun BigInt.sqrt(): BigInt = when {
    sigNum.isNegative() -> err("Can not calculate the square root of a negative value.")
    sigNum.isZero() -> BigInt.zero
    bitLength <= Long.SIZE_BITS -> ofLong(sqrt(toLong().toDouble()).toLong())
    else -> MutableBigInt.squareRoot(this)
}

internal fun MutableBigInt.Companion.squareRoot(value: BigInt): BigInt {
    var dividend = value
    var root = BigInt.zero
    var mask = bitMask(dividend.bitLength)
    val size = when(value.bitLength > 127) {
        true -> 63
        else -> 31
    }

    while(dividend.bitLength >= size) {
        var shift = dividend.bitLength - size
        if (shift % 2 == 1) shift++
        mask = mask.shiftRight(size)
        val sqrt = ofLong(ceil(sqrt(dividend.shiftRight(shift).toLong().toDouble())).toLong())
        root += sqrt.toBigInt().shiftLeft(shift / 2 - 1) * BigInt.two
        dividend = dividend.and(mask)
    }

    while(true) {
        val corr = value.divide(root).add(root).shiftRight(1)
        if (corr.compareSpecial(root).isGreaterOrEqual()) break
        root = corr
    }
    return root
}

internal fun MutableBigInt.Companion.bitMask(bitCount: Int): BigInt {
    return when {
        bitCount <= 0 -> BigInt.zero
        bitCount == 1 -> BigInt.one
        else -> BigInt.one.shiftLeft(bitCount).add(BigInt.minusOne)
    }
}