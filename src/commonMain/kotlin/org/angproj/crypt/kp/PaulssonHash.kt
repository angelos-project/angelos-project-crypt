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
public class PaulssonHash(domain: Long = 0) : AbstractPaulssonSponge(), HashEngine, EndianAware {

    private var lasting = byteArrayOf()
    private var count: Long = 0

    init {
        initVector.setDomain(domain)
        sponge = initVector.toSponge()
    }

    private fun push(chunk: ByteArray) = buffer.indices.forEach { idx ->
        buffer[idx] = chunk.readLongAt(idx * wordSize).asBig()
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
            sponge.absorb(buffer)

            count += blockSize
        }
    }

    override val type: String
        get() = "Paulsson"

    public override fun final(): ByteArray {
        count += lasting.size
        lasting += ByteArray(blockSize - lasting.size)

        push(lasting)
        sponge.absorb(buffer)
        sponge.absorbOne(count * Byte.SIZE_BITS)
        sponge.scrambleFull()

        val hash = ByteArray(16 * wordSize)
        sponge.squeezeEnd(buffer)
        buffer.forEachIndexed {idx, reg ->
            hash.writeLongAt(idx * wordSize, reg.asBig()) }
        return hash
    }

    internal companion object: Hash {
        public override val name: String = "Paulsson"
        public override val blockSize: Int = 1024.inByteSize
        public override val wordSize: Int = Long.SIZE_BYTES
        public override val messageDigestSize: Int = blockSize

        override fun create(): PaulssonHash = PaulssonHash()
    }
}