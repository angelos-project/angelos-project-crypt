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
package org.angproj.crypt.sha

import org.angproj.aux.util.readIntAt
import org.angproj.aux.util.swapEndian
import org.angproj.aux.util.writeIntAt
import org.angproj.crypt.Hash

class Sha1Hash: AbstractShaHashEngine() {

    override val h = intArrayOf(
        0x67452301, -0x10325477, -0x67452302, 0x10325476, -0x3c2d1e10
    )

    override val w = IntArray(80)

    private fun push(chunk: ByteArray) = (0 until 16).forEach { i ->
        w[i] = chunk.readIntAt(i * wordSize).swapEndian()
    }

    private fun push(block: IntArray) = block.copyInto(w, 0, 0, 16)

    private fun transform() {
        (16 until w.size).forEach { i ->
            w[i] = (w[i-3] xor w[i-8] xor w[i-14] xor w[i-16]).rotateLeft(1)
        }
        val temp = h.copyOf()
        w.indices.forEach { i ->
            val f = when(i) {
                in 0 until 20 -> (temp[1] and temp[2]) or (temp[1].inv() and temp[3])
                in 20 until 40 -> temp[1] xor temp[2] xor temp[3]
                in 40 until 60 -> (temp[1] and temp[2]) or (temp[1] and temp[3]) or (temp[2] and temp[3])
                in 60 until 80 -> temp[1] xor temp[2] xor temp[3]
                else -> error("Won't happen!")
            }
            val x = temp[0].rotateLeft(5) + f + temp[4] + k[i/20] + w[i]
            temp[4] = temp[3]
            temp[3] = temp[2]
            temp[2] = temp[1].rotateLeft(30)
            temp[1] = temp[0]
            temp[0] = x
        }
        h.indices.forEach { t -> h[t] += temp[t] }
    }

    override fun update(messagePart: ByteArray) {
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

    override fun final(): ByteArray {
        count += lasting.size
        val blocksNeeded = if (lasting.size + 1 + 8 > blockSize) 2 else 1
        val blockLength = lasting.size / wordSize
        val end = IntArray(blocksNeeded * 16)

        (0 until blockLength).forEach { i ->
            end[i] = lasting.readIntAt(i * wordSize).swapEndian()
        }

        val remainder = (lasting.copyOfRange(
            blockLength * wordSize, lasting.size
        ) + 128.toByte()).copyOf(wordSize)
        end[blockLength] = remainder.readIntAt(0).swapEndian()

        val totalSize = count * 8L
        end[end.size - 2] = ((totalSize) ushr 32).toInt()
        end[end.size - 1] = totalSize.toInt()

        if (blocksNeeded == 1) {
            push(end)
            transform()
        } else {
            push(end.copyOfRange(0, 16))
            transform()
            push(end.copyOfRange(16, 32))
            transform()
        }

        val hash = ByteArray(h.size * wordSize)
        h.indices.forEach { hash.writeIntAt(it * wordSize, h[it].swapEndian()) }
        return truncate(hash)
    }

    override fun truncate(hash: ByteArray) = hash

    override val type: String
        get() = "SHA1"

    companion object: Hash {
        override val name = "${Hash.TYPE}-1"
        override val blockSize: Int = 512 / ShaHashEngine.byteSize
        override val wordSize: Int = 32 / ShaHashEngine.byteSize
        override val messageDigestSize: Int = 160 / ShaHashEngine.byteSize

        override fun create() = Sha1Hash()

        internal val k = intArrayOf(0x5A827999, 0x6ED9EBA1, -0x70e44324, -0x359d3e2a)
    }
}