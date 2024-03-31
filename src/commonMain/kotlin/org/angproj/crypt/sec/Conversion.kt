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

import org.angproj.aux.util.unsignedBigIntOf

/**
 * sec1-v2.pdf -- 2.3 Data Types and Conversions
 * */
public object Conversion {
    /**
     * sec1-v2.pdf -- 2.3.1 Bit-String-to-Octet-String Conversion.
     *
     * TR-03111 -- 3.1.1 Requires the leftmost part of the bit string to be padded with zeros until a multiple of 8.
     * Pre-padded bit-array is assumed, due to relying on an underlying ByteArray i.e. virtually an octets string.
     * */
    public fun bitString2octetString(B: BitString): OctetString = OctetString(B.value)

    /**
     * sec1-v2.pdf -- 2.3.2 Octet-String-to-Bit-String Conversion.
     * Literally an octets string is just a bit string.
     * */
    public fun octetString2bitString(M: OctetString): BitString = BitString(M.value)

    /**
     * sec1-v2.pdf -- 2.3.3 Elliptic-Curve-Point-to-Octet-String Conversion.
     * */
    /*public fun ellipticCurvePoint2octetString(
        P: EllipticCurvePoint,
        q: AbstractDomainParameters,
        compress: Boolean
    ): OctetString {
        return when {
            Convention.isAtInfinity(P, q) -> OctetString(byteArrayOf(0))
            compress -> {
                val mlen = Convention.mlen(q) + 1
                val X = fieldElement2octetString(P.x, q)
                val yBit = when(q) {
                    is PrimeDomainParameters -> P.y.mod(BigInt.two).toLong().toInt()
                    else -> error("Not implemented.")
                }
                OctetString(byteArrayOf(if(yBit == 1) 3 else 2) + X.octets).also {
                    check(it.octets.size == mlen) { "Not expected size." } }
            }
            !compress -> {
                val mlen = 2 * Convention.mlen(q) + 1
                val X = fieldElement2octetString(P.x, q)
                val Y = fieldElement2octetString(P.y, q)
                OctetString(byteArrayOf(4) + X.octets + Y.octets).also {
                    check(it.octets.size == mlen) { "Not expected size." } }
            }
            else -> error("Uncalled for.")
        }
    }*/

    /**
     * sec1-v2.pdf -- 2.3.4 Octet-String-to-Elliptic-Curve-Point Conversion.
     * */
    /*public fun octetString2ellipticCurvePoint(M: OctetString, q: AbstractDomainParameters): EllipticCurvePoint {
        val mlen = Convention.mlen(q)
        val type = M.octets.first().toInt()
        return when(type) {
            0 -> {
                check(M.octets.size == 1) { "Wrong size for infinity point." }
                EllipticCurvePoint(FieldElement(BigInt.zero), FieldElement(BigInt.zero))
            }
            2, 3 -> {
                check(M.octets.size == mlen + 1) { "Wrong size for compressed point." }
                val X = OctetString(M.octets.copyOfRange(1, 1 + mlen))
                val x = octetString2fieldElement(X, q)
                val yBit = if(type == 2) false else true //type - 2
                println("YBIT $yBit")
                when(q) {
                    is PrimeDomainParameters -> {
                        val alpha = x.value.multiply(x.value.pow2().add(q.a)).add(q.b)
                        //val alpha = x.value.pow(3) + q.a.value * x.value + q.b.value
                        val beta = alpha.sqrt()
                        //check((alpha / q.p).compareTo(BigInt.one).isEqual()) { "alpha is not square." }
                        val yBit2 = beta.testBit(0)
                        println("YBIT2 $yBit")
                        val y = when(yBit2 == yBit) {
                            true -> beta
                            false -> q.p - beta
                        }.let { FieldElement(it.toBigInt()) }
                        EllipticCurvePoint(x, y)
                    }
                    else -> error("Not implemented.")
                }
            }
            4 -> {
                check(M.octets.size == 2 * mlen + 1) { "Wrong size for uncompressed point." }
                val X = OctetString(M.octets.copyOfRange(1, 1 + mlen))
                val Y = OctetString(M.octets.copyOfRange(1 + mlen, M.octets.size))
                EllipticCurvePoint(octetString2fieldElement(X, q), octetString2fieldElement(Y, q))
            }
            else -> error("Wrong curve or invalid data octets.")
        }
    }*/

    /**
     * sec1-v2.pdf -- 2.3.5 Field-Element-to-Octet-String Conversion.
     * */
    public fun fieldElement2octetString(a: FieldElement, q: AbstractDomainParameters): OctetString = when(q) {
        is PrimeDomainParameters -> integer2octetSting(Integer(a.value), Convention.mlen(q))
        else -> error("Not implemented.")
    }

    /**
     * sec1-v2.pdf -- 2.3.6 Octet-String-to-Field-Element Conversion.
     * */
    public fun octetString2fieldElement(M: OctetString, q: AbstractDomainParameters): FieldElement = when(q) {
        is PrimeDomainParameters -> {
            val x = octetString2integer(M).also {
                check(Convention.primeSatisfyInterval(it.value, q)) { "Value not inside [0, p − 1]" } }
            FieldElement(x.value)
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

        val binary = when(x.value.bitLength < mlen * 8) {
            true -> x.value.toZeroFilledByteArray(mlen)
            else -> x.value.toByteArray()
        }
        return OctetString(binary.copyOfRange(binary.size - mlen, binary.size))
    }

    /**
     * sec1-v2.pdf -- 2.3.8 Octet-String-to-Integer Conversion.
     * */
    public fun octetString2integer(M: OctetString): Integer = Integer(
        unsignedBigIntOf(byteArrayOf(0) + M.value)) // Assumes BigInt methodology applies directly!

    /**
     * sec1-v2.pdf -- 2.3.9 Field-Element-to-Integer Conversion.
     * */
    public fun fieldElement2integer(a: FieldElement, q: AbstractDomainParameters): Integer =  when(q) {
        is PrimeDomainParameters -> Integer(a.value)
        else -> error("Not implemented.")
    }
}