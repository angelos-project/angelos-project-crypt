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
package org.angproj.crypt.ec

import org.angproj.aux.num.BigInt
import org.angproj.aux.num.bigInt
import org.angproj.aux.util.NullObject

public data class EcPoint(
    public val x: BigInt,
    public val y: BigInt,
    public val z: BigInt = BigInt.zero
) {
    public fun isAtInfinity(): Boolean = y.compareTo(BigInt.zero).isEqual()
}

public fun EcPoint.isNull(): Boolean = NullObject.ecPoint === this

private val nullEcPoint = EcPoint(NullObject.bigInt, NullObject.bigInt, NullObject.bigInt)
public val NullObject.ecPoint: EcPoint
    get() = nullEcPoint