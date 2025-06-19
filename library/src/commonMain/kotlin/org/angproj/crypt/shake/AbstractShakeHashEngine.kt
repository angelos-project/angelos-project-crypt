/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.shake

import org.angproj.crypt.keccak.AbstractKeccakHashEngine

public abstract class AbstractShakeHashEngine(private val vMessageDigestSize: Int = messageDigestSize, protected val blockSize: Int): AbstractKeccakHashEngine() {
    override fun pad(size: Int): ByteArray = when (size) {
        1 -> byteArrayOf(0x9F.toByte())
        else -> ByteArray(size).also {
            it[0] = 0x1f
            it[size - 1] = -128
        }
    }

    override fun squeeze(): ByteArray {
        w.fill(0)
        ioLoop(vMessageDigestSize) { i ->
            w.writeLongAt(i, state[i / 8]) // Little Endian
        }
        return w.copyOfRange(0, vMessageDigestSize)
    }

    override fun update(messagePart: ByteArray) {
        updateWithSize(messagePart, blockSize)
    }

    override fun final(): ByteArray = finalWithSize(blockSize)
}