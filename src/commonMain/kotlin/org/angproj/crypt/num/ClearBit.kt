/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.num

import org.angproj.aux.num.*
import kotlin.math.max

public fun BigInt.clearBit(pos: Int): BigInt {
    req(pos >= 0) { "Can not clear an imaginary bit at a negative position." }

    val bigCnt = pos.floorDiv(Int.SIZE_BITS)
    val result = IntArray(max(intSize(), (pos + 1).floorDiv(Int.SIZE_BITS) + 1))

    result.indices.forEach { result.revSet(it, getIdx(it))  }
    result.revSet(bigCnt, result.revGet(bigCnt) and bigMask(pos).inv())

    return ofIntArray(result)
}