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

import org.angproj.crypt.kdf.PasswordBasedKeyDerivation

public interface Pbkd: Crypto {
    public val name: String
    override fun create(): PasswordBasedKeyDerivation
    public fun create(algo: Hash, keySize: Int, count: Int): PasswordBasedKeyDerivation
}