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
import org.angproj.crypt.dsa.MPN

public infix fun AbstractBigInt<*>.pow(exponent: Int): AbstractBigInt<*> = when {
    exponent < 0 -> error("Negative exponent")
    exponent == 0 -> BigInt.one
    sigNum.isZero() -> this
    else -> {
        val size = (bitCount * exponent shr 5) + 2 * mag.size
        check(size < Int.MAX_VALUE) { "Resulting memory to large. Size $size 32-bit integer array is required." }
        val negative = sigNum.isNegative() && exponent and 1 != 0
        val result = pow(this.abs(), exponent)
        if (negative) result.negate() else result
    }
}

internal fun pow(base: AbstractBigInt<*>, exponent: Int): AbstractBigInt<*> {
    var rest = exponent
    var total = BigInt.zero
    while(rest > 1) {
        var square = base
        val pow2 = AbstractBigInt.bitSizeForInt(rest)
        rest -= (pow2 shl 0x01)
        (0 until pow2).forEach { _ -> square *= square }
        total = total.add(square) as BigInt
    }
    if(rest == 1) total = total.add(base) as BigInt
    return total
}

/* public fun pow(exponent0: Int): BigInt2? {
    var exponent = exponent0
    if (exponent <= 0) {
        if (exponent == 0) return BigInt2.ONE
        throw ArithmeticException("negative exponent")
    }
    if (isZero) return this
    var plen = if (words == null) 1 else ival // Length of pow2.
    val blen = (bitLength() * exponent shr 5) + 2 * plen
    val negative = isNegative && exponent and 1 != 0
    var pow2 = IntArray(blen)
    var rwords = IntArray(blen)
    var work = IntArray(blen)
    getAbsolute(pow2) // pow2 = abs(this);
    var rlen = 1
    rwords[0] = 1 // rwords = 1;
    while (true) {

        // pow2 == this**(2**i)
        // prod = this**(sum(j=0..i-1, (exponent>>j)&1))
        if (exponent and 1 != 0) { // r *= pow2
            MPN.mul(work, pow2, plen, rwords, rlen)
            val temp = work
            work = rwords
            rwords = temp
            rlen += plen
            while (rwords[rlen - 1] == 0) rlen--
        }
        exponent = exponent shr 1
        if (exponent == 0) break
        // pow2 *= pow2;
        MPN.mul(work, pow2, plen, pow2, plen)
        val temp = work
        work = pow2
        pow2 = temp // swap to avoid a copy
        plen *= 2
        while (pow2[plen - 1] == 0) plen--
    }
    if (rwords[rlen - 1] < 0) rlen++
    if (negative) BigInt2.negate(rwords, rwords, rlen)
    return BigInt2.make(rwords, rlen)
} */


/*public fun AbstractBigInt<*>.gcd(value: AbstractBigInt<*>): AbstractBigInt<*> {
    var len = (if (xval > yval) xval else yval) + 1
    val xwords = IntArray(len)
    val ywords = IntArray(len)
    getAbsolute(xwords)
    y.getAbsolute(ywords)
    len = MPN.gcd(xwords, ywords, len)
    val result = BigInt2(0)
    result.ival = len
    result.words = xwords
    return result.canonicalize()
}

internal fun gcd(x: IntArray, y: IntArray, len0: Int): Int {
    var len = len0
    var i: Int
    var word: Int
    // Find sh such that both x and y are divisible by 2**sh.
    i = 0
    while (true) {
        word = x[i] or y[i]
        if (word != 0) {
            // Must terminate, since x and y are non-zero.
            break
        }
        i++
    }
    val initShiftWords = i
    val initShiftBits = MPN.findLowestBit(word)
    // Logically: sh = initShiftWords * 32 + initShiftBits

    // Temporarily devide both x and y by 2**sh.
    len -= initShiftWords
    MPN.rshift0(x, x, initShiftWords, len, initShiftBits)
    MPN.rshift0(y, y, initShiftWords, len, initShiftBits)
    var odd_arg: IntArray /* One of x or y which is odd. */
    var other_arg: IntArray /* The other one can be even or odd. */
    if (x[0] and 1 != 0) {
        odd_arg = x
        other_arg = y
    } else {
        odd_arg = y
        other_arg = x
    }
    while (true) {

        // Shift other_arg until it is odd; this doesn't
        // affect the gcd, since we divide by 2**k, which does not
        // divide odd_arg.
        i = 0
        while (other_arg[i] == 0) {
            i++
        }
        if (i > 0) {
            var j: Int
            j = 0
            while (j < len - i) {
                other_arg[j] = other_arg[j + i]
                j++
            }
            while (j < len) {
                other_arg[j] = 0
                j++
            }
        }
        i = MPN.findLowestBit(other_arg[0])
        if (i > 0) MPN.rshift(other_arg, other_arg, 0, len, i)

        // Now both odd_arg and other_arg are odd.

        // Subtract the smaller from the larger.
        // This does not change the result, since gcd(a-b,b)==gcd(a,b).
        i = MPN.cmp(odd_arg, other_arg, len)
        if (i == 0) break
        if (i > 0) { // odd_arg > other_arg
            MPN.sub_n(odd_arg, odd_arg, other_arg, len)
            // Now odd_arg is even, so swap with other_arg;
            val tmp = odd_arg
            odd_arg = other_arg
            other_arg = tmp
        } else { // other_arg > odd_arg
            MPN.sub_n(other_arg, other_arg, odd_arg, len)
        }
        while (odd_arg[len - 1] == 0 && other_arg[len - 1] == 0) len--
    }
    if (initShiftWords + initShiftBits > 0) {
        if (initShiftBits > 0) {
            val sh_out = MPN.lshift(x, initShiftWords, x, len, initShiftBits)
            if (sh_out != 0) x[len++ + initShiftWords] = sh_out
        } else {
            i = len
            while (--i >= 0) {
                x[i + initShiftWords] = x[i]
            }
        }
        i = initShiftWords
        while (--i >= 0) {
            x[i] = 0
        }
        len += initShiftWords
    }
    return len
}


internal fun findLowestBit(word0: Int): Int {
    var word = word0
    var i = 0
    while (word and 0xF == 0) {
        word = word shr 4
        i += 4
    }
    if (word and 3 == 0) {
        word = word shr 2
        i += 2
    }
    if (word and 1 == 0) i += 1
    return i
}*/