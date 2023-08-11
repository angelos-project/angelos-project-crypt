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

class Sha224Hash: AbstractSha256HashEngine() {

    override val h = intArrayOf(
        -0x3efa6128, 0x367cd507, 0x3070dd17, -0x8f1a6c7,
        -0x3ff4cf, 0x68581511, 0x64f98fa7, -0x4105b05c
    )

    override fun truncate(hash: ByteArray) = hash.copyOf(messageDigestSize)

    override val type: String
        get() = "SHA224"

    companion object: Hash {
        override val name = "${Hash.TYPE}-224"
        override val blockSize: Int = 512 / ShaHashEngine.byteSize
        override val wordSize: Int = 32 / ShaHashEngine.byteSize
        override val messageDigestSize: Int = 224 / ShaHashEngine.byteSize

        override fun create() = Sha224Hash()
    }
}