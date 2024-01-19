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

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.bigIntOf
import org.angproj.aux.util.BinHex

public interface Curves {
    public val name: String

    public fun fromHex(block: () -> String): Lazy<BigInt> = lazy { bigIntOf(BinHex.decodeToBin(block())) }
}