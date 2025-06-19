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

public interface PBEParametersGenerator<P: CipherParameters, M: CipherParameters> {

    public val password: ByteArray

    public val salt: ByteArray

    public val iterationCount: Int

    public fun setup(password: ByteArray, salt: ByteArray, iterationCount: Int)

    public fun generateDerivedParameters(keySize: Int): P

    public fun generateDerivedParameters(keySize: Int, ivSize: Int): P

    public fun generateDerivedMacParameters(keySize: Int): M
}
