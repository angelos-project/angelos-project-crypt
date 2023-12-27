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
package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex
import org.angproj.crypt.number.BigInt
import org.angproj.crypt.number.bigIntOf

public interface DomainParameters {
    public val name: String

    public fun fromHex(block: () -> String): Lazy<BigInt> = lazy { bigIntOf(BinHex.decodeToBin(block())) }
}