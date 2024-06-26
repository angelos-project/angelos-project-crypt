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
package org.angproj.crypt.ec

import org.angproj.crypt.sec.SecPKoblitz
import org.angproj.crypt.sec.Secp256Koblitz1

public enum class BitcoinKoblitz(public val curve: SecPKoblitz) {
    BTC(Secp256Koblitz1),
}