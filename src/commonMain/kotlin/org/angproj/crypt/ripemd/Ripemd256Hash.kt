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
package org.angproj.crypt.ripemd

import org.angproj.aux.util.EndianAware
import org.angproj.aux.util.readIntAt
import org.angproj.aux.util.writeIntAt
import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine

/**
 * https://homes.esat.kuleuven.be/~bosselae/ripemd160.html
 *
 * https://homes.esat.kuleuven.be/~bosselae/ripemd/rmd128.txt
 * https://homes.esat.kuleuven.be/~bosselae/ripemd/rmd160.txt
 * https://homes.esat.kuleuven.be/~bosselae/ripemd/rmd256.txt
 * https://homes.esat.kuleuven.be/~bosselae/ripemd/rmd320.txt
 */

internal class Ripemd256Hash: HashEngine, EndianAware {

    val h: IntArray = intArrayOf(
        0x67452301, -0x10325477, -0x67452302, 0x10325476,
        0x76543210, -0x1234568, -0x76543211, 0x01234567
    )

    protected var lasting: ByteArray = ByteArray(0)

    protected var byteCount: Int = 0

    protected var blockCount: Int = 0

    private fun push(chunk: ByteArray): IntArray = IntArray(16).also {
        it.indices.forEach { idx -> it[idx] = chunk.readIntAt(idx * wordSize).asLittle() }
    }

    private fun transform(x: IntArray) {
        val h0 = h.copyOfRange(0, 4)
        val h1 = h.copyOfRange(4, 8)

        round(x, h0, h1 , 0..15, k0[0], k1[0])
        round(x, h0, h1 , 16..31, k0[1], k1[1])
        round(x, h0, h1 , 32..47, k0[2], k1[2])
        round(x, h0, h1 , 48..63, k0[3], k1[3])

        (h0 + h1).forEachIndexed { idx, value -> h[idx] += value }
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

            transform(push(chunk))
            byteCount += blockSize
        }
    }

    override fun final(): ByteArray {
        byteCount += lasting.size
        val bitCount: Long = byteCount * 8L // WARNING! It's sensitive that bitCount is a Long.
        val buffer = lasting + byteArrayOf(128.toByte(), 0, 0, 0).copyOfRange(0, 4 - lasting.size.rem(4))

        val x = IntArray(16)
        val wordCount = buffer.size.floorDiv(wordSize)
        (0 until wordCount).forEach { idx -> x[idx] = buffer.readIntAt(idx * wordSize).asLittle() }

        if(wordCount > 14) {
            transform(x)
            x.fill(0)
        }

        x[14] = (bitCount and -0x1).toInt()
        x[15] = (bitCount ushr Int.SIZE_BITS).toInt()
        transform(x)

        val hash = ByteArray(h.size * wordSize)
        h.indices.forEach { hash.writeIntAt(it * wordSize, h[it].asLittle()) }
        return truncate(hash)
    }

    fun truncate(hash: ByteArray): ByteArray = hash

    override val type: String
        get() = "RIPEMD"

    public companion object: Hash {
        public override val name: String = "${Hash.TYPE}-256"
        public override val blockSize: Int = 512 / Byte.SIZE_BITS
        public override val wordSize: Int = 32 / Byte.SIZE_BITS
        public override val messageDigestSize: Int = 256 / Byte.SIZE_BITS

        public override fun create(): Ripemd256Hash = Ripemd256Hash()

        private fun round(x: IntArray, h0: IntArray, h1: IntArray, range: IntRange, k0: Int, k1: Int) {
            var t = 0
            range.forEach { j ->
                t = (h0[0] + f(j, h0[1], h0[2], h0[3]) + x[r0[j]] + k0).rotateLeft(s0[j])
                h0[0] = h0[3]
                h0[3] = h0[2]
                h0[2] = h0[1]
                h0[1] = t

                t = (h1[0] + f(63-j, h1[1], h1[2], h1[3]) + x[r1[j]] + k1).rotateLeft(s1[j])
                h1[0] = h1[3]
                h1[3] = h1[2]
                h1[2] = h1[1]
                h1[1] = t
            }
            when(range.last) {
                15 -> {
                    t = h0[0]
                    h0[0] = h1[0]
                    h1[0] = t
                }
                31 -> {
                    t = h0[1]
                    h0[1] = h1[1]
                    h1[1] = t
                }
                47 -> {
                    t = h0[2]
                    h0[2] = h1[2]
                    h1[2] = t
                }
                63 -> {
                    t = h0[3]
                    h0[3] = h1[3]
                    h1[3] = t
                }
                else -> Unit
            }
        }

        private fun f(j: Int, x: Int, y: Int, z: Int): Int = when(j) {
            in 0..15 -> x xor y xor z
            in 16..31 -> (x and y) or (x.inv() and z)
            in 32..47 -> (x or y.inv()) xor z
            in 48..63 -> (x and z) or (y and z.inv())
            else -> error("Can't happen")
        }

        private val k0 = intArrayOf(0x00000000, 0x5A827999, 0x6ED9EBA1, -0x70e44324)
        private val k1 = intArrayOf(0x50A28BE6, 0x5C4DD124, 0x6D703EF3, 0x00000000)

        private val r0 = intArrayOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2
        )

        private val r1 = intArrayOf(
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14
        )

        private val s0 = intArrayOf(
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12
        )

        private val s1 = intArrayOf(
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8
        )
    }
}