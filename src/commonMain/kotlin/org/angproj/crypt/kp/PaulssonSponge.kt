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
package org.angproj.crypt.kp

import org.angproj.aux.util.readLongAt
import kotlin.math.max
import kotlin.math.min

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class PaulssonSponge(iv: LongArray = LongArray(16), preScramble: Boolean = false) {

    private var side = LongArray(4)
    private var mask = LongArray(4)
    private var state = LongArray(16)
    private var counter: Long = 1

    init {
        if(iv.sum() != 0L) absorb(iv)
        if(preScramble) scrambleFull()
    }

    protected fun cycle() {
        xorColFromStateRows(side, state)
        shuffleState(state)
        rotateStateLeft(state)
        oddNegateEvenInvertOnState(state)
        nonLinearFeedbackFromAll(state, mask, counter)
        xorMergeRowToStateCols(side, state)
        counter = max(1, counter + 1)
    }

    public fun scramble(): Unit = repeat(15) { cycle() }

    public fun scrambleFull(): Unit = repeat(16) { cycle() }

    public fun absorbOne(inVal: Long) {
        state[0] = state[0] xor inVal
        cycle()
    }

    public fun absorb(inBuf: LongArray) {
        for (idx in 0 until min(16, inBuf.size)) state[idx] = state[idx] xor inBuf[idx]
        cycle()
    }

    public fun squeeze(outBuf: LongArray) {
        state.copyInto(outBuf)
        xorMergeMaskToOutput(mask, outBuf)
        cycle()
    }

    public fun squeezeOne(): Long {
        val outValue = state[0] xor mask[0]
        cycle()
        return outValue
    }

    public fun squeezeEnd(outBuf: LongArray) {
        state.copyInto(outBuf)
        xorMergeMaskToOutput(mask, outBuf)
    }

    private companion object {
        private val shufflePattern = arrayOf(
            8, 9, 1, 5, 12, 13, 0, 4, 11, 15, 3, 7, 10, 14, 2, 6)

        private val rotationPattern = arrayOf(
            37, 19, 17, 5, 47, 53, 2, 11, 43, 41, 23, 31, 3, 13, 29, 7)

        private fun shuffleState(state: LongArray) {
            val temp = state[0]
            (shufflePattern.lastIndex downTo 1).forEach { idx ->
                state[shufflePattern[idx]] = state[idx]
            }
            state[shufflePattern[0]] = temp
        }

        private fun rotateStateLeft(state: LongArray) {
            state.indices.forEach { idx ->
                state[idx] = state[idx].rotateLeft(rotationPattern[idx])
            }
        }

        private fun oddNegateEvenInvertOnState(state: LongArray) {
            state.indices.forEach { idx ->
                when(idx % 2) {
                    0 -> state[idx] = state[idx].inv()
                    1 -> state[idx] = -state[idx]
                }
            }
        }

        private fun nonLinearFeedbackFromAll(state: LongArray, mask: LongArray, counter: Long) {
            val idn = counter.mod(4)
            mask[idn] = (mask[0] and mask[1] and mask[2] and mask[3] and counter and state[0]) xor
                    ((state[1] and state[2] and state[3] and state[4] and state[5]) * 2) xor
                    ((state[6] and state[7] and state[8] and state[9]) * 4) xor
                    ((state[10] and state[11] and state[12]) * 8) xor
                    ((state[13] and state[14]) * 16) xor
                    (state[15] * 32)
        }

        private fun xorColFromStateRows(side: LongArray, state: LongArray): Unit = side.indices.forEach { idx ->
            val idy = idx * 4
            side[idx] = state[idy] xor state[idy + 1] xor state[idy + 2] xor state[idy + 3]
        }

        private fun xorMergeRowToStateCols(side: LongArray, state: LongArray): Unit = side.indices.forEach { idy ->
            state[idy] = side[idy] xor state[idy] + 1
            state[idy + 4] = side[idy] xor state[idy + 4]
            state[idy + 8] = side[idy] xor state[idy + 8]
            state[idy + 12] = side[idy] xor state[idy + 12]
        }

        private fun xorMergeMaskToOutput(mask: LongArray, state: LongArray): Unit = mask.indices.forEach { idx ->
            val idy = idx * 4
            state[idy] = mask[idx] xor state[idy]
            state[idy + 1] = mask[idx] xor state[idy + 1]
            state[idy + 2] = mask[idx] xor state[idy + 2]
            state[idy + 3] = mask[idx] xor state[idy + 3]
        }
    }
}

public fun scrambleLock(sponge: PaulssonSponge) {
    val history = LongArray(16)
    repeat(16) {
        history[it] = sponge.squeezeOne()
        sponge.absorb(history)
    }
}

public fun saltBytes(salt: ByteArray): LongArray {
    val data = salt + ByteArray(128 - salt.size.mod(128))
    val sponge = PaulssonSponge()
    val buffer = LongArray(16)
    (data.indices step 128).forEach { i ->
        buffer.indices.forEach { j -> buffer[j] = data.readLongAt(i + j * Long.SIZE_BYTES) }
        sponge.absorb(buffer)
    }
    sponge.scramble()
    sponge.squeezeEnd(buffer)
    return buffer
}