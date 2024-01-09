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
package org.angproj.crypt.sec

public enum class NistPrime(public val curve: SecPRandom) {
    P_192 (Secp192Random1),
    P_224 (Secp224Random1),
    P_256 (Secp256Random1),
    P_384 (Secp384Random1),
    P_521 (Secp521Random1),
}