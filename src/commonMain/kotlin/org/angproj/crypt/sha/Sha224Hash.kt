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

import org.angproj.crypt.Hash

internal class Sha224Hash: AbstractSha256HashEngine() {

    override val h: IntArray = intArrayOf(
        -0x3efa6128, 0x367cd507, 0x3070dd17, -0x8f1a6c7,
        -0x3ff4cf, 0x68581511, 0x64f98fa7, -0x4105b05c
    )

    override fun truncate(hash: ByteArray): ByteArray = hash.copyOf(messageDigestSize)

    override val type: String
        get() = "SHA224"

    public companion object: Hash {
        public override val name: String = "${Hash.TYPE}-224"
        public override val blockSize: Int = 512 / ShaHashEngine.byteSize
        public override val wordSize: Int = 32 / ShaHashEngine.byteSize
        public override val messageDigestSize: Int = 224 / ShaHashEngine.byteSize

        public override fun create(): Sha224Hash = Sha224Hash()
    }
}