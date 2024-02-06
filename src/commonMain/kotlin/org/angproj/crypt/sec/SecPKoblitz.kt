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
package org.angproj.crypt.sec

public interface SecPKoblitz: Curves<PrimeDomainParameters> {
    public val p: OctetString

    public companion object {
        public fun build(curve: SecPKoblitz): PrimeDomainParameters = PrimeDomainParameters(
            Conversion.octetString2integer(curve.p)
        ).also { q ->
            q.setup(
                Conversion.octetString2fieldElement(curve.a, q),
                Conversion.octetString2fieldElement(curve.b, q),
                Conversion.octetString2ellipticCurvePoint(curve.G, q),
                Conversion.octetString2fieldElement(curve.n, q),
                Conversion.octetString2fieldElement(curve.h, q)
            )
        }
    }
}