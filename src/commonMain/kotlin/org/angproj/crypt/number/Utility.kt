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
import org.angproj.aux.num.AbstractBigInt.Companion.getL
import org.angproj.aux.num.BigInt
import org.angproj.aux.num.MutableBigInt

internal fun MutableBigInt.Companion.divWord(n: Long, d: Int): Long {
    val dLong = d.getL()
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
    var c = value[value.lastIndex]
     (value.lastIndex downTo 1).forEach { idx ->
        val b = c
        c = value[idx - 1]
        value[idx] = c shl n2 or (b ushr n)
    }
    value[0] = value[0] ushr n
    return value
}

internal fun MutableBigInt.Companion.primitiveLeftShift(value: IntArray, n: Int): IntArray {
    val n2 = Int.SIZE_BITS - n
    var c = value[0]
    (0 until value.lastIndex).forEach { idx ->
        val b = c
        c = value[idx + 1]
        value[idx] = b shl n or (c ushr n2)
    }
    value[value.lastIndex] = value[value.lastIndex] shl n
    return value
}

internal fun MutableBigInt.Companion.copyAndShift(
    src: IntArray, srcFrom_: Int, srcLen: Int,
    dst: IntArray, dstFrom: Int, shift: Int
) {
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

internal fun MutableBigInt.Companion.mulSub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var carry: Long = 0
    (len - 1 downTo 0).forEach { idx ->
        val prod: Long = a[idx].getL() * x.getL() + carry
        val diff = q[offset + idx + 1] - prod
        q[offset + idx + 1] = diff.toInt()
        carry = (prod ushr Int.SIZE_BITS) + (
                if ((diff and 0xffffffffL) > (prod.inv() and 0xffffffffL)) 1 else 0)
    }
    return carry.toInt()
}

internal fun MutableBigInt.Companion.divAdd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = a[idx].getL() + result[idx + offset].getL() + carry
        result[idx + offset] = sum.toInt()
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}

// https://developer.classpath.org/doc/java/math/BigInteger-source.html
internal fun MutableBigInt.Companion.euclidInv(
    a: AbstractBigInt<*>,
    b: AbstractBigInt<*>,
    prevDiv: AbstractBigInt<*>,
    xy_: Pair<AbstractBigInt<*>, AbstractBigInt<*>>
): Pair<AbstractBigInt<*>, AbstractBigInt<*>> = when {
    b.compareTo(BigInt.zero).isEqual() -> error("Not invertible")
    b.compareTo(BigInt.one).isEqual() -> Pair(
        prevDiv.negate(),
        BigInt.one
    )
    else -> {
        val qr = a.divideAndRemainder(b)
        val xy = euclidInv(b, qr.second, qr.first, xy_)
        Pair(
            xy.second.subtract(xy.first.times(prevDiv)),
            xy.first
        )
    }
}