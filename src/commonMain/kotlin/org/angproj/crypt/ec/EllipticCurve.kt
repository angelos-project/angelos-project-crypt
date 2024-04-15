/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 * Copyright (c) 2019 Stark Bank S.A.
 *
 * This software is available under the terms of the MIT license.
 * The legal terms are attached to the LICENSE file and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Rafael Stark - original implementation
 *      Dalton Menezes - original implementation
 *      Caio Dottori - original implementation
 *      Thales Mello - original implementation
 *      Kristoffer Paulsson - additions
 */
package org.angproj.crypt.ec

import org.angproj.aux.num.BigInt
import org.angproj.crypt.num.*
import org.angproj.crypt.sec.Curves
import org.angproj.crypt.sec.PrimeDomainParameters

public object EllipticCurve {
    public fun isPointOnCurve(curve: Curves<PrimeDomainParameters>, p: EcPoint): Boolean {
        val dp = curve.domainParameters
        if (p.x.compareTo(Jacobian.zero) < 0) {
            return false
        }
        if (p.x.compareTo(dp.p) >= 0) {
            return false
        }
        if (p.y.compareTo(Jacobian.zero) < 0) {
            return false
        }
        if (p.y.compareTo(dp.p) >= 0) {
            return false
        }

        return p.y.pow(2).subtract(p.x.pow(3).add(dp.a.multiply(p.x)).add(dp.b)).mod(dp.p).toInt() == 0
    }

    public fun isPointAtInfinity(point: EcPoint): Boolean {
        return point.y.compareSpecial(Jacobian.zero).isEqual()
    }

    public fun generatePrivateKey(curve: Curves<PrimeDomainParameters>): EcPrivateKey = EcPrivateKey(
        BigInt.between(Jacobian.one, curve.domainParameters.n),
        curve
    )

    public fun importPrivateKey(curve: Curves<PrimeDomainParameters>, secret: BigInt): EcPrivateKey {
        // Add private key checks in relation to curve!?
        return EcPrivateKey(secret, curve)
    }


    public fun derivePublicKeyFrom(privKey: EcPrivateKey): EcPublicKey {
        val dp = privKey.curve.domainParameters
        return EcPublicKey(
            Jacobian.multiply(dp.G, privKey.secret, dp.n, dp.a, dp.p),
            privKey.curve
        )
    }

    public fun importPublicKeyFrom(curve: Curves<PrimeDomainParameters>, point: EcPoint): EcPublicKey {
        require(isPointAtInfinity(point)) { "Point is at infinity!" }
        require(isPointOnCurve(curve, point)) { "Point is not on curve!" }
        return EcPublicKey(point, curve)
    }
}