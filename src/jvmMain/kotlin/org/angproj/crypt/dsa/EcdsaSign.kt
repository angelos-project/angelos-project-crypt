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

import org.angproj.crypt.Hash
import org.angproj.crypt.SignatureGenerationEngine
import org.angproj.crypt.ellipticcurve.Curve
import org.angproj.crypt.ellipticcurve.Math
import org.angproj.crypt.ellipticcurve.PrivateKey
import org.angproj.crypt.ellipticcurve.Signature
import org.angproj.crypt.ellipticcurve.utils.BinaryAscii
import org.angproj.crypt.ellipticcurve.utils.RandomInteger
import org.angproj.crypt.number.*
import kotlin.math.min
import java.math.BigInteger

public class EcdsaSign(private val curve: Curve, private val hash: Hash): SignatureGenerationEngine<PrivateKey, Signature> {

    private val algo = hash.create()

    override fun update(messagePart: ByteArray) { algo.update(messagePart) }

    override fun final(privKey: PrivateKey): Signature {
        require(privKey.curve.name == curve.name) { "Private key not valid for curve ${curve.name}" }

        val hashMessage: ByteArray = algo.final()
        val numberMessage = BinaryAscii.numberFromString(hashMessage.copyOf(min(hash.messageDigestSize, curve.digestSize)))
        val curve: Curve = privKey.curve
        val randNum = RandomInteger.between(BigInteger.ONE, curve.N)
        val randomSignPoint = Math.multiply(curve.G, randNum, curve.N, curve.A, curve.P)
        val r = randomSignPoint.x.mod(curve.N)
        val s = numberMessage.add(r.multiply(privKey.secret)).multiply(Math.inv(randNum, curve.N)).mod(curve.N)

        return Signature(r, s)
    }

    override val type: String
        get() = "${curve.name}-${algo.type}"
}