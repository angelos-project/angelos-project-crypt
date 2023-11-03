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
import org.angproj.crypt.dsa.BigInt.Companion.revGet
import org.angproj.crypt.dsa.BigInt.Companion.revSet

public operator fun BigInt.unaryMinus(): BigInt = negate()

public operator fun BigInt.plus(other: BigInt): BigInt = add(other)

public fun BigInt.add(value: BigInt): BigInt = when {
    sigNum.isZero() -> value
    value.sigNum.isZero() -> this
    else -> biggerFirst(this, value) { big, little ->
        return@biggerFirst BigInt.fromIntArray(BigInt.add(big, little))
    }
}

internal fun BigInt.Companion.add(x: BigInt, y: BigInt): IntArray {
    val result = IntArray(x.mag.size+1)

    var carry: Long = 0
    result.indices.forEach { idx ->
        carry += x.getIdxL(idx) + y.getIdxL(idx)
        result.revSetL(idx, carry)
        carry = carry ushr Int.SIZE_BITS
    }
    return result
}


public operator fun BigInt.minus(other: BigInt): BigInt = subtract(other)

public fun BigInt.subtract(value: BigInt): BigInt = when {
    value.sigNum.isZero() -> this
    sigNum.isZero() -> value.negate()
    else -> BigInt.fromIntArray(BigInt.subtract(this, value))
}

internal fun BigInt.Companion.subtract(x: BigInt, y: BigInt): IntArray {
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

public operator fun BigInt.times(other: BigInt): BigInt = multiply(other)

private fun BigInt.multiply(value: BigInt): BigInt = when {
    sigNum.isZero() || value.sigNum.isZero() -> BigInt.zero
    else -> biggerFirst(this, value) { big, little ->
        val negative = big.sigNum.isNegative().let { if(little.sigNum.isNegative()) !it else it }
        val result = BigInt(BigInt.multiply(big.abs(), little.abs()), BigSigned.POSITIVE)
        return@biggerFirst if(negative) result.negate() else result
    }
}

internal fun BigInt.Companion.multiply(x: BigInt, y: BigInt): IntArray {
    val result = IntArray(x.mag.size + y.mag.size)
    result.revSet(x.mag.size, multiply(result, x, y.getIdx(0)))
    (1 until y.mag.size).forEach { idy ->
        val num = y.getIdx(idy).toLong() and 0xffffffffL
        var carry: Long = 0
        x.mag.indices.forEach { idx ->
            carry += ((
                    x.getIdx(idx).toLong() and 0xffffffffL) * num + (
                    result.revGet(idy + idx).toLong() and 0xffffffffL))
            result.revSet(idy + idx, carry.toInt())
            carry = carry ushr Int.SIZE_BITS
        }
        result.revSet(idy + x.mag.size, carry.toInt())
    }
    return result
}

internal fun BigInt.Companion.multiply(result: IntArray, x: BigInt, y: Int): Int {
    val first = y.toLong() and 0xffffffffL
    var carry: Long = 0
    x.mag.indices.forEach { idx ->
        carry += (x.getIdx(idx).toLong() and 0xffffffffL) * first
        result.revSet(idx, carry.toInt())
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

public fun BigInt.divideAndRemainder(value: BigInt): Pair<BigInt, BigInt> = when {
    value.sigNum.isZero() -> error { "Divisor is zero" }
    sigNum.isZero() -> Pair(BigInt.zero, BigInt.zero)
    else -> {
        val cmp = this.compareTo(value)
        when {
            cmp.isLesser() -> Pair(BigInt.zero, this)
            cmp.isEqual() -> Pair(BigInt.one, BigInt.zero)
            else -> {
                val result = divmnu(this, value,)
                Pair(
                    BigInt.fromIntArray(result.first),
                    BigInt.fromIntArray(result.second)
                )
            }
        }
    }
}

/* This divides an n-word dividend by an m-word divisor, giving an
n-m+1-word quotient and m-word remainder. The bignums are in arrays of
words. Here a "word" is 32 bits. This routine is designed for a 64-bit
machine which has a 64/64 division instruction. */

internal fun nlz(x0: Int): Int {
    var x = x0
    if (x == 0) return Int.SIZE_BITS
    var n = 0
    if (x <= 0x0000FFFF) {n += 16; x = x shl 16}
    if (x <= 0x00FFFFFF) {n += 8; x = x shl 8}
    if (x <= 0x0FFFFFFF) {n += 4; x = x shl 4}
    if (x <= 0x3FFFFFFF) {n += 2; x = x shl 2}
    if (x <= 0x7FFFFFFF) {n += 1}
    return n
}

/* q[0], r[0], u[0], and v[0] contain the LEAST significant words.
(The sequence is in little-endian order).

This is a fairly precise implementation of Knuth's Algorithm D, for a
binary computer with base b = 2**32. The caller supplies:
   1. Space q for the quotient, m - n + 1 words (at least one).
   2. Space r for the remainder (optional), n words.
   3. The dividend u, m words, m >= 1.
   4. The divisor v, n words, n >= 2.
The most significant digit of the divisor, v[n-1], must be nonzero.  The
dividend u may have leading zeros; this just makes the algorithm take
longer and makes the quotient contain more leading zeros.  A value of
NULL may be given for the address of the remainder to signify that the
caller does not want the remainder.
   The program does not alter the input parameters u and v.
   The quotient and remainder returned may have leading zeros.  The
function itself returns a value of 0 for success and 1 for invalid
parameters (e.g., division by 0).
   For now, we must have m >= n.  Knuth's Algorithm D also requires
that the dividend be at least as long as the divisor.  (In his terms,
m >= 0 (unstated).  Therefore m+n >= n.) */

// https://raw.githubusercontent.com/hcs0/Hackers-Delight/master/divmnu64.c.txt
public fun divmnu(dividend: BigInt, divisor: BigInt, m: Int = dividend.mag.size, n: Int = divisor.mag.size): Pair<IntArray, IntArray> {
    val b: Long = 0xffffffffL // Number base (2**32).
    var qhat: Long // Estimated quotient digit.
    var rhat: Long // A remainder.
    var p: Long // Product of two digits.
    var t: Long
    var k: Long
    var s: Int
    var i: Int
    var j: Int

    val q = IntArray(m - n + 1)
    val r = IntArray(n)
    if (m < n || n <= 0 || divisor.getIdx(n - 1) == 0)
        error("Invalid state")
    if (n == 1) { // Take care of
        k = 0 // the case of a
        j = m - 1
        while (j >= 0) { // single-digit
            //q[j] = ((k * b + u[j]) / v[0].toLong()).toInt() // divisor here.
            q.revSet(j, (k * b + dividend.getIdx(j)).toInt() / divisor.getIdx(0)) // divisor here.
            k = (k * b + dividend.getIdx(j)) - q.revGet(j) * divisor.getIdx(0)
            j--
        }
        if (r.isNotEmpty()) r.revSet(0, k.toInt())
        return Pair(q, r)
    }
    /* Normalize by shifting v left just enough so that its high-order
    bit is on, and shift u left the same amount. We may have to append a
    high-order digit on the dividend; we do that unconditionally. */
    s = nlz(divisor.getIdx(n - 1)) // 0 <= s <= 31.
    //val vn = IntArray(4 * n)
    val vn = IntArray(n)
    i = n - 1
    while (i > 0) {
        vn.revSet(i, (divisor.getIdx(i) shl s) or (divisor.getIdx(i - 1) shr (Int.SIZE_BITS - s)))
        i--
    }
    vn.revSet(0, divisor.getIdx(0) shl s)
    //val un = IntArray(4 * (m + 1))
    val un = IntArray(m + 1)
    un.revSet(m, (dividend.getIdx(m - 1) shr (Int.SIZE_BITS - s)))
    i = m - 1
    while (i > 0) {
        un.revSet(i, (dividend.getIdx(i) shl s) or (dividend.getIdx(i - 1) shr (Int.SIZE_BITS - s)))
        i--
    }
    un.revSet(0, dividend.getIdx(0) shl s)
    j = m - n // Main loop.
    while (j >= 0) {
        // Compute estimate qhat of q[j].
        qhat = (un.revGet(j + n) * b + un.revGet(j + n - 1)) / vn.revGet(n - 1)
        rhat = (un.revGet(j + n) * b + un.revGet(j + n - 1)) - qhat * vn.revGet(n - 1)
        /*again:
        if (qhat >= b || qhat * vn[n - 2] > b * rhat + un[j + n - 2]) {
            qhat = qhat - 1uL
            rhat = rhat + vn[n - 1]
            if (rhat < b) goto@again
        }*/

        while (qhat >= b || qhat * vn.revGet(n - 2) > b * rhat + un.revGet(j + n - 2)) {
            qhat -= 1L
            rhat += vn.revGet(n - 1)
            if(rhat >= b) break
        }

        // Multiply and subtract.
        k = 0
        i = 0
        while (i < n) {
            p = qhat * vn.revGet(i)
            t = un.revGet(i + j) - k - (p and 0xFFFFFFFFL)
            un.revSet(i + j, t.toInt())
            k = (p shr Int.SIZE_BITS) - (t shr Int.SIZE_BITS)
            i++
        }
        t = un.revGet(j + n) - k
        un.revSet(j + n, t.toInt())
        q.revSet(j, qhat.toInt()) // Store quotient digit.
        if (t < 0) { // If we subtracted too
            q.revSet(j, q.revGet(j) - 1) // much, add back.
            k = 0
            i = 0
            while (i < n) {
                //t = (un[i + j].toLong() + vn[i] + k).toInt().toLong()
                t = (un.revGet(i + j).toLong() + vn.revGet(i) + k)
                un.revSet(i + j, t.toInt())
                k = t shr Int.SIZE_BITS
                i++
            }
            un.revSet(j + n, (un.revGet(j + n) + k).toInt())
        }
        j--
    } // End j.
    // If the caller wants the remainder, unnormalize
    // it and pass it back.
    if (r.isNotEmpty()) {
        i = 0
        while (i < n - 1) {
            r.revSet(i, (un.revGet(i) shr s) or (un.revGet(i + 1) shl (Int.SIZE_BITS - s)))
            i++
        }
        r.revSet(n - 1, un.revGet(n - 1) shr s)
    }
    return Pair(q, r)
}


