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
        val cmp = compareTo(value)
        when {
            cmp.isLesser() -> {println("Dividend in smaller"); Pair(BigInt.zero, this)}
            cmp.isEqual() -> {println("Dividend is equal"); Pair(BigInt.one, BigInt.zero)}
            else -> {
                val result = divideOneWord(value.getIdx(0))
                println("Do Knuth")
                Pair(
                    of(result.first, if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                    of(result.second, this.sigNum)
                )
            }
        }
    }
}

public fun AbstractBigInt<*>.divideOneWord(divisor: Int): Pair<IntArray, IntArray> {
    val divisorLong = divisor.toLong() and 0xffffffffL

    // Special case of one word dividend
    if (mag.size == 1) {
        val dividendValue: Long = getUnreversedIdxL(0)
        val q = (dividendValue / divisorLong).toInt()
        val r = (dividendValue - q * divisorLong).toInt()
        return Pair(intArrayOf(q), intArrayOf(r))
    }
    val quotient =  IntArray(mag.size)

    // Normalize the divisor
    val shift: Int = divisor.countLeadingZeroBits()
    var rem: Int = getUnreversedIdx(0)
    var remLong = rem.toLong() and 0xffffffffL
    if (remLong < divisorLong) {
        quotient[0] = 0
    } else {
        quotient[0] = (remLong / divisorLong).toInt()
        rem = (remLong - quotient[0] * divisorLong).toInt()
        remLong = rem.toLong() and 0xffffffffL
    }
    var xlen: Int = mag.size
    while (--xlen > 0) {
        val dividendEstimate = remLong shl 32 or getUnreversedIdxL(mag.size - xlen)
        var q: Int
        if (dividendEstimate >= 0) {
            q = (dividendEstimate / divisorLong).toInt()
            rem = (dividendEstimate - q * divisorLong).toInt()
        } else {
            val tmp = divWord(dividendEstimate, divisor)
            q = (tmp and 0xffffffffL).toInt()
            rem = (tmp ushr 32).toInt()
        }
        quotient[mag.size - xlen] = q
        remLong = rem.toLong() and 0xffffffffL
    }
    // Unnormalize
    return when {
        shift > 0 -> Pair(quotient, intArrayOf(rem % divisor))
        else -> Pair(quotient, intArrayOf(rem))
    }
}

public fun AbstractBigInt<*>.divideMagnitude(div: MutableBigInt): Pair<IntArray, IntArray> {
    // assert div.intLen > 1
    // D1 normalize the divisor
    //val shift: Int = java.lang.Integer.numberOfLeadingZeros(div.value.get(div.offset))
    val shift = div.mag.first().countLeadingZeroBits()
    // Copy divisor value to protect divisor
    //val dlen: Int = div.intLen
    val dlen: Int = div.mag.size
    val divisor: IntArray
    //val rem: MutableBigInt
    val remarr: IntArray // Remainder starts as dividend with space for a leading zero
    if (shift > 0) {
        divisor = IntArray(dlen)
        copyAndShift(div.mag.toIntArray(), 0, dlen, divisor, 0, shift)
        //divisor = div.shiftLeft(shift).toComplementedIntArray()
        //if (java.lang.Integer.numberOfLeadingZeros(value.get(offset)) >= shift) {
        if (mag.first().countLeadingZeroBits() >= shift) {
            remarr = IntArray(mag.size + 1)
            //rem = mutableBigIntOf(remarr)
            //rem.intLen = intLen
            //rem.offset = 1
            copyAndShift(mag.toIntArray(), 1, mag.size, remarr, 1, shift)
            remarr[0] = 0
            //rem = mutableBigIntOf(remarr)
            //val remarr = intArrayOf(0) + shiftLeft(shift).toComplementedIntArray()
        } else {
            remarr = IntArray(mag.size + 2)
            //rem.intLen = intLen + 1
            //rem.offset = 1
            var rFrom: Int = 0
            var c = 0
            val n2 = 32 - shift
            var i = 1
            while (i < mag.size + 1) {
                val b = c
                c = getUnreversedIdx(rFrom)
                remarr[i] = b shl shift or (c ushr n2)
                i++
                rFrom++
            }
            remarr[mag.size + 1] = c shl shift
            //rem = mutableBigIntOf(remarr)
        }
    } else {
        //divisor = java.util.Arrays.copyOfRange<Any>(div.value, div.offset, div.offset + div.intLen)
        divisor = div.mag.toIntArray()
        remarr = IntArray(mag.size + 1)
        //rem = mutableBigIntOf(IntArray(mag.size + 1))
        //java.lang.System.arraycopy(value, offset, rem.value, 1, intLen)
        //rem.intLen = intLen
        //rem.offset = 1
    }
    //val nlen: Int = rem.intLen
    val nlen: Int = remarr.size

    // Set the quotient size
    val limit = nlen - dlen + 1
    val quotarr = IntArray(limit)
    val quotient: MutableBigInt
    //if (quotient.value.length < limit) {
    //    quotient.value = IntArray(limit)
    //    quotient.offset = 0
    //}
    //quotient.intLen = limit
    //val q: IntArray = quotient.value
    val q: IntArray = quotarr

    // Insert leading 0 in rem
    //rem.offset = 0
    //rem.value.get(0) = 0
    //rem.intLen++
    remarr[0] = 0
    val dh = divisor[0]
    val dhLong = (dh.toLong() and 0xffffffffL)
    val dl = divisor[1]

    // D2 Initialize j
    for (j in 0 until limit - 1) {
        // D3 Calculate qhat
        // estimate qhat
        var qhat = 0
        var qrem = 0
        var skipCorrection = false
        //val nh: Int = rem.value.get(j + rem.offset)
        val nh: Int = remarr[j]
        val nh2 = nh + -0x80000000
        //val nm: Int = rem.value.get(j + 1 + rem.offset)
        val nm: Int = remarr[j + 1]
        if (nh == dh) {
            qhat = 0.inv()
            qrem = nh + nm
            skipCorrection = qrem + -0x80000000 < nh2
        } else {
            val nChunk = nh.toLong() shl 32 or (nm.toLong() and 0xffffffffL)
            if (nChunk >= 0) {
                qhat = (nChunk / dhLong).toInt()
                qrem = (nChunk - qhat * dhLong).toInt()
            } else {
                val tmp: Long = divWord(nChunk, dh)
                qhat = (tmp and 0xffffffffL).toInt()
                qrem = (tmp ushr 32).toInt()
            }
        }
        if (qhat == 0) continue
        if (!skipCorrection) { // Correct qhat
            //val nl: Long = rem.value.get(j + 2 + rem.offset) and 0xffffffffL
            val nl: Long = remarr[j + 2].toLong() and 0xffffffffL
            var rs = qrem.toLong() and 0xffffffffL shl 32 or nl
            var estProduct = (dl.toLong() and 0xffffffffL) * (qhat.toLong() and 0xffffffffL)
            if (unsignedLongCompare(estProduct, rs)) {
                qhat--
                qrem = ((qrem.toLong() and 0xffffffffL) + dhLong).toInt()
                if (qrem.toLong() and 0xffffffffL >= dhLong) {
                    estProduct -= dl.toLong() and 0xffffffffL
                    rs = (qrem.toLong() and 0xffffffffL shl 32 or nl)
                    if (unsignedLongCompare(estProduct, rs)) qhat--
                }
            }
        }

        // D4 Multiply and subtract
        //rem.value.get(j + rem.offset) = 0
        remarr[j] = 0
        //val borrow: Int = mulsub(rem.value, divisor, qhat, dlen, j + rem.offset)
        val borrow: Int = mulsub(remarr, divisor, qhat, dlen, j)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            //divadd(divisor, rem.value, j + 1 + rem.offset)
            divadd(divisor, remarr, j + 1)
            qhat--
        }

        // Store the quotient digit
        q[j] = qhat
    } // D7 loop on j
    // D3 Calculate qhat
    // estimate qhat
    var qhat = 0
    var qrem = 0
    var skipCorrection = false
    //val nh: Int = rem.value.get(limit - 1 + rem.offset)
    val nh: Int = remarr[limit - 1]
    val nh2 = nh + -0x80000000
    //val nm: Int = rem.value.get(limit + rem.offset)
    val nm: Int = remarr[limit]
    if (nh == dh) {
        qhat = 0.inv()
        qrem = nh + nm
        skipCorrection = qrem + -0x80000000 < nh2
    } else {
        val nChunk = nh.toLong() shl 32 or (nm.toLong() and 0xffffffffL)
        if (nChunk >= 0) {
            qhat = (nChunk / dhLong).toInt()
            qrem = (nChunk - qhat * dhLong).toInt()
        } else {
            val tmp: Long = divWord(nChunk, dh)
            qhat = (tmp and 0xffffffffL).toInt()
            qrem = (tmp ushr 32).toInt()
        }
    }
    if (qhat != 0) {
        if (!skipCorrection) { // Correct qhat
            //val nl: Long = rem.value.get(limit + 1 + rem.offset) and 0xffffffffL
            val nl: Long = remarr[limit + 1].toLong() and 0xffffffffL
            var rs = qrem.toLong() and 0xffffffffL shl 32 or nl
            var estProduct = (dl.toLong() and 0xffffffffL) * (qhat.toLong() and 0xffffffffL)
            if (unsignedLongCompare(estProduct, rs)) {
                qhat--
                qrem = ((qrem.toLong() and 0xffffffffL) + dhLong).toInt()
                if (qrem.toLong() and 0xffffffffL >= dhLong) {
                    estProduct -= dl.toLong() and 0xffffffffL
                    rs = qrem.toLong() and 0xffffffffL shl 32 or nl
                    if (unsignedLongCompare(estProduct, rs)) qhat--
                }
            }
        }


        // D4 Multiply and subtract
        val borrow: Int
        //rem.value.get(limit - 1 + rem.offset) = 0
        remarr[limit - 1] = 0
        //if (needRemainder)
        borrow = mulsub(remarr, divisor, qhat, dlen, limit - 1)
        //else borrow = mulsubBorrow(remarr, divisor, qhat, dlen, limit - 1)

        // D5 Test remainder
        if (borrow + -0x80000000 > nh2) {
            // D6 Add back
            //if (needRemainder)
            divadd(divisor, remarr, limit - 1 + 1)
            qhat--
        }

        // Store the quotient digit
        q[limit - 1] = qhat
    }
    //if (needRemainder) {
        // D8 Unnormalize
        if (shift > 0) //rem.rightShift(shift)
            primitiveRightShift(remarr, shift)
        //rem.normalize()
    //}
    //quotient.normalize()
    return Pair(quotarr, remarr)
}

private fun unsignedLongCompare(one: Long, two: Long): Boolean {
    return one + Long.MIN_VALUE > two + Long.MIN_VALUE
}

private fun mulsub(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var offset1 = offset
    val xLong = x.toLong() and 0xffffffffL
    var carry: Long = 0
    offset1 += len
    for (j in len - 1 downTo 0) {
        val product = (a[j].toLong() and 0xffffffffL) * xLong + carry
        val difference = q[offset1] - product
        q[offset1--] = difference.toInt()
        carry = ((product ushr 32)
                + if (difference and 0xffffffffL > product.inv() and 0xffffffffL) 1 else 0)
    }
    return carry.toInt()
}

private fun divadd(a: IntArray, result: IntArray, offset: Int): Int {
    var carry: Long = 0
    for (j in a.indices.reversed()) {
        val sum = (a[j].toLong() and 0xffffffffL) + (result[j + offset].toLong() and 0xffffffffL) + carry
        result[j + offset] = sum.toInt()
        carry = sum ushr 32
    }
    return carry.toInt()
}

private fun mulsubBorrow(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
    var offset = offset
    val xLong = (x.toLong()and 0xffffffffL).toLong()
    var carry: Long = 0
    offset += len
    for (j in len - 1 downTo 0) {
        val product = (a[j].toLong() and 0xffffffffL) * xLong + carry
        val difference = q[offset--] - product
        carry = ((product ushr 32)
                + if (difference and 0xffffffffL > product.inv() and 0xffffffffL) 1 else 0)
    }
    return carry.toInt()
}

private fun divWord(n: Long, d: Int): Long {
    val dLong = (d.toLong() and 0xffffffffL)
    var r: Long
    var q: Long
    if (dLong == 1L) {
        q = n.toInt().toLong()
        r = 0
        return r shl 32 or (q and 0xffffffffL)
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
    return r shl 32 or (q and 0xffffffffL)
}

private fun copyAndShift(src: IntArray, srcFrom: Int, srcLen: Int, dst: IntArray, dstFrom: Int, shift: Int) {
    var srcFrom1 = srcFrom
    val n2 = 32 - shift
    var c = src[srcFrom1]
    for (i in 0 until srcLen - 1) {
        val b = c
        c = src[++srcFrom1]
        dst[dstFrom + i] = b shl shift or (c ushr n2)
    }
    dst[dstFrom + srcLen - 1] = c shl shift
}

private fun primitiveRightShift(value: IntArray, n: Int) {
    val n2 = 32 - n
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