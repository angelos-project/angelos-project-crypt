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

internal class Ripemd128Hash: AbstractRipemdHashEngine() {

    override val h: IntArray = intArrayOf(
        0x67452301, -0x10325477, -0x67452302, 0x10325476
    )

    override fun transform(x: IntArray) {
        val h0 = h.copyOf()
        val h1 = h.copyOf()

        round(x, h0, h1 , 0..15, k0[0], k1[0])
        round(x, h0, h1 , 16..31, k0[1], k1[1])
        round(x, h0, h1 , 32..47, k0[2], k1[2])
        round(x, h0, h1 , 48..63, k0[3], k1[3])

        val t = h[1] + h0[2] + h1[3]
        h[1] = h[2] + h0[3] + h1[0]
        h[2] = h[3] + h0[0] + h1[1]
        h[3] = h[0] + h0[1] + h1[2]
        h[0] = t
    }

    override val type: String
        get() = "RIPEMD"

    public companion object: Hash {
        public override val name: String = "${Hash.TYPE}-128"
        public override val blockSize: Int = 512.inByteSize
        public override val wordSize: Int = 32.inByteSize
        public override val messageDigestSize: Int = 128.inByteSize

        public override fun create(): Ripemd128Hash = Ripemd128Hash()

        private fun round(x: IntArray, h0: IntArray, h1: IntArray, range: IntRange, k0: Int, k1: Int) {
            range.forEach { j ->
                var t = (h0[0] + f(j, h0[1], h0[2], h0[3]) + x[r0[j]] + k0).rotateLeft(s0[j])
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
        }

        private val k0 = intArrayOf(0x00000000, 0x5A827999, 0x6ED9EBA1, -0x70e44324)
        private val k1 = intArrayOf(0x50A28BE6, 0x5C4DD124, 0x6D703EF3, 0x00000000)
    }
}