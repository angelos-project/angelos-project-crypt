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
import org.angproj.crypt.HashEngine
import org.angproj.crypt.keccak.AbstractKeccakHashEngine
import org.angproj.crypt.keccak.KeccakHashEngine

internal class Sha3384Hash: AbstractKeccakHashEngine() {
    override val type: String
        get() = TODO("Not yet implemented")

    override fun update(messagePart: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun final(): ByteArray {
        TODO("Not yet implemented")
    }

    public companion object: Hash {
        public override val name: String = "${KeccakHashEngine.TYPE}3-384"
        public override val blockSize: Int = 832 / ShaHashEngine.byteSize
        public override val wordSize: Int = 64 / ShaHashEngine.byteSize
        public override val messageDigestSize: Int = 384 / ShaHashEngine.byteSize

        override fun create(): HashEngine {
            TODO("Not yet implemented")
        }
    }
}