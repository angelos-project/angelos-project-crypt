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

import org.angproj.crypt.Hash

internal class Ripemd320Hash: AbstractRipemdHashEngine() {

    override val h: IntArray = intArrayOf(
        0x67452301, -0x10325477, -0x67452302, 0x10325476, -0x3c2d1e10,
        0x76543210, -0x1234568, -0x76543211, 0x01234567, 0x3C2D1E0F
    )

    override fun transform(x: IntArray) {
        val h0 = h.copyOfRange(0, 5)
        val h1 = h.copyOfRange(5, 10)

        round(x, h0, h1 , 0..15, k0[0], k1[0])
        round(x, h0, h1 , 16..31, k0[1], k1[1])
        round(x, h0, h1 , 32..47, k0[2], k1[2])
        round(x, h0, h1 , 48..63, k0[3], k1[3])
        round(x, h0, h1 , 64..79, k0[4], k1[4])

        (h0 + h1).forEachIndexed { idx, value -> h[idx] += value }
    }

    override val type: String
        get() = "RIPEMD"

    public companion object: Hash {
        public override val name: String = "${Hash.TYPE}-320"
        public override val blockSize: Int = 512 / Byte.SIZE_BITS
        public override val wordSize: Int = 32 / Byte.SIZE_BITS
        public override val messageDigestSize: Int = 320 / Byte.SIZE_BITS

        public override fun create(): Ripemd320Hash = Ripemd320Hash()

        private fun round(x: IntArray, h0: IntArray, h1: IntArray, range: IntRange, k0: Int, k1: Int) {
            var t: Int
            range.forEach { j ->
                t = (h0[0] + f(j, h0[1], h0[2], h0[3]) + x[r0[j]] + k0).rotateLeft(s0[j]) + h0[4]
                h0[0] = h0[4]
                h0[4] = h0[3]
                h0[3] = h0[2].rotateLeft(10)
                h0[2] = h0[1]
                h0[1] = t

                t = (h1[0] + f(79-j, h1[1], h1[2], h1[3]) + x[r1[j]] + k1).rotateLeft(s1[j]) + h1[4]
                h1[0] = h1[4]
                h1[4] = h1[3]
                h1[3] = h1[2].rotateLeft(10)
                h1[2] = h1[1]
                h1[1] = t
            }
            when(range.last) {
                15 -> {
                    t = h0[1]
                    h0[1] = h1[1]
                    h1[1] = t

                }
                31 -> {
                    t = h0[3]
                    h0[3] = h1[3]
                    h1[3] = t
                }
                47 -> {
                    t = h0[0]
                    h0[0] = h1[0]
                    h1[0] = t
                }
                63 -> {
                    t = h0[2]
                    h0[2] = h1[2]
                    h1[2] = t
                }
                79 -> {
                    t = h0[4]
                    h0[4] = h1[4]
                    h1[4] = t
                }
                else -> Unit
            }
        }

        private val k0 = intArrayOf(0x00000000, 0x5A827999, 0x6ED9EBA1, -0x70e44324, -0x56ac02b2)
        private val k1 = intArrayOf(0x50A28BE6, 0x5C4DD124, 0x6D703EF3, 0x7A6D76E9, 0x000000000)
    }
}