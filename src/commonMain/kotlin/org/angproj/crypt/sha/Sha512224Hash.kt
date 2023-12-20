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

internal class Sha512224Hash: AbstractSha512HashEngine() {

    override val h: LongArray = longArrayOf(
        -0x73c2c837e6abb25eL, 0x73E1996689DCD4D6L, 0x1DFAB7AE32FF9C82L, 0x679DD514582F9FCFL,
        0x0F6D2B697BD44DA8L, 0x77E36F7304C48942L, 0x3F9D85A86A1D36C8L, 0x1112E6AD91D692A1L
    )

    override fun truncate(hash: ByteArray): ByteArray = hash.copyOf(messageDigestSize)

    override val type: String
        get() = "SHA512/224"

    public companion object: Hash {
        public override val name: String = "${Hash.TYPE}-512/224"
        public override val blockSize: Int = 1024.inByteSize
        public override val wordSize: Int = 64.inByteSize
        public override val messageDigestSize: Int = 224.inByteSize

        public override fun create(): Sha512224Hash = Sha512224Hash()
    }
}