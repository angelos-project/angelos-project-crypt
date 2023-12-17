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
package org.angproj.crypt.kp

import org.angproj.aux.util.writeIntAt
import org.angproj.aux.util.writeLongAt
import org.angproj.aux.util.writeShortAt

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class SimpleRandom(salt: Long = 0) {

    private var seed: Long = 4165467438104248319 + salt

    private fun cycle(): Long {
        seed = (seed + 1).inv().rotateLeft(31) xor -seed.rotateRight(43)
        return seed
    }

    public fun reseed(salt: Long) { seed += salt }

    public fun randLong(): Long = cycle()
    public fun randInt(): Int = cycle().toInt()
    public fun randShort(): Short = cycle().toShort()
    public fun randChar(): Char = Char(cycle().toInt())
    public fun randByte(): Byte = cycle().toByte()

    public fun randByteArray(data: ByteArray) {
        var offset = data.size - data.size.mod(Long.SIZE_BYTES)
        (data.indices step Long.SIZE_BYTES).forEach { data.writeLongAt(it, cycle()) }
        if (offset >= Int.SIZE_BYTES) {
            data.writeIntAt(offset, cycle().toInt())
            offset += Int.SIZE_BYTES }
        if (offset >= Short.SIZE_BYTES) {
            data.writeShortAt(offset, cycle().toShort())
            offset += Short.SIZE_BYTES }
        if (offset >= Byte.SIZE_BYTES) data[offset] = cycle().toByte()
    }
}


