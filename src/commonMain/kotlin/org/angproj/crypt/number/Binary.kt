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


public inline fun maxOfArrays(a: IntArray, b: IntArray, extra: Int = 1): IntArray = IntArray(max(a.size, b.size) + extra)

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

public infix fun BigInt.and(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@and.getIdx(idx) and value.getIdx(idx) } }
    return valueOf(result)
}

public infix fun BigInt.or(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@or.getIdx(idx) or value.getIdx(idx) } }
    return valueOf(result)
}

public infix fun BigInt.xor(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@xor.getIdx(idx) xor value.getIdx(idx) } }
    return valueOf(result)
}

public fun BigInt.not(): BigInt {
    val result = IntArray(size+1).apply {
        indices.forEach { this[it] = this@not[revIdx(it, this)].inv() } }
    return valueOf(result)
}

public fun BigInt.andNot(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@andNot.getIdx(idx) and value.getIdx(idx).inv() } }
    return valueOf(result)
}