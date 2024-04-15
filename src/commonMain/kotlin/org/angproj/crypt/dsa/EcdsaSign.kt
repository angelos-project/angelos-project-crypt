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

import org.angproj.crypt.num.*
import org.angproj.crypt.Hash
import org.angproj.crypt.SignatureGenerationEngine
import org.angproj.crypt.ec.EcPrivateKey
import org.angproj.crypt.ec.EcSignature
import org.angproj.crypt.ec.Jacobian
import org.angproj.aux.num.*
import org.angproj.crypt.sec.Curves
import org.angproj.crypt.sec.PrimeDomainParameters
import kotlin.math.min


public class EcdsaSign(
    private val curve: Curves<PrimeDomainParameters>,
    private val hash: Hash
): SignatureGenerationEngine<EcPrivateKey, EcSignature> {

    private val algo = hash.create()

    override fun update(messagePart: ByteArray) { algo.update(messagePart) }

    override fun final(privKey: EcPrivateKey): EcSignature {
        require(privKey.curve.name == curve.name) { "Private key not valid for curve ${curve.name}" }
        val dp = curve.domainParameters

        val hashMessage: ByteArray = algo.final()
        val numberMessage = unsignedBigIntOf(hashMessage.copyOf(min(hash.messageDigestSize, curve.digestSize)))
        val randNum = BigInt.between(Jacobian.one, dp.n)
        val randomSignPoint = Jacobian.multiply(dp.G, randNum, dp.n, dp.a, dp.p)
        val r = randomSignPoint.x.mod(dp.n)
        val s = numberMessage.add(r.multiply(privKey.secret)).multiply(Jacobian.inv(randNum, dp.n)).mod(dp.n)

        return EcSignature(r.toBigInt(), s.toBigInt())
    }

    override val type: String
        get() = "${curve.name}-${algo.type}"
}