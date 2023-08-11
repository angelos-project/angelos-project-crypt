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

class Sha512224Hash: AbstractSha512HashEngine() {

    override val h = longArrayOf(
        -0x73c2c837e6abb25eL, 0x73E1996689DCD4D6L, 0x1DFAB7AE32FF9C82L, 0x679DD514582F9FCFL,
        0x0F6D2B697BD44DA8L, 0x77E36F7304C48942L, 0x3F9D85A86A1D36C8L, 0x1112E6AD91D692A1L
    )

    override fun truncate(hash: ByteArray) = hash.copyOf(messageDigestSize)

    companion object: Hash {
        override val name = "${Hash.TYPE}-512/224"
        override val blockSize = 1024 / ShaHashEngine.byteSize
        override val wordSize = 64 / ShaHashEngine.byteSize
        override val messageDigestSize = 224 / ShaHashEngine.byteSize

        override fun create() = Sha512224Hash()
    }
}