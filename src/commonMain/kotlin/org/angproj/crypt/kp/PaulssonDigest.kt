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

import org.angproj.aux.util.EndianAware
import org.angproj.aux.util.floorMod
import org.angproj.aux.util.readLongAt
import org.angproj.crypto.c.ExtendedDigest
import kotlin.math.min

public class PaulssonDigest: PaulssonSponge(), ExtendedDigest, EndianAware {

    private var buffer = byteArrayOf()

    override val algorithmName: String
        get() = "PAULSSON-1024"
    override val digestSize: Int
        get() = 128

    override val byteLength: Int
        get() = 128 + 32 + 8

    private fun processBlock() {
        val inBuf = LongArray(16)
        repeat(buffer.size.floorDiv(digestSize)) {
            val offset = it * digestSize
            inBuf.forEachIndexed { index, l ->
                inBuf[index] = buffer.readLongAt(offset + index * Long.SIZE_BYTES).asBig() }
            absorb(inBuf)
        }
        buffer = buffer.copyOfRange(buffer.size - buffer.size.floorMod(digestSize), buffer.size)
    }

    override fun update(input: Byte) {
        buffer += byteArrayOf(input)
        if (buffer.size >= digestSize) processBlock()
    }

    override fun update(input: ByteArray, inOff: Int, len: Int) {
        buffer += input.copyOfRange(inOff, inOff + len)
        if (buffer.size >= digestSize) processBlock()
    }

    override fun doFinal(out: ByteArray, outOff: Int): Int {
        buffer += ByteArray(digestSize - buffer.size)
        processBlock()
        scramble()
        val outBuf = LongArray(16)
        squeeze(outBuf)

        val length = min(out.size - outOff, digestSize)
        outBuf.asBigByteArray().copyInto(out, outOff, 0, length)

        reset()
        return length
    }

    override fun reset() {
        buffer = byteArrayOf()
        spongeReset()
    }
}