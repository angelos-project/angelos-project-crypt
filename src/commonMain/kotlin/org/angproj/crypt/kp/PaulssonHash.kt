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
    absorb = true
), HashEngine, EndianAware {

    private var lasting = byteArrayOf()
    private var count: Long = 0

    private fun push(chunk: ByteArray) = inBuf.indices.forEach { idx ->
        inBuf[idx] = chunk.readLongAt(idx * wordSize).asBig()
    }

    public override fun update(messagePart: ByteArray) {
        val message = lasting + messagePart
        lasting = byteArrayOf()

        (0..message.size step blockSize).forEach { idx ->

            val chunk = try {
                messagePart.copyOfRange(idx, idx + blockSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = message.copyOfRange(idx, message.size)
                return
            }

            push(chunk)
            PaulssonSponge.absorb(inBuf, state)

            count += blockSize
        }
    }

    override val type: String
        get() = "Paulsson"

    public override fun final(): ByteArray {
        count += lasting.size
        lasting += ByteArray(blockSize - lasting.size)

        push(lasting)
        inBuf[0] = inBuf[0] xor (count * Byte.SIZE_BITS)
        PaulssonSponge.absorb(inBuf, state)
        PaulssonSponge.scrambleLock(state.first)

        val hash = ByteArray(PaulssonSponge.stateSize * wordSize)
        state.first.forEachIndexed {idx, reg ->
            hash.writeLongAt(idx * wordSize, reg.asBig()) }
        return hash
    }

    internal companion object: Hash {
        public override val name: String = "Paulsson"
        public override val blockSize: Int = PaulssonSponge.stateSize * Long.SIZE_BYTES
        public override val wordSize: Int = Long.SIZE_BYTES
        public override val messageDigestSize: Int = blockSize

        override fun create(): PaulssonHash = PaulssonHash()
    }
}