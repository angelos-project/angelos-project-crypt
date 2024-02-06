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
import org.angproj.crypt.number.*
import kotlin.math.ceil

public object Convention {

    public val voidBitString: BitString = BitString(byteArrayOf())
    public val voidOctetString: OctetString = OctetString(byteArrayOf())
    public val voidInteger: Integer = Integer(BigInt.zero)
    public val voidFieldElement: FieldElement = FieldElement(BigInt.zero)
    public val voidEllipticCurvePoint: EllipticCurvePoint = EllipticCurvePoint(voidFieldElement, voidFieldElement)

    public fun isAtInfinity(P: EllipticCurvePoint, q: AbstractDomainParameters): Boolean = when(q) {
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
    public fun log2q(q: AbstractDomainParameters): Int = when(q) {
        is PrimeDomainParameters -> q.p.value.bitLength
        is Char2DomainParameters -> q.m
        else -> error("Not implemented.")
    }

    public fun mlen(q: AbstractDomainParameters): Int = ceil(log2q(q) / 8f).toInt()

    /**
     * sec1-v2.pdf -- 2.2.1 Elliptic Curves over Fp.
     *     Defining equation: y^2 ≡ x^3 + a * x + b (mod p)
     * sec1-v2.pdf -- 2.2.2 Elliptic Curves over F2m
     *     Defining equation (?): y^2 + x * y = x^3 + a * x^2 + b
     * */
    public fun pointSatisfyDefiningEquation(P: EllipticCurvePoint, q: AbstractDomainParameters): Boolean = when(q) {
        is PrimeDomainParameters -> ((P.y.value.pow(2)).mod(q.p.value)).compareTo(
            (P.x.value.pow(3) + q.a.value * P.x.value + q.b.value).mod(q.p.value)).isEqual()
        is Char2DomainParameters -> (P.y.value.pow(2) + P.x.value * P.y.value).compareTo(
            (P.x.value.pow(3) + q.a.value * P.x.value.pow(2) + q.b.value)).isEqual()
        else -> error("Not implemented.")
    }

    public fun primeSatisfyInterval(value: BigInt, q: PrimeDomainParameters): Boolean {
        return value.compareTo(BigInt.zero).isGreaterOrEqual() && value.compareTo(q.p.value).isLesser() }

    public fun char2satisfyDegree(value: ByteArray, q: Char2DomainParameters): Boolean {
        val mlen = 8 * mlen(q) - q.m
        val mask: Long = 0xffffffff shl (8 - mlen)
        return mask and value.first().toLong() == 0L
    }

    /**
     * 2.1.1 The Finite Field Fp.
     * */

    internal fun primeIsFinite(value: BigInt, q: PrimeDomainParameters): Boolean {
        return value.compareTo(BigInt.zero).isGreaterOrEqual() && value.compareTo(q.p.value).isLesser() }

    private fun primeAddition(r: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(r, q)) { "Value is not within the finite field." }
        return ((q.a.value + q.b.value) mod q.p.value).compareTo(r mod q.p.value).isEqual()
    }

    private fun primeMultiplication(s: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(s, q)) { "Value is not within the finite field." }
        return ((q.a.value * q.b.value) mod q.p.value).compareTo(s mod q.p.value).isEqual()
    }

    private fun primeAddativeInverse(x: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(q.a.value, q)) { "Value is not within the finite field." }
        return ((q.a.value.negate() + x) mod q.p.value).compareTo(BigInt.zero).isEqual()
    }

    private fun primeMultiplicativeInverse(x: BigInt, q: PrimeDomainParameters): Boolean {
        require(primeIsFinite(q.a.value, q)) { "Value is not within the finite field." }
        require(q.a.value.compareTo(BigInt.zero).isNotEqual()) { "Value is zero." }
        // ... multiplicative inverse a^−1 of a ... unique solution...a*x ≡ 1(mod p).
        // Because: a/b mod p is a(b^−1) mod p. Then: a*x -> a^-1*x -> x/a.
        return ((x / q.a.value) mod q.p.value).compareTo(BigInt.one mod q.p.value).isEqual()
    }

    public fun add(x: FieldElement, b: FieldElement, q: PrimeDomainParameters): FieldElement {
        return FieldElement(modAdd(x.value, b.value, q).toBigInt())
    }

    public fun addOne(x: FieldElement, q: PrimeDomainParameters): FieldElement {
        var x2 = x.value.add(BigInt.one)
        if (x2.compareTo(q.p.value).isEqual()) {
            x2 = BigInt.zero
        }
        return FieldElement(x2.toBigInt())
    }

    public fun subtract(x: FieldElement, b: FieldElement, q: PrimeDomainParameters): FieldElement {
        return FieldElement(modSubtract(x.value, b.value, q).toBigInt())
    }

    public fun multiply(x: FieldElement, b: FieldElement, q: PrimeDomainParameters): FieldElement {
        return FieldElement(modMult(x.value, b.value, q).toBigInt())
    }

    public fun divide(x: FieldElement, b: FieldElement, q: PrimeDomainParameters): FieldElement {
        return FieldElement(modMult(x.value, modInverse(b.value, q), q).toBigInt())
    }

    internal fun modInverse(x: AbstractBigInt<*>, q: PrimeDomainParameters): AbstractBigInt<*> {
        val bits: Int = q.p.value.bitLength
        val len = (bits + 31) shr 5
        val p: IntArray = natFromBigInteger(bits, q.p.value)
        val n: IntArray = natFromBigInteger(bits, x)
        val z: IntArray = natCreate(len)
        Mod.invert(p, n, z)
        return Nat.toBigInteger(len, z)
    } // org.bouncycastle.math.raw.

    internal fun modMult(x1: AbstractBigInt<*>, x2: AbstractBigInt<*>, q: PrimeDomainParameters): AbstractBigInt<*> {
        return modReduce(x1.multiply(x2), q)
    }

    internal fun modReduce(x: AbstractBigInt<*>, q: PrimeDomainParameters): AbstractBigInt<*> {
        /*var x = x_
        if (r_ != null) {
            val negative: Boolean = x.sigNum.isNegative()
            if (negative) {
                x = x.abs().toBigInt()
            }
            val qLen: Int = q.p.value.bitLength
            val rIsOne = r_ == BigInt.zero
            while (x.bitLength > (qLen + 1)) {
                var u = x.shiftRight(qLen)
                val v = x.subtract(u.shiftLeft(qLen))
                if (!rIsOne) {
                    u = u.multiply(r_)
                }
                x = u.add(v)
            }
            while (x.compareTo(q.p.value).isGreaterOrEqual()) {
                x = x.subtract(q.p.value)
            }
            if (negative && x.sigNum.isNonZero()) {
                x = q.p.value.subtract(x)
            }
        } else{
            x = x.mod(q.p.value)
        }
        return x*/
        return x.mod(q.p.value)
    }

    internal fun modAdd(x1: AbstractBigInt<*>, x2: AbstractBigInt<*>, q: PrimeDomainParameters): AbstractBigInt<*> {
        var x3 = x1.add(x2)
        if (x3.compareTo(q.p.value).isGreaterOrEqual()) {
            x3 = x3.subtract(q.p.value)
        }
        return x3
    }

    internal fun modSubtract(x1: AbstractBigInt<*>, x2: AbstractBigInt<*>, q: PrimeDomainParameters): AbstractBigInt<*> {
        var x3 = x1.subtract(x2)
        if (x3.sigNum.isNegative()) {
            x3 = x3.add(q.p.value)
        }
        return x3
    }

    public fun natCreate(len: Int): IntArray {
        return IntArray(len)
    }

    public fun natFromBigInteger(bits: Int, x_: AbstractBigInt<*>): IntArray {
        var x = x_
        if (x.sigNum.isNegative() || x.bitLength > bits) error("")

        val len = (bits + 31) shr 5
        val z: IntArray = natCreate(len)

        // NOTE: Use a fixed number of loop iterations
        for (i in 0 until len) {
            z[i] = x.toLong().toInt()
            x = x.shiftRight(32)
        }
        return z
    }

    public fun invert(p: IntArray, x: IntArray?, z: IntArray?) {
        val len = p.size
        if (Nat.isZero(len, x)) {
            error("'x' cannot be 0")
        }
        if (Nat.isOne(len, x)) {
            java.lang.System.arraycopy(x, 0, z, 0, len)
            return
        }

        val u: IntArray = Nat.copy(len, x)
        val a: IntArray = Nat.create(len)
        a[0] = 1
        var ac = 0

        if ((u[0] and 1) == 0) {
            ac = Mod.inversionStep(p, u, len, a, ac)
        }
        if (Nat.isOne(len, u)) {
            Mod.inversionResult(p, ac, a, z)
            return
        }

        val v: IntArray = Nat.copy(len, p)
        val b: IntArray = Nat.create(len)
        var bc = 0

        var uvLen = len

        while (true) {
            while (u[uvLen - 1] == 0 && v[uvLen - 1] == 0) {
                --uvLen
            }

            if (Nat.gte(uvLen, u, v)) {
                Nat.subFrom(uvLen, v, u)
                //              assert (u[0] & 1) == 0;
                ac += Nat.subFrom(len, b, a) - bc
                ac = Mod.inversionStep(p, u, uvLen, a, ac)
                if (Nat.isOne(uvLen, u)) {
                    Mod.inversionResult(p, ac, a, z)
                    return
                }
            } else {
                Nat.subFrom(uvLen, u, v)
                //              assert (v[0] & 1) == 0;
                bc += Nat.subFrom(len, a, b) - ac
                bc = Mod.inversionStep(p, v, uvLen, b, bc)
                if (Nat.isOne(uvLen, v)) {
                    Mod.inversionResult(p, bc, b, z)
                    return
                }
            }
        }
    }
}