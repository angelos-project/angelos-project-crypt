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
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.bigIntOf
import kotlin.math.absoluteValue
import kotlin.math.sqrt

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
    else -> MutableBigInt.squareRoot(this)
}

internal fun MutableBigInt.Companion.squareRoot(value: AbstractBigInt<*>): AbstractBigInt<*> {
    var root = value.shr(value.bitLength / 2 + 1)
    var pow2: AbstractBigInt<*>
    var diff: AbstractBigInt<*>
    var loops = 0

    do {
        pow2 = root.pow2()
        diff = value - pow2
        val corr =  diff.shr(pow2.bitLength / 2 + 2)
        println("C: " + corr.sigNum + " " + BinHex.encodeToHex(corr.toByteArray()))
        root += corr
        loops++
    } while (corr.bitLength != 0)
    println("LOOPS: " + loops)
    //val diff = value - pow2
    println("V: " + value.sigNum + " " + BinHex.encodeToHex(value.toByteArray()))
    println("P: " + pow2.sigNum + " " + BinHex.encodeToHex(pow2.toByteArray()))
    println("D: " + diff.sigNum + " " + BinHex.encodeToHex(diff.toByteArray()))
    return root
}