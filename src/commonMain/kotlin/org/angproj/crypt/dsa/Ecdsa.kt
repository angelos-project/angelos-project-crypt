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
    public fun isPointOnCurve(curve: Curves<PrimeDomainParameters>, point: EcPoint): Boolean {
        val dp = curve.domainParameters
        return when {
            point.x.compareTo(BigInt.zero).isLesser() -> false
            point.x.compareTo(dp.p).isGreater() -> false
            point.y.compareTo(BigInt.zero).isLesser() -> false
            point.y.compareTo(dp.p).isGreater() -> false
            else -> point.y.pow(2).subtract(
                point.x.pow(3).add(dp.a.multiply(point.x)).add(dp.b)
            ).mod(dp.p).compareTo(BigInt.zero).isEqual()
        }
    }
}