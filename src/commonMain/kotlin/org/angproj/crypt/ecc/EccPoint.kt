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

import org.angproj.crypt.number.BigInt
import org.angproj.crypt.sec.SecDomainParameters

public class EccPoint (
    public val x: BigInt,
    public val y: BigInt,
    public val z: BigInt = BigInt.zero
) {
    public constructor(coordinates: Pair<BigInt, BigInt>): this(coordinates.first, coordinates.second)

    public fun isAtInfinity(): Boolean = this.y.equals(BigInt.zero)
}

public fun SecDomainParameters.getCoord(): EccPoint = EccPoint(this.Gc)