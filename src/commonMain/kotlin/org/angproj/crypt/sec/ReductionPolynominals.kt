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
import org.angproj.crypt.number.plus
import org.angproj.crypt.number.pow
import org.angproj.crypt.number.times

public enum class ReductionPolynominals(public val bits: Int) {
    F163(163),
    F233(233),
    F239(239),
    F283(283),
    F409(409),
    F571(571);

    public companion object {

        /**
         * f(x)=x^163 + x^7 + x^6 + x^3 +1
         * */
        private fun f_163(a: BigInt = BigInt.one): BigInt = (
                a * BigInt.two.pow(163) + a * BigInt.two.pow(7) +
                        a * BigInt.two.pow(3) + a).toBigInt()

        /**
         * f(x)=x^233 + x^74 +1
         * */
        private fun f_233(a: BigInt = BigInt.one): BigInt = (
                a * BigInt.two.pow(233) + a * BigInt.two.pow(74) + a).toBigInt()

        /**
         * f(x)=x^239 + x^36 + 1 or x^239 + x^158 +1
         * */
        private fun f_239(a: BigInt = BigInt.one): BigInt = (
                a * BigInt.two.pow(239) + a * BigInt.two.pow(36) + a).toBigInt()

        /**
         * f(x)=x^283 + x^12 + x^7 + x^5 + 1
         * */
        private fun f_283(a: BigInt = BigInt.one): BigInt = (
                a * BigInt.two.pow(283) + a * BigInt.two.pow(12) +
                        a * BigInt.two.pow(7) + a * BigInt.two.pow(5) + a).toBigInt()

        /**
         * f(x)=x^409 + x^87 + 1
         * */
        private fun f_409(a: BigInt = BigInt.one): BigInt = (
                a * BigInt.two.pow(409) + a * BigInt.two.pow(87) + a).toBigInt()

        /**
         * f(x)=x^571 + x^10 + x^5 + x^2 + 1
         * */
        private fun f_571(a: BigInt = BigInt.one): BigInt = (
                a * BigInt.two.pow(571) + a * BigInt.two.pow(10) +
                        a * BigInt.two.pow(5) + a * BigInt.two.pow(2) + a).toBigInt()

        /**
         * sec1-v2.pdf -- 2.1.2 The Finite Field F2m -- Table 1
         * */
        public fun fx(m: ReductionPolynominals, a: BigInt): BigInt = when (m) {
            F163 -> f_163(a)
            F233 -> f_233(a)
            F239 -> f_239(a)
            F283 -> f_283(a)
            F409 -> f_409(a)
            F571 -> f_571(a)
        }
    }
}