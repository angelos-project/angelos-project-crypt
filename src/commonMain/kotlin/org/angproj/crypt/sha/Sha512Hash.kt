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

class Sha512Hash: AbstractSha512HashEngine() {

    override val h = longArrayOf(
        0x6A09E667F3BCC908L, -0x4498517a7b3558c5L, 0x3C6EF372FE94F82BL, -0x5ab00ac5a0e2c90fL,
        0x510E527FADE682D1L, -0x64fa9773d4c193e1L, 0x1F83D9ABFB41BD6BL, 0x5BE0CD19137E2179L
    )

    override fun truncate(hash: ByteArray) = hash

    override val type: String
        get() = "SHA512"

    companion object: Hash {
        override val name = "${Hash.TYPE}-512"
        override val blockSize = 1024 / ShaHashEngine.byteSize
        override val wordSize = 64 / ShaHashEngine.byteSize
        override val messageDigestSize = 512 / ShaHashEngine.byteSize

        override fun create() = Sha512Hash()
    }
}