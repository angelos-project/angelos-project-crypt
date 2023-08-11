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

class Sha384Hash: AbstractSha512HashEngine() {

    override val h = longArrayOf(
        -0x344462a23efa6128L, 0x629a292a367cd507L, -0x6ea6fea5cf8f22e9L, 0x152fecd8f70e5939L,
        0x67332667ffc00b31L, -0x714bb57897a7eaefL, -0x24f3d1f29b067059L, 0x47b5481dbefa4fa4L
    )

    override fun truncate(hash: ByteArray) = hash.copyOf(messageDigestSize)

    override val type: String
        get() = "SHA384"

    companion object: Hash {
        override val name = "${Hash.TYPE}-384"
        override val blockSize = 1024 / ShaHashEngine.byteSize
        override val wordSize = 64 / ShaHashEngine.byteSize
        override val messageDigestSize = 384 / ShaHashEngine.byteSize

        override fun create() = Sha384Hash()
    }
}