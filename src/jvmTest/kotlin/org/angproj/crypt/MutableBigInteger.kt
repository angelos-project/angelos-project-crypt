/*
 * Copyright (c) 1999, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.angproj.crypt

/**
 * A class used to represent multiprecision integers that makes efficient
 * use of allocated space by allowing a number to occupy only part of
 * an array so that the arrays do not have to be reallocated as often.
 * When performing an operation with many iterations the array used to
 * hold a number is only reallocated when necessary and does not have to
 * be the same size as the number it represents. A mutable number allows
 * calculations to occur on the same number without having to create
 * a new number for every step of the calculation as occurs with
 * BigIntegers.
 *
 *
 * @author  Michael McCloskey
 * @author  Timothy Buktu
 * @since   1.3
 */
internal open class MutableBigInteger {
    var value: IntArray

    constructor() {
        value = IntArray(1)
    }

    /**
     * Construct a new MutableBigInteger with the specified value array
     * up to the length of the array supplied.
     */
    constructor(`val`: IntArray) {
        value = `val`
    }

    /**
     * Convert this MutableBigInteger into an int array with no leading
     * zeros, of a length that is equal to this MutableBigInteger's intLen.
     */
    fun toIntArray(): IntArray {
        return value.copyOf()
    }

    /**
     * Right shift this MutableBigInteger n bits. The MutableBigInteger is left
     * in normal form.
     */
    fun rightShift(value: IntArray, n: Int): IntArray {
        if (value.size == 0) return value
        val nInts = n ushr 5
        val nBits = n and 0x1F
        val value2 = value.copyOf(value.size - nInts)
        if (nBits == 0) return value2
        val bitsInHighWord = Int.SIZE_BITS - value2[0].countLeadingZeroBits()
        return if (nBits >= bitsInHighWord) {
            primitiveLeftShift(value2, Int.SIZE_BITS - nBits).copyOf(value.lastIndex)
        } else {
            primitiveRightShift(value2, nBits)
        }
    }

    /**
     * Right shift this MutableBigInteger n bits, where n is
     * less than 32.
     * Assumes that intLen > 0, n > 0 for speed
     */
    private fun primitiveRightShift(value: IntArray, n: Int): IntArray {
        val n2 = Int.SIZE_BITS - n
        var i = value.lastIndex //offset + intLen - 1
        var c = value[i]
        while (i > 0) {
            val b = c
            c = value[i - 1]
            value[i] = c shl n2 or (b ushr n)
            i--
        }
        value[0] = value[0] ushr n
        return value
    }

    /**
     * Left shift this MutableBigInteger n bits, where n is
     * less than 32.
     * Assumes that intLen > 0, n > 0 for speed
     */
    private fun primitiveLeftShift(value: IntArray, n: Int): IntArray {
        val n2 = Int.SIZE_BITS - n
        var i = 0
        var c = value[i]
        val m = i + value.lastIndex //intLen - 1
        while (i < m) {
            val b = c
            c = value[i + 1]
            value[i] = b shl n or (c ushr n2)
            i++
        }
        //`val`[offset + intLen - 1] = `val`[offset + intLen - 1] shl n
        value[value.lastIndex] = value[value.lastIndex] shl n
        return value
    }

    companion object {
        private fun copyAndShift(src: IntArray, srcFrom_: Int, srcLen: Int, dst: IntArray, dstFrom: Int, shift: Int) {
            var srcFrom = srcFrom_
            val n2 = Int.SIZE_BITS - shift
            var c = src[srcFrom]
            for (i in 0 until srcLen - 1) {
                val b = c
                c = src[++srcFrom]
                dst[dstFrom + i] = b shl shift or (c ushr n2)
            }
            dst[dstFrom + srcLen - 1] = c shl shift
        }

        /**
         * This method divides a long quantity by an int to estimate
         * qhat for two multi precision numbers. It is used when
         * the signed value of n is less than zero.
         * Returns long value where high 32 bits contain remainder value and
         * low 32 bits contain quotient value.
         */
        fun divWord(n: Long, d: Int): Long {
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

        private fun mulsub2(q: IntArray, a: IntArray, x: Int, len: Int, offset: Int): Int {
            val xLong = x.toLong() and 0xffffffffL
            var carry: Long = 0
            var offset_: Int = offset + len
            (len - 1 downTo 0).forEach { idx ->
                val product: Long = (a[idx].toLong() and 0xffffffffL) * xLong + carry
                val difference = q[offset_] - product
                q[offset_--] = difference.toInt()
                carry = (product ushr Int.SIZE_BITS) + (
                        if ((difference and 0xffffffffL) > (product.inv() and 0xffffffffL)) 1 else 0)
            }
            return carry.toInt()
        }

        private fun divadd2(a: IntArray, result: IntArray, offset: Int): Int {
            var carry: Long = 0
            (a.lastIndex downTo 0).forEach { idx ->
                val sum: Long = (a[idx].toLong() and 0xffffffffL) + (
                        result[idx + offset].toLong() and 0xffffffffL) + carry
                result[idx + offset] = sum.toInt()
                carry = sum ushr Int.SIZE_BITS
            }
            return carry.toInt()
        }

        fun divideMagnitude(
            dividend: MutableBigInteger,
            divisor: MutableBigInteger,
        ): Pair<MutableBigInteger, MutableBigInteger> {
            val shift = divisor.value[0].countLeadingZeroBits()

            val dlen = divisor.value.size
            val sorarr: IntArray = when {
                shift > 0 -> IntArray(divisor.value.size).also {
                    copyAndShift(divisor.value, 0, divisor.value.size, it, 0, shift) }
                else -> divisor.value.copyOfRange(0, divisor.value.size)
            }
            val remainder: MutableBigInteger = when {
                shift <= 0 ->  {
                    val remarr = IntArray(dividend.value.size + 1)
                    dividend.value.copyInto(remarr, 1, 0, dividend.value.size)
                    MutableBigInteger(remarr)
                }
                dividend.value[0].countLeadingZeroBits() >= shift -> {
                    val remarr = IntArray(dividend.value.size + 1)
                    copyAndShift(dividend.value, 0, remarr.lastIndex, remarr, 1, shift)
                    MutableBigInteger(remarr)
                }
                else -> {
                    val remarr = IntArray(dividend.value.size + 2)
                    var rFrom = 0
                    var c = 0
                    val n2 = Int.SIZE_BITS - shift
                    var i = 1
                    while (i < dividend.value.size + 1) {
                        val b = c
                        c = dividend.value[rFrom]
                        remarr[i] = b shl shift or (c ushr n2)
                        i++
                        rFrom++
                    }
                    remarr[dividend.value.size + 1] = c shl shift
                    MutableBigInteger(remarr)
                }
            }
            //remainder.offset = 0
            //remainder.intLen = remainder.value.size

            val nlen = remainder.value.lastIndex

            val limit = nlen - dlen + 1
            val quotient = MutableBigInteger()
            //quotient.offset = 0
            quotient.value = IntArray(limit)
            //quotient.intLen = limit
            val quotarr = quotient.value

            /*remainder.offset = 0
            remainder.intLen = remainder.value.size*/
            val dh = sorarr[0]
            val dhLong = dh.toLong() and 0xffffffffL
            val dl = sorarr[1]

            for (j in 0 until limit - 1) {
                var qhat = 0
                var qrem = 0
                var skipCorrection = false
                val nh = remainder.value[j]
                val nh2 = nh + -0x80000000
                val nm = remainder.value[j + 1]
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
                        val tmp = divWord(nChunk, dh)
                        qhat = (tmp and 0xffffffffL).toInt()
                        qrem = (tmp ushr Int.SIZE_BITS).toInt()
                    }
                }
                if (qhat == 0) continue
                if (!skipCorrection) {
                    val nl = remainder.value[j + 2].toLong() and 0xffffffffL
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

                remainder.value[j] = 0
                val borrow = mulsub2(remainder.value, sorarr, qhat, dlen, j)

                if (borrow + -0x80000000 > nh2) {
                    divadd2(sorarr, remainder.value, j + 1)
                    qhat--
                }

                quotarr[j] = qhat
            }

            var qhat = 0
            var qrem = 0
            var skipCorrection = false
            val nh = remainder.value[limit - 1]
            val nh2 = nh + -0x80000000
            val nm = remainder.value[limit]
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
                    val tmp = divWord(nChunk, dh)
                    qhat = (tmp and 0xffffffffL).toInt()
                    qrem = (tmp ushr Int.SIZE_BITS).toInt()
                }
            }
            if (qhat != 0) {
                if (!skipCorrection) {
                    val nl = remainder.value[limit + 1].toLong() and 0xffffffffL
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
                remainder.value[limit - 1] = 0
                borrow = mulsub2(remainder.value, sorarr, qhat, dlen, limit - 1)

                if (borrow + -0x80000000 > nh2) {
                    divadd2(sorarr, remainder.value, limit)
                    qhat--
                }

                quotarr[limit - 1] = qhat
            }

            if (shift > 0) remainder.value = remainder.rightShift(remainder.value, shift)
            return Pair(quotient, remainder)
        }
    }
}