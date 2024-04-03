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

import org.angproj.aux.util.mutableBigIntOf
import org.angproj.aux.num.*
import org.angproj.aux.num.AbstractBigInt.Companion.getL

public fun AbstractBigInt<*>.isOdd(): Boolean = when {
    sigNum.isZero() -> false
    else -> mag.last() and 1 == 1
}

public fun AbstractBigInt<*>.isEven(): Boolean = when {
    sigNum.isZero() -> true
    else -> mag.last() and 1 == 0
}

public operator fun AbstractBigInt<*>.unaryMinus(): AbstractBigInt<*> = negate()

public operator fun AbstractBigInt<*>.plus(other: AbstractBigInt<*>): AbstractBigInt<*> = add(other)

public fun AbstractBigInt<*>.add(value: AbstractBigInt<*>): AbstractBigInt<*> = when {
    sigNum.isZero() -> value
    value.sigNum.isZero() -> this
    else -> biggerFirst(this, value) { big, little ->
        return@biggerFirst of(MutableBigInt.add(big, little).toRawIntArray())
    }
}

internal fun MutableBigInt.Companion.add(x: AbstractBigInt<*>, y: AbstractBigInt<*>): MutableBigInt {
    val result = emptyMutableBigIntOf(IntArray(x.mag.size + 1))

    var carry: Long = 0
    result.mag.indices.forEach { idx ->
        carry += x.getIdxL(idx) + y.getIdxL(idx)
        result.setIdxL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return result
}


public operator fun AbstractBigInt<*>.minus(other: AbstractBigInt<*>): AbstractBigInt<*> = subtract(other)

public fun AbstractBigInt<*>.subtract(value: AbstractBigInt<*>): AbstractBigInt<*> = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value.negate()
    else -> of(MutableBigInt.subtract(this, value).toRawIntArray())
}

internal fun MutableBigInt.Companion.subtract(x: AbstractBigInt<*>, y: AbstractBigInt<*>): MutableBigInt {
    val result = emptyMutableBigIntOf(maxOfArrays(x.mag, y.mag))
    var carry = 0
    result.mag.indices.forEach { idr ->
        var yNum = y.getIdx(idr) + carry
        val xNum = x.getIdx(idr)
        carry = if (yNum xor -0x80000000 < carry xor -0x80000000) 1 else 0
        yNum = xNum - yNum
        carry += if (yNum xor -0x80000000 > xNum xor -0x80000000) 1 else 0
        result.setIdx(idr, yNum)
    }
    return result
}

public operator fun AbstractBigInt<*>.times(other: AbstractBigInt<*>): AbstractBigInt<*> = multiply(other)

internal fun AbstractBigInt<*>.multiply(value: AbstractBigInt<*>): AbstractBigInt<*> = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value) { big, little ->
        val negative = big.sigNum.isNegative().let { if (little.sigNum.isNegative()) !it else it }
        val result = of(MutableBigInt.multiply(big.abs(), little.abs()).toRawIntArray(), BigSigned.POSITIVE)
        return@biggerFirst if (negative) result.negate() else result
    }
}

internal fun MutableBigInt.Companion.multiply(x: AbstractBigInt<*>, y: AbstractBigInt<*>): MutableBigInt {
    val result = emptyMutableBigIntOf(IntArray(x.mag.size + y.mag.size))
    result.setIdx(x.mag.size, multiply(result, x, y.getIdx(0)))
    (1 until y.mag.size).forEach { idy ->
        val num = y.getIdxL(idy)
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += x.getIdxL(idx) * num + result.getIdxL(idy + idx)
            result.setIdxL(idy + idx, carry)
            carry = carry ushr Int.SIZE_BITS
        }
        result.setIdxL(idy + x.mag.size, carry)
    }
    return result
}

internal fun MutableBigInt.Companion.multiply(result: MutableBigInt, x: AbstractBigInt<*>, y: Int): Int {
    val first = y.getL()
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += x.getIdxL(idx) * first
        result.setIdxL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return carry.toInt()
}


public operator fun AbstractBigInt<*>.div(other: AbstractBigInt<*>): AbstractBigInt<*> = divide(other)
public fun AbstractBigInt<*>.divide(value: AbstractBigInt<*>): AbstractBigInt<*> = divideAndRemainder(value).first

public operator fun AbstractBigInt<*>.rem(other: AbstractBigInt<*>): AbstractBigInt<*> = remainder(other)

public infix fun AbstractBigInt<*>.mod(other: AbstractBigInt<*>): AbstractBigInt<*> = when {
    other.sigNum.isNegative() -> error("Modulus not positive")
    else -> {
        val result = this.remainder(other)
        if(result.sigNum.isPositive()) result else result.add(other)
    }
}

public fun AbstractBigInt<*>.remainder(value: AbstractBigInt<*>): AbstractBigInt<*> = divideAndRemainder(value).second

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
                        of(
                            result.first.toComplementedIntArray(),
                            if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE
                        ),
                        of(
                            result.second.toComplementedIntArray(),
                            this.sigNum
                        )
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
    var remLong = rem.getL()
    if (remLong < sorLong) {
        quotient.setUnreversedIdx(0, 0)
    } else {
        quotient.setUnreversedIdxL(0, remLong / sorLong)
        rem = (remLong - quotient.getUnreversedIdx(0) * sorLong).toInt()
        remLong = rem.getL()
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
        remLong = rem.getL()
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

    val sorLen = divisor.mag.size
    val sorArr: IntArray = when {
        shift > 0 -> IntArray(divisor.mag.size).also {
            MutableBigInt.copyAndShift(divisor.mag.toIntArray(), 0, divisor.mag.size, it, 0, shift) }
        else -> divisor.mag.toIntArray().copyOfRange(0, divisor.mag.size)
    }
    val remArr: IntArray = when {
        shift <= 0 -> IntArray(dividend.mag.size + 1).also { arr ->
            dividend.mag.toIntArray().copyInto(arr, 1, 0, dividend.mag.size)
        }
        dividend.mag[0].countLeadingZeroBits() >= shift -> IntArray(dividend.mag.size + 1).also { arr ->
            MutableBigInt.copyAndShift(dividend.mag.toIntArray(), 0, arr.lastIndex, arr, 1, shift)
        }
        else -> IntArray(dividend.mag.size + 2).also { arr ->
            var c = 0
            val n2 = Int.SIZE_BITS - shift
            (1 until dividend.mag.size + 1).forEach { idx ->
                val b = c
                c = dividend.mag[idx - 1]
                arr[idx] = b shl shift or (c ushr n2)
            }
            arr[dividend.mag.size + 1] = c shl shift
        }
    }

    val remLen = remArr.lastIndex
    val quotLen = remLen - sorLen + 1
    val quotArr = IntArray(quotLen)

    val sorHigh = sorArr[0]
    val sorHighLong = sorHigh.getL()
    val sorLow = sorArr[1]

    quotArr.indices.forEach { idx ->
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        val nh = remArr[idx]
        val nh2 = nh + -0x80000000
        val nm = remArr[idx + 1]
        if (nh == sorHigh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.getL()
            if (nChunk >= 0) {
                qhat = (nChunk / sorHighLong).toInt()
                qrem = (nChunk - qhat * sorHighLong).toInt()
            } else {
                val tmp = MutableBigInt.divWord(nChunk, sorHigh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr Int.SIZE_BITS).toInt()
            }
        }
        if (qhat == 0) return@forEach
        if (!skipCorrection) {
            val nl = remArr[idx + 2].getL()
            var rs = qrem.getL() shl Int.SIZE_BITS or nl
            var estProd = sorLow.getL() * qhat.getL()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.getL() + sorHighLong).toInt()
                if (qrem.getL() >= sorHighLong) {
                    estProd -= sorLow.getL()
                    rs = qrem.getL() shl Int.SIZE_BITS or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[idx] = 0
        val borrow = MutableBigInt.mulSub(remArr, sorArr, qhat, sorLen, idx)

        if (borrow + -0x80000000 > nh2) {
            MutableBigInt.divAdd(sorArr, remArr, idx + 1)
            qhat--
        }

        quotArr[idx] = qhat
    }

    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    val nh = remArr[quotLen - 1]
    val nh2 = nh + -0x80000000
    val nm = remArr[quotLen]
    if (nh == sorHigh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = nh.toLong() shl Int.SIZE_BITS or nm.getL()
        if (nChunk >= 0) {
            qhat = (nChunk / sorHighLong).toInt()
            qrem = (nChunk - qhat * sorHighLong).toInt()
        } else {
            val tmp = MutableBigInt.divWord(nChunk, sorHigh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr Int.SIZE_BITS).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) {
            val nl = remArr[quotLen + 1].getL()
            var rs = qrem.getL() shl Int.SIZE_BITS or nl
            var estProd = sorLow.getL() * qhat.getL()
            if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) {
                qhat--
                qrem = (qrem.getL() + sorHighLong).toInt()
                if (qrem.getL() >= sorHighLong) {
                    estProd -= sorLow.getL()
                    rs = qrem.getL() shl Int.SIZE_BITS or nl
                    if (estProd + Long.MIN_VALUE > rs + Long.MIN_VALUE) qhat--
                }
            }
        }

        remArr[quotLen - 1] = 0
        val borrow = MutableBigInt.mulSub(remArr, sorArr, qhat, sorLen, quotLen - 1)

        if (borrow + -0x80000000 > nh2) {
            MutableBigInt.divAdd(sorArr, remArr, quotLen)
            qhat--
        }

        quotArr[quotLen - 1] = qhat
    }

    return Pair(
        emptyMutableBigIntOf(quotArr),
        emptyMutableBigIntOf(if (shift > 0) MutableBigInt.rightShift(remArr, shift) else remArr)
    )
}
