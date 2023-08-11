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

class Sha512256Hash: AbstractSha512HashEngine() {

    override val h = longArrayOf(
        0x22312194FC2BF72CL, -0x60aaa05c37b39b3eL, 0x2393B86B6F53B151L, -0x69c788e6a6bf1543L,
        -0x69d7c11d5771001dL, -0x41a1e1daac79c66eL, 0x2B0199FC2C85B8AAL, 0x0EB72DDC81C52CA2L
    )

    override fun truncate(hash: ByteArray) = hash.copyOf(messageDigestSize)

    companion object: Hash {
        override val name = "${Hash.TYPE}-512/256"
        override val blockSize = 1024 / ShaHashEngine.byteSize
        override val wordSize = 64 / ShaHashEngine.byteSize
        override val messageDigestSize = 256 / ShaHashEngine.byteSize

        override fun create() = Sha512256Hash()
    }
}