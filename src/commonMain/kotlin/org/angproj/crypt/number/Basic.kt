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
import org.angproj.crypt.dsa.AbstractBigInt.Companion.revGetL
import org.angproj.crypt.dsa.AbstractBigInt.Companion.revSet
import org.angproj.crypt.dsa.AbstractBigInt.Companion.revSetL


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
    val result = IntArray(x.mag.size+1)

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
        val negative = big.sigNum.isNegative().let { if(little.sigNum.isNegative()) !it else it }
        val result = of(multiply(big.abs(), little.abs()), BigSigned.POSITIVE)
        return@biggerFirst if(negative) result.negate() else result
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

// https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/math/MutableBigInteger.java#L1481
// divideMagnitude here!

public fun AbstractBigInt<*>.divideAndRemainder(value: AbstractBigInt<*>): Pair<AbstractBigInt<*>, AbstractBigInt<*>> = when {
    value.sigNum.isZero() -> error { "Divisor is zero" }
    value.compareTo(BigInt.one) == BigCompare.EQUAL  -> Pair(this, BigInt.zero)
    sigNum.isZero() -> {println("Dividend is zero"); Pair(BigInt.zero, BigInt.zero)}
    else -> {
        val cmp = compareMagnitude(value)
        when {
            cmp.isLesser() -> {println("Dividend in smaller"); Pair(BigInt.zero, this)}
            cmp.isEqual() -> {println("Dividend is equal"); Pair(BigInt.one, BigInt.zero)}
            else -> {
                println("Do Knuth")
                val result = when {
                    value.mag.size == 1 -> divideOneWord(this.abs(), value.abs())
                    else -> divideMagnitude(this.abs(), value.abs())
                }
                Pair(
                    of(result.first, if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                    of(result.second, this.sigNum)
                )
            }
        }
    }
}

public fun divideOneWord(
    dividend: AbstractBigInt<*>,
    divisor: AbstractBigInt<*>,
): Pair<IntArray, IntArray> {
    val sorLong = divisor.getIdxL(divisor.mag.lastIndex)
    val sorInt = sorLong.toInt()

    if (dividend.mag.size == 1) {
        val dendValue: Long = dividend.getIdxL(dividend.mag.lastIndex)
        val q = (dendValue / sorLong).toInt()
        val r = (dendValue - q * sorLong).toInt()
        return Pair(intArrayOf(q), intArrayOf(r))
    }
    val quotient =  IntArray(dividend.mag.size)

    val shift: Int = sorInt.countLeadingZeroBits()
    var rem: Int = dividend.getUnreversedIdx(0)
    var remLong = rem.toLong() and 0xffffffffL
    if (remLong < sorLong) {
        quotient[0] = 0
    } else {
        quotient[0] = (remLong / sorLong).toInt()
        rem = (remLong - quotient[0] * sorLong).toInt()
        remLong = rem.toLong() and 0xffffffffL
    }

    (dividend.mag.lastIndex downTo 1).forEach { idx ->
        val dendEst = remLong shl Int.SIZE_BITS or dividend.getIdxL(idx - 1)
        var q: Int
        if (dendEst >= 0) {
            q = (dendEst / sorLong).toInt()
            rem = (dendEst - q * sorLong).toInt()
        } else {
            val tmp = divWord(dendEst, sorInt)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr Int.SIZE_BITS).toInt()
        }
        quotient.revSet(idx - 1, q)
        remLong = rem.toLong() and 0xffffffffL
    }

    return when {
        shift > 0 -> Pair(quotient, intArrayOf(rem % sorInt))
        else -> Pair(quotient, intArrayOf(rem))
    }
}

public fun AbstractBigInt<*>.divideMagnitude(
    dividend: AbstractBigInt<*>,
    divisor: AbstractBigInt<*>
): Pair<IntArray, IntArray> {
    val shift: Int = divisor.getIdx(divisor.mag.lastIndex).countLeadingZeroBits()
    val sorLen = divisor.mag.size
    val sor = when(shift > 0) {
        true -> divisor.shiftLeft(shift)
        else -> divisor
    }.toComplementedIntArray()
    val rem = when {
        shift <= 0 -> {
            println("HELLO3")
            intArrayOf(0) + dividend.toComplementedIntArray()
        }
        dividend.getIdx(dividend.mag.lastIndex).countLeadingZeroBits() >= shift -> {
            println("HELLO1")
            val remarr = dividend.shiftLeft(shift).toComplementedIntArray()
            //require(remarr.size == dividend.mag.size + 1) // Needs extra leading zero?
            remarr
        }
        else -> {
            println("HELLO2")
            val remarr = IntArray(dividend.mag.size + 2)
            //var rFrom: Int = 0 // offset
            var c = 0
            val n2 = 32 - shift
            /*var i = 1
            while (i < dividend.mag.size + 1) {
                val b = c
                c = dividend.getUnreversedIdx(rFrom)
                remarr[i] = b shl shift or (c ushr n2)
                i++
                rFrom++
            }
            remarr[dividend.mag.size + 1] = c shl shift*/
            (1 until remarr.lastIndex).forEach { idx ->
                val b = c
                c = dividend.getUnreversedIdx(idx - 1)
                remarr[idx] = b shl shift or (c ushr n2)
            }
            remarr[remarr.lastIndex] = c shl shift
            remarr
        }
    }

    val remLen = rem.size
    val limit = remLen - sorLen + 1
    val quot = IntArray(limit)
    rem[0] = 0
    // rem.intLen++;

    val sorHighLong = divisor.getUnreversedIdxL(0)
    val sorHigh = sorHighLong.toInt()
    val sorLow = divisor.getUnreversedIdx(1)

    for(idx in 0 until limit-1) {
        var qhat: Int
        var qrem: Int
        var skipCorrection = false
        val nh: Int = rem[idx]
        val nh2 = nh + -0x80000000
        val nm: Int = rem[idx + 1]

        if (nh == sorHigh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = nh.toLong() shl Int.SIZE_BITS or (nm.toLong() and 0xffffffffL)
            if (nChunk >= 0) {
                qhat = (nChunk / sorHighLong).toInt()
                qrem = (nChunk - qhat * sorHighLong).toInt()
            } else {
                val tmp = divWord(nChunk, sorHigh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr Int.SIZE_BITS).toInt()
            }
        }

        if (qhat == 0) continue

        if (!skipCorrection) {
            val nl: Long = rem[idx + 2].toLong() and 0xffffffffL
            var rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
            var estProduct: Long = (sorLow.toLong() and 0xffffffffL) * (qhat.toLong() and 0xffffffffL)
            if (unsignedLongCompare(estProduct, rs)) {
                qhat--
                qrem = ((qrem.toLong() and 0xffffffffL) + sorHighLong).toInt()
                if (qrem.toLong() and 0xffffffffL >= sorHighLong) {
                    estProduct -= sorLow.toLong() and 0xffffffffL
                    rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
                    if (unsignedLongCompare(estProduct, rs)) qhat--
                }
            }
        }

        rem[idx] = 0
        println("IDX ${idx}")
        val borrow = mulsub(rem, sor, qhat, sorLen, idx)

        if (borrow + -0x80000000 > nh2) {
            divadd(sor, rem, idx + 1)
            qhat--
        }

        quot[idx] = qhat
    }

    var qhat: Int
    var qrem: Int
    var skipCorrection = false
    val nh: Int = rem[limit - 1]
    val nh2 = nh + -0x80000000
    val nm: Int = rem[limit]

    if (nh == sorHigh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.toLong() and 0xffffffffL
        if (nChunk >= 0) {
            qhat = (nChunk / sorHighLong).toInt()
            qrem = (nChunk - qhat * sorHighLong).toInt()
        } else {
            val tmp = divWord(nChunk, sorHigh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr Int.SIZE_BITS).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) {
            val nl: Long = rem[limit + 1].toLong() and 0xffffffffL
            var rs: Long = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
            var estProduct: Long = (sorLow.toLong() and 0xffffffffL) * (qhat.toLong() and 0xffffffffL)
            if (unsignedLongCompare(estProduct, rs)) {
                qhat--
                qrem = ((qrem.toLong() and 0xffffffffL) + sorHighLong).toInt()
                if (qrem.toLong() and 0xffffffffL >= sorHighLong) {
                    estProduct -= sorLow.toLong() and 0xffffffffL
                    rs = qrem.toLong() and 0xffffffffL shl Int.SIZE_BITS or nl
                    if (unsignedLongCompare(estProduct, rs)) qhat--
                }
            }
        }

        rem[limit - 1] = 0
        println("LIMIT ${limit - 1}")
        val borrow = mulsub(rem, sor, qhat, sorLen, limit - 1)
        //borrow = if (needRemainder) mulsub(rem.value, divisor, qhat, dlen, limit - 1 + rem.offset)
        //else mulsubBorrow(rem.value, divisor, qhat, dlen, limit - 1 + rem.offset)

        if (borrow + -0x80000000 > nh2) {
            divadd(sor, rem, limit - 1)
            //if (needRemainder) divadd(divisor, rem.value, limit - 1 + 1 + rem.offset)
            qhat--
        }

        quot[limit - 1] = qhat
    }

    return Pair(quot, if (shift > 0) of(rem).shiftRight(shift).toComplementedIntArray() else rem)
}

private fun unsignedLongCompare(one: Long, two: Long): Boolean {
    return one + Long.MIN_VALUE > two + Long.MIN_VALUE
}

private fun mulsub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    println("Q ${q.size}")
    println("A ${a.size}")
    var offset1 = offset
    val xLong = x.toLong() and 0xffffffffL
    var carry: Long = 0
    offset1 += len
    println("O $offset1, $len")
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

private fun divWord(n: Long, d: Int): Long {
    val dLong = d.toLong() and 0xffffffffL
    var r: Long
    var q: Long
    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return r shl Int.SIZE_BITS or (q and 0xffffffffL)
    }

    // Approximate the quotient and remainder
    q = (n ushr 1) / (dLong ushr 1)
    r = n - q * dLong

    // Correct the approximation
    while (r < 0) {
        r += dLong
        q--
    }
    while (r >= dLong) {
        r -= dLong
        q++
    }
    // n - q*dlong == r && 0 <= r <dLong, hence we're done.
    return r shl Int.SIZE_BITS or (q and 0xffffffffL)
}

private fun copyAndShift(src: IntArray, srcFrom: Int, srcLen: Int, dst: IntArray, dstFrom: Int, shift: Int) {
    var srcFrom1 = srcFrom
    val n2 = Int.SIZE_BITS - shift
    var c = src[srcFrom1]
    for (i in 0 until srcLen - 1) {
        val b = c
        c = src[++srcFrom1]
        dst[dstFrom + i] = b shl shift or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}

private fun primitiveRightShift(value: IntArray, n: Int) {
    val n2 = Int.SIZE_BITS - n
    var i: Int = value.lastIndex
    var c = value[i]
    while (i > 0) {
        val b = c
        c = value[i - 1]
        value[i] = c shl n2 or (b ushr n)
        i--
    }
    value[0] = value[0] ushr n
}