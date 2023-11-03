@file:OptIn(ExperimentalUnsignedTypes::class)

package org.angproj.crypt.number

/* This divides an n-word dividend by an m-word divisor, giving an
n-m+1-word quotient and m-word remainder. The bignums are in arrays of
words. Here a "word" is 32 bits. This routine is designed for a 64-bit
machine which has a 64/64 division instruction. */

internal fun nlz0(x0: UInt): Int {
    var x = x0
    if (x == 0u) return 32
    var n: Int = 0
    if (x <= 0x0000FFFFu) {n += 16; x = x shl 16}
    if (x <= 0x00FFFFFFu) {n += 8; x = x shl 8}
    if (x <= 0x0FFFFFFFu) {n += 4; x = x shl 4}
    if (x <= 0x3FFFFFFFu) {n += 2; x = x shl 2}
    if (x <= 0x7FFFFFFFu) {n += 1}
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

/* public fun divmnu0(q: UIntArray, r: UIntArray, u: UIntArray, v: UIntArray, m: Int, n: Int): Int {
    val b: ULong = 4294967296uL // Number base (2**32).
    val un: UIntArray // Normalized form of u, v.
    val vn: UIntArray
    var qhat: ULong // Estimated quotient digit.
    var rhat: ULong // A remainder.
    var p: ULong // Product of two digits.
    var t: Long
    var k: Long
    var s: Int
    var i: Int
    var j: Int
    if (m < n || n <= 0 || v[n - 1] == 0u)
        return 1 // Return if invalid param.
    if (n == 1) { // Take care of
        k = 0 // the case of a
        j = m - 1
        while (j >= 0) { // single-digit
            q[j] = (k * b + u[j]) / v[0] // divisor here.
            k = (k * b + u[j]) - q[j] * v[0]
            j--
        }
        if (r.isNotEmpty()) r[0] = k.toUInt()
        return 0
    }
    Normalize by shifting v left just enough so that its high-order
    bit is on, and shift u left the same amount. We may have to append a
    high-order digit on the dividend; we do that unconditionally.
    s = nlz(v[n - 1]) // 0 <= s <= 31.
    vn = UIntArray(4 * n)
    i = n - 1
    while (i > 0) {
        vn[i] = (v[i] shl s) or (v[i - 1] shr (32 - s)).toUInt()
        i--
    }
    vn[0] = v[0] shl s
    un = UIntArray(4 * (m + 1))
    un[m] = (u[m - 1] shr (32 - s)).toUInt()
    i = m - 1
    while (i > 0) {
        un[i] = (u[i] shl s) or (u[i - 1] shr (32 - s)).toUInt()
        i--
    }
    un[0] = u[0] shl s
    j = m - n // Main loop.
    while (j >= 0) {
        // Compute estimate qhat of q[j].
        qhat = (un[j + n] * b + un[j + n - 1]) / vn[n - 1]
        rhat = (un[j + n] * b + un[j + n - 1]) - qhat * vn[n - 1]
        do{
            if (qhat >= b || qhat * vn[n - 2] > b * rhat + un[j + n - 2]) {
                qhat = qhat - 1uL
                rhat = rhat + vn[n - 1]
            }
        } while(rhat < b)

        // Multiply and subtract.
        k = 0
        i = 0
        while (i < n) {
            p = qhat * vn[i]
            t = un[i + j] - k - (p and 0xFFFFFFFFuL).toLong()
            un[i + j] = t.toUInt()
            k = (p shr 32) - (t shr 32)
            i++
        }
        t = un[j + n] - k
        un[j + n] = t.toUInt()
        q[j] = qhat // Store quotient digit.
        if (t < 0) { // If we subtracted too
            q[j] = q[j] - 1u // much, add back.
            k = 0
            i = 0
            while (i < n) {
                t = (un[i + j].toULong() + vn[i] + k).toUInt().toLong()
                un[i + j] = t.toUInt()
                k = t shr 32
                i++
            }
            un[j + n] = (un[j + n] + k).toUInt()
        }
        j--
    } // End j.
    // If the caller wants the remainder, unnormalize
    // it and pass it back.
    if (r.isNotEmpty()) {
        i = 0
        while (i < n - 1) {
            r[i] = (un[i] shr s) or (un[i + 1] shl (32 - s)).toUInt()
            i++
        }
        r[n - 1] = un[n - 1] shr s
    }
    return 0
} */