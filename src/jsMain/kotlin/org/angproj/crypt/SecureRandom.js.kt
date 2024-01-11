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
package org.angproj.crypt

import kotlin.js.Date
import kotlin.math.max
import kotlin.math.min

private var counter: Long = Date.now().toLong()
private var seed: Long = 0xFFEB910A03212AF1uL.toLong()

internal actual fun entropy(size: Int): ByteArray {
    val timestamp = Date.now().toLong()
    return ByteArray(min(Short.MAX_VALUE.toInt(), size)) {
        counter = max(1, counter + 1)
        seed = -(seed + 1).rotateRight(53) xor (seed-1).inv().rotateLeft(53) * counter
        val temp = -(timestamp + 1).rotateRight(19) xor (timestamp-1).inv().rotateLeft(19)
        (seed or temp).toByte()
    }
}