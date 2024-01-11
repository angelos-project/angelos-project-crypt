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
import org.angproj.crypt.SignatureGenerationEngine
import org.angproj.crypt.ecc.EccPoint
import org.angproj.crypt.ecc.EccPrivateKey
import org.angproj.crypt.ecc.EccSignature
import org.angproj.crypt.ecc.JacobianMath
import org.angproj.crypt.number.*
import org.angproj.crypt.sha.Sha256Hash


public class EcdsaSign: SignatureGenerationEngine<EccPrivateKey, EccSignature> {

    private val algo = Sha256Hash.create()

    override fun update(messagePart: ByteArray) { algo.update(messagePart) }

    override fun final(privKey: EccPrivateKey): EccSignature {
        val curve = privKey.curve
        val rand = BigInt.randomBetween(BigInt.one, curve.n)
        val r = JacobianMath.multiply(
            EccPoint(curve.Gc), rand, curve.n, curve.a, curve.p).x.mod(curve.n).toBigInt()
        val s = bigIntOf(algo.final()).add(
            r.multiply(privKey.secret)).multiply(JacobianMath.inv(rand, curve.n)).mod(curve.n).toBigInt()
        return EccSignature(r, s)
    }

    override val type: String
        get() = TODO("Not yet implemented")
}