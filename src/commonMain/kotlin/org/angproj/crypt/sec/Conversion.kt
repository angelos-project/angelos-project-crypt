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
import org.angproj.crypt.number.mod

/**
 * sec1-v2.pdf -- 2.3 Data Types and Conversions
 * */
public object Conversion {
    /**
     * sec1-v2.pdf -- 2.3.1 Bit-String-to-Octet-String Conversion.
     * */
    public fun bitString2octetString(B: BitString): OctetString = OctetString(B.value)

    /**
     * sec1-v2.pdf -- 2.3.2 Octet-String-to-Bit-String Conversion.
     * */
    public fun octetString2bitString(M: OctetString): BitString = BitString(M.value)

    /**
     * sec1-v2.pdf -- 2.3.3 Elliptic-Curve-Point-to-Octet-String Conversion.
     * */
    public fun ellipticCurvePoint2octetString(
        P: EllipticCurvePoint,
        q: DomainParameters,
        compress: Boolean
    ): OctetString {
        return when {
            Convention.isAtInfinity(P, q) -> OctetString(byteArrayOf(0))
            !compress -> {
                val mlen = Convention.mlen(q) + 1
                val X = fieldElement2octetString(P.x, q)
                val yBit = when(q) {
                    is PrimeDomainParameters -> P.y.value.mod(BigInt.two).toLong().toInt()
                    is Char2DomainParameters -> when {
                        P.x.value.sigNum.isZero() -> 0
                        //else -> ((P.y.value / P.x.value) and BigInt.one).toLong().toInt() // <- Is this correct?
                        else -> 1 // <- or is this correct?
                    }
                    else -> error("Not implemented.")
                }
                OctetString(byteArrayOf(if(yBit == 1) 3 else 2) + X.octets).also {
                    check(it.octets.size == mlen) { "Mot expected size." } }
            }
            compress -> {
                val mlen = 2 * Convention.mlen(q) + 1
                val X = fieldElement2octetString(P.x, q)
                val Y = fieldElement2octetString(P.y, q)
                OctetString(byteArrayOf(4) + X.octets + Y.octets).also {
                    check(it.octets.size == mlen) { "Mot expected size." } }
            }
            else -> error("Uncalled for.")
        }
    }

    /**
     * sec1-v2.pdf -- 2.3.4 Octet-String-to-Elliptic-Curve-Point Conversion.
     * */
    public fun octetString2ellipticCurvePoint(M: OctetString, q: DomainParameters): EllipticCurvePoint = TODO("To be implemented soon!")

    /**
     * sec1-v2.pdf -- 2.3.5 Field-Element-to-Octet-String Conversion.
     * */
    public fun fieldElement2octetString(a: FieldElement, q: DomainParameters): OctetString = when(q) {
        is PrimeDomainParameters -> integer2octetSting(Integer(a.value), Convention.mlen(q))
        is Char2DomainParameters -> {
            val mlen = Convention.mlen(q)
            integer2octetSting(Integer(a.value), mlen) // <- This is may be correct and may be totally wrong!
        }
        else -> error("Not implemented.")
    }

    /**
     * sec1-v2.pdf -- 2.3.6 Octet-String-to-Field-Element Conversion.
     * */
    public fun octetString2fieldElement(M: OctetString, q: DomainParameters): FieldElement = when(q) {
        is PrimeDomainParameters -> {
            val x = octetString2integer(M).also {
                check(Convention.primeSatisfyInterval(it.value, q)) { "Value not inside [0, p − 1]" } }
            FieldElement(x.value)
        }
        is Char2DomainParameters -> {
            check(Convention.char2SatisfyDegree(M.octets, q)) { "Left most bits are not zeros." }
            FieldElement(bigIntOf(byteArrayOf(0) + M.octets)) // <- Presumably this is right but can be totally wrong.
        }
        else -> error("Not implemented.")
    }

    /**
     * sec1-v2.pdf -- 2.3.7 Integer-to-Octet-String Conversion.
     * */
    public fun integer2octetSting(x: Integer, mlen: Int): OctetString {
        require(x.value.sigNum.isNonNegative())
        require(8 * mlen >= x.value.bitLength) // <- If this is wrong use the one under
        //require(BigInt.two.pow(8 * mlen).compareTo(x.value).isGreater()) // <- Absolute correct

        val binary = x.value.toZeroFilledByteArray(mlen)
        return OctetString(binary.copyOfRange(binary.size - mlen, binary.size))
    }

    /**
     * sec1-v2.pdf -- 2.3.8 Octet-String-to-Integer Conversion.
     * */
    public fun octetString2integer(M: OctetString): Integer = Integer(
        bigIntOf(byteArrayOf(0) + M.value)) // Assumes BigInt methodology applies directly!

    /**
     * sec1-v2.pdf -- 2.3.9 Field-Element-to-Integer Conversion.
     * */
    public fun fieldElement2integer(a: FieldElement, q: DomainParameters): Integer =  when(q) {
        is PrimeDomainParameters -> Integer(a.value)
        is Char2DomainParameters -> Integer(a.value) // <- Seriously the same or not?
        else -> error("Not implemented.")
    }
}