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
package org.angproj.crypt.sha

import org.angproj.aux.util.writeLongAt
import org.angproj.crypt.keccak.AbstractKeccakHashEngine

public abstract class AbstractSha3HashEngine(protected val messageDigestSize: Int, protected val blockSize: Int): AbstractKeccakHashEngine() {
    override fun pad(size: Int): ByteArray = when (size) {
        1 -> byteArrayOf(-122)
        else -> ByteArray(size).also {
            it[0] = 6
            it[size - 1] = -128
        }
    }

    override fun squeeze(): ByteArray {
        w.fill(0)
        ioLoop(messageDigestSize) { i ->
            w.writeLongAt(i, state[i / 8]) // Little Endian
        }
        return w.copyOfRange(0, messageDigestSize)
    }

    override fun update(messagePart: ByteArray) {
        updateWithSize(messagePart, blockSize)
    }

    override fun final(): ByteArray = finalWithSize(blockSize)
}