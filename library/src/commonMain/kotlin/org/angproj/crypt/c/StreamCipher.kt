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

public interface StreamCipher {

    //@Throws(IllegalArgumentException)
    public fun setup(forEncryption: Boolean, params: CipherParameters)

    public val algorithmName: String

    public fun returnByte(input: Byte): Byte

    //@Throws(DataLengthException)
    public fun processBytes(input: ByteArray, inOff: Int, len: Int, out: ByteArray, outOff: Int): Int

    public fun reset()
}
