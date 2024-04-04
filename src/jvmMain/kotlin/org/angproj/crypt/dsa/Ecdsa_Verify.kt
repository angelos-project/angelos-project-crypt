/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 * Copyright (c) 2019 Stark Bank S.A.
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
 *      Kristoffer Paulsson - initial implementation and merging from starbank-ecdsa
 */
package org.angproj.crypt.dsa

import kotlin.math.min
import org.angproj.crypt.Hash
import org.angproj.crypt.SignatureVerificationEngine
import org.angproj.crypt.ellipticcurve.Curve
import org.angproj.crypt.ellipticcurve.Math
import org.angproj.crypt.ellipticcurve.PublicKey
import org.angproj.crypt.ellipticcurve.Signature
import org.angproj.crypt.ellipticcurve.utils.BinaryAscii
import java.math.BigInteger


public class Ecdsa_Verify(private val curve: Curve, private val hash: Hash) : SignatureVerificationEngine<PublicKey, Signature> {

    private val algo = hash.create()

    override fun update(messagePart: ByteArray) { algo.update(messagePart) }

    override fun final(pubKey: PublicKey, signature: Signature): Boolean {
        require(pubKey.curve.name == curve.name) { "Public key not valid for curve ${curve.name}" }
        check(curve.contains(pubKey.point)) { "Public key point not found on the curve ${curve.name}." }

        val hashMessage: ByteArray = algo.final()
        val numberMessage = BinaryAscii.numberFromString(hashMessage.copyOf(min(hash.messageDigestSize, curve.digestSize)))
        val curve: Curve = pubKey.curve
        val r = signature.r
        val s = signature.s

        if (r.compareTo(BigInteger(1.toString())) < 0) {
            return false
        }
        if (r.compareTo(curve.N) >= 0) {
            return false
        }
        if (s.compareTo(BigInteger(1.toString())) < 0) {
            return false
        }
        if (s.compareTo(curve.N) >= 0) {
            return false
        }

        val w = Math.inv(s, curve.N)
        val u1 = Math.multiply(curve.G, numberMessage.multiply(w).mod(curve.N), curve.N, curve.A, curve.P)
        val u2 = Math.multiply(pubKey.point, r.multiply(w).mod(curve.N), curve.N, curve.A, curve.P)
        val v = Math.add(u1, u2, curve.A, curve.P)
        if (v.isAtInfinity) {
            return false
        }

        return v.x.mod(curve.N) == r
    }

    override val type: String
        get() = "${curve.name}-${algo.type}"
}