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
package org.angproj.crypt.shake

import org.angproj.crypt.Hash
import org.angproj.crypt.keccak.KeccakHashEngine

public class Shake256Hash(vMessageDigestSize: Int = messageDigestSize): AbstractShakeHashEngine(vMessageDigestSize, blockSize) {

    override val type: String
        get() = "SHA3/SHAKE"

    public companion object : Hash {
        override val name: String = "${KeccakHashEngine.TYPE}3-SHAKE256"
        override val blockSize: Int = 1088.inByteSize
        override val wordSize: Int = 64.inByteSize
        override val messageDigestSize: Int = 256.inByteSize

        override fun create(): Shake256Hash = create(Shake256Hash.messageDigestSize)

        public fun create(messageDigestSize: Int): Shake256Hash = Shake256Hash(messageDigestSize)
    }
}