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

public interface Digest {

    public val algorithmName: String

    public val digestSize: Int

    public fun update(input: Byte)

    public fun update(input: ByteArray, inOff: Int, len: Int)

    public fun doFinal(out: ByteArray, outOff: Int): Int

    public fun reset()
}
