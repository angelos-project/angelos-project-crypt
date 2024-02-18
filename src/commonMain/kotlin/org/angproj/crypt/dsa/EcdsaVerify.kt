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
package org.angproj.crypt.dsa

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.bigIntOf
import org.angproj.crypt.SignatureVerificationEngine
import org.angproj.crypt.ecc.EccPoint
import org.angproj.crypt.ecc.EccPublicKey
import org.angproj.crypt.ecc.EccSignature
import org.angproj.crypt.ecc.JacobianMath
import org.angproj.crypt.number.mod
import org.angproj.crypt.number.multiply
import org.angproj.crypt.sha.Sha256Hash


public class EcdsaVerify : SignatureVerificationEngine<EccPublicKey, EccSignature> {

    private val algo = Sha256Hash.create()

    override fun update(messagePart: ByteArray) { algo.update(messagePart) }

    override fun final(pubKey: EccPublicKey, signature: EccSignature): Boolean {
        /*val curve = pubKey.curve
        val r = signature.r
        val s = signature.s

        if (r.compareTo(BigInt.one).isLesser()) {
            return false
        }
        if (!r.compareTo(curve.n).isLesser()) {
            return false
        }
        if (s.compareTo(BigInt.one).isLesser()) {
            return false
        }
        if (!s.compareTo(curve.n).isLesser()) {
            return false
        }

        val w = JacobianMath.inv(s, curve.n)
        val u1 = JacobianMath.multiply(
            EccPoint(curve.G),
            bigIntOf(algo.final()).multiply(w).mod(curve.n).toBigInt(),
            curve.n, curve.a.value, curve.p
        )
        val u2 = JacobianMath.multiply(
            pubKey.point,
            r.multiply(w).mod(curve.n).toBigInt(),
            curve.n, curve.a.value, curve.p
        )
        val v = JacobianMath.add(u1, u2, curve.a.value, curve.p)
        return when (v.isAtInfinity()) {
            true -> false
            else -> { v.x.mod(curve.n).equals(r) }
        }*/
        TODO("FIX")
    }

    override val type: String
        get() = TODO("Not yet implemented")
}