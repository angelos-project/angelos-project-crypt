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

import org.angproj.aux.sec.SecureRandom


public interface ECKeyPairGenerator<U: ECPublicKeyParameters, R: ECPrivateKeyParameters>: AsymmetricCipherKeyPairGenerator<U, R> {

    public val name: String
    public val params: ECDomainParameters
    public val random: SecureRandom

    public fun setup(param: ECKeyGenerationParameters)

    public override fun generateKeyPair(): AsymmetricCipherKeyPair<U, R>
}
