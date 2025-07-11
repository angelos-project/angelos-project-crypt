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
package org.angproj.crypt.c

import org.angproj.big.BigInt

public interface ECDomainParameters {

    public val hInv: BigInt

    public fun getCurve(): ECCurve

    public val G: ECPoint

    public val N: BigInt

    public val H: BigInt

    public val seed: ByteArray

    //public fun equals(obj: Any): Boolean

    public override fun hashCode(): Int

    public fun validatePrivateScalar(d: BigInt): BigInt

    public fun validatePublicPoint(q: ECPoint): ECPoint
}
