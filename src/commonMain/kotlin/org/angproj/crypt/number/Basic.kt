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

import org.angproj.crypt.dsa.*
import org.angproj.crypt.dsa.AbstractBigInt.Companion.bitSizeForInt
import org.angproj.crypt.dsa.AbstractBigInt.Companion.revGetL
import org.angproj.crypt.dsa.AbstractBigInt.Companion.revSet
import org.angproj.crypt.dsa.AbstractBigInt.Companion.revSetL

//import org.angproj.crypt.dsa.MutableBigInteger





public operator fun AbstractBigInt<*>.unaryMinus(): AbstractBigInt<*> = negate()

public operator fun AbstractBigInt<*>.plus(other: AbstractBigInt<*>): AbstractBigInt<*> = add(other)

public fun AbstractBigInt<*>.add(value: AbstractBigInt<*>): AbstractBigInt<*> = when {
    sigNum.isZero() -> value
    value.sigNum.isZero() -> this
    else -> biggerFirst(this, value) { big, little ->
        return@biggerFirst of(add(big, little))
    }
}

internal fun add(x: AbstractBigInt<*>, y: AbstractBigInt<*>): IntArray {
    val result = IntArray(x.mag.size + 1)

    var carry: Long = 0
    result.indices.forEach { idx ->
        carry += x.getIdxL(idx) + y.getIdxL(idx)
        result.revSetL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return result
}


public operator fun AbstractBigInt<*>.minus(other: AbstractBigInt<*>): AbstractBigInt<*> = subtract(other)

public fun AbstractBigInt<*>.subtract(value: AbstractBigInt<*>): AbstractBigInt<*> = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value.negate()
    else -> of(subtract(this, value))
}

internal fun subtract(x: AbstractBigInt<*>, y: AbstractBigInt<*>): IntArray {
    val result = maxOfArrays(x.mag, y.mag)
    var carry = 0
    result.indices.forEach { idr ->
        var yNum = y.getIdx(idr) + carry
        val xNum = x.getIdx(idr)
        carry = if (yNum xor -0x80000000 < carry xor -0x80000000) 1 else 0
        yNum = xNum - yNum
        carry += if (yNum xor -0x80000000 > xNum xor -0x80000000) 1 else 0
        result.revSet(idr, yNum)
    }
    return result
}

public operator fun AbstractBigInt<*>.times(other: AbstractBigInt<*>): AbstractBigInt<*> = multiply(other)

internal fun AbstractBigInt<*>.multiply(value: AbstractBigInt<*>): AbstractBigInt<*> = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value) { big, little ->
        val negative = big.sigNum.isNegative().let { if (little.sigNum.isNegative()) !it else it }
        val result = of(multiply(big.abs(), little.abs()), BigSigned.POSITIVE)
        return@biggerFirst if (negative) result.negate() else result
    }
}

internal fun multiply(x: AbstractBigInt<*>, y: AbstractBigInt<*>): IntArray {
    val result = IntArray(x.mag.size + y.mag.size)
    result.revSet(x.mag.size, multiply(result, x, y.getIdx(0)))
    (1 until y.mag.size).forEach { idy ->
        val num = y.getIdxL(idy)
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += x.getIdxL(idx) * num + result.revGetL(idy + idx)
            result.revSetL(idy + idx, carry)
            carry = carry ushr Int.SIZE_BITS
        }
        result.revSetL(idy + x.mag.size, carry)
    }
    return result
}

internal fun multiply(result: IntArray, x: AbstractBigInt<*>, y: Int): Int {
    val first = y.toLong() and 0xffffffffL
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += x.getIdxL(idx) * first
        result.revSetL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return carry.toInt()
}


/*public fun divide(value: BigInt): BigInt {
    check(value.sigNum.isNonZero()) { "Divisor is zero" }
    val quot = BigInt()
    BigInt.divide(this, value, quot, null, BigInt.TRUNCATE)
    return quot.canonicalize()
}

public fun remainder(value: BigInt): BigInt {
    check(value.sigNum.isNonZero()) { "Divisor is zero" }
    val rem = BigInt()
    BigInt.divide(this, value, null, rem, BigInt.TRUNCATE)
    return rem.canonicalize()
}*/

public fun AbstractBigInt<*>.divideAndRemainder(value: AbstractBigInt<*>): Pair<AbstractBigInt<*>, AbstractBigInt<*>> =
    when {
        value.sigNum.isZero() -> error { "Divisor is zero" }
        value.compareTo(BigInt.one) == BigCompare.EQUAL -> Pair(this, BigInt.zero)
        sigNum.isZero() -> Pair(BigInt.zero, BigInt.zero)

        else -> {
            val cmp = compareMagnitude(value)
            when {
                cmp.isLesser() -> Pair(BigInt.zero, this)
                cmp.isEqual() ->  Pair(BigInt.one, BigInt.zero)
                else -> {
                    val result = when {
                        value.mag.size == 1 -> divideOneWord(this.abs(), value.abs())
                        else -> divideMagnitude(this.abs(), value.abs())
                    }
                    Pair(
                        of(result.first.toComplementedIntArray(), if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                        of(result.second.toComplementedIntArray(), this.sigNum)
                    )
                }
            }
        }
    }

public fun AbstractBigInt<*>.divideOneWord(
    dividend: AbstractBigInt<*>,
    divisor: AbstractBigInt<*>,
): Pair<MutableBigInt, MutableBigInt> {
    val sorLong = divisor.getIdxL(divisor.mag.lastIndex)
    val sorInt = sorLong.toInt()

    if (dividend.mag.size == 1) {
        val dendValue: Long = dividend.getIdxL(dividend.mag.lastIndex)
        val q = (dendValue / sorLong).toInt()
        val r = (dendValue - q * sorLong).toInt()
        return Pair(mutableBigIntOf(q.toLong()), mutableBigIntOf(r.toLong()))
    }
    val quotient = emptyMutableBigIntOf(IntArray(dividend.mag.size))

    val shift: Int = sorInt.countLeadingZeroBits()
    var rem: Int = dividend.getUnreversedIdx(0)
    var remLong = rem.toLong() and 0xffffffffL
    if (remLong < sorLong) {
        quotient.setUnreversedIdx(0, 0)
    } else {
        quotient.setUnreversedIdxL(0, remLong / sorLong)
        rem = (remLong - quotient.getUnreversedIdx(0) * sorLong).toInt()
        remLong = rem.toLong() and 0xffffffffL
    }

    (dividend.mag.lastIndex downTo 1).forEach { idx ->
        val dendEst = remLong shl Int.SIZE_BITS or dividend.getIdxL(idx - 1)
        var q: Int
        if (dendEst >= 0) {
            q = (dendEst / sorLong).toInt()
            rem = (dendEst - q * sorLong).toInt()
        } else {
            val tmp = quotient.divWord(dendEst, sorInt)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr Int.SIZE_BITS).toInt()
        }
        quotient.setIdx(idx - 1, q)
        remLong = rem.toLong() and 0xffffffffL
    }

    return when {
        shift > 0 -> Pair(quotient, mutableBigIntOf((rem % sorInt).toLong()))
        else -> Pair(quotient, mutableBigIntOf(rem.toLong()))
    }
}

internal fun MutableBigInt.divWord(n: Long, d: Int): Long {
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

public fun AbstractBigInt<*>.divideMagnitude(
    dividend: AbstractBigInt<*>,
    divisor: AbstractBigInt<*>,
): Pair<MutableBigInt, MutableBigInt>  {
    val shift = divisor.getUnreversedIdx(0).countLeadingZeroBits()

    val dlen = divisor.range.count()
    val sorarr: IntArray
    val remainder: MutableBigInt // Remainder starts as dividend with space for a leading zero
    if (shift > 0) {
        sorarr = IntArray(dlen)
        copyAndShift(divisor, sorarr, 0, shift)
        if (dividend.getUnreversedIdx(0).countLeadingZeroBits() >= shift) {
            println("3")
            val remarr = IntArray(dividend.mag.size)
            copyAndShift(dividend, remarr, 0, shift)
            remainder = emptyMutableBigIntOf(intArrayOf(0) + remarr)
            //remainder.range = 1..1 + dividend.range.count()
        } else {
            println("1")
            val remarr = IntArray(dividend.range.count() + 1)
            var remOffset = 0
            var c = 0
            val n2 = Int.SIZE_BITS - shift
            var i = 1
            while (i < dividend.mag.lastIndex + 1) {
                val b = c
                c = dividend.getUnreversedIdx(remOffset)
                remarr[i] = b shl shift or (c ushr n2)
                i++
                remOffset++
            }
            remarr[dividend.mag.lastIndex + 1] = c shl shift
            remainder = emptyMutableBigIntOf(intArrayOf(0) + remarr)
            //remainder.range = 1..1 + dividend.range.count() + 1
        }
    } else {
        println("2")
        sorarr = divisor.mag.slice(divisor.range).toIntArray() // Maybe use toComplementedArray
        val remarr = dividend.toComplementedIntArray()
        remainder = emptyMutableBigIntOf(intArrayOf(0) + remarr)
        //dividend.mag.indices.forEach { remainder.setUnreversedIdx(it + 1, dividend.getUnreversedIdx(it)) }
        //remainder.range = 1..1 + dividend.range.count()
    }
    val nlen = remainder.mag.size - 1 //remainder.range.count()

    val limit = nlen - dlen + 1
    val quotient = emptyMutableBigIntOf(IntArray(limit))
    quotient.range = 0..0 + limit
    val quotarr = quotient.mag

    remainder.setUnreversedIdx(0, 0)
    //remainder.range = 0..0 + remainder.range.last + 1
    val dh = sorarr[0]
    val dhLong = dh.toLong() and 0xffffffffL
    val dl = sorarr[1]

    for (j in 0 until limit - 1) {
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        val nh = remainder.getUnreversedIdx(j + remainder.range.first)
        val nh2 = nh + -0x80000000
        val nm = remainder.getUnreversedIdx(j + 1 + remainder.range.first)
        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = nh.toLong() shl Int.SIZE_BITS or (nm.toLong() and 0xffffffffL)
            if (nChunk >= 0) {
                qhat = (nChunk / dhLong).toInt()
                qrem = (nChunk - qhat * dhLong).toInt()
            } else {
                val tmp = quotient.divWord(nChunk, dh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr Int.SIZE_BITS).toInt()
            }
        }
        if (qhat == 0) continue
        if (!skipCorrection) {
            val nl = remainder.getUnreversedIdxL(j + 2 + remainder.range.first)
            var rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
            var estProduct = (dl.toLong() and 0xffffffffL) * (qhat.toLong() and 0xffffffffL)
            if (estProduct + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = ((qrem.toLong() and 0xffffffffL) + dhLong).toInt()
                if (qrem.toLong() and 0xffffffffL >= dhLong) {
                    estProduct -= dl.toLong() and 0xffffffffL
                    rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
                    if (estProduct + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remainder.setUnreversedIdx(j + remainder.range.first, 0)
        val borrow = mulsub2(remainder, sorarr, qhat, dlen, j + remainder.range.first)

        if (borrow + -0x80000000 > nh2) {
            divadd2(sorarr, remainder, j + 1 + remainder.range.first)
            qhat--
        }

        quotient.setUnreversedIdx(j, qhat)
    }

    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    val nh = remainder.getUnreversedIdx(limit - 1 + remainder.range.first)
    val nh2 = nh + -0x80000000
    val nm = remainder.getUnreversedIdx(limit + remainder.range.first)
    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = nh.toLong() shl Int.SIZE_BITS or (nm.toLong() and 0xffffffffL)
        if (nChunk >= 0) {
            qhat = (nChunk / dhLong).toInt()
            qrem = (nChunk - qhat * dhLong).toInt()
        } else {
            val tmp = quotient.divWord(nChunk, dh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr Int.SIZE_BITS).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) {
            val nl = remainder.getUnreversedIdxL(limit + 1 + remainder.range.first)
            var rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
            var estProduct = (dl.toLong() and 0xffffffffL) * (qhat.toLong() and 0xffffffffL)
            if (estProduct + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = ((qrem.toLong() and 0xffffffffL) + dhLong).toInt()
                if (qrem.toLong() and 0xffffffffL >= dhLong) {
                    estProduct -= dl.toLong() and 0xffffffffL
                    rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
                    if (estProduct + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        val borrow: Int
        remainder.setUnreversedIdx(limit - 1 + remainder.range.first, 0)
        borrow = mulsub2(remainder, sorarr, qhat, dlen, limit - 1 + remainder.range.first)

        if (borrow + -0x80000000 > nh2) {
            divadd2(sorarr, remainder, limit - 1 + 1 + remainder.range.first)
            qhat--
        }

        //quotarr[limit - 1] = qhat
        quotient.setUnreversedIdx(limit - 1, qhat)
    }

    if (shift > 0) rightShift(remainder, shift)
    return Pair(quotient, remainder)
}

private fun mulsub2(q: MutableBigInt, a: IntArray, x: Int, len: Int, offset: Int): Int {
    val xLong = x.toLong() and 0xffffffffL
    var carry: Long = 0
    var offset_: Int = offset + len
    (len - 1 downTo 0).forEach { idx ->
        val product: Long = (a[idx].toLong() and 0xffffffffL) * xLong + carry
        val difference = q.getUnreversedIdx(offset_) - product
        q.setUnreversedIdxL(offset_--, difference)
        carry = (product ushr Int.SIZE_BITS) + (
                if ((difference and 0xffffffffL) > (product.inv() and 0xffffffffL)) 1 else 0)
    }
    return carry.toInt()
}

private fun divadd2(a: IntArray, r: MutableBigInt, offset: Int): Int {
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = (a[idx].toLong() and 0xffffffffL) + r.getUnreversedIdxL(idx + offset) + carry
        r.setUnreversedIdxL(idx + offset, sum)
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}

/*internal fun MutableBigInt.rightShift(n: Int) {
    if (range.last == 0) return
    val nInts = n ushr 5
    val nBits = n and 0x1F
    range = range.first..range.last - nInts
    if (nBits == 0) return
    val bitsInHighWord = Int.SIZE_BITS - mag[range.first].countLeadingZeroBits()
    if (nBits >= bitsInHighWord) {
        primitiveLeftShift(this, Int.SIZE_BITS - nBits)
        range = range.first until range.last
    } else {
        primitiveRightShift(this, nBits)
    }
}*/

/*private fun MutableBigInt.primitiveRightShift(n: Int) {
    val value = mag
    val n2 = Int.SIZE_BITS - n
    var i = range.first + range.count() - 1
    var c = value[i]
    while (i > range.first) {
        val b = c
        c = value[i - 1]
        value[i] = c shl n2 or (b ushr n)
        i--
    }
    value[range.first] = value[range.first] ushr n
}

private fun MutableBigInt.primitiveLeftShift(n: Int) {
    val value = mag
    val n2 = Int.SIZE_BITS - n
    var i = range.first
    var c = value[i]
    val m = i + range.count() - 1
    while (i < m) {
        val b = c
        c = value[i + 1]
        value[i] = b shl n or (c ushr n2)
        i++
    }
    value[range.first + range.count() - 1] = value[range.first + range.count() - 1] shl n
}*/

private fun rightShift(value: MutableBigInt, n: Int) {
    if (value.mag.isEmpty()) return
    val nInts = n ushr 5
    val nBits = n and 0x1F
    var size = value.mag.size - nInts
    if (nBits == 0) return
    val bitsInHighWord: Int = bitSizeForInt(value.getUnreversedIdx(0))
    if (nBits >= bitsInHighWord) {
        primitiveLeftShift(value, 32 - nBits)
        value.mag.removeAt(value.mag.lastIndex)
        value.range = value.range.first until value.range.last
    } else {
        primitiveRightShift(value, nBits)
    }
}

private fun primitiveRightShift(value: MutableBigInt, n: Int) {
    val n2: Int = Int.SIZE_BITS - n
    var c: Int = value.getUnreversedIdx(value.mag.lastIndex)
    (value.mag.lastIndex downTo 1).forEach { idx ->
        val b: Int = c
        c = value.getUnreversedIdx(idx - 1)
        value.setUnreversedIdx(idx, (c shl n2) or (b ushr n))
    }
    value.setUnreversedIdx(0, value.getUnreversedIdx(0) ushr n)
}

private fun primitiveLeftShift(value: MutableBigInt, n: Int) {
    val n2 = 32 - n
    var c = value.getUnreversedIdx(0)
    (0 until value.mag.lastIndex).forEach { idx ->
        val b = c
        c = value.getUnreversedIdx(idx + 1)
        value.setUnreversedIdx(idx, b shl n or (c ushr n2))
    }
    value.setUnreversedIdx(value.mag.lastIndex, value.getUnreversedIdx(value.mag.lastIndex) shl n)
}

private fun unsignedLongCompare0(one: Long, two: Long): Boolean {
    return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE)
}

private fun mulsub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
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

private fun mulsub0(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var offset1 = offset
    val xLong = x.toLong() and 0xffffffffL
    var carry: Long = 0
    offset1 += len
    for (j in len - 1 downTo 0) {
        val product = (a[j].toLong() and 0xffffffffL) * xLong + carry
        val difference = q[offset1] - product
        q[offset1--] = difference.toInt()
        carry = ((product ushr Int.SIZE_BITS)
                + if (difference and 0xffffffffL > product.inv() and 0xffffffffL) 1 else 0)
    }
    return carry.toInt()
}

private fun divadd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    (a.lastIndex downTo 0).forEach { idx ->
        val sum: Long = (a[idx].toLong() and 0xffffffffL) + (
                result[idx + offset].toLong() and 0xffffffffL) + carry
        result[idx + offset] = sum.toInt()
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}

private fun divadd0(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    for (j in a.indices.reversed()) {
        val sum = (a[j].toLong() and 0xffffffffL) + (result[j + offset].toLong() and 0xffffffffL) + carry
        result[j + offset] = sum.toInt()
        carry = sum ushr Int.SIZE_BITS
    }
    return carry.toInt()
}

private fun mulsubBorrow(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var offset0 = offset
    val xLong = x.toLong() and 0xffffffffL
    var carry: Long = 0
    offset0 += len
    for (j in len - 1 downTo 0) {
        val product = (a[j].toLong() and 0xffffffffL) * xLong + carry
        val difference = q[offset0--] - product
        carry = ((product ushr Int.SIZE_BITS)
                + if (difference and 0xffffffffL > product.inv() and 0xffffffffL) 1 else 0)
    }
    return carry.toInt()
}

private fun copyAndShift(src: AbstractBigInt<*>, dst: IntArray, destOffset: Int, shift: Int) {
    val srcOffset = src.range.first
    val srcSize = src.range.count()
    val n2 = Int.SIZE_BITS - shift
    var c = src.getUnreversedIdx(srcOffset)
    (0 until srcSize - 1).forEach { idx ->
        val b = c
        c = src.getUnreversedIdx(srcOffset + idx)
        dst[destOffset + idx] = b shl shift or (c ushr n2)
    }
    dst[destOffset + srcSize - 1] = c shl shift
}

private fun copyAndShift1(src: AbstractBigInt<*>, dst: IntArray, shift: Int) {
    val n2 = Int.SIZE_BITS - shift
    var c = src.getUnreversedIdx(0) // <---- ARRAY HERE
    (0 until src.mag.lastIndex).forEach { idx ->
        val b = c
        c = src.getUnreversedIdx(idx + 1) // <---- ARRAY HERE
        dst[idx] = (b shl shift) or (c ushr n2)
    }
    dst[src.mag.lastIndex] = c shl shift
}