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

public interface AEADCipher {

    //@Throws(IllegalArgumentException)
    public fun setup(forEncryption: Boolean, params: CipherParameters)

    public val algorithmName: String

    public fun processAADByte(input: Byte)

    public fun processAADBytes(input: ByteArray, inOff: Int, len: Int)

    //@Throws(DataLengthException)
    public fun processByte(input: Byte, out: ByteArray, outOff: Int): Int

    //@Throws(DataLengthException)
    public fun processBytes(input: ByteArray, inOff: Int, len: Int, out: ByteArray, outOff: Int): Int

    //@Throws(IllegalStateException, InvalidCipherTextException)
    public fun doFinal(out: ByteArray, outOff: Int): Int

    public val mac: ByteArray

    public fun getUpdateOutputSize(len: Int): Int

    public fun getOutputSize(len: Int): Int

    public fun reset()
}
