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
package org.angproj.crypt

public interface Crypto {

    public val Int.inByteSize: Int
        get() = this.floorDiv(Byte.SIZE_BITS)

    public val Int.inBitLength: Int
        get() = this * Byte.SIZE_BITS

    public fun create(): Any

    public companion object {
        public val empty: ByteArray = byteArrayOf()
    }
}