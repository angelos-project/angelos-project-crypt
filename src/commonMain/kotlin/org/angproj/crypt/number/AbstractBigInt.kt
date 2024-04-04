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

import org.angproj.crypt.num.AbstractBigInt
import kotlin.math.max

internal fun biggerFirst(x: IntArray, y: IntArray, block: (x: IntArray, y: IntArray) -> IntArray): IntArray =
    when (x.size < y.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

internal fun biggerFirst(x: AbstractBigInt<*>, y: AbstractBigInt<*>, block: (x: AbstractBigInt<*>, y: AbstractBigInt<*>) -> AbstractBigInt<*>): AbstractBigInt<*> =
    when (x.mag.size < y.mag.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

internal inline fun <A: List<Int>, B: List<Int>> maxOfArrays(x: A, y: B, extra: Int = 1): IntArray =
    IntArray(max(x.size, y.size) + extra)

internal inline fun bigMask(pos: Int): Int = 1 shl (pos and Int.SIZE_BITS - 1)