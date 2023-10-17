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
package org.angproj.crypt.number

import org.angproj.crypt.dsa.BigInt
import kotlin.math.max

public fun BigInt.and(value: BigInt): BigInt {
    val result = IntArray(max(intLength(), value.intLength()))
    for (i in result.indices) result[i] = (getInt(result.size - i - 1) and value.getInt(result.size - i - 1))
    return valueOf(result)
}

public fun BigInt.or(value: BigInt): BigInt {
    val result = IntArray(max(intLength(), value.intLength()))
    for (i in result.indices) result[i] = (getInt(result.size - i - 1) or value.getInt(result.size - i - 1))
    return valueOf(result)
}

public fun BigInt.xor(value: BigInt): BigInt {
    val result = IntArray(max(intLength(), value.intLength()))
    for (i in result.indices) result[i] = (getInt(result.size - i - 1) xor value.getInt(result.size - i - 1))
    return valueOf(result)
}

public fun BigInt.not(): BigInt {
    val result = IntArray(intLength())
    for (i in result.indices) result[i] = getInt(result.size - i - 1).inv()
    return valueOf(result)
}

public fun BigInt.andNot(value: BigInt): BigInt {
    val result = IntArray(max(intLength(), value.intLength()))
    for (i in result.indices) result[i] = (getInt(result.size - i - 1) and value.getInt(result.size - i - 1).inv())
    return valueOf(result)
}