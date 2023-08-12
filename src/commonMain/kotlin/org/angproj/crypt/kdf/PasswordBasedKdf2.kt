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
package org.angproj.crypt.kdf

import org.angproj.aux.util.swapEndian
import org.angproj.aux.util.writeIntAt
import org.angproj.crypt.Hash
import org.angproj.crypt.Pbkd
import org.angproj.crypt.hmac.HmacKey
import org.angproj.crypt.hmac.KeyHashedMac
import org.angproj.crypt.sha.Sha256Hash
import kotlin.math.ceil

class PasswordBasedKdf2(private val algo: Hash, val keySize: Int, val count: Int = 1000): PasswordBasedKeyDerivation {

    init {
        require(keySize >= 14) { "Key length to small" }
        require(keySize < Int.MAX_VALUE - 1) { "Key length to big" }
    }

    private val len = ceil(keySize.toDouble() / algo.messageDigestSize.toDouble()).toInt()
    private val r = keySize - (len - 1) * algo.messageDigestSize

    override fun newKey(password: ByteArray, salt: ByteArray): ByteArray {
        //require(salt.size >= 16) { "The salt must be at least 128 bits" }

        val mk = ByteArray(keySize)
        val prfPrep = KeyHashedMac.prepareHmacKey(password, algo)

        (1..len).forEach { i ->
            val t = ByteArray(algo.messageDigestSize)
            var u = initU(salt, i)
            (1..count).forEach { _ ->
                u = prf(prfPrep, u)
                t.indices.forEach { k -> t[k] = (t[k].toInt() xor u[k].toInt()).toByte() }
            }
            t.copyInto(mk, (i-1) * algo.messageDigestSize, 0, r)
        }
        return mk.copyOfRange(0, keySize)
    }

    override val type: String
        get() = "PBKDF2-${KeyHashedMac.name}-${algo.name}"

    companion object: Pbkd {
        override val name = "PBKDF2"

        override fun create(): PasswordBasedKeyDerivation = create(Sha256Hash, Sha256Hash.messageDigestSize, 1)

        override fun create(algo: Hash, keySize: Int, count: Int): PasswordBasedKeyDerivation = PasswordBasedKdf2(algo, keySize, count)

        private fun prf(prep: HmacKey, u: ByteArray): ByteArray {
            val prf = KeyHashedMac(prep)
            prf.update(u)
            return prf.final()
        }

        private fun initU(s: ByteArray, i: Int): ByteArray {
            val u = ByteArray(s.size + Int.SIZE_BYTES)
            s.copyInto(u)
            u.writeIntAt(s.size, i.swapEndian())
            return u
        }
    }
}