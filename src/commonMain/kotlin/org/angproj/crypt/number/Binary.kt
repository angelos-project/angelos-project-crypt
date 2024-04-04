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
import org.angproj.crypt.num.AbstractBigInt.Companion.revGet
import org.angproj.crypt.num.AbstractBigInt.Companion.revIdx
import org.angproj.crypt.num.AbstractBigInt.Companion.revSet
import kotlin.math.max

public infix fun AbstractBigInt<*>.and(value: AbstractBigInt<*>): AbstractBigInt<*> {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = this@and.getIdx(idx) and value.getIdx(idx)
        }
    }
    return of(result)
}

public infix fun AbstractBigInt<*>.or(value: AbstractBigInt<*>): AbstractBigInt<*> {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = this@or.getIdx(idx) or value.getIdx(idx)
        }
    }
    return of(result)
}

public infix fun AbstractBigInt<*>.xor(value: AbstractBigInt<*>): AbstractBigInt<*> {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = this@xor.getIdx(idx) xor value.getIdx(idx)
        }
    }
    return of(result)
}

public fun AbstractBigInt<*>.not(): AbstractBigInt<*> {
    val result = IntArray(mag.size + 1).apply {
        indices.forEach { this[it] = this@not.getIdx(revIdx(it)).inv() }
    }
    return of(result)
}

public fun AbstractBigInt<*>.andNot(value: AbstractBigInt<*>): AbstractBigInt<*> {
    val result = maxOfArrays(mag, value.mag).apply {
        indices.forEach {
            val idx = revIdx(it)
            this[it] = this@andNot.getIdx(idx) and value.getIdx(idx).inv()
        }
    }
    return of(result)
}

public fun AbstractBigInt<*>.clearBit(pos: Int): AbstractBigInt<*> {
    check(pos >= 0) { "Negative position" }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intSize(), (pos + 1).floorDiv(Int.SIZE_BITS) + 1))

    result.indices.forEach { result.revSet(it, getIdx(it))  }
    result.revSet(bigCnt, result.revGet(bigCnt) and bigMask(pos).inv())

    return of(result)
}

public fun AbstractBigInt<*>.setBit(pos: Int): AbstractBigInt<*> {
    check(pos >= 0) { "Negative position" }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intSize(), bigCnt + 2))

    result.indices.forEach { result.revSet(it, getIdx(it)) }
    result.revSet(bigCnt, result.revGet(bigCnt) or bigMask(pos))

    return of(result)
}

public fun AbstractBigInt<*>.testBit(pos: Int): Boolean {
    check(pos >= 0) { "Negative position" }
    return getIdx(pos.floorDiv(Int.SIZE_BITS)) and bigMask(pos) != 0
}

public fun AbstractBigInt<*>.flipBit(pos: Int): AbstractBigInt<*> {
    check(pos >= 0) { "Negative position" }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intSize(), bigCnt + 2))

    result.indices.forEach { result.revSet(it, getIdx(it)) }
    result.revSet(bigCnt, result.revGet(bigCnt) xor bigMask(pos))

    return of(result)
}

