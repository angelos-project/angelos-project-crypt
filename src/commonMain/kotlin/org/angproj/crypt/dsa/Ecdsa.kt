/**
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
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.crypt.dsa

import org.angproj.aux.num.BigInt
import org.angproj.crypt.ec.EcPoint
import org.angproj.crypt.number.*
import org.angproj.crypt.number.multiply
import org.angproj.crypt.sec.Curves
import org.angproj.crypt.sec.PrimeDomainParameters


// https://csrc.nist.gov/files/pubs/fips/186-3/final/docs/fips_186-3.pdf
// https://datatracker.ietf.org/doc/html/rfc6979


// https://csrc.nist.gov/Projects/Cryptographic-Algorithm-Validation-Program/Secure-Hashing
// https://csrc.nist.gov/Projects/Cryptographic-Algorithm-Validation-Program/Digital-Signatures

// https://github.com/starkbank/ecdsa-java

// https://github.com/carterharrison/ecdsa-kotlin.git

public object Ecdsa {
    public fun pointOnCurve(curve: Curves<PrimeDomainParameters>, point: EcPoint): Boolean {
        val dp = curve.domainParameters
        if (point.x.compareTo(BigInt.zero).state < 0) {
            println("X Lesser than 0")
            return false
        }
        if (point.x.compareTo(dp.p).state >= 0) {

            return false
        }
        if (point.y.compareTo(BigInt.zero).state < 0) {
            println("Y Lesser than 0")
            return false
        }
        if (point.y.compareTo(dp.p).state >= 0) {
            println("Y Larger than prime")
            return false
        }
        println(point.y.pow(2).subtract(point.x.pow(3).add(dp.a.multiply(point.x)).add(dp.b)).mod(dp.p).toLong())
        return point.y.pow(2).subtract(point.x.pow(3).add(dp.a.multiply(point.x)).add(dp.b)).mod(dp.p).toLong() == 0L
        // p.y.pow(2).subtract(p.x.pow(3).add(A.multiply(p.x)).add(B)).mod(P).intValue() == 0;
    }
}