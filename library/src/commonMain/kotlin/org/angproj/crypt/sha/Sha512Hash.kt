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

public class Sha512Hash : AbstractSha512HashEngine() {

    override val h: LongArray = longArrayOf(
        0x6A09E667F3BCC908L, -0x4498517a7b3558c5L, 0x3C6EF372FE94F82BL, -0x5ab00ac5a0e2c90fL,
        0x510E527FADE682D1L, -0x64fa9773d4c193e1L, 0x1F83D9ABFB41BD6BL, 0x5BE0CD19137E2179L
    )

    override fun truncate(hash: ByteArray): ByteArray = hash

    override val type: String
        get() = "SHA512"

    public companion object : Hash {
        public override val name: String = "${Hash.TYPE}-512"
        public override val blockSize: Int = 1024.inByteSize
        public override val wordSize: Int = 64.inByteSize
        public override val messageDigestSize: Int = 512.inByteSize

        public override fun create(): Sha512Hash = Sha512Hash()
    }
}