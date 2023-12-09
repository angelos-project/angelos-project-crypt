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

import org.angproj.aux.util.EndianAware
import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.writeLongAt
import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine


/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public class Paulsson512Hash : HashEngine, EndianAware {

    private val h = longArrayOf(
        0xCEC3C1D60769F33BuL.toLong(),
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
    //private val h = longArrayOf(0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe , 0xf)
    //private val h = longArrayOf(-1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1)
    //private val h = longArrayOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    //private val h = longArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    private val w: LongArray = LongArray(16)

    protected var lasting: ByteArray = ByteArray(0)

    protected var count: Int = 0

    private fun push(chunk: ByteArray) = (0 until 16).forEach { i ->
        w[i] = chunk.readLongAt(i * wordSize).asBig()
    }

    private fun push(block: LongArray) = block.copyInto(w, 0, 0, 16)

    private fun transform() {
        w.forEach {
            h[0] = h[0] xor it
            val c = xorColFromStateRows(h)
            shuffleState(crissCrossShuffle, h)
            rotateStateLeft(primeParallelAscDescRotation, h)
            oddNegateEvenInvertOnState(h)
            xorMergeRowToStateCols(c, h)
        }
    }

    public override fun update(messagePart: ByteArray) {
        val buffer = lasting + messagePart
        lasting = ByteArray(0) // Setting an empty array

        (0..buffer.size step blockSize).forEach { i ->

            // Slicing the buffer in ranges of 64, if too small it's lasting.
            val chunk = try {
                messagePart.copyOfRange(i, i + blockSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = buffer.copyOfRange(i, buffer.size)
                return
            }

            push(chunk)
            transform()

            count += blockSize
        }
    }

    override val type: String
        get() = "Paulsson512"

    public override fun final(): ByteArray {
        count += lasting.size
        val blocksNeeded = if (lasting.size + 1 + 16 > blockSize) 2 else 1
        val blockLength = lasting.size / wordSize
        val end = LongArray(blocksNeeded * 16)

        (0 until blockLength).forEach { i ->
            end[i] = lasting.readLongAt(i * wordSize).asBig()
        }

        val remainder = (lasting.copyOfRange(
            blockLength * wordSize, lasting.size
        ) + 128.toByte()).copyOf(wordSize)
        end[blockLength] = remainder.readLongAt(0).asBig()

        val totalSize = count * 8L
        end[end.size - 1] = totalSize

        if (blocksNeeded == 1) {
            push(end)
            transform()
        } else {
            push(end.copyOfRange(0, 16))
            transform()
            push(end.copyOfRange(16, 32))
            transform()
        }

        val hash = ByteArray(h.size * wordSize)
        h.indices.forEach { hash.writeLongAt(it * wordSize, h[it].asBig()) }
        return hash
    }

    internal companion object: Hash {
        public override val name: String = "Paulsson-512"
        public override val blockSize: Int = 1024 / Byte.SIZE_BITS
        public override val wordSize: Int = 64 / Byte.SIZE_BITS
        public override val messageDigestSize: Int = 512 / Byte.SIZE_BITS

        override fun create(): HashEngine {
            TODO("Not yet implemented")
        }

        //  0  1  2  3
        //  4  5  6  7
        //  8  9 10 11
        // 12 13 14 15

        private val forwardShuffle = listOf(
            0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 6, 6 to 7, 7 to 8,
            8 to 9, 9 to 10, 11 to 12, 12 to 13, 13 to 14, 14 to 15, 15 to 0
        )

        private val fanPropellerShuffle = listOf(
            0 to 5, 5 to 4, 4 to 8, 8 to 12, 12 to 9, 9 to 13, 13 to 14, 14 to 15,
            15 to 10, 10 to 11, 11 to 7, 7 to 3, 3 to 6, 6 to 2, 2 to 1, 1 to 0,
        )

        private val crissCrossShuffle = listOf(
            8 to 13, 13 to 0, 0 to 5, 5 to 10, 10 to 15, 15 to 2, 2 to 7, 7 to 1,
            1 to 4, 4 to 3, 3 to 6, 6 to 9, 9 to 12, 12 to 11, 11 to 14, 14 to 8
        )

        /**
         * The purpose is to copy each value in a circular pattern but also in
         * and out of the inner circle. Zero to five is explicitly copied via a temp variable.
         */
        private fun shuffleState(shuffle: List<Pair<Int, Int>>, state: LongArray) {
            val temp = state[shuffle[0].first]
            (shuffle.lastIndex downTo 1).forEach { idx ->
                state[shuffle[idx].second] = state[shuffle[idx].first]
            }
            state[shuffle[0].second] = temp
        }

        private val primeParallelAscDescRotation = intArrayOf(
            61,  5, 53, 11, 43, 17, 37, 23,
            29, 31, 19, 41, 13, 47,  7, 59
        )

        private val primeSimpleAscendingRotation = intArrayOf(
             5,  7, 11, 13, 17, 19, 23, 29,
            31, 37, 41, 43, 47, 53, 59, 61
        )

        private val primeParallelAscendingRotation = intArrayOf(
             5, 31,  7, 37, 11, 41, 13, 43,
            17, 47, 19, 53, 23, 59, 29, 61
        )

        private val stepFourRotation = intArrayOf(
            0, 4, 8, 12, 16, 20, 24, 28,
            32, 36, 40, 44, 48, 52, 56, 60
        )

        private val stepDoubleAndFourRotation = intArrayOf(
             1,  2,  4,  8, 16, 32, 20, 24,
            28, 36, 40, 44, 48, 52, 56, 60
        )

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

        private fun xorColFromStateRows(state: LongArray): LongArray = LongArray(4).also { col ->
            col.indices.forEach { idx ->
                val idy = idx * 4
                col[idx] = state[idy] xor state[idy + 1] xor state[idy + 2] xor state[idy + 3]
            }
        }

        private fun xorMergeRowToStateCols(row: LongArray, state: LongArray): Unit = row.indices.forEach { idy ->
            state[idy] = row[idy] xor state[idy]
            state[idy + 4] = row[idy] xor state[idy + 4]
            state[idy + 8] = row[idy] xor state[idy + 8]
            state[idy + 12] = row[idy] xor state[idy + 12]
        }
    }
}