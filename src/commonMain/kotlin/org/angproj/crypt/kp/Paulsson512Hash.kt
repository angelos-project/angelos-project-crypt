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
public class Paulsson512Hash : AbstractPaulssonSponge(
    PaulssonSponge.entropyState.toLongArray()
), HashEngine, EndianAware {

    protected val w: LongArray = LongArray(PaulssonSponge.stateSize)

    protected var lasting: ByteArray = ByteArray(0)

    protected var count: Int = 0


    private fun push(chunk: ByteArray) = (0 until PaulssonSponge.stateSize).forEach { i ->
        w[i] = chunk.readLongAt(i * wordSize).asBig()
    }

    private fun push(block: LongArray) = block.copyInto(w, 0, 0, 16)

    private fun transform(): Unit = PaulssonSponge.absorb(w, side, state)

    public override fun update(messagePart: ByteArray) {
        val buffer = lasting + messagePart
        lasting = ByteArray(0) // Setting an empty array

        (0..buffer.size step blockSize).forEach { i ->

            // Slicing the buffer in ranges of 64, if too small it's lasting.
            val chunk = try {
                messagePart.copyOfRange(i, i + blockSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = buffer.copyOfRange(i, buffer.size)
                return
            }

            push(chunk)
            transform()

            count += blockSize
        }
    }

    override val type: String
        get() = "Paulsson512"

    public override fun final(): ByteArray {
        count += lasting.size
        val blocksNeeded = if (lasting.size + 1 + 8 > blockSize) 2 else 1
        val blockLength = lasting.size / wordSize
        val end = LongArray(blocksNeeded * PaulssonSponge.stateSize)

        (0 until blockLength).forEach { i ->
            end[i] = lasting.readLongAt(i * wordSize).asBig()
        }

        val remainder = (lasting.copyOfRange(
            blockLength * wordSize, lasting.size
        ) + 128.toByte()).copyOf(wordSize)
        end[blockLength] = remainder.readLongAt(0).asBig()

        val totalSize = count * 8L
        end[end.size - 1] = totalSize

        if (blocksNeeded == 1) {
            push(end)
            transform()
        } else {
            push(end.copyOfRange(0, 16))
            transform()
            push(end.copyOfRange(16, 32))
            transform()
        }

        val hash = ByteArray(state.size * wordSize)
        state.indices.forEach { hash.writeLongAt(it * wordSize, state[it].asBig()) }
        return hash.copyOfRange(0, hash.size / 2)
    }

    internal companion object: Hash {
        public override val name: String = "Paulsson-512"
        public override val blockSize: Int = 1024 / Byte.SIZE_BITS
        public override val wordSize: Int = 64 / Byte.SIZE_BITS
        public override val messageDigestSize: Int = 512 / Byte.SIZE_BITS

        override fun create(): Paulsson512Hash = Paulsson512Hash()
    }
}