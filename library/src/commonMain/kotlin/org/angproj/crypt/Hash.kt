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

public interface Hash: Crypto {
    public val name: String
    public val blockSize: Int
    public val wordSize: Int
    public val messageDigestSize: Int

    override fun create(): HashEngine

    public companion object {
        public const val TYPE: String = "SHA"
    }
}