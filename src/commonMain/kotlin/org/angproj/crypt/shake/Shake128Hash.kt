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

public class Shake128Hash(vMessageDigestSize: Int = messageDigestSize): AbstractShakeHashEngine(vMessageDigestSize, blockSize) {

    override val type: String
        get() = "SHA3/SHAKE"

    public companion object : Hash {
        override val name: String = "${KeccakHashEngine.TYPE}3-SHAKE128"
        override val blockSize: Int = 1344.inByteSize
        override val wordSize: Int = 64.inByteSize
        override val messageDigestSize: Int = 128.inByteSize
        override fun create(): Shake128Hash = create(messageDigestSize)

        public fun create(messageDigestSize: Int): Shake128Hash = Shake128Hash(messageDigestSize)
    }
}