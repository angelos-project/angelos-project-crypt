/**
 * Copyright (c) 2019 Stark Bank S.A.
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
 *      Stark Bank S.A. - original implementation in Java
 *      Kristoffer Paulsson - port to Kotlin/Common and adaption
 *
 * Original code:
 *      https://github.com/starkbank/ecdsa-java/blob/master/src/main/java/com/starkbank/ellipticcurve/Math.java
 *      https://github.com/starkbank/ecdsa-java/blob/master/LICENSE
 */
package org.angproj.crypt.ecc

import org.angproj.crypt.number.*

public object JacobianMath {
    /**
     * Fast way to multiply point and scalar in elliptic curves
     *
     * @param p First Point to multiply
     * @param n Scalar to multiply
     * @param N Order of the elliptic curve
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point that represents the sum of First and Second Point
     */
    public fun multiply(
        p: EccPoint, n: BigInt, N: BigInt, A: BigInt, P: BigInt
    ): EccPoint {
        return eccFrom(eccMultiply(eccTo(p), n, N, A, P), P)
    }

    /**
     * Fast way to add two points in elliptic curves
     *
     * @param p First Point you want to add
     * @param q Second Point you want to add
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point that represents the sum of First and Second Point
     */
    public fun add(p: EccPoint, q: EccPoint, A: BigInt, P: BigInt): EccPoint {
        return eccFrom(eccAdd(eccTo(p), eccTo(q), A, P), P)
    }

    /**
     * Extended Euclidean Algorithm. It's the 'division' in elliptic curves
     *
     * @param x Divisor
     * @param n Mod for division
     * @return Value representing the division
     */
    public fun inv(x: BigInt, n: BigInt): BigInt {
        if (x.compareTo(BigInt.zero).isEqual()) {
            return BigInt.zero
        }
        var lm = BigInt.one
        var hm = BigInt.zero
        var high = n
        var low = x.mod(n)
        while (low.compareTo(BigInt.one).isGreater()) {
            val r = high.divide(low)
            val nm = hm.subtract(lm.multiply(r))
            val nw = high.subtract(low.multiply(r))
            high = low.toBigInt()
            hm = lm
            low = nw
            lm = nm.toBigInt()
        }
        return lm.mod(n).toBigInt()
    }

    /**
     * Convert point to Jacobian coordinates
     *
     * @param p the point you want to transform
     * @return Point in Jacobian coordinates
     */
    public fun eccTo(p: EccPoint): EccPoint {
        return EccPoint(p.x, p.y, BigInt.one)
    }

    /**
     * Convert point back from Jacobian coordinates
     *
     * @param p the point you want to transform
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point in default coordinates
     */
    public fun eccFrom(p: EccPoint, P: BigInt): EccPoint {
        val z = inv(p.z, P)
        val x = p.x.multiply(z.pow(2)).mod(P)
        val y = p.y.multiply(z.pow(3)).mod(P)
        return EccPoint(x.toBigInt(), y.toBigInt(), BigInt.zero)
    }

    /**
     * Double a point in elliptic curves
     *
     * @param p the point you want to transform
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return the result point doubled in elliptic curves
     */
    public fun eccDouble(p: EccPoint, A: BigInt, P: BigInt): EccPoint {
        if (p.y.equals(BigInt.zero)) {
            return EccPoint(BigInt.zero, BigInt.zero, BigInt.zero)
        }
        val ysq = p.y.pow(2).mod(P)
        val S = bigIntOf(4).multiply(p.x).multiply(ysq).mod(P)
        val M = bigIntOf(3).multiply(p.x.pow(2)).add(A.multiply(p.z.pow(4))).mod(P)
        val nx = M.pow(2).subtract(bigIntOf(2).multiply(S)).mod(P)
        val ny = M.multiply(S.subtract(nx)).subtract(bigIntOf(8).multiply(ysq.pow(2))).mod(P)
        val nz = bigIntOf(2).multiply(p.y).multiply(p.z).mod(P)
        return EccPoint(nx.toBigInt(), ny.toBigInt(), nz.toBigInt())
    }

    /**
     * Add two points in elliptic curves
     *
     * @param p First Point you want to add
     * @param q Second Point you want to add
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point that represents the sum of First and Second Point
     */
    public fun eccAdd(p: EccPoint, q: EccPoint, A: BigInt, P: BigInt): EccPoint {
        if (p.y.equals(BigInt.zero)) {
            return q
        }
        if (q.y.equals(BigInt.zero)) {
            return p
        }
        val U1 = p.x.multiply(q.z.pow(2)).mod(P)
        val U2 = q.x.multiply(p.z.pow(2)).mod(P)
        val S1 = p.y.multiply(q.z.pow(3)).mod(P)
        val S2 = q.y.multiply(p.z.pow(3)).mod(P)
        if (U1.compareTo(U2).isEqual()) {
            if (S1.compareTo(S2).isNotEqual()) {
                return EccPoint(BigInt.zero, BigInt.zero, BigInt.one)
            }
            return eccDouble(p, A, P)
        }
        val H = U2.subtract(U1)
        val R = S2.subtract(S1)
        val H2 = H.multiply(H).mod(P)
        val H3 = H.multiply(H2).mod(P)
        val U1H2 = U1.multiply(H2).mod(P)
        val nx = R.pow(2).subtract(H3).subtract(bigIntOf(2).multiply(U1H2)).mod(P)
        val ny = R.multiply(U1H2.subtract(nx)).subtract(S1.multiply(H3)).mod(P)
        val nz = H.multiply(p.z).multiply(q.z).mod(P)
        return EccPoint(nx.toBigInt(), ny.toBigInt(), nz.toBigInt())
    }

    /**
     * Multiply point and scalar in elliptic curves
     *
     * @param p First Point to multiply
     * @param n Scalar to multiply
     * @param N Order of the elliptic curve
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point that represents the product of First Point and scalar
     */
    public fun eccMultiply(
        p: EccPoint, n: BigInt, N: BigInt, A: BigInt, P: BigInt
    ): EccPoint = when {
        BigInt.zero.compareTo(p.y).isEqual() || BigInt.zero.compareTo(n).isEqual() -> EccPoint(
            BigInt.zero, BigInt.zero, BigInt.one)
        BigInt.one.compareTo(n).isEqual() -> p
        n.compareTo(BigInt.zero).isLesser() || n.compareTo(N).isGreater() -> eccMultiply(
            p, n.mod(N).toBigInt(), N, A, P)
        n.mod(bigIntOf(2)).compareTo(BigInt.zero).isEqual() -> eccDouble(
            eccMultiply(p, n.divide(bigIntOf(2)).toBigInt(), N, A, P), A, P)
        n.mod(bigIntOf(2)).compareTo(BigInt.one).isEqual() -> eccAdd(
            eccDouble(eccMultiply(p, n.divide(bigIntOf(2)).toBigInt(), N, A, P), A, P), p, A, P)
        else -> error("Must not happen!")
    }
}