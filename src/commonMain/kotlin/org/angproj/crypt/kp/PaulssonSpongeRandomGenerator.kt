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
package org.angproj.crypt.kp

import org.angproj.aux.util.floorMod
import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.toByteArray
import org.angproj.crypt.c.RandomGenerator
import kotlin.math.min

public class PaulssonSpongeRandomGenerator:  PaulssonSponge(), RandomGenerator {
    override fun addSeedMaterial(seed: ByteArray) {
        val buffer = seed + ByteArray(128 - seed.size.floorMod(128))
        val inBuf = LongArray(16)
        repeat(buffer.size.floorDiv(128)) {
            val offset = it * 128
            inBuf.forEachIndexed { index, l ->
                inBuf[index] = buffer.readLongAt(offset + index * Long.SIZE_BYTES) }
            absorb(inBuf)
        }
        scramble()
    }

    override fun addSeedMaterial(seed: Long) {
        absorb(longArrayOf(seed))
        scramble()
    }

    override fun nextBytes(bytes: ByteArray) {
        val outBuf = LongArray(16)
        (0..bytes.size step 128).forEach {
            squeeze(outBuf)
            outBuf.toByteArray().copyInto(bytes, it, 0, min(128, bytes.size - it))
        }
    }

    override fun nextBytes(bytes: ByteArray, start: Int, len: Int) {
        val outBuf = LongArray(16)
        (0..len step 128).forEach {
            squeeze(outBuf)
            outBuf.toByteArray().copyInto(bytes, start + it, 0, min(128, len - it))
        }
    }
}