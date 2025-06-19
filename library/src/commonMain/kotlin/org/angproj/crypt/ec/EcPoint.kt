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

import org.angproj.big.BigInt

public data class EcPoint(
    override val x: BigInt,
    override val y: BigInt
): Point {
    public companion object {
        public val nullEcPoint: EcPoint by lazy {
            EcPoint(BigInt.nullObject, BigInt.nullObject)
        }
    }
}

public fun EcPoint.isNull(): Boolean = EcPoint.nullEcPoint === this
