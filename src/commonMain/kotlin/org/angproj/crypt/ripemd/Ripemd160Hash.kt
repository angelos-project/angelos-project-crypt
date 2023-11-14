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

internal class Ripemd160Hash: HashEngine, EndianAware {

    val h: IntArray = intArrayOf(
        0x67452301, -0x10325477, -0x67452302, 0x10325476, -0x3c2d1e10
    )

    protected var lasting: ByteArray = ByteArray(0)

    protected var count: Int = 0

    private fun push(chunk: ByteArray): IntArray = IntArray(16).also {
        it.indices.forEach { idx -> it[idx] = chunk.readIntAt(idx * wordSize).asLittle() }
    }

    private fun transform(x: IntArray) {
        val lh = h.copyOf()
        val rh = h.copyOf()

        rl1(lh, x)
        rr1(rh, x, k[0])

        rl2(lh, x, k[1])
        rr2(rh, x, k[2])

        rl3(lh, x, k[3])
        rr3(rh, x, k[4])

        rl4(lh, x, k[5])
        rr4(rh, x, k[6])

        rl5(lh, x, k[7])
        rr5(rh, x)

        rh[3] += lh[2] + h[1]
        h[1] = h[2] + lh[3] + rh[4]
        h[2] = h[3] + lh[4] + rh[0]
        h[3] = h[4] + lh[0] + rh[1]
        h[4] = h[0] + lh[1] + rh[2]
        h[0] = rh[3]
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
            count += blockSize
        }
    }

    override fun final(): ByteArray {
        count += lasting.size
        val bitCount: Long = count * 8L // WARNING! It's sensitive that bitCount is a Long.
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
        public override val name: String = "${Hash.TYPE}-160"
        public override val blockSize: Int = 512 / Byte.SIZE_BITS
        public override val wordSize: Int = 32 / Byte.SIZE_BITS
        public override val messageDigestSize: Int = 160 / Byte.SIZE_BITS

        public override fun create(): Ripemd160Hash = Ripemd160Hash()
        private fun rr5(rh: IntArray, x: IntArray) {
            rh[1] = rlmd(rh[1] + aa(rh[2], rh[3], rh[4]) + x[12], 8) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + aa(rh[1], rh[2], rh[3]) + x[15], 5) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + aa(rh[0], rh[1], rh[2]) + x[10], 12) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + aa(rh[4], rh[0], rh[1]) + x[4], 9) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + aa(rh[3], rh[4], rh[0]) + x[1], 12) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + aa(rh[2], rh[3], rh[4]) + x[5], 5) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + aa(rh[1], rh[2], rh[3]) + x[8], 14) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + aa(rh[0], rh[1], rh[2]) + x[7], 6) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + aa(rh[4], rh[0], rh[1]) + x[6], 8) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + aa(rh[3], rh[4], rh[0]) + x[2], 13) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + aa(rh[2], rh[3], rh[4]) + x[13], 6) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + aa(rh[1], rh[2], rh[3]) + x[14], 5) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + aa(rh[0], rh[1], rh[2]) + x[0], 15) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + aa(rh[4], rh[0], rh[1]) + x[3], 13) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + aa(rh[3], rh[4], rh[0]) + x[9], 11) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + aa(rh[2], rh[3], rh[4]) + x[11], 11) + rh[0]
            rh[3] = rlmd(rh[3], 10)
        }

        private fun rl5(lh: IntArray, x: IntArray, c: Int) {
            lh[1] = rlmd(lh[1] + ee(lh[2], lh[3], lh[4]) + x[4] + c, 9) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + ee(lh[1], lh[2], lh[3]) + x[0] + c, 15) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + ee(lh[0], lh[1], lh[2]) + x[5] + c, 5) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + ee(lh[4], lh[0], lh[1]) + x[9] + c, 11) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + ee(lh[3], lh[4], lh[0]) + x[7] + c, 6) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + ee(lh[2], lh[3], lh[4]) + x[12] + c, 8) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + ee(lh[1], lh[2], lh[3]) + x[2] + c, 13) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + ee(lh[0], lh[1], lh[2]) + x[10] + c, 12) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + ee(lh[4], lh[0], lh[1]) + x[14] + c, 5) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + ee(lh[3], lh[4], lh[0]) + x[1] + c, 12) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + ee(lh[2], lh[3], lh[4]) + x[3] + c, 13) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + ee(lh[1], lh[2], lh[3]) + x[8] + c, 14) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + ee(lh[0], lh[1], lh[2]) + x[11] + c, 11) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + ee(lh[4], lh[0], lh[1]) + x[6] + c, 8) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + ee(lh[3], lh[4], lh[0]) + x[15] + c, 5) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + ee(lh[2], lh[3], lh[4]) + x[13] + c, 6) + lh[0]
            lh[3] = rlmd(lh[3], 10)
        }

        private fun rr4(rh: IntArray, x: IntArray, c: Int) {
            rh[2] = rlmd(rh[2] + bb(rh[3], rh[4], rh[0]) + x[8] + c, 15) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + bb(rh[2], rh[3], rh[4]) + x[6] + c, 5) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + bb(rh[1], rh[2], rh[3]) + x[4] + c, 8) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + bb(rh[0], rh[1], rh[2]) + x[1] + c, 11) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + bb(rh[4], rh[0], rh[1]) + x[3] + c, 14) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + bb(rh[3], rh[4], rh[0]) + x[11] + c, 14) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + bb(rh[2], rh[3], rh[4]) + x[15] + c, 6) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + bb(rh[1], rh[2], rh[3]) + x[0] + c, 14) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + bb(rh[0], rh[1], rh[2]) + x[5] + c, 6) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + bb(rh[4], rh[0], rh[1]) + x[12] + c, 9) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + bb(rh[3], rh[4], rh[0]) + x[2] + c, 12) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + bb(rh[2], rh[3], rh[4]) + x[13] + c, 9) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + bb(rh[1], rh[2], rh[3]) + x[9] + c, 12) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + bb(rh[0], rh[1], rh[2]) + x[7] + c, 5) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + bb(rh[4], rh[0], rh[1]) + x[10] + c, 15) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + bb(rh[3], rh[4], rh[0]) + x[14] + c, 8) + rh[1]
            rh[4] = rlmd(rh[4], 10)
        }

        private fun rl4(lh: IntArray, x: IntArray, c: Int) {
            lh[2] = rlmd(lh[2] + dd(lh[3], lh[4], lh[0]) + x[1] + c, 11) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + dd(lh[2], lh[3], lh[4]) + x[9] + c, 12) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + dd(lh[1], lh[2], lh[3]) + x[11] + c, 14) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + dd(lh[0], lh[1], lh[2]) + x[10] + c, 15) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + dd(lh[4], lh[0], lh[1]) + x[0] + c, 14) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + dd(lh[3], lh[4], lh[0]) + x[8] + c, 15) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + dd(lh[2], lh[3], lh[4]) + x[12] + c, 9) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + dd(lh[1], lh[2], lh[3]) + x[4] + c, 8) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + dd(lh[0], lh[1], lh[2]) + x[13] + c, 9) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + dd(lh[4], lh[0], lh[1]) + x[3] + c, 14) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + dd(lh[3], lh[4], lh[0]) + x[7] + c, 5) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + dd(lh[2], lh[3], lh[4]) + x[15] + c, 6) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + dd(lh[1], lh[2], lh[3]) + x[14] + c, 8) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + dd(lh[0], lh[1], lh[2]) + x[5] + c, 6) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + dd(lh[4], lh[0], lh[1]) + x[6] + c, 5) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + dd(lh[3], lh[4], lh[0]) + x[2] + c, 12) + lh[1]
            lh[4] = rlmd(lh[4], 10)
        }

        private fun rr3(rh: IntArray, x: IntArray, c: Int) {
            rh[3] = rlmd(rh[3] + cc(rh[4], rh[0], rh[1]) + x[15] + c, 9) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + cc(rh[3], rh[4], rh[0]) + x[5] + c, 7) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + cc(rh[2], rh[3], rh[4]) + x[1] + c, 15) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + cc(rh[1], rh[2], rh[3]) + x[3] + c, 11) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + cc(rh[0], rh[1], rh[2]) + x[7] + c, 8) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + cc(rh[4], rh[0], rh[1]) + x[14] + c, 6) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + cc(rh[3], rh[4], rh[0]) + x[6] + c, 6) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + cc(rh[2], rh[3], rh[4]) + x[9] + c, 14) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + cc(rh[1], rh[2], rh[3]) + x[11] + c, 12) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + cc(rh[0], rh[1], rh[2]) + x[8] + c, 13) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + cc(rh[4], rh[0], rh[1]) + x[12] + c, 5) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + cc(rh[3], rh[4], rh[0]) + x[2] + c, 14) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + cc(rh[2], rh[3], rh[4]) + x[10] + c, 13) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + cc(rh[1], rh[2], rh[3]) + x[0] + c, 13) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + cc(rh[0], rh[1], rh[2]) + x[4] + c, 7) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + cc(rh[4], rh[0], rh[1]) + x[13] + c, 5) + rh[2]
            rh[0] = rlmd(rh[0], 10)
        }

        private fun rl3(lh: IntArray, x: IntArray, c: Int) {
            lh[3] = rlmd(lh[3] + cc(lh[4], lh[0], lh[1]) + x[3] + c, 11) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + cc(lh[3], lh[4], lh[0]) + x[10] + c, 13) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + cc(lh[2], lh[3], lh[4]) + x[14] + c, 6) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + cc(lh[1], lh[2], lh[3]) + x[4] + c, 7) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + cc(lh[0], lh[1], lh[2]) + x[9] + c, 14) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + cc(lh[4], lh[0], lh[1]) + x[15] + c, 9) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + cc(lh[3], lh[4], lh[0]) + x[8] + c, 13) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + cc(lh[2], lh[3], lh[4]) + x[1] + c, 15) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + cc(lh[1], lh[2], lh[3]) + x[2] + c, 14) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + cc(lh[0], lh[1], lh[2]) + x[7] + c, 8) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + cc(lh[4], lh[0], lh[1]) + x[0] + c, 13) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + cc(lh[3], lh[4], lh[0]) + x[6] + c, 6) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + cc(lh[2], lh[3], lh[4]) + x[13] + c, 5) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + cc(lh[1], lh[2], lh[3]) + x[11] + c, 12) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + cc(lh[0], lh[1], lh[2]) + x[5] + c, 7) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + cc(lh[4], lh[0], lh[1]) + x[12] + c, 5) + lh[2]
            lh[0] = rlmd(lh[0], 10)
        }

        private fun rr2(rh: IntArray, x: IntArray, c: Int) {
            rh[4] = rlmd(rh[4] + dd(rh[0], rh[1], rh[2]) + x[6] + c, 9) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + dd(rh[4], rh[0], rh[1]) + x[11] + c, 13) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + dd(rh[3], rh[4], rh[0]) + x[3] + c, 15) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + dd(rh[2], rh[3], rh[4]) + x[7] + c, 7) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + dd(rh[1], rh[2], rh[3]) + x[0] + c, 12) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + dd(rh[0], rh[1], rh[2]) + x[13] + c, 8) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + dd(rh[4], rh[0], rh[1]) + x[5] + c, 9) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + dd(rh[3], rh[4], rh[0]) + x[10] + c, 11) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + dd(rh[2], rh[3], rh[4]) + x[14] + c, 7) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + dd(rh[1], rh[2], rh[3]) + x[15] + c, 7) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + dd(rh[0], rh[1], rh[2]) + x[8] + c, 12) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + dd(rh[4], rh[0], rh[1]) + x[12] + c, 7) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + dd(rh[3], rh[4], rh[0]) + x[4] + c, 6) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + dd(rh[2], rh[3], rh[4]) + x[9] + c, 15) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + dd(rh[1], rh[2], rh[3]) + x[1] + c, 13) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + dd(rh[0], rh[1], rh[2]) + x[2] + c, 11) + rh[3]
            rh[1] = rlmd(rh[1], 10)
        }

        private fun rl2(lh: IntArray, x: IntArray, c: Int) {
            lh[4] = rlmd(lh[4] + bb(lh[0], lh[1], lh[2]) + x[7] + c, 7) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + bb(lh[4], lh[0], lh[1]) + x[4] + c, 6) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + bb(lh[3], lh[4], lh[0]) + x[13] + c, 8) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + bb(lh[2], lh[3], lh[4]) + x[1] + c, 13) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + bb(lh[1], lh[2], lh[3]) + x[10] + c, 11) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + bb(lh[0], lh[1], lh[2]) + x[6] + c, 9) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + bb(lh[4], lh[0], lh[1]) + x[15] + c, 7) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + bb(lh[3], lh[4], lh[0]) + x[3] + c, 15) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + bb(lh[2], lh[3], lh[4]) + x[12] + c, 7) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + bb(lh[1], lh[2], lh[3]) + x[0] + c, 12) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + bb(lh[0], lh[1], lh[2]) + x[9] + c, 15) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + bb(lh[4], lh[0], lh[1]) + x[5] + c, 9) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + bb(lh[3], lh[4], lh[0]) + x[2] + c, 11) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + bb(lh[2], lh[3], lh[4]) + x[14] + c, 7) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + bb(lh[1], lh[2], lh[3]) + x[11] + c, 13) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + bb(lh[0], lh[1], lh[2]) + x[8] + c, 12) + lh[3]
            lh[1] = rlmd(lh[1], 10)
        }

        private fun rr1(rh: IntArray, x: IntArray, c: Int) {
            rh[0] = rlmd(rh[0] + ee(rh[1], rh[2], rh[3]) + x[5] + c, 8) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + ee(rh[0], rh[1], rh[2]) + x[14] + c, 9) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + ee(rh[4], rh[0], rh[1]) + x[7] + c, 9) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + ee(rh[3], rh[4], rh[0]) + x[0] + c, 11) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + ee(rh[2], rh[3], rh[4]) + x[9] + c, 13) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + ee(rh[1], rh[2], rh[3]) + x[2] + c, 15) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + ee(rh[0], rh[1], rh[2]) + x[11] + c, 15) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + ee(rh[4], rh[0], rh[1]) + x[4] + c, 5) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + ee(rh[3], rh[4], rh[0]) + x[13] + c, 7) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + ee(rh[2], rh[3], rh[4]) + x[6] + c, 7) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + ee(rh[1], rh[2], rh[3]) + x[15] + c, 8) + rh[4]
            rh[2] = rlmd(rh[2], 10)
            rh[4] = rlmd(rh[4] + ee(rh[0], rh[1], rh[2]) + x[8] + c, 11) + rh[3]
            rh[1] = rlmd(rh[1], 10)
            rh[3] = rlmd(rh[3] + ee(rh[4], rh[0], rh[1]) + x[1] + c, 14) + rh[2]
            rh[0] = rlmd(rh[0], 10)
            rh[2] = rlmd(rh[2] + ee(rh[3], rh[4], rh[0]) + x[10] + c, 14) + rh[1]
            rh[4] = rlmd(rh[4], 10)
            rh[1] = rlmd(rh[1] + ee(rh[2], rh[3], rh[4]) + x[3] + c, 12) + rh[0]
            rh[3] = rlmd(rh[3], 10)
            rh[0] = rlmd(rh[0] + ee(rh[1], rh[2], rh[3]) + x[12] + c, 6) + rh[4]
            rh[2] = rlmd(rh[2], 10)
        }

        private fun rl1(lh: IntArray, x: IntArray) {
            lh[0] = rlmd(lh[0] + aa(lh[1], lh[2], lh[3]) + x[0], 11) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + aa(lh[0], lh[1], lh[2]) + x[1], 14) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + aa(lh[4], lh[0], lh[1]) + x[2], 15) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + aa(lh[3], lh[4], lh[0]) + x[3], 12) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + aa(lh[2], lh[3], lh[4]) + x[4], 5) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + aa(lh[1], lh[2], lh[3]) + x[5], 8) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + aa(lh[0], lh[1], lh[2]) + x[6], 7) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + aa(lh[4], lh[0], lh[1]) + x[7], 9) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + aa(lh[3], lh[4], lh[0]) + x[8], 11) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + aa(lh[2], lh[3], lh[4]) + x[9], 13) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + aa(lh[1], lh[2], lh[3]) + x[10], 14) + lh[4]
            lh[2] = rlmd(lh[2], 10)
            lh[4] = rlmd(lh[4] + aa(lh[0], lh[1], lh[2]) + x[11], 15) + lh[3]
            lh[1] = rlmd(lh[1], 10)
            lh[3] = rlmd(lh[3] + aa(lh[4], lh[0], lh[1]) + x[12], 6) + lh[2]
            lh[0] = rlmd(lh[0], 10)
            lh[2] = rlmd(lh[2] + aa(lh[3], lh[4], lh[0]) + x[13], 7) + lh[1]
            lh[4] = rlmd(lh[4], 10)
            lh[1] = rlmd(lh[1] + aa(lh[2], lh[3], lh[4]) + x[14], 9) + lh[0]
            lh[3] = rlmd(lh[3], 10)
            lh[0] = rlmd(lh[0] + aa(lh[1], lh[2], lh[3]) + x[15], 8) + lh[4]
            lh[2] = rlmd(lh[2], 10)
        }

        private val k = intArrayOf(
            0x50a28be6, 0x5a827999, 0x5c4dd124, 0x6ed9eba1,
            0x6d703ef3, -0x70e44324, 0x7a6d76e9, -0x56ac02b2
        )

        private fun rlmd(x: Int, n: Int) = x.rotateLeft(n) // x shl n or x.ushr(32 - n)
        private fun aa(x: Int, y: Int, z: Int) = x xor y xor z
        private fun bb(x: Int, y: Int, z: Int) = x and y or (x.inv() and z)
        private fun cc(x: Int, y: Int, z: Int) = x or y.inv() xor z
        private fun dd(x: Int, y: Int, z: Int) = x and z or (y and z.inv())
        private fun ee(x: Int, y: Int, z: Int) = x xor (y or z.inv())
    }
}