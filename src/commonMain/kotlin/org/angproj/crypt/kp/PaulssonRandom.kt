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

import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.writeLongAt
import kotlin.jvm.JvmStatic

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class PaulssonRandom(salt: Long = 0, key: ByteArray = byteArrayOf()): AbstractPaulssonSponge(
    entropyState.toLongArray()
) {

    init {
        require(arrayOf(0, 16, 32, 64, 128).contains(key.size))
        seed(salt)
    }

    protected val mask: LongArray = maskFromKey(key)

    protected fun seed(salt: Long) {
        state[0] = state[0] xor salt
        repeat(16) { cycle() }
    }

    protected fun maskFromKey(key: ByteArray): LongArray = when(key.size == 0) {
        true -> longArrayOf()
        else -> LongArray(16) { idx ->
            key.readLongAt((idx * Long.SIZE_BYTES).mod(key.size / Long.SIZE_BYTES))
        }.also { mask ->
            rotateMaskRight(rotationPattern.toIntArray(), mask)
            oddInvertEvenNegateOnMAsk(mask)
        }
    }

    protected fun obfuscate(buffer: ByteArray, mask: LongArray, state: LongArray) {
        (0 until 16).forEach { idx ->
            buffer.writeLongAt(idx * Long.SIZE_BYTES, mask[idx] xor state[idx]) }
    }

    protected fun unobfuscated(buffer: ByteArray, state: LongArray) {
        (0 until 16).forEach { idx ->
            buffer.writeLongAt(idx * Long.SIZE_BYTES, state[idx]) }
    }

    public fun nextBytes(rand: ByteArray): ByteArray {
        when(mask.size == state.size) {
            true -> obfuscate(rand, mask, state)
            else -> unobfuscated(rand, state)
        }
        cycle()
        return rand
    }

    public fun nextState(): LongArray {
        cycle()
        return state.copyOf()
    }

    protected companion object {

        @JvmStatic
        protected val fourWithOffsetTwoRotation: IntArray = intArrayOf(
             2,  6, 10, 14, 18, 22, 26, 30,
            34, 38, 42, 46, 50, 54, 58, 62
        )
    }
}