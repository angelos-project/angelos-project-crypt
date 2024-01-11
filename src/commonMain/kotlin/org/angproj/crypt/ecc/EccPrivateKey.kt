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
import org.angproj.crypt.number.randomBetween
import org.angproj.crypt.sec.SecPKoblitz


public class EccPrivateKey (
    public val secret: BigInt,
    public val curve: SecPKoblitz
) {

    public constructor(curve: SecPKoblitz) : this (BigInt.randomBetween(BigInt.one, curve.n), curve)

    public fun publicKey(): EccPublicKey {
        return EccPublicKey(
            JacobianMath.multiply(curve.getCoord(), this.secret, curve.n, curve.a, curve.p),
            curve
        )
    }
}