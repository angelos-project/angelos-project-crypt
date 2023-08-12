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
package org.angproj.crypt.hmac

import org.angproj.crypt.Hash
import org.angproj.crypt.Hmac
import org.angproj.crypt.sha.Sha512Hash


class KeyHashedMac(prepared: HmacKey): AbstractHmacEngine() {

    constructor(key: ByteArray, algo: Hash): this(prepareHmacKey(key, algo))

    private val inner = prepared.algo.create()
    private val outer = prepared.algo.create()

    init {
        inner.update(prepared.iPadKey)
        outer.update(prepared.oPadKey)
    }

    override fun update(messagePart: ByteArray) = inner.update(messagePart)

    override fun final(): ByteArray {
        outer.update(inner.final())
        return outer.final()
    }

    override val type: String
        get() = "HMAC-${inner.type}"

    companion object: Hmac {
        override val name = "HMAC"
        override fun create(): KeyHashedMac = create(ByteArray(0), Sha512Hash)

        override fun create(key: ByteArray, algo: Hash) = KeyHashedMac(key, algo)

        private fun determineKey(key: ByteArray, algo: Hash): ByteArray = when {
            key.size == algo.blockSize -> key
            key.size > algo.blockSize -> {
                val k0 = algo.create()
                k0.update(key)
                k0.final().copyOf(algo.blockSize)
            }
            key.size < algo.blockSize -> key.copyOf(algo.blockSize)
            else -> error("Could not determine key")
        }

        private fun pad(filler: Byte, algo: Hash): ByteArray {
            val c = ByteArray(algo.blockSize)
            c.fill(filler)
            return c
        }

        private fun padKey(k0: ByteArray, pad: ByteArray, algo: Hash): ByteArray {
            val xPad = ByteArray(algo.blockSize)
            xPad.indices.forEachIndexed { idx, _ -> xPad[idx] = (k0[idx].toInt() xor pad[idx].toInt()).toByte() }
            return xPad
        }

        fun prepareHmacKey(key: ByteArray, algo: Hash): HmacKey {
            val key0 = determineKey(key, algo)
            return HmacKey(Triple(
                padKey(key0, pad(0x36, algo), algo),
                padKey(key0, pad(0x5c, algo), algo),
                algo
            ))
        }
    }
}