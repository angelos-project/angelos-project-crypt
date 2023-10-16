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

import org.angproj.crypt.dsa.BigInt
import org.angproj.crypt.dsa.BigSignedInt
import kotlin.math.max

private fun BigInt.trustedStripLeadingZeroInts(value: IntArray): IntArray {
    val vlen = value.size
    var keep: Int

    // Find first nonzero byte
    keep = 0
    while (keep < vlen && value[keep] == 0) {
        keep++
    }
    return if (keep == 0) value else value.copyOfRange(keep, vlen)
}

private fun BigInt.makePositive(a: IntArray): IntArray {
    var keep: Int
    var j: Int

    // Find first non-sign (0xffffffff) int of input
    keep = 0
    while (keep < a.size && a[keep] == -1) {
        keep++
    }

    /* Allocate output array.  If all non-sign ints are 0x00, we must
         * allocate space for one extra output int. */j = keep
    while (j < a.size && a[j] == 0) {
        j++
    }
    val extraInt = if (j == a.size) 1 else 0
    val result = IntArray(a.size - keep + extraInt)

    /* Copy one's complement of input into output, leaving extra
         * int (if it exists) == 0x00 */
    for (i in keep until a.size)
        result[i - keep + extraInt] = a[i].inv()

    // Add one to one's complement to generate two's complement
    var i = result.size - 1
    while (++result[i] == 0) {
        i--
    }
    return result
}

public fun bitLengthForInt(n: Int): Int {
    return 32 - n.countTrailingZeroBits()
}

public fun BigInt.bitLength(): Int {
    var n: Int = bitLengthPlusOne - 1
    if (n == -1) { // bitLength not initialized yet
        val m: IntArray = magnitude
        val len = m.size
        if (len == 0) {
            n = 0 // offset by one to initialize
        } else {
            // Calculate the bit length of the magnitude
            val magBitLength: Int = (len - 1 shl 5) + bitLengthForInt(magnitude.get(0))
            if (signedNumber.state < 0) {
                // Check if magnitude is a power of two
                var pow2 = magnitude.get(0).countOneBits() == 1
                var i = 1
                while (i < len && pow2) {
                    pow2 = magnitude.get(i) == 0
                    i++
                }
                n = if (pow2) magBitLength - 1 else magBitLength
            } else {
                n = magBitLength
            }
        }
        bitLengthPlusOne = n + 1
    }
    return n
}

public fun BigInt.bitCount(): Int {
    var bc: Int = bitCountPlusOne - 1
    if (bc == -1) {  // bitCount not initialized yet
        bc = 0 // offset by one to initialize
        // Count the bits in the magnitude
        for (i in 0 until magnitude.size) bc += magnitude.get(i).countOneBits()
        if (signedNumber.state < 0) {
            // Count the trailing zeros in the magnitude
            var magTrailingZeroCount = 0
            var j: Int
            j = magnitude.size - 1
            while (magnitude.get(j) == 0) {
                magTrailingZeroCount += 32
                j--
            }
            magTrailingZeroCount += magnitude.get(j).countTrailingZeroBits()
            bc += magTrailingZeroCount - 1
        }
        bitCountPlusOne = bc + 1
    }
    return bc
}

public fun BigInt.getInt(n: Int): Int {
    if (n < 0) return 0
    if (n >= magnitude.size) return signedNumber.signed
    val magInt: Int = magnitude.get(magnitude.size - n - 1)
    return if (signedNumber.state >= 0) magInt else if (n <= firstNonzeroIntNum) -magInt else magInt.inv()
}

public fun BigInt.intLength(): Int {
    return (bitLength() ushr 5) + 1
}

public fun BigInt.fromIntArray(value: IntArray): BigInt {
    if (value.size == 0) error("Zero length BigInteger")
    var signum: BigSignedInt

    val magnitude = if (value[0] < 0) {
        signum = BigSignedInt.NEGATIVE
        makePositive(value)
    } else {
        val newMag = trustedStripLeadingZeroInts(magnitude)
        signum = if (newMag.size == 0) BigSignedInt.ZERO else BigSignedInt.POSITIVE
        newMag
    }
    return fromIntArrayAndSigNum(magnitude, signum)
}

public fun BigInt.fromIntArrayAndSigNum(magnitude: IntArray, signum: BigSignedInt): BigInt {
    val newSigNum = if (magnitude.size == 0) BigSignedInt.ZERO else signum
    return BigInt(magnitude, signum)
}

public fun BigInt.valueOf(value: IntArray): BigInt {
    return if (value[0] > 0) BigInt(value, BigSignedInt.POSITIVE) else {
        fromIntArray(value)
    }

}

public fun BigInt.longValue(): Long {
    var result: Long = 0
    for (i in 1 downTo 0) result = (result shl 32) + (getInt(i).toLong() and 0xffffffffL)
    return result
}

public fun BigInt.longValueExact(): Long = if (magnitude.size <= 2 && bitLength() <= 63) longValue() else error("BigInteger out of long range")

public fun BigInt.and(value: BigInt): BigInt {
    val result = IntArray(max(intLength(), value.intLength()))
    for (i in result.indices) result[i] = (getInt(result.size - i - 1)
            and value.getInt(result.size - i - 1))

    return valueOf(result)
}