/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 * Copyright (c) 2019 Stark Bank S.A.
 *
 * This software is available under the terms of the MIT license.
 * The legal terms are attached to the LICENSE file and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Rafael Stark - original implementation
 *      Dalton Menezes - original implementation
 *      Caio Dottori - original implementation
 *      Thales Mello - original implementation
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.crypt.ec

import org.angproj.big.*


public object Jacobian {

    public val zero: BigInt = BigInt.zero
    public val one: BigInt = BigInt.one
    public val two: BigInt = BigInt.two
    public val three: BigInt = bigIntOf(3)
    public val four: BigInt = bigIntOf(4)
    public val eight: BigInt = bigIntOf(8)

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
    public fun multiply(p: EcPoint, n: BigInt, N: BigInt, A: BigInt, P: BigInt): EcPoint = fromJacobian(
        jacobianMultiply(toJacobian(p), n, N, A, P), P)

    /**
     * Fast way to add two points in elliptic curves
     *
     * @param p First Point you want to add
     * @param q Second Point you want to add
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point that represents the sum of First and Second Point
     */

    public fun add(p: EcPoint, q: EcPoint, A: BigInt, P: BigInt): EcPoint = fromJacobian(
        jacobianAdd(toJacobian(p), toJacobian(q), A, P), P)


    /**
     * Extended Euclidean Algorithm. It's the 'division' in elliptic curves
     *
     * @param x Divisor
     * @param n Mod for division
     * @return Value representing the division
     */
    public fun inv(x: BigInt, n: BigInt): BigInt {
        if (x.compareTo(zero) == 0) {
            return zero
        }
        var lm = one
        var hm = zero
        var high = n
        var low = x.mod(n)
        var r: BigInt
        var nm: BigInt
        var nw: BigInt
        while (low.compareTo(one) > 0) {
            r = high.divide(low)
            nm = hm.subtract(lm.multiply(r))
            nw = high.subtract(low.multiply(r))
            high = low
            hm = lm
            low = nw
            lm = nm
        }
        return lm.mod(n)
    }

    /**
     * Convert point to Jacobian coordinates
     *
     * @param p the point you want to transform
     * @return Point in Jacobian coordinates
     */
    public fun toJacobian(p: EcPoint): JacobianPoint = JacobianPoint(p.x, p.y, one)

    /**
     * Convert point back from Jacobian coordinates
     *
     * @param p the point you want to transform
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return Point in default coordinates
     */
    public fun fromJacobian(p: JacobianPoint, P: BigInt): EcPoint {
        val z = inv(p.z, P)
        val x = p.x.multiply(z.pow(2)).mod(P)
        val y = p.y.multiply(z.pow(3)).mod(P)
        return EcPoint(x, y)
    }

    /**
     * Double a point in elliptic curves
     *
     * @param p the point you want to transform
     * @param A Coefficient of the first-order term of the equation Y^2 = X^3 + A*X + B (mod P)
     * @param P Prime number in the module of the equation Y^2 = X^3 + A*X + B (mod P)
     * @return the result point doubled in elliptic curves
     */
    public fun jacobianDouble(p: JacobianPoint, A: BigInt, P: BigInt): JacobianPoint {
        if (p.y.isNull() || p.y == zero) {
            return JacobianPoint(zero, zero, zero)
        }
        val ysq = p.y.pow(2).mod(P)
        val S = four.multiply(p.x).multiply(ysq).mod(P)
        val M  = three.multiply(p.x.pow(2)).add(A.multiply(p.z.pow(4))).mod(P)
        val nx = M.pow(2).subtract(two.multiply(S)).mod(P)
        val ny = M.multiply(S.subtract(nx)).subtract(eight.multiply(ysq.pow(2))).mod(P)
        val nz = two.multiply(p.y).multiply(p.z).mod(P)
        return JacobianPoint(nx, ny, nz)
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
    public fun jacobianAdd(p: JacobianPoint, q: JacobianPoint, A: BigInt, P: BigInt): JacobianPoint {
        if (p.y.isNull() || p.y == zero) {
            return q
        }
        if (q.y.isNull() || q.y == zero) {
            return p
        }
        val U1 = p.x.multiply(q.z.pow(2)).mod(P)
        val U2 = q.x.multiply(p.z.pow(2)).mod(P)
        val S1 = p.y.multiply(q.z.pow(3)).mod(P)
        val S2 = q.y.multiply(p.z.pow(3)).mod(P)
        if (U1.compareTo(U2) == 0) {
            if (S1.compareTo(S2) != 0) {
                return JacobianPoint(zero, zero, one)
            }
            return jacobianDouble(p, A, P)
        }
        val H = U2.subtract(U1)
        val R = S2.subtract(S1)
        val H2 = H.multiply(H).mod(P)
        val H3 = H.multiply(H2).mod(P)
        val U1H2 = U1.multiply(H2).mod(P)
        val nx = R.pow(2).subtract(H3).subtract(two.multiply(U1H2)).mod(P)
        val ny = R.multiply(U1H2.subtract(nx)).subtract(S1.multiply(H3)).mod(P)
        val nz = H.multiply(p.z).multiply(q.z).mod(P)
        return JacobianPoint(nx, ny, nz)
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
    public fun jacobianMultiply(p: JacobianPoint, n: BigInt, N: BigInt, A: BigInt, P: BigInt): JacobianPoint {
        if (zero.compareTo(p.y) == 0 || zero.compareTo(n) == 0) {
            return JacobianPoint(zero, zero, one)
        }
        if (one.compareTo(n) == 0) {
            return p
        }
        if (n.compareTo(zero) < 0 || n.compareTo(N) >= 0) {
            return jacobianMultiply(p, n.mod(N), N, A, P)
        }
        if (n.mod(two).compareTo(zero) == 0) {
            val NUM: BigInt = n.divide(two)
            return jacobianDouble(jacobianMultiply(p, NUM, N, A, P), A, P)
        }
        if (n.mod(two).compareTo(one) == 0) {
            val NUM: BigInt = n.divide(two)
            return jacobianAdd(jacobianDouble(jacobianMultiply(p, NUM, N, A, P), A, P), p, A, P)
        }
        return JacobianPoint.nullJacobianPoint
    }
}
