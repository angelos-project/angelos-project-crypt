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

import org.angproj.aux.num.AbstractBigInt
import org.angproj.aux.num.BigInt
import org.angproj.aux.util.bigIntOf
import org.angproj.crypt.number.*
import kotlin.math.ceil

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
            1 -> EllipticCurvePoint(FieldElement(BigInt.minusOne), FieldElement(BigInt.minusOne))
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


    public fun isAtInfinity(P: EllipticCurvePoint, q: DomainParameters): Boolean = when(q) {
        is PrimeDomainParameters -> primeIsAtInfinity(P)
        is Char2DomainParameters -> char2isAtInfinity(P)
        else -> error("Not implemented.")
    }

    /**
     * sec1-v2.pdf -- 2.2.1 Elliptic Curves over Fp
     * 3) Rule to add ... same x-coordinates when ... have y-coordinate 0.
     * */
    private fun primeIsAtInfinity(P: EllipticCurvePoint): Boolean = P.y.value.compareTo(BigInt.zero).isEqual()

    /**
     * sec1-v2.pdf -- 2.2.2 Elliptic Curves over F2m
     * 3) Rule to add ... same x-coordinates when ... have x-coordinate 0.
     * */
    private fun char2isAtInfinity(P: EllipticCurvePoint): Boolean = P.x.value.compareTo(BigInt.zero).isEqual()


    /**
     * sec1-v2.pdf -- 2.1.1 The Finite Field Fp
     * sec1-v2.pdf -- 2.2.2 Elliptic Curves over F2m
     * */
    public fun log2q(q: DomainParameters): Int = when(q) {
        is PrimeDomainParameters -> q.p.bitLength
        is Char2DomainParameters -> q.m
        else -> error("Not implemented.")
    }

    public fun mlen(q: DomainParameters): Int = ceil(log2q(q) / 8f).toInt()

    public fun primeSatisfyInterval(value: BigInt, q: PrimeDomainParameters): Boolean {
        return value.compareTo(BigInt.zero).isGreaterOrEqual() && value.compareTo(q.p).isLesser() }

    public fun char2SatisfyDegree(value: ByteArray, q: Char2DomainParameters): Boolean {
        val mlen = 8 * mlen(q) - q.m
        val mask: Long = 0xffffffff shl (8 - mlen)
        return mask and value.first().toLong() == 0L
    }

    internal fun isWithinFinite(f: BigInt, q: DomainParameters): Boolean = when(q) {
        is PrimeDomainParameters -> f.sigNum.isNonNegative() && f.compareTo(q.p).isLesser()
        is Char2DomainParameters -> BigInt.two.pow(q.m - 1).compareTo(f).isGreaterOrEqual()
        else -> error("Unsupported curve!")
    }

    private fun messageLengthOf(q: DomainParameters): Int = when(q) {
        is PrimeDomainParameters -> q.p.bitLength
        is Char2DomainParameters -> q.m
        else -> error("Unsupported curve!")
    } / Byte.SIZE_BITS

    private fun isC2LeftmostZero(b: ByteArray, q: Char2DomainParameters): Boolean {
        val mask = 0xffffffffL shl (8 - (b.size * Byte.SIZE_BITS - q.m))
        return (b[0].toLong() and 0xffffffffL) and mask == 0L
    }

    private fun setC2LeftmostZero(b: ByteArray, q: Char2DomainParameters) {
        val mask = 0xffL shl (b.size * Byte.SIZE_BITS - q.m)
        b[0] = ((b[0].toLong() and 0xffffffffL) and mask).toByte()
    }


    /**
     * 2.1.1 The Finite Field Fp.
     * */

    internal fun primeIsFinite(value: BigInt, q: PrimeDomainParameters): Boolean {
        return value.compareTo(BigInt.zero).isGreaterOrEqual() && value.compareTo(q.p).isLesser() }

    private fun primeAddition(r: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(r, q)) { "Value is not within the finite field." }
        return ((q.a.value + q.b.value) mod q.p).compareTo(r mod q.p).isEqual()
    }

    private fun primeMultiplication(s: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(s, q)) { "Value is not within the finite field." }
        return ((q.a.value * q.b.value) mod q.p).compareTo(s mod q.p).isEqual()
    }

    private fun primeAddativeInverse(x: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(q.a.value, q)) { "Value is not within the finite field." }
        return ((q.a.value.negate() + x) mod q.p).compareTo(BigInt.zero).isEqual()
    }

    private fun primeMultiplicativeInverse(x: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(q.a.value, q)) { "Value is not within the finite field." }
        require(q.a.value.compareTo(BigInt.zero).isNotEqual()) { "Value is zero." }
        // ... multiplicative inverse a^−1 of a ... unique solution...a*x ≡ 1(mod p).
        // Because: a/b mod p is a(b^−1) mod p. Then: a*x -> a^-1*x -> x/a.
        return ((x / q.a.value) mod q.p).compareTo(BigInt.one mod q.p).isEqual()
    }

    internal fun primeLog2(q: PrimeDomainParameters): Int = q.p.bitLength

    /**
     * 2.2.1 Elliptic Curves over Fp
     * */

    internal fun primeSatisfy(q: PrimeDomainParameters): Boolean = (
            // Must satisfy 4*a^3 +27*b^2  ̸≡ 0 (mod p)
            (bigIntOf(4) * q.a.value.pow(3) + bigIntOf(27) * q.b.value.pow(2)) mod q.p
            ).compareTo(BigInt.zero).isNotEqual()

    /*internal fun primeDefiningEquation(P: EllipticCurvePoint, q: PrimeDomainParameters): Boolean {
        // Definition y^2 ≡ x^3+a*x+b (mod p)
        return (P.y.pow(2) mod q.p).compareTo(
            (P.x.pow(3) + q.a.value * P.x + q.b.value) mod q.p).isEqual()
    }*/

    private fun primeHasseUpper(q: PrimeDomainParameters): AbstractBigInt<*> = q.p + BigInt.one + BigInt.two * q.p.sqrt()

    private fun primeHasseLower(q: PrimeDomainParameters): AbstractBigInt<*> = q.p + BigInt.one - BigInt.two * q.p.sqrt()

    private fun primeHasseTheorem(q: PrimeDomainParameters): AbstractBigInt<*> = primeHasseUpper(q) - primeHasseLower(q)

    /*private fun primeAdditionRule4(P1: EllipticCurvePoint, P2: EllipticCurvePoint, q: PrimeDomainParameters): EllipticCurvePoint {
        check(P1.x.compareTo(P2.x).isNotEqual()) { "x1 and x2 must not be equal." }

        val lambda = (P2.y - P1.y) / (P2.x - P1.x)
        val x3 = lambda.pow(2) - P1.x - P2.x
        val y3 = lambda * (P1.x - x3) - P1.y
        return EllipticCurvePoint(x3.toBigInt(), y3.toBigInt())
    }*/

    /*private fun primeAdditionRule5(P1: EllipticCurvePoint, q: PrimeDomainParameters): EllipticCurvePoint {
        check(P1.y.compareTo(BigInt.zero).isNotEqual()) { "x1 and x2 must not be equal." }

        val lambda = (bigIntOf(3) * P1.x.pow(2) + q.a.value) / (BigInt.two * P1.y)
        val x3 = lambda.pow(2) - BigInt.two * P1.x
        val y3 = lambda * (P1.x - x3) - P1.y
        return EllipticCurvePoint(x3.toBigInt(), y3.toBigInt())
    }*/

    /*private fun primeScalar(k: Int, P1: EllipticCurvePoint, q: PrimeDomainParameters): EllipticCurvePoint {
        check(k > 1) { "k must be above 1." }
        var kP = P1
        repeat(k) { kP = primeAdditionRule5(kP, q) }
        return kP
    }*/

    /**
     * 3.1.1 Elliptic Curve Domain Parameters over Fp
     * */
    /*private fun primeDefineEllipticCurve(x: BigInt, q: PrimeDomainParameters): EllipticCurvePoint {
        val y = (x.pow(3) + x * q.a.value + q.b.value).sqrt()
        return EllipticCurvePoint(x, y.toBigInt())
    }*/


    /**
     * 2.1.2 The Finite Field F2m.
     * */

    //private fun char2isFinite(value: BigInt, q: Char2DomainParameters): Boolean {}

    /*private fun char2addition(r: BigInt, q: Char2DomainParameters): Boolean {
        return ((q.a.value + q.b.value) mod BigInt.two).compareTo(r mod BigInt.two).isEqual()
    }

    private fun char2multiplication(s: BigInt, q: Char2DomainParameters): Boolean {
        require(primeIsFinite(s, q)) { "Value is not within the finite field." }
        return ((q.a.value * q.b.value) mod q.p).compareTo(s mod q.p).isEqual()
    }*/


}