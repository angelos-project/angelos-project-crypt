/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.sec

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.bigIntOf
import org.angproj.crypt.number.pow

public object Convention {

    /**
     * sec1-v2.pdf -- 2.3.1 Bit-String-to-Octet-String Conversion.
     * */
    public fun bitString2octetString(): Unit = error("Not implemented or will not be implemented.")

    /**
     * sec1-v2.pdf -- 2.3.2 Octet-String-to-Bit-String Conversion.
     * */
    public fun octetString2bitString(): Unit = error("Not implemented or will not be implemented.")

    /**
     * sec1-v2.pdf -- 2.3.3 Elliptic-Curve-Point-to-Octet-String Conversion.
     * */
    public fun ellipticCurvePoint2octetString(q: EllipticCurvePoint): OctetString = TODO("To be implemented soon!")

    /**
     * sec1-v2.pdf -- 2.3.4 Octet-String-to-Elliptic-Curve-Point Conversion.
     * */
    public fun octetString2ellipticCurvePoint(M: OctetString, q: DomainParameters): EllipticCurvePoint {
        val len = messageLengthOf(q)
        return when(M.octets.size) {
            1 -> EllipticCurvePoint(BigInt.minusOne, BigInt.minusOne)
            len + 1 -> {
                val Y = OctetString(byteArrayOf(M.octets.first()))
                val X = OctetString(M.octets.sliceArray(1..M.octets.size))
                val xp = octetString2fieldElement(X, q)
                val yp = when(Y.octets.first().toInt()) {
                    0x02 -> FieldElement(BigInt.zero)
                    0x03 -> FieldElement(BigInt.one)
                    else -> error("Invalid Y value, 0x02 or 0x03 expected.")
                }
                TODO("CONTINUE HERE, after implementing BigInt sqrt")
            }
            2 * len + 1 -> { TODO("IMPLEMENT SOON") }
            else -> error("Unknown standard of elliptic curve point.")
        }
    }

    /**
     * sec1-v2.pdf -- 2.3.5 Field-Element-to-Octet-String Conversion.
     * */
    public fun fieldElement2octetString(a: FieldElement, q: DomainParameters): OctetString {
        val mlen = messageLengthOf(q)
        val M = integer2octetSting(Integer(a.value), mlen)
        return when(q) {
            is PrimeDomainParameters -> M
            is Char2DomainParameters -> M.also { setC2LeftmostZero(M.octets, q) }
            else -> error("Unsupported curve.")
        }
    }

    /**
     * sec1-v2.pdf -- 2.3.6 Octet-String-to-Field-Element Conversion.
     * */
    public fun octetString2fieldElement(M: OctetString, q: DomainParameters): FieldElement {
        val mlen = messageLengthOf(q)
        check(M.value.size == mlen) {
            "The octet string is not of required size regarding the curve requirements." }
        return when(q) {
            is PrimeDomainParameters -> {
                val tmp = octetString2integer(M)
                check(isWithinFinite(tmp.value, q)) { "Not within the finite field." }
                FieldElement(tmp.value)
            }
            is Char2DomainParameters -> {
                check(isC2LeftmostZero(M.octets, q)) { "Leftmost bits are not zero." }
                FieldElement(bigIntOf(M.octets))
            }
            else -> error("Unsupported curve")
        }
    }

    /**
     * sec1-v2.pdf -- 2.3.7 Integer-to-Octet-String Conversion.
     * */
    public fun integer2octetSting(x: Integer, mlen: Int): OctetString {
        check(x.value.bitLength < mlen * Byte.SIZE_BITS) {
            "Integer is to large to fit within $mlen octets." }
        check(x.value.sigNum.isNonNegative()) {
            "The integer must be a non-negative value." }
        check(BigInt.two.pow(8 * mlen).compareTo(x.value).isGreater()) {
            "The integer doesn't fit inside the proposed message length." }
        val M = x.value.toZeroFilledByteArray(mlen)
        return when {
            M.size == mlen -> M
            (M.size == (mlen + 1) && M[0].toInt() == 0x00) -> M.copyOfRange(1, M.size)
            else -> error("Unknown calibration error.")
        }.let { OctetString(it) }
    }

    /**
     * sec1-v2.pdf -- 2.3.8 Octet-String-to-Integer Conversion.
     * */
    public fun octetString2integer(M: OctetString): Integer = Integer(bigIntOf(byteArrayOf(0) + M.value))

    /**
     * sec1-v2.pdf -- 2.3.9 Field-Element-to-Integer Conversion.
     * */
    public fun fieldElement2integer(a: FieldElement, q: DomainParameters): Integer {
        check(isWithinFinite(a.value, q)) { "Field element not within the finite field perimeter for given curve." }
        return Integer(a.value)
    }

    private fun isWithinFinite(f: BigInt, q: DomainParameters): Boolean = when(q) {
        is PrimeDomainParameters -> f.sigNum.isNonNegative() && f.compareTo(q.p).isLesser()
        is Char2DomainParameters -> BigInt.two.pow(q.m.bits - 1).compareTo(f).isGreaterOrEqual()
        else -> error("Unsupported curve!")
    }

    private fun messageLengthOf(q: DomainParameters): Int = when(q) {
        is PrimeDomainParameters -> q.p.bitLength
        is Char2DomainParameters -> q.m.bits
        else -> error("Unsupported curve!")
    } / Byte.SIZE_BITS

    private fun isC2LeftmostZero(b: ByteArray, q: Char2DomainParameters): Boolean {
        val mask = 0xffffffffL shl (8 - (b.size * Byte.SIZE_BITS - q.m.bits))
        return (b[0].toLong() and 0xffffffffL) and mask == 0L
    }

    private fun setC2LeftmostZero(b: ByteArray, q: Char2DomainParameters) {
        val mask = 0xffL shl (b.size * Byte.SIZE_BITS - q.m.bits)
        b[0] = ((b[0].toLong() and 0xffffffffL) and mask).toByte()
    }

    /**
     * 2.1.1 The Finite Field Fp.
     * */
}