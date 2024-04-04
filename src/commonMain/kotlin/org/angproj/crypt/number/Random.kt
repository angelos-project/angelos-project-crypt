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
package org.angproj.crypt.number

import org.angproj.crypt.num.BigInt
import org.angproj.aux.sec.SecureRandom
import org.angproj.crypt.num.bigIntOf

public fun BigInt.Companion.randomBetween(start: BigInt, end: BigInt): BigInt {
    val random = ByteArray(end.bitLength / 8 + 1)
    SecureRandom.read(random)
    return bigIntOf(random).let { it.shr(it.bitLength - end.bitLength + 1).abs().add(start).toBigInt() }
}