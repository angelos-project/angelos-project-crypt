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
package org.angproj.crypt.kp

import org.angproj.crypt.c.KeyParameter
import org.angproj.crypt.c.Mac
import kotlin.math.max

public class PaulssonMac: Mac {

    private val keyDigest = ByteArray(macSize) { 0x01 }
    private val messageDigest = PaulssonDigest()

    override fun setup(params: KeyParameter) {
        check(params.keyLength >= 8) { "Key must be at least eight bytes" }
        check( params.key.sum() != 0) { "Key must not have zero value" }

        val digest = PaulssonDigest()
        val key = params.key + ByteArray(max(0, digest.digestSize - params.keyLength)) { 0x01 }
        digest.update(key, 0, key.size)
        digest.doFinal(keyDigest, 0)
    }

    override val algorithmName: String
        get() = "PAULSSON-MAC"

    override val macSize: Int
        get() = 128

    override fun update(input: Byte) { messageDigest.update(input) }

    override fun update(input: ByteArray, inOff: Int, len: Int) { messageDigest.update(input, inOff, len) }

    override fun doFinal(out: ByteArray, outOff: Int): Int {
        messageDigest.update(keyDigest, 0, messageDigest.digestSize)
        return messageDigest.doFinal(out, outOff).also { reset() }
    }

    override fun reset() { messageDigest.reset() }
}