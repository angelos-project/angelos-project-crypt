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
import org.angproj.crypt.sha.ShaHashEngine


/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class Paulsson512Hash : AbstractPaulssonSponge(
    PaulssonSponge.entropyState.toLongArray()
), HashEngine, EndianAware {

    private val w = LongArray(PaulssonSponge.stateSize)

    private var lasting = ByteArray(0)

    private var count: Long = 0

    private var cycles: Int = 0


    private fun push(chunk: ByteArray, out: LongArray) = out.indices.forEach { idx ->
        out[idx] = chunk.readLongAt(idx * wordSize).asBig()
    }

    private fun push(block: LongArray) = block.copyInto(w, 0, 0, 16)

    private fun transform(block: LongArray) {
        PaulssonSponge.absorb(block, side, state)
        cycles += block.size
    }

    public override fun update(messagePart: ByteArray) {
        val buffer = lasting + messagePart
        lasting = ByteArray(0) // Setting an empty array

        (0..buffer.size step blockSize).forEach { idx ->

            // Slicing the buffer in ranges of 64, if too small it's lasting.
            val chunk = try {
                messagePart.copyOfRange(idx, idx + blockSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = buffer.copyOfRange(idx, buffer.size)
                return
            }

            push(chunk, w)
            transform(w)

            count += blockSize
        }
    }

    override val type: String
        get() = "Paulsson512"

    public override fun final(): ByteArray {
        lasting += 128.toByte()
        count += lasting.size
        lasting += ByteArray(lasting.size.rem(wordSize))

        val end = LongArray(lasting.size / wordSize)
        push(lasting, end)
        transform(end)
        transform(longArrayOf(count * Byte.SIZE_BITS))

        if (cycles < 16) finalize(16 - cycles)

        val hash = ByteArray(state.size * wordSize / 2)
        (0 until state.size / 2).forEach {
            hash.writeLongAt(it * wordSize, state[it].asBig()) }
        return hash
    }

    internal companion object: Hash {
        public override val name: String = "Paulsson-512"
        public override val blockSize: Int = PaulssonSponge.stateSize * Long.SIZE_BYTES
        public override val wordSize: Int = Long.SIZE_BYTES
        public override val messageDigestSize: Int = blockSize / 2

        override fun create(): Paulsson512Hash = Paulsson512Hash()
    }
}