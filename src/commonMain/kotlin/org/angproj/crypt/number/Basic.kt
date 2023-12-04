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
            val tmp = MutableBigInt.divWord(dendEst, sorInt)
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

public fun AbstractBigInt<*>.divideMagnitude(
    dividend: AbstractBigInt<*>,
    divisor: AbstractBigInt<*>,
): Pair<MutableBigInt, MutableBigInt> {
    val shift = divisor.mag[0].countLeadingZeroBits()

    val dlen = divisor.mag.size
    val sorarr: IntArray = when {
        shift > 0 -> IntArray(divisor.mag.size).also {
            MutableBigInt.copyAndShift(divisor.mag.toIntArray(), 0, divisor.mag.size, it, 0, shift) }
        else -> divisor.mag.toIntArray().copyOfRange(0, divisor.mag.size)
    }
    val remainder: IntArray = when {
        shift <= 0 ->  {
            val remarr = IntArray(dividend.mag.size + 1)
            dividend.mag.toIntArray().copyInto(remarr, 1, 0, dividend.mag.size)
            remarr
        }
        dividend.mag[0].countLeadingZeroBits() >= shift -> {
            val remarr = IntArray(dividend.mag.size + 1)
            MutableBigInt.copyAndShift(dividend.mag.toIntArray(), 0, remarr.lastIndex, remarr, 1, shift)
            remarr
        }
        else -> {
            val remarr = IntArray(dividend.mag.size + 2)
            var rFrom = 0
            var c = 0
            val n2 = Int.SIZE_BITS - shift
            var i = 1
            while (i < dividend.mag.size + 1) {
                val b = c
                c = dividend.mag[rFrom]
                remarr[i] = b shl shift or (c ushr n2)
                i++
                rFrom++
            }
            remarr[dividend.mag.size + 1] = c shl shift
            remarr
        }
    }

    val nlen = remainder.lastIndex

    val limit = nlen - dlen + 1
    //val quotient = MutableBigInteger()
    //quotient.value =
    val quotarr = IntArray(limit) //quotient.value

    val dh = sorarr[0]
    val dhLong = dh.toLong() and 0xffffffffL
    val dl = sorarr[1]

    for (j in 0 until limit - 1) {
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        val nh = remainder[j]
        val nh2 = nh + -0x80000000
        val nm = remainder[j + 1]
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
                val tmp = MutableBigInt.divWord(nChunk, dh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr Int.SIZE_BITS).toInt()
            }
        }
        if (qhat == 0) continue
        if (!skipCorrection) {
            val nl = remainder[j + 2].toLong() and 0xffffffffL
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

        remainder[j] = 0
        val borrow = MutableBigInt.mulsub(remainder, sorarr, qhat, dlen, j)

        if (borrow + -0x80000000 > nh2) {
            MutableBigInt.divadd(sorarr, remainder, j + 1)
            qhat--
        }

        quotarr[j] = qhat
    }

    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    val nh = remainder[limit - 1]
    val nh2 = nh + -0x80000000
    val nm = remainder[limit]
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
            val tmp = MutableBigInt.divWord(nChunk, dh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr Int.SIZE_BITS).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) {
            val nl = remainder[limit + 1].toLong() and 0xffffffffL
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
        remainder[limit - 1] = 0
        borrow = MutableBigInt.mulsub(remainder, sorarr, qhat, dlen, limit - 1)

        if (borrow + -0x80000000 > nh2) {
            MutableBigInt.divadd(sorarr, remainder, limit)
            qhat--
        }

        quotarr[limit - 1] = qhat
    }

    return Pair(
        emptyMutableBigIntOf(quotarr),
        emptyMutableBigIntOf(if (shift > 0) MutableBigInt.rightShift(remainder, shift) else remainder)
    )
}
