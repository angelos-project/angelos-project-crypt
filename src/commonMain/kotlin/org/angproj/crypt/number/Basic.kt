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

public operator fun AbstractBigInt<*>.plus(other: BigInt): AbstractBigInt<*> = add(other)

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

public fun BigInt.divideAndRemainder(value: BigInt): Pair<BigInt, BigInt> = when {
    value.sigNum.isZero() -> error { "Divisor is zero" }
    sigNum.isZero() -> {println("Dividend is zero"); Pair(BigInt.zero, BigInt.zero)}
    else -> {
        val cmp = this.compareTo(value)
        when {
            cmp.isLesser() -> {println("Dividend in smaller"); Pair(BigInt.zero, this)}
            cmp.isEqual() -> {println("Dividend is equal"); Pair(BigInt.one, BigInt.zero)}
            else -> {
                val result = divmnu(this, value)
                println("Do Knuth")
                Pair(
                    BigInt(result.first, if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                    BigInt(result.second, this.sigNum)
                )
            }
        }
    }
}

/* This divides an n-word dividend by an m-word divisor, giving an
n-m+1-word quotient and m-word remainder. The bignums are in arrays of
words. Here a "word" is 32 bits. This routine is designed for a 64-bit
machine which has a 64/64 division instruction. */

internal fun nlz(x0: Long): Int {
    var x = x0
    if (x == 0L) return Int.SIZE_BITS
    var n = 0
    if (x <= 0x0000FFFFL) {n += 16; x = x shl 16}
    if (x <= 0x00FFFFFFL) {n += 8; x = x shl 8}
    if (x <= 0x0FFFFFFFL) {n += 4; x = x shl 4}
    if (x <= 0x3FFFFFFFL) {n += 2; x = x shl 2}
    if (x <= 0x7FFFFFFFL) {n += 1}
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
    val base32 = 0xffffffffL // Number base (2**32).
    var quotHat: Long // Estimated quotient digit.
    var remHat: Long // A remainder.
    var product: Long // Product of two digits.
    var t: Long
    var k: Long
    var i: Int
    var j: Int

    val quotient = IntArray(m - n + 1)
    val remainder = IntArray(n)
    if (m < n || n <= 0 || divisor.getIdx(n - 1) == 0)
        error("Invalid state")
    if (n == 1) { // Take care of
        k = 0 // the case of a
        j = m - 1
        while (j >= 0) { // single-digit
            //q[j] = ((k * b + u[j]) / v[0].toLong()).toInt() // divisor here.
            quotient.revSetL(j, (k * base32 + dividend.getIdxL(j)) / divisor.getIdxL(0)) // divisor here.
            k = (k * base32 + dividend.getIdxL(j)) - quotient.revGetL(j) * divisor.getIdxL(0)
            j--
        }
        if (remainder.isNotEmpty()) remainder.revSetL(0, k)
        return Pair(quotient, remainder)
    }
    /* Normalize by shifting v left just enough so that its high-order
    bit is on, and shift u left the same amount. We may have to append a
    high-order digit on the dividend; we do that unconditionally. */
    val s = nlz(divisor.getIdxL(n - 1)) // 0 <= s <= 31.
    //val vn = IntArray(4 * n)
    val vn = IntArray(n)
    i = n - 1
    while (i > 0) {
        vn.revSetL(i, (divisor.getIdxL(i) shl s) or (divisor.getIdxL(i - 1) shr (Int.SIZE_BITS - s)))
        i--
    }
    vn.revSetL(0, divisor.getIdxL(0) shl s)
    //val un = IntArray(4 * (m + 1))
    val un = IntArray(m + 1)
    un.revSetL(m, (dividend.getIdxL(m - 1) shr (Int.SIZE_BITS - s)))
    i = m - 1
    while (i > 0) {
        un.revSetL(i, (dividend.getIdxL(i) shl s) or (dividend.getIdxL(i - 1) shr (Int.SIZE_BITS - s)))
        i--
    }
    un.revSetL(0, dividend.getIdxL(0) shl s)
    j = m - n // Main loop.
    while (j >= 0) {
        // Compute estimate qhat of q[j].
        quotHat = (un.revGetL(j + n) * base32 + un.revGetL(j + n - 1)) / vn.revGetL(n - 1)
        remHat = (un.revGetL(j + n) * base32 + un.revGetL(j + n - 1)) - quotHat * vn.revGetL(n - 1)

        while (quotHat >= base32 || quotHat * vn.revGetL(n - 2) > base32 * remHat + un.revGetL(j + n - 2)) {
            quotHat -= 1L
            remHat += vn.revGetL(n - 1)
            if(remHat >= base32) break
        }

        // Multiply and subtract.
        k = 0
        i = 0
        while (i < n) {
            product = quotHat * vn.revGetL(i)
            t = un.revGetL(i + j) - k - (product and 0xFFFFFFFFL)
            un.revSetL(i + j, t)
            k = (product shr Int.SIZE_BITS) - (t shr Int.SIZE_BITS)
            i++
        }
        t = un.revGetL(j + n) - k
        un.revSetL(j + n, t)
        quotient.revSetL(j, quotHat) // Store quotient digit.
        if (t < 0) { // If we subtracted too
            quotient.revSetL(j, quotient.revGetL(j) - 1) // much, add back.
            k = 0
            i = 0
            while (i < n) {
                //t = (un[i + j].toLong() + vn[i] + k).toInt().toLong()
                t = (un.revGetL(i + j) + vn.revGetL(i) + k)
                un.revSetL(i + j, t)
                k = t shr Int.SIZE_BITS
                i++
            }
            un.revSetL(j + n, (un.revGetL(j + n) + k))
        }
        j--
    } // End j.
    // If the caller wants the remainder, unnormalize
    // it and pass it back.
    if (remainder.isNotEmpty()) {
        i = 0
        while (i < n - 1) {
            remainder.revSetL(i, (un.revGetL(i) shr s) or (un.revGetL(i + 1) shl (Int.SIZE_BITS - s)))
            i++
        }
        remainder.revSetL(n - 1, un.revGetL(n - 1) shr s)
    }
    return Pair(quotient, remainder)
}


