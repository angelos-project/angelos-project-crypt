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
 *      Kristoffer Paulsson - adaption and additions
 */
package org.angproj.crypt.dsa

import org.angproj.big.compareTo
import org.angproj.big.mod
import org.angproj.big.multiply
import org.angproj.big.unsignedBigIntOf
import org.angproj.crypt.Hash
import org.angproj.crypt.SignatureVerificationEngine
import org.angproj.crypt.ec.EcPublicKey
import org.angproj.crypt.ec.EcSignature
import org.angproj.crypt.ec.Jacobian
import org.angproj.crypt.sec.Curves
import org.angproj.crypt.sec.PrimeDomainParameters
import kotlin.math.min


public class EcdsaVerify(
    private val curve: Curves<PrimeDomainParameters>,
    private val hash: Hash
) : SignatureVerificationEngine<EcPublicKey, EcSignature> {

    private val algo = hash.create()

    override fun update(messagePart: ByteArray) { algo.update(messagePart) }

    override fun final(pubKey: EcPublicKey, signature: EcSignature): Boolean {
        require(pubKey.curve.name == curve.name) { "Public key not valid for curve ${curve.name}" }
        check(Ecdsa.isPointOnCurve(curve, pubKey.point)) { "Public key point not found on the curve ${curve.name}." }
        val dp = curve.domainParameters

        val hashMessage: ByteArray = algo.final()
        val numberMessage = unsignedBigIntOf(hashMessage.copyOf(min(hash.messageDigestSize, curve.digestSize)))
        val r = signature.r
        val s = signature.s

        if (r.compareTo(Jacobian.one) < 0) {
            return false
        }
        if (r.compareTo(dp.n) >= 0) {
            return false
        }
        if (s.compareTo(Jacobian.one) < 0) {
            return false
        }
        if (s.compareTo(dp.n) >= 0) {
            return false
        }

        val w = Jacobian.inv(s, dp.n)
        val u1 = Jacobian.multiply(dp.G, numberMessage.multiply(w).mod(dp.n), dp.n, dp.a, dp.p)
        val u2 = Jacobian.multiply(pubKey.point, r.multiply(w).mod(dp.n), dp.n, dp.a, dp.p)
        val v = Jacobian.add(u1, u2, dp.a, dp.p)
        if (Ecdsa.isPointAtInfinity(v)) {
            return false
        }

        return v.x.mod(dp.n) == r
    }

    override val type: String
        get() = "${curve.name}-${algo.type}"
}