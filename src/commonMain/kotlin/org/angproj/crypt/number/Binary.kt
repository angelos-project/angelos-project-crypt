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


public inline fun maxOfArrays(a: IntArray, b: IntArray, extra: Int = 1): IntArray =
    IntArray(max(a.size, b.size) + extra)

public inline fun revIdx(idx: Int, arr: IntArray): Int = arr.lastIndex - idx
public inline fun revGet(idx: Int, arr: IntArray): Int = arr[revIdx(idx, arr)]
public inline fun revSet(idx: Int, arr: IntArray, value: Int) { arr[revIdx(idx, arr)] = value }

public inline fun bigMask(pos: Int): Int = 1 shl (pos and Int.SIZE_BITS - 1)


public inline fun BigInt.getIdx(idx: Int): Int = when {
    idx < 0 -> 0
    idx >= mag.size -> sigNum.signed
    else -> {
        val num = revGet(idx, mag)
        when {
            sigNum.isNonNegative() -> num
            idx <= firstNonZero -> -num
            else -> num.inv()
        }
    }
}

public inline fun BigInt.getNum(idx: Int): Int = when {
    idx < 0 -> 0
    idx >= mag.size -> sigNum.signed
    else -> mag[revIdx(idx, mag)]
}

public inline fun BigInt.adjNum(idx: Int, num: Int): Int = when {
    sigNum.isNonNegative() -> num
    idx <= firstNonZero -> -num
    else -> num.inv()
}

public infix fun BigInt.and(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@and.getIdx(idx) and value.getIdx(idx)
        }
    }
    return valueOf(result)
}

public infix fun BigInt.or(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@or.getIdx(idx) or value.getIdx(idx)
        }
    }
    return valueOf(result)
}

public infix fun BigInt.xor(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@xor.getIdx(idx) xor value.getIdx(idx)
        }
    }
    return valueOf(result)
}

public fun BigInt.not(): BigInt {
    val result = IntArray(size + 1).apply {
        indices.forEach { this[it] = this@not[revIdx(it, this)].inv() }
    }
    return valueOf(result)
}

public fun BigInt.andNot(value: BigInt): BigInt {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it, this)
            this[it] = this@andNot.getIdx(idx) and value.getIdx(idx).inv()
        }
    }
    return valueOf(result)
}

public fun BigInt.clearBit(pos: Int): BigInt {
    check(pos >= 0) { "Negative position" }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intLength(), (pos + 1).floorDiv(Int.SIZE_BITS) + 1))

    result.indices.forEach { revSet(it, result, getIdx(it))  }
    revSet(bigCnt, result,
        revGet(bigCnt, result) and bigMask(pos).inv())

    return valueOf(result)
    //return and(one.shiftLeft(pos).not())
}

public fun BigInt.setBit(pos: Int): BigInt {
    check(pos >= 0) { "Negative position" }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intLength(), bigCnt + 2))

    result.indices.forEach { revSet(it, result, getIdx(it)) }
    revSet(bigCnt, result,
        revGet(bigCnt, result) or bigMask(pos))

    return valueOf(result)
    //return or(one.shiftLeft(pos))
}

public fun BigInt.testBit(pos: Int): Boolean {
    check(pos >= 0) { "Negative position" }
    return getIdx(pos.floorDiv(Int.SIZE_BITS)) and bigMask(pos) != 0
    //return !and(one.shiftLeft(pos)).sigNum.isZero()
}

public fun BigInt.flipBit(pos: Int): BigInt {
    check(pos >= 0) { "Negative position" }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intLength(), bigCnt + 2))

    result.indices.forEach { revSet(it, result, getIdx(it)) }
    revSet(bigCnt, result,
        revGet(bigCnt, result) xor bigMask(pos))

    return valueOf(result)
    //return xor(one.shiftLeft(pos))
}