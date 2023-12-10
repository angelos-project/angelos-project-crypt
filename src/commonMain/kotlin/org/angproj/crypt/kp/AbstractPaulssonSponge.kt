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

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public abstract class AbstractPaulssonSponge {

    protected val state: LongArray = LongArray(16)
    private val rc = LongArray(4)

    protected fun cycle() {
        xorColFromStateRows(rc, state)
        shuffleState(fanPropellerShuffle, state)
        rotateStateLeft(primeParallelAscDescRotation, state)
        oddNegateEvenInvertOnState(state)
        xorMergeRowToStateCols(rc, state)
    }

    protected fun reset() { start.copyInto(state) }

    protected companion object {

        private val start = longArrayOf(
            0xF95EE459B44D3AFBuL.toLong(),
            0xCF2524721D9D20F1uL.toLong(),
            0xE2983D21B9932D9DuL.toLong(),
            0xF1D564FE73279615uL.toLong(),
            0xF2B1571605DDD83FuL.toLong(),
            0xD113337954AE5BBDuL.toLong(),
            0xF5EEF9DB9430601FuL.toLong(),
            0xEAE5782F4E4A0E0DuL.toLong(),
            0xE4259C85787070DFuL.toLong(),
            0xDECBBF314C2D80B3uL.toLong(),
            0xDDCF79715B7C03E5uL.toLong(),
            0xD4D90604AA798353uL.toLong(),
            0xF4EC80F6B4640611uL.toLong(),
            0xC3FAD13DB2080321uL.toLong(),
            0xE6BED75357868AC5uL.toLong(),
            0xD5DC8181CDE9E23FuL.toLong()
        )

        private val fanPropellerShuffle = arrayOf(
            0 to 5, 5 to 4, 4 to 8, 8 to 12, 12 to 9, 9 to 13, 13 to 14, 14 to 15,
            15 to 10, 10 to 11, 11 to 7, 7 to 3, 3 to 6, 6 to 2, 2 to 1, 1 to 0,
        )

        private val primeParallelAscDescRotation = intArrayOf(
            61,  5, 53, 11, 43, 17, 37, 23,
            29, 31, 19, 41, 13, 47,  7, 59
        )

        private fun shuffleState(shuffle: Array<Pair<Int, Int>>, state: LongArray) {
            val temp = state[shuffle[0].first]
            (shuffle.lastIndex downTo 1).forEach { idx ->
                state[shuffle[idx].second] = state[shuffle[idx].first]
            }
            state[shuffle[0].second] = temp
        }

        private fun rotateStateLeft(rotation: IntArray, state: LongArray) {
            state.indices.forEach { idx ->
                state[idx] = state[idx].rotateLeft(rotation[idx])}
        }

        private fun oddNegateEvenInvertOnState(state: LongArray) {
            (state.indices step 2).forEach { idx ->
                state[idx] = state[idx].inv()
                state[idx + 1] = -state[idx + 1]
            }
        }

        private fun xorColFromStateRows(rc: LongArray, state: LongArray): Unit =  rc.indices.forEach { idx ->
            val idy = idx * 4
            rc[idx] = state[idy] xor state[idy + 1] xor state[idy + 2] xor state[idy + 3]
        }

        private fun xorMergeRowToStateCols(rc: LongArray, state: LongArray): Unit = rc.indices.forEach { idy ->
            state[idy] = rc[idy] xor state[idy]
            state[idy + 4] = rc[idy] xor state[idy + 4]
            state[idy + 8] = rc[idy] xor state[idy + 8]
            state[idy + 12] = rc[idy] xor state[idy + 12]
        }
    }
}