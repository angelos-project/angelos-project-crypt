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
import org.angproj.crypt.keccak.KeccakHashEngine

public class Sha3512Hash : AbstractSha3HashEngine(messageDigestSize, blockSize) {

    override val type: String
        get() = "SHA3"

    public companion object : Hash {
        override val name: String = "${KeccakHashEngine.TYPE}3-384"
        override val blockSize: Int = 576.inByteSize
        override val wordSize: Int = 64.inByteSize
        override val messageDigestSize: Int = 512.inByteSize

        override fun create(): Sha3512Hash = Sha3512Hash()

    }
}