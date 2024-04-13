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

public fun BigInt.pow(exponent: Int): BigInt = when {
    exponent < 0 -> err("Exponent can not be negative")
    exponent == 0 -> BigInt.one
    exponent == 1 -> this
    sigNum.isZero() -> this
    else -> {
        val size = (bitCount * exponent shr 5) + 2 * mag.size
        chk(size < Int.MAX_VALUE) { "Exponent is so large so there is to little memory left." }
        MutableBigInt.power(this, exponent)
    }
}

internal fun MutableBigInt.Companion.power(base: BigInt, exponent: Int): BigInt {
    var rest = exponent
    var total = BigInt.zero
    while(rest > 1) {
        var square = base
        val pow2 = BigMath.bitSizeForInt(rest)
        rest = (rest and (1 shl pow2-1).inv())
        (0 until pow2-1).forEach { _ -> square = square.multiply(square) }
        total = when(total.sigNum.isZero()) {
            false -> (total.multiply(square) )
            else -> square
        }
    }
    if(rest == 1) total = (total * base)
    return total
}