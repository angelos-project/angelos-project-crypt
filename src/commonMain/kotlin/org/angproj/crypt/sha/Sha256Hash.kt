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

public class Sha256Hash: AbstractSha256HashEngine() {

    override val h: IntArray = intArrayOf(
        0x6a09e667, -0x4498517b, 0x3c6ef372, -0x5ab00ac6,
        0x510e527f, -0x64fa9774, 0x1f83d9ab, 0x5be0cd19
    )

    override fun truncate(hash: ByteArray): ByteArray = hash

    override val type: String
        get() = "SHA256"

    public companion object: Hash {
        public override val name: String = "${Hash.TYPE}-256"
        public override val blockSize: Int = 512 / ShaHashEngine.byteSize
        public override val wordSize: Int = 32 / ShaHashEngine.byteSize
        public override val messageDigestSize: Int = 256 / ShaHashEngine.byteSize

        public override fun create(): Sha256Hash = Sha256Hash()
    }
}