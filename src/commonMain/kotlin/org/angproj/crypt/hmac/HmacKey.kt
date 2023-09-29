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
package org.angproj.crypt.hmac

import org.angproj.crypt.Hash
import kotlin.jvm.JvmInline

@JvmInline
public value class HmacKey(private val prepared: Triple<ByteArray, ByteArray, Hash>) {

    init {
        require(prepared.first.size == algo.blockSize)
        require(prepared.second.size == algo.blockSize)
    }

    public val algo: Hash
        get() = prepared.third

    public val iPadKey: ByteArray
        get() = prepared.first

    public val oPadKey: ByteArray
        get() = prepared.second
}