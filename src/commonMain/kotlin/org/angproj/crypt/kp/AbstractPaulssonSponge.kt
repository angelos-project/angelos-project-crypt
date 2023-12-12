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

import kotlin.jvm.JvmStatic

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public abstract class AbstractPaulssonSponge(protected val state: LongArray) {

    private val rc = LongArray(4)

    protected fun cycle() {
        xorColFromStateRows(rc, state)
        shuffleState(state)
        rotateStateLeft(state)
        oddNegateEvenInvertOnState(state)
        xorMergeRowToStateCols(rc, state)
    }

    public companion object {

        @JvmStatic
        protected val entropyState: List<Long> = listOf(
            2761899119146431915,
            8177232236604328481,
            -2683022088977940051,
            8297643565527658886,
            6314988026415961142,
            7168096847635899458,
            7028997605562976397,
            5160888127703030859,
            2193126384059359087,
            -71050805361049744,
            -4636386313816014018,
            789548178545734435,
            -4283424856243090559,
            -8124456396478961627,
            6782707806115057139,
            7514471421073573375,
        )

        @JvmStatic
        protected val shufflePattern: List<Int> = listOf(
            8, 9, 1, 5, 12, 13, 0, 4, 11, 15, 3, 7, 10, 14, 2, 6
        )

        @JvmStatic
        protected val rotationPattern: List<Int> = listOf(
            37, 19, 17, 5, 47, 53, 2, 11, 43, 41, 23, 31, 3, 13, 29, 7
        )

        @JvmStatic
        public fun shuffleState(state: LongArray) {
            val temp = state[0]
            (shufflePattern.lastIndex downTo 1).forEach { idx ->
                state[shufflePattern[idx]] = state[idx]
            }
            state[shufflePattern[0]] = temp
        }

        @JvmStatic
        public fun rotateStateLeft(state: LongArray) {
            state.indices.forEach { idx ->
                state[idx] = state[idx].rotateLeft(rotationPattern[idx])
            }
        }

        @JvmStatic
        public fun oddNegateEvenInvertOnState(state: LongArray) {
            (state.indices step 2).forEach { idx ->
                state[idx] = state[idx].inv()
                state[idx + 1] = -state[idx + 1]
            }
        }

        @JvmStatic
        public fun xorColFromStateRows(rc: LongArray, state: LongArray): Unit = rc.indices.forEach { idx ->
            val idy = idx * 4
            rc[idx] = state[idy] xor state[idy + 1] xor state[idy + 2] xor state[idy + 3]
        }

        @JvmStatic
        public fun xorMergeRowToStateCols(rc: LongArray, state: LongArray): Unit = rc.indices.forEach { idy ->
            state[idy] = rc[idy] xor state[idy]
            state[idy + 4] = rc[idy] xor state[idy + 4]
            state[idy + 8] = rc[idy] xor state[idy + 8]
            state[idy + 12] = rc[idy] xor state[idy + 12]
        }

        @JvmStatic
        public fun rotateMaskRight(rotation: IntArray, mask: LongArray) {
            mask.indices.forEach { idx ->
                mask[idx] = mask[idx].rotateRight(rotation[idx])
            }
        }

        @JvmStatic
        public fun oddInvertEvenNegateOnMAsk(mask: LongArray) {
            (mask.indices step 2).forEach { idx ->
                mask[idx] = -mask[idx]
                mask[idx + 1] = mask[idx + 1].inv()
            }
        }
    }
}