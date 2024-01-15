/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.c

public interface Wrapper {

    public fun setup(forWrapping: Boolean, param: CipherParameters)

    public val algorithmName: String

    public fun wrap(input: ByteArray, inOff: Int, inLen: Int): ByteArray

    //@Throws(InvalidCipherTextException)
    public fun unwrap(input: ByteArray, inOff: Int, inLen: Int): ByteArray
}
