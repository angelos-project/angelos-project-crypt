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

internal class Ripemd256Hash : AbstractRipemdHashEngine() {

    override val h: IntArray = intArrayOf(
        0x67452301, -0x10325477, -0x67452302, 0x10325476,
        0x76543210, -0x1234568, -0x76543211, 0x01234567
    )

    override fun transform(x: IntArray) {
        val h0 = h.copyOfRange(0, 4)
        val h1 = h.copyOfRange(4, 8)

        round(x, h0, h1, 0..15, k0[0], k1[0])
        round(x, h0, h1, 16..31, k0[1], k1[1])
        round(x, h0, h1, 32..47, k0[2], k1[2])
        round(x, h0, h1, 48..63, k0[3], k1[3])

        (h0 + h1).forEachIndexed { idx, value -> h[idx] += value }
    }

    override val type: String
        get() = "RIPEMD"

    companion object : Hash {
        override val name: String = "${Hash.TYPE}-256"
        override val blockSize: Int = 512.inByteSize
        override val wordSize: Int = 32.inByteSize
        override val messageDigestSize: Int = 256.inByteSize

        override fun create(): Ripemd256Hash = Ripemd256Hash()

        private fun round(x: IntArray, h0: IntArray, h1: IntArray, range: IntRange, k0: Int, k1: Int) {
            var t: Int
            range.forEach { j ->
                t = (h0[0] + f(j, h0[1], h0[2], h0[3]) + x[r0[j]] + k0).rotateLeft(s0[j])
                h0[0] = h0[3]
                h0[3] = h0[2]
                h0[2] = h0[1]
                h0[1] = t

                t = (h1[0] + f(63 - j, h1[1], h1[2], h1[3]) + x[r1[j]] + k1).rotateLeft(s1[j])
                h1[0] = h1[3]
                h1[3] = h1[2]
                h1[2] = h1[1]
                h1[1] = t
            }
            when (range.last) {
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

        private val k0 = intArrayOf(0x00000000, 0x5A827999, 0x6ED9EBA1, -0x70e44324)
        private val k1 = intArrayOf(0x50A28BE6, 0x5C4DD124, 0x6D703EF3, 0x00000000)
    }
}