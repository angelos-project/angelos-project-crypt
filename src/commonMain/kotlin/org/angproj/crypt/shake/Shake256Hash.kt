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
import org.angproj.crypt.HashEngine
import org.angproj.crypt.keccak.AbstractKeccakHashEngine

internal class Shake256Hash: AbstractKeccakHashEngine() {
    override val type: String
        get() = TODO("Not yet implemented")

    override fun update(messagePart: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun final(): ByteArray {
        TODO("Not yet implemented")
    }

    public companion object: Hash {
        override val name: String
            get() = TODO("Not yet implemented")
        override val blockSize: Int
            get() = TODO("Not yet implemented")
        override val wordSize: Int
            get() = TODO("Not yet implemented")
        override val messageDigestSize: Int
            get() = TODO("Not yet implemented")

        override fun create(): HashEngine {
            TODO("Not yet implemented")
        }
    }
}