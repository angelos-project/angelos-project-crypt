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

import org.angproj.crypt.dsa.MutableBigInt


internal fun MutableBigInt.Companion.divWord(n: Long, d: Int): Long {
    val dLong = d.toLong() and 0xffffffffL
    var r: Long
    var q: Long
    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return r shl Int.SIZE_BITS or (q and 0xffffffffL)
    }

    q = (n ushr 1) / (dLong ushr 1)
    r = n - q * dLong

    while (r < 0) {
        r += dLong
        q--
    }
    while (r >= dLong) {
        r -= dLong
        q++
    }
    return r shl Int.SIZE_BITS or (q and 0xffffffffL)
}

internal fun MutableBigInt.Companion.rightShift(value: IntArray, n: Int): IntArray {
    if (value.size == 0) return value
    val nInts = n ushr 5
    val nBits = n and 0x1F
    val value2 = value.copyOf(value.size - nInts)
    if (nBits == 0) return value2
    val bitsInHighWord = Int.SIZE_BITS - value2[0].countLeadingZeroBits()
    return if (nBits >= bitsInHighWord) {
        primitiveLeftShift(value2, Int.SIZE_BITS - nBits).copyOf(value.lastIndex)
    } else {
        primitiveRightShift(value2, nBits)
    }
}

internal fun MutableBigInt.Companion.primitiveRightShift(value: IntArray, n: Int): IntArray {
    val n2 = Int.SIZE_BITS - n
    var i = value.lastIndex
    var c = value[i]
    while (i > 0) {
        val b = c
        c = value[i - 1]
        value[i] = c shl n2 or (b ushr n)
        i--
    }
    value[0] = value[0] ushr n
    return value
}

internal fun MutableBigInt.Companion.primitiveLeftShift(value: IntArray, n: Int): IntArray {
    val n2 = Int.SIZE_BITS - n
    var i = 0
    var c = value[i]
    val m = i + value.lastIndex
    while (i < m) {
        val b = c
        c = value[i + 1]
        value[i] = b shl n or (c ushr n2)
        i++
    }
    value[value.lastIndex] = value[value.lastIndex] shl n
    return value
}

internal fun MutableBigInt.Companion.copyAndShift(src: IntArray, srcFrom_: Int, srcLen: Int, dst: IntArray, dstFrom: Int, shift: Int) {
    var srcFrom = srcFrom_
    val n2 = Int.SIZE_BITS - shift
    var c = src[srcFrom]
    for (i in 0 until srcLen - 1) {
        val b = c
        c = src[++srcFrom]
        dst[dstFrom + i] = b shl shift or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}

internal fun MutableBigInt.Companion.mulsub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    val xLong = x.toLong() and 0xffffffffL
    var carry: Long = 0
    var offset_: Int = offset + len
    (len - 1 downTo 0).forEach { idx ->
        val product: Long = (a[idx].toLong() and 0xffffffffL) * xLong + carry
        val difference = q[offset_] - product
        q[offset_--] = difference.toInt()
        carry = (product ushr Int.SIZE_BITS) + (
                if ((difference and 0xffffffffL) > (product.inv() and 0xffffffffL)) 1 else 0)
    }
    return carry.toInt()
}

internal fun MutableBigInt.Companion.divadd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = (a[idx].toLong() and 0xffffffffL) + (
                result[idx + offset].toLong() and 0xffffffffL) + carry
        result[idx + offset] = sum.toInt()
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}