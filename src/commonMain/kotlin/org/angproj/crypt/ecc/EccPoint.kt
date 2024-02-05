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
package org.angproj.crypt.ecc

import org.angproj.aux.num.BigInt
import org.angproj.crypt.sec.EllipticCurvePoint
import org.angproj.crypt.sec.PrimeDomainParameters

public class EccPoint (
    public val x: BigInt,
    public val y: BigInt,
    public val z: BigInt = BigInt.zero
) {
    public constructor(coordinates: EllipticCurvePoint): this(coordinates.x.value, coordinates.y.value)

    public fun isAtInfinity(): Boolean = this.y.equals(BigInt.zero)
}

public fun PrimeDomainParameters.getCoord(): EccPoint = EccPoint(this.G)