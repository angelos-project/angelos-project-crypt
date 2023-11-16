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

public fun AbstractBigInt<*>.divideAndRemainder(value: AbstractBigInt<*>): Pair<AbstractBigInt<*>, AbstractBigInt<*>> = when {
    value.sigNum.isZero() -> error { "Divisor is zero" }
    sigNum.isZero() -> {println("Dividend is zero"); Pair(BigInt.zero, BigInt.zero)}
    else -> {
        val cmp = compareTo(value)
        when {
            cmp.isLesser() -> {println("Dividend in smaller"); Pair(BigInt.zero, this)}
            cmp.isEqual() -> {println("Dividend is equal"); Pair(BigInt.one, BigInt.zero)}
            else -> {
                val result = divmnu(this, value)
                println("Do Knuth")
                Pair(
                    of(result.first, if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                    of(result.second, this.sigNum)
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
public fun divmnu(dividend: AbstractBigInt<*>, divisor: AbstractBigInt<*>, m: Int = dividend.mag.size, n: Int = divisor.mag.size): Pair<IntArray, IntArray> {
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


/*
public fun divideAndRemainder(`val`: BigInt2): Array<BigInt2?> {
    if (`val`.isZero) throw ArithmeticException("divisor is zero")
    val result = arrayOfNulls<BigInt2>(2)
    result[0] = BigInt2()
    result[1] = BigInt2()
    BigInt2.divide(this, `val`, result[0], result[1], BigInt2.TRUNCATE)
    result[0]!!.canonicalize()
    result[1]!!.canonicalize()
    return result
}


internal fun divide(
    x: BigInt2?, y: BigInt2?,
    quotient: BigInt2?, remainder: BigInt2?,
    rounding_mode: Int
) {
    if (((x!!.words == null || x.ival <= 2)
                && (y!!.words == null || y.ival <= 2))
    ) {
        val x_l = x.toLong()
        val y_l = y.toLong()
        if (x_l != Long.MIN_VALUE && y_l != Long.MIN_VALUE) {
            BigInt2.divide(x_l, y_l, quotient, remainder, rounding_mode)
            return
        }
    }
    val xNegative = x.isNegative
    val yNegative = y!!.isNegative
    val qNegative = xNegative xor yNegative
    var ylen = if (y.words == null) 1 else y.ival
    var ywords = IntArray(ylen)
    y.getAbsolute(ywords)
    while (ylen > 1 && ywords[ylen - 1] == 0) ylen--
    var xlen = if (x.words == null) 1 else x.ival
    var xwords = IntArray(xlen + 2)
    x.getAbsolute(xwords)
    while (xlen > 1 && xwords[xlen - 1] == 0) xlen--
    var qlen: Int
    var rlen: Int
    val cmpval: Int = MPN.cmp(xwords, xlen, ywords, ylen)
    if (cmpval < 0) // abs(x) < abs(y)
    { // quotient = 0;  remainder = num.
        val rwords = xwords
        xwords = ywords
        ywords = rwords
        rlen = xlen
        qlen = 1
        xwords[0] = 0
    } else if (cmpval == 0) // abs(x) == abs(y)
    {
        xwords[0] = 1
        qlen = 1 // quotient = 1
        ywords[0] = 0
        rlen = 1 // remainder = 0;
    } else if (ylen == 1) {
        qlen = xlen
        // Need to leave room for a word of leading zeros if dividing by 1
        // and the dividend has the high bit set.  It might be safe to
        // increment qlen in all cases, but it certainly is only necessary
        // in the following case.
        if (ywords[0] == 1 && xwords[xlen - 1] < 0) qlen++
        rlen = 1
        ywords[0] = MPN.divmod_1(xwords, xwords, xlen, ywords[0])
    } else  // abs(x) > abs(y)
    {
        // Normalize the denominator, i.e. make its most significant bit set by
        // shifting it normalization_steps bits to the left.  Also shift the
        // numerator the same number of steps (to keep the quotient the same!).
        val nshift: Int = MPN.count_leading_zeros(ywords[ylen - 1])
        if (nshift != 0) {
            // Shift up the denominator setting the most significant bit of
            // the most significant word.
            MPN.lshift(ywords, 0, ywords, ylen, nshift)

            // Shift up the numerator, possibly introducing a new most
            // significant word.
            val x_high: Int = MPN.lshift(xwords, 0, xwords, xlen, nshift)
            xwords[xlen++] = x_high
        }
        if (xlen == ylen) xwords[xlen++] = 0
        MPN.divide(xwords, xlen, ywords, ylen)
        rlen = ylen
        MPN.rshift0(ywords, xwords, 0, rlen, nshift)
        qlen = xlen + 1 - ylen
        if (quotient != null) {
            for (i in 0 until qlen) xwords[i] = xwords[i + ylen]
        }
    }
    if (ywords[rlen - 1] < 0) {
        ywords[rlen] = 0
        rlen++
    }

    // Now the quotient is in xwords, and the remainder is in ywords.
    var add_one = false
    if (rlen > 1 || ywords[0] != 0) { // Non-zero remainder i.e. in-exact quotient.
        when (rounding_mode) {
            BigInt2.TRUNCATE -> {}
            BigInt2.CEILING, BigInt2.FLOOR -> if (qNegative == (rounding_mode == BigInt2.FLOOR)) add_one = true
            BigInt2.ROUND -> {
                // int cmp = compareTo(remainder<<1, abs(y));
                var tmp: BigInt2? = remainder ?: BigInt2()
                tmp!![ywords] = rlen
                tmp = BigInt2.shift(tmp, 1)
                if (yNegative) tmp!!.setNegative()
                var cmp = BigInt2.compareTo(tmp, y)
                // Now cmp == compareTo(sign(y)*(remainder<<1), y)
                if (yNegative) cmp = -cmp
                add_one = (cmp == 1) || (cmp == 0 && (xwords[0] and 1) != 0)
            }
        }
    }
    if (quotient != null) {
        quotient[xwords] = qlen
        if (qNegative) {
            if (add_one) // -(quotient + 1) == ~(quotient)
                quotient.setInvert() else quotient.setNegative()
        } else if (add_one) quotient.setAdd(1)
    }
    if (remainder != null) {
        // The remainder is by definition: X-Q*Y
        remainder[ywords] = rlen
        if (add_one) {
            // Subtract the remainder from Y:
            // abs(R) = abs(Y) - abs(orig_rem) = -(abs(orig_rem) - abs(Y)).
            val tmp: BigInt2?
            if (y.words == null) {
                tmp = remainder
                tmp.set((if (yNegative) ywords[0] + y.ival else ywords[0] - y.ival).toLong())
            } else tmp = BigInt2.add(remainder, y, if (yNegative) 1 else -1)
            // Now tmp <= 0.
            // In this case, abs(Q) = 1 + floor(abs(X)/abs(Y)).
            // Hence, abs(Q*Y) > abs(X).
            // So sign(remainder) = -sign(X).
            if (xNegative) remainder.setNegative(tmp) else remainder.set(tmp)
        } else {
            // If !add_one, then: abs(Q*Y) <= abs(X).
            // So sign(remainder) = sign(X).
            if (xNegative) remainder.setNegative()
        }
    }
}


internal fun divmod_1(
    quotient: IntArray, dividend: IntArray,
    len: Int, divisor: Int
): Int {
    var i = len - 1
    var r = dividend[i].toLong()
    if (r and 0xffffffffL >= divisor.toLong() and 0xffffffffL) r = 0 else {
        quotient[i--] = 0
        r = r shl 32
    }
    while (i >= 0) {
        val n0 = dividend[i]
        r = r and 0xffffffffL.inv() or (n0.toLong() and 0xffffffffL)
        r = MPN.udiv_qrnnd(r, divisor)
        quotient[i] = r.toInt()
        i--
    }
    return (r shr 32).toInt()
}


internal fun udiv_qrnnd(N: Long, D: Int): Long {
    var q: Long
    var r: Long
    val a1 = N ushr 32
    val a0 = N and 0xffffffffL
    if (D >= 0) {
        if (a1 < D - a1 - (a0 ushr 31) and 0xffffffffL) {
            /* dividend, divisor, and quotient are nonnegative */
            q = N / D
            r = N % D
        } else {
            /* Compute c1*2^32 + c0 = a1*2^32 + a0 - 2^31*d */
            val c = N - (D.toLong() shl 31)
            /* Divide (c1*2^32 + c0) by d */q = c / D
            r = c % D
            /* Add 2^31 to quotient */q += (1 shl 31).toLong()
        }
    } else {
        val b1 = (D ushr 1).toLong() /* d/2, between 2^30 and 2^31 - 1 */
        //long c1 = (a1 >> 1); /* A/2 */
        //int c0 = (a1 << 31) + (a0 >> 1);
        var c = N ushr 1
        if (a1 < b1 || a1 shr 1 < b1) {
            if (a1 < b1) {
                q = c / b1
                r = c % b1
            } else  /* c1 < b1, so 2^31 <= (A/2)/b1 < 2^32 */ {
                c = (c - (b1 shl 32)).inv()
                q = c / b1 /* (A/2) / (d/2) */
                r = c % b1
                q = q.inv() and 0xffffffffL /* (A/2)/b1 */
                r = b1 - 1 - r /* r < b1 => new r >= 0 */
            }
            r = 2 * r + (a0 and 1L)
            if (D and 1 != 0) {
                if (r >= q) {
                    r = r - q
                } else if (q - r <= D.toLong() and 0xffffffffL) {
                    r = r - q + D
                    q -= 1
                } else {
                    r = r - q + D + D
                    q -= 2
                }
            }
        } else  /* Implies c1 = b1 */ {                /* Hence a1 = d - 1 = 2*b1 - 1 */
            if (a0 >= (-D).toLong() and 0xffffffffL) {
                q = -1
                r = a0 + D
            } else {
                q = -2
                r = a0 + D + D
            }
        }
    }
    return r shl 32 or (q and 0xFFFFFFFFL)
}


internal fun divide(zds: IntArray, nx: Int, y: IntArray, ny: Int) {
    // This is basically Knuth's formulation of the classical algorithm,
    // but translated from in scm_divbigbig in Jaffar's SCM implementation.

    // Correspondance with Knuth's notation:
    // Knuth's u[0:m+n] == zds[nx:0].
    // Knuth's v[1:n] == y[ny-1:0]
    // Knuth's n == ny.
    // Knuth's m == nx-ny.
    // Our nx == Knuth's m+n.

    // Could be re-implemented using gmp's mpn_divrem:
    // zds[nx] = mpn_divrem (&zds[ny], 0, zds, nx, y, ny).
    var j = nx
    do {                          // loop over digits of quotient
        // Knuth's j == our nx-j.
        // Knuth's u[j:j+n] == our zds[j:j-ny].
        var qhat: Int // treated as unsigned
        qhat = if (zds[j] == y[ny - 1]) -1 // 0xffffffff
        else {
            val w = (zds[j].toLong() shl 32) + (zds[j - 1].toLong() and 0xffffffffL)
            MPN.udiv_qrnnd(w, y[ny - 1]).toInt()
        }
        if (qhat != 0) {
            val borrow = MPN.submul_1(zds, j - ny, y, ny, qhat)
            val save = zds[j]
            var num = (save.toLong() and 0xffffffffL) - (borrow.toLong() and 0xffffffffL)
            while (num != 0L) {
                qhat--
                var carry: Long = 0
                for (i in 0 until ny) {
                    carry += ((zds[j - ny + i].toLong() and 0xffffffffL)
                            + (y[i].toLong() and 0xffffffffL))
                    zds[j - ny + i] = carry.toInt()
                    carry = carry ushr 32
                }
                zds[j] += carry.toInt()
                num = carry - 1
            }
        }
        zds[j] = qhat
    } while (--j >= ny)
}


internal fun submul_1(dest: IntArray, offset: Int, x: IntArray, len: Int, y: Int): Int {
    val yl = y.toLong() and 0xffffffffL
    var carry = 0
    var j = 0
    do {
        val prod = (x[j].toLong() and 0xffffffffL) * yl
        var prod_low = prod.toInt()
        val prod_high = (prod shr 32).toInt()
        prod_low += carry
        // Invert the high-order bit, because: (unsigned) X > (unsigned) Y
        // iff: (int) (X^0x80000000) > (int) (Y^0x80000000).
        carry = ((if (prod_low xor -0x80000000 < carry xor -0x80000000) 1 else 0)
                + prod_high)
        val x_j = dest[offset + j]
        prod_low = x_j - prod_low
        if (prod_low xor -0x80000000 > x_j xor -0x80000000) carry++
        dest[offset + j] = prod_low
    } while (++j < len)
    return carry
}*/