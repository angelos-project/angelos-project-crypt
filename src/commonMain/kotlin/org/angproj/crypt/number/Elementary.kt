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

import org.angproj.aux.num.AbstractBigInt
import org.angproj.aux.num.BigInt
import org.angproj.aux.num.MutableBigInt
import org.angproj.aux.util.bigIntOf
import kotlin.math.*

public fun AbstractBigInt<*>.pow2(): AbstractBigInt<*> = when {
    sigNum.isZero() -> this
    else -> of((this * this).toRawIntArray())
}

public infix fun AbstractBigInt<*>.pow(exponent: Int): AbstractBigInt<*> = when {
    exponent < 0 -> error("Negative exponent")
    exponent == 0 -> BigInt.one
    exponent == 1 -> this
    sigNum.isZero() -> this
    else -> {
        val size = (bitCount * exponent shr 5) + 2 * mag.size
        check(size < Int.MAX_VALUE) { "Resulting memory to large. Size $size 32-bit integer array is required." }
        val negative = sigNum.isNegative() && exponent and 1 != 0
        val result = of(MutableBigInt.power(abs(), exponent).toRawIntArray())
        if (negative) result.negate() else result
    }
}

internal fun MutableBigInt.Companion.power(base: AbstractBigInt<*>, exponent: Int): MutableBigInt {
    var rest = exponent
    var total = zero
    while(rest > 1) {
        var square = base
        val pow2 = AbstractBigInt.bitSizeForInt(rest)
        rest = (rest and (1 shl pow2-1).inv())
        (0 until pow2-1).forEach { _ -> square *= square }
        total = when(total.sigNum.isZero()) {
            false -> (total * square)
            else -> square
        }.toMutableBigInt()
    }
    if(rest == 1) total = (total * base).toMutableBigInt()
    return total
}

public fun AbstractBigInt<*>.log2(): Long {
    return bitLength.toLong()
}

public fun AbstractBigInt<*>.sqrt(): AbstractBigInt<*> = when {
    this.sigNum.isNegative() -> error("Negative value")
    this.sigNum.isZero() -> BigInt.zero
    this.bitLength <= 64 -> bigIntOf(sqrt(this.toLong().toDouble()).toLong())
    this.bitLength <= 128 -> MutableBigInt.squareRootBelow128(this)
    else -> MutableBigInt.squareRoot(this)
}

internal fun MutableBigInt.Companion.squareRoot(value: AbstractBigInt<*>): AbstractBigInt<*> {
    var qoutient = value
    var root: AbstractBigInt<*> = BigInt.zero

    do {
        var shift = qoutient.bitLength - 63
        if (shift % 2 == 1) shift++
        val mask = AbstractBigInt.bitMask(shift)
        val sqrt = bigIntOf(ceil(sqrt(qoutient.shr(shift).toLong().toDouble())).toLong())
        root += sqrt.shl(shift / 2 - 1) * BigInt.two
        qoutient = qoutient and mask
    } while(qoutient.bitLength >= 63)

    while(true) {
        val tmp = value.divide(root).add(root).shr(1)
        if (tmp.compareTo(root).isGreaterOrEqual()) break
        root = tmp
    }
    return root
}

internal fun MutableBigInt.Companion.squareRootBelow128(value: AbstractBigInt<*>): AbstractBigInt<*> {
    var shift = value.bitLength - 63
    if (shift % 2 == 1) shift++

    var root = bigIntOf(ceil(sqrt(value.shr(shift).toLong().toDouble())).toLong()).shl(shift / 2)

    while(true) {
        val corr = value.divide(root).add(root).shr(1)
        if (corr.compareTo(root).isGreaterOrEqual()) break
        root = corr
    }
    return root
}