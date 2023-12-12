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
package org.angproj.crypt.kp

import org.angproj.aux.util.EndianAware
import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.writeLongAt
import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class PaulssonHash : AbstractPaulssonSponge(
    PaulssonSponge.entropyState.toLongArray()
), HashEngine, EndianAware {

    private val inBuf = LongArray(PaulssonSponge.stateSize)
    private var lasting = ByteArray(0)
    private var count: Long = 0

    private fun push(chunk: ByteArray) = inBuf.indices.forEach { idx ->
        inBuf[idx] = chunk.readLongAt(idx * wordSize).asBig()
    }

    private fun transform() {
        PaulssonSponge.absorb(inBuf, side, state)
    }

    public override fun update(messagePart: ByteArray) {
        val message = lasting + messagePart
        lasting = ByteArray(0) // Setting an empty array

        (0..message.size step blockSize).forEach { idx ->

            // Slicing the buffer in ranges of 64, if too small it's lasting.
            val chunk = try {
                messagePart.copyOfRange(idx, idx + blockSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = message.copyOfRange(idx, message.size)
                return
            }

            push(chunk)
            transform()

            count += blockSize
        }
    }

    override val type: String
        get() = "Paulsson"

    public override fun final(): ByteArray {
        count += lasting.size
        lasting += ByteArray(blockSize - lasting.size)

        push(lasting)
        inBuf[15] = inBuf[15] xor (count * Byte.SIZE_BITS)
        transform()
        val salt = PaulssonSponge.scrambleWithSalt(side, state)
        val screen = LongArray(16)
        PaulssonSponge.squeeze(screen, side, state)
        PaulssonSponge.scramble(side, state)
        obfuscate(salt, screen, state)

        val hash = ByteArray(state.size * wordSize)
        state.indices.forEach {
            hash.writeLongAt(it * wordSize, state[it].asBig()) }
        return hash
    }

    private fun obfuscate(start: Long, screen: LongArray, buffer: LongArray): Long {
        buffer[0] = buffer[0] xor screen[0] xor start
        for (idx in 1 until buffer.size ) {
            buffer[idx] = buffer[idx] xor screen[idx] xor buffer[idx-1]
        }
        return buffer.last()
    }

    internal companion object: Hash {
        public override val name: String = "Paulsson"
        public override val blockSize: Int = PaulssonSponge.stateSize * Long.SIZE_BYTES
        public override val wordSize: Int = Long.SIZE_BYTES
        public override val messageDigestSize: Int = blockSize

        override fun create(): PaulssonHash = PaulssonHash()
    }
}