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
import org.angproj.crypt.number.*

public object Primitives {

    /**
     * sec1-v2.pdf -- 3.1.1.1 Elliptic Curve Domain Parameters over Fp Generation Primitive
     * */
    public fun primeGenerateCurveParameters() {

    }

    /**
     * sec1-v2.pdf -- 3.1.1.2.1 Elliptic Curve Domain Parameters over Fp Validation Primitive
     * */
    /*public fun primeValidateCurveParameters(q: PrimeDomainParameters, t: Int) {
        // #1
        check(q.p.log2() == t * 2L) { "The prime number doesn't match the required security strength." }

        // #2
        check(Convention.primeIsFinite(q.a.value, q)) { "a is not in an integers in the interval [0, p − 1]." }
        check(Convention.primeIsFinite(q.b.value, q)) { "b s not in an integers in the interval [0, p − 1]." }
        check(Convention.primeIsFinite(q.G.x, q)) { "Gx is not in an integers in the interval [0, p − 1]." }
        check(Convention.primeIsFinite(q.G.y, q)) { "Gy is not in an integers in the interval [0, p − 1]." }

        // #3
        check(Convention.primeSatisfy(q)) { "a and b does not satisfy 4 * a^3 + 27 * b^2  ̸≡ 0 (mod p)." }

        // #4
        check(Convention.primeDefiningEquation(q.G, q)) {
            "G not on the elliptic curve definition according to y^2 ≡ x^3 + a * x + b (mod p)." }

        // #5
        // ? Check that n is prime.

        // #6
        check(q.h.compareTo(BigInt.two.pow(t / 8)).isLesserOrEqual()) { "h is not h ≤ 2^(t/8)." }
        check(q.h.compareTo(((q.p.sqrt() + BigInt.one).pow2() / q.n).abs()).isEqual()) { "h is not h = ⌊((√p + 1)^2)/n⌋." }

        // #7
        // ? Check that n * G = O (is at infinity).

        // #8
        check(q.n.compareTo(q.p).isNotEqual()) { "p and n can not be equal." }
        repeat(100) { B -> check(q.p.pow(B).mod(q.n).compareTo(BigInt.one).isNotEqual()) {
            "p^B  ̸≡ 1 (mod n) failed on B = $B." } }

    }*/
}