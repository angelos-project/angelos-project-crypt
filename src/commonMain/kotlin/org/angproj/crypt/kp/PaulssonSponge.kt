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


public typealias StateBuffer = LongArray
public typealias Sponge = Pair<StateBuffer, LongArray>

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public interface PaulssonSponge {
    public companion object {

        public const val stateSize: Int = 16
        public const val sideSize: Int = 4

        public val entropyState: List<Long> = listOf(
            2761899119146431915, 8177232236604328481, -2683022088977940051, 8297643565527658886,
            6314988026415961142, 7168096847635899458, 7028997605562976397, 5160888127703030859,
            2193126384059359087, -71050805361049744, -4636386313816014018, 789548178545734435,
            -4283424856243090559, -8124456396478961627, 6782707806115057139, 7514471421073573375
        )

        private val shufflePattern = arrayOf(
            8, 9, 1, 5, 12, 13, 0, 4, 11, 15, 3, 7, 10, 14, 2, 6)

        private val rotationPattern = arrayOf(
            37, 19, 17, 5, 47, 53, 2, 11, 43, 41, 23, 31, 3, 13, 29, 7)

        private fun shuffleState(state: StateBuffer) {
            val temp = state[0]
            (shufflePattern.lastIndex downTo 1).forEach { idx ->
                state[shufflePattern[idx]] = state[idx]
            }
            state[shufflePattern[0]] = temp
        }

        private fun rotateStateLeft(state: StateBuffer) {
            state.indices.forEach { idx ->
                state[idx] = state[idx].rotateLeft(rotationPattern[idx])
            }
        }

        private fun rotateStateRight(state: StateBuffer) {
            state.indices.forEach { idx ->
                state[idx] = state[idx].rotateRight(rotationPattern[idx])
            }
        }

        private fun oddNegateEvenInvertOnState(state: StateBuffer) {
            (state.indices step 2).forEach { idx ->
                state[idx] = state[idx].inv()
                state[idx + 1] = -state[idx + 1]
            }
        }

        private fun xorColFromStateRows(side: LongArray, state: StateBuffer): Unit = side.indices.forEach { idx ->
            val idy = idx * 4
            side[idx] = state[idy] xor state[idy + 1] xor state[idy + 2] xor state[idy + 3]
        }

        private fun xorMergeRowToStateCols(side: LongArray, state: StateBuffer): Unit = side.indices.forEach { idy ->
            state[idy] = side[idy] xor state[idy]
            state[idy + 4] = side[idy] xor state[idy + 4]
            state[idy + 8] = side[idy] xor state[idy + 8]
            state[idy + 12] = side[idy] xor state[idy + 12]
        }

        protected fun cycle(side: LongArray, state: StateBuffer) {
            xorColFromStateRows(side, state)
            shuffleState(state)
            rotateStateLeft(state)
            oddNegateEvenInvertOnState(state)
            xorMergeRowToStateCols(side, state)
        }

        protected fun cycleRight(side: LongArray, state: StateBuffer) {
            xorColFromStateRows(side, state)
            shuffleState(state)
            rotateStateRight(state)
            oddNegateEvenInvertOnState(state)
            xorMergeRowToStateCols(side, state)
        }

        @JvmStatic
        public fun scramble(state: Sponge) {
            repeat(15) { cycle(state.second, state.first) }
        }

        @JvmStatic
        public fun scrambleRight(mask: Sponge) {
            repeat(16) { cycleRight(mask.second, mask.first) }
        }

        @JvmStatic
        public fun absorb(inBuf: StateBuffer, state: Sponge) {
            for (idx in 0 until 16) state.first[idx] = state.first[idx] xor inBuf[idx]
            cycle(state.second, state.first)
        }

        @JvmStatic
        public fun squeeze(outBuf: StateBuffer, state: Sponge) {
            state.first.copyInto(outBuf)
            cycle(state.second, state.first)
        }

        @JvmStatic
        public fun simpleCipher(result: StateBuffer, mask: StateBuffer, state: Sponge): Unit = result.indices.forEach { idx ->
            result[idx] = mask[idx] xor state.first[idx]
        }

        @JvmStatic
        public fun sponge(): Sponge = Sponge(
            StateBuffer(stateSize) { idx -> entropyState[idx] },
            LongArray(sideSize)
        )

        @JvmStatic
        public fun sponge(from: Sponge): Sponge = Sponge(
            from.first.copyOf(),
            LongArray(sideSize)
        )

        @JvmStatic
        public fun sponge(from: StateBuffer): Sponge = Sponge(
            from.copyOf(),
            LongArray(sideSize)
        )

        @JvmStatic
        public fun buffer(): StateBuffer = StateBuffer(stateSize)

        @JvmStatic
        public fun buffer(from: Sponge): StateBuffer = from.first.copyOf()

        @JvmStatic
        public fun buffer(from: StateBuffer): StateBuffer = from.copyOf()
    }
}