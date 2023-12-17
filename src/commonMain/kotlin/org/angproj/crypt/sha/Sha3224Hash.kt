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
package org.angproj.crypt.sha

import org.angproj.aux.util.*
import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine
import org.angproj.crypt.keccak.AbstractKeccakHashEngine
import org.angproj.crypt.keccak.KeccakHashEngine
import org.angproj.crypt.keccak.KeccakPValues

// https://github.com/brainhub/SHA3IUF/blob/master/sha3.c
// https://github.com/komputing/KHash/blob/master/keccak/src/commonMain/kotlin/org/komputing/khash/keccak/Keccak.kt

// https://csrc.nist.gov/Projects/cryptographic-algorithm-validation-program/Secure-Hashing
// https://csrc.nist.gov/pubs/fips/202/final

internal class Sha3224Hash(private val b: KeccakPValues = KeccakPValues.P_200): AbstractKeccakHashEngine() {

    private val xIndices = listOf(3, 4, 0, 1, 2)
    private val yIndices = xIndices

    private val state = createState()

    private val a = createState()

    protected var lasting: ByteArray = byteArrayOf()

    protected var count: Int = 0

    private fun createState(): LongArray = LongArray(5 * 5)
    private fun createPlane(): LongArray = LongArray(5)

    private fun LongArray.getBitOfState(x: Int, y: Int, z: Int): Boolean = this[y + 5 * x] and (1 shl (b.wSize - z - 1)).toLong() != 0L
    private fun LongArray.setBitOfState(x: Int, y: Int, z: Int, v: Boolean) {
        val ids = y + 5 * x
        val mask = (1 shl (b.wSize - z - 1)).toLong()
        when(v) {
            true -> this[ids] = this[ids] or mask
            else -> this[ids] = this[ids] and mask.inv()
        }
    }

    private fun LongArray.getBitOfPlane(x: Int, z: Int): Boolean = this[x] and (1 shl (b.wSize - z - 1)).toLong() != 0L
    private fun LongArray.setBitOfPlane(x: Int, z: Int, v: Boolean)  {
        val mask = (1 shl (b.wSize - z - 1)).toLong()
        when(v) {
            true -> this[x] = this[x] or mask
            else -> this[x] = this[x] and mask.inv()
        }
    }

    private fun loopState(block: (idx: Int, idy: Int, idz: Int) -> Unit): Unit {
        for (x in 0 until 5) for (y in 0 until 5) for(z in 0 until b.wSize) block(x, y, z)
    }

    private fun loopPush(block: (idx: Int, idy: Int, idz: Int) -> Unit): Unit {
        for (y in 0 until 5) for (x in 0 until 5) for(z in 0 until b.wSize) block(x, y, z)
    }

    private fun loopPull(block: (idi: Int, idj: Int, ids: Int) -> Unit): Unit {
        for (j in 0 until 5) for (i in 0 until 5) block(i, j, i + j * 5)
    }

    private fun loopPlane(block: (idx: Int, idz: Int) -> Unit): Unit {
        for (x in 0 until 5) for(z in 0 until b.wSize) block(x, z)
    }

    private fun stepTheta(aState: LongArray): LongArray {
        val cPlane = createPlane()
        loopPlane { idx, idz ->
            cPlane.setBitOfPlane(idx, idz, aState.getBitOfState(idx, 0, idz
            ) xor aState.getBitOfState(idx, 1, idz
            ) xor aState.getBitOfState(idx, 2, idz
            ) xor aState.getBitOfState(idx, 3, idz
            ) xor aState.getBitOfState(idx, 4, idz)
            )
        }

        val dPlane = createPlane()
        loopPlane { idx, idz ->
            dPlane.setBitOfPlane(idx, idz,
                cPlane.getBitOfPlane((idx - 1).mod(5), idz
                ) xor cPlane.getBitOfPlane((idx + 1).mod(5), (idz - 1).mod(b.wSize))
            )
        }

        val adState = createState()
        loopState { idx, idy, idz ->
            adState.setBitOfState(idx, idy, idz,
                aState.getBitOfState(idx, idy, idz
                ) xor dPlane.getBitOfPlane(idx, idz)
            )
        }
        return adState
    }

    private fun stepRho(aState: LongArray): LongArray {
        val adState = createState()
        (0 until b.wSize).forEach { idz ->
            adState.setBitOfState(0, 0, idz, aState.getBitOfState(0, 0, idz)) }

        var idx = 1
        var idy = 0
        (0 until 24).forEach { idt ->
            (0 until b.wSize).forEach { idz ->
                adState.setBitOfState(
                    idx, idy, idz,
                    aState.getBitOfState(
                        idx, idy, (idz - (idt + 1)*(idt + 2) / 2).mod(b.wSize)
                    )
                )
            }
            val tmp = (2 * idx + 3 * idy).mod(5)
            idx = idy
            idy = tmp
        }

        return adState
    }

    private fun stepPi(aState: LongArray): LongArray {
        val adState = createState()
        loopState { idx, idy, idz ->
            adState.setBitOfState(idx, idy, idz,
                aState.getBitOfState((idx + 3 * idy).mod(5), idx, idz)
            )
        }
        return adState
    }
    private fun stepChi(aState: LongArray): LongArray {
        val adState = createState()
        loopState { idx, idy, idz ->
            adState.setBitOfState(idx, idy, idz,
                aState.getBitOfState(idx, idy, idz
                ) xor ((aState.getBitOfState((idx + 1).mod(5), idy, idz
                ) xor true) and aState.getBitOfState((idx+2).mod (5), idy, idz)
                        ))
        }
        return adState
    }

    private fun roundConstant(t: Int): Boolean {
        if(t.mod(255) == 0) return true
        var r = booleanArrayOf(true, false, false, false, false, false, false, false)
        for(i in 1 until t.mod(255)) {
            r = booleanArrayOf(false) + r // a
            r[0] = r[0] xor r[8] // b
            r[4] = r[4] xor r[8] // c
            r[5] = r[5] xor r[8] // d
            r[6] = r[6] xor r[8] // e
            r = r.sliceArray(0 until 8)
        }
        return r[0]
    }

    private fun stepIota(aState: LongArray, i: Int): LongArray {
        val adState = aState.copyOf()
        val rc = BooleanArray(b.wSize)

        for (j in 0 until b.log2)
            rc[(1 shl j) - 1] = roundConstant(j + 7 * i)

        for(z in 0 until b.wSize)
            adState.setBitOfState(0, 0, z, adState.getBitOfState(0, 0, z) xor rc[z])

        return aState
    }

    private fun pad10_1(x: Int, m: Int): ByteArray {
        val j = (-m-2).mod(x)
        val p = ByteArray(j / 8)
        p[0].flipOnFlag7()
        p[p.lastIndex].flipOnFlag0()
        return p
    }

    private fun keccakPRound(s: ByteArray, n: Int): ByteArray {
        var aState = absorb(s)

        for(i in(12 + 2 * b.log2 - n) until (12 + 2 * b.log2 - 1))
            aState = stepIota(stepChi(stepPi(stepRho(stepTheta(aState)))), i)

        return squeeze(aState)
    }

    private fun absorb(value: ByteArray): LongArray {
        val aState = createState()
        loopPush { idx, idy, idz ->
            val pos = b.wSize * (5 * idy + idx) + idz
            val g = pos.floorDiv(8)
            val bit = when(pos.mod(8)) {
                7 -> value[g].checkFlag0()
                6 -> value[g].checkFlag1()
                5 -> value[g].checkFlag2()
                4 -> value[g].checkFlag3()
                3 -> value[g].checkFlag4()
                2 -> value[g].checkFlag5()
                1 -> value[g].checkFlag6()
                0 -> value[g].checkFlag7()
                else -> false
            }
            a.setBitOfState(idx, idy, idz, bit)
        }
        return aState
    }

    private fun squeeze(aState: LongArray): ByteArray {
        val s = ByteArray(b.bSize)
        loopPull { idi, idj, ids ->
            s.writeLongAt(ids, aState[idj + 5 * idi])
        }
        return s
    }

    private fun rotateLeft64(x: Long, y: Int): Long = (x shl y) or (x shr (64 - y))

    private fun push(chunk: ByteArray) = loopPush { idx, idy, idz ->
        val pos = b.wSize * (5 * idy + idx) + idz
        val g = pos.floorDiv(8)
        val bit = when(pos.mod(8)) {
            7 -> chunk[g].checkFlag0()
            6 -> chunk[g].checkFlag1()
            5 -> chunk[g].checkFlag2()
            4 -> chunk[g].checkFlag3()
            3 -> chunk[g].checkFlag4()
            2 -> chunk[g].checkFlag5()
            1 -> chunk[g].checkFlag6()
            0 -> chunk[g].checkFlag7()
            else -> false
        }
        a.setBitOfState(idx, idy, idz, bit)
    }

    fun transform() {}

    override fun update(messagePart: ByteArray) {
        val buffer = lasting + messagePart
        lasting = ByteArray(0) // Setting an empty array

        (0..buffer.size step b.bSize).forEach { i ->

            // Slicing the buffer in ranges of 64, if too small it's lasting.
            val chunk = try {
                messagePart.copyOfRange(i, i + b.bSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = buffer.copyOfRange(i, buffer.size)
                return
            }

            push(chunk)
            transform()

            count += b.bSize
        }
    }

    public override fun final(): ByteArray {
        val hash = ByteArray(b.bSize)
        loopPull { idi, idj, ids ->
            hash.writeLongAt(ids, state[idj + 5 * idi])
        }
       /* count += lasting.size
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
        h.indices.forEach { hash.writeLongAt(it * wordSize, h[it].asBig()) }*/
        return hash
    }

    override val type: String
        get() = "SHA3 Under developlment"

    public companion object: Hash {
        public override val name: String = "${KeccakHashEngine.TYPE}3-224"
        public override val blockSize: Int = 1152 / ShaHashEngine.byteSize
        public override val wordSize: Int = 64 / ShaHashEngine.byteSize
        public override val messageDigestSize: Int = 224 / ShaHashEngine.byteSize

        override fun create(): HashEngine {
            TODO("Not yet implemented")
        }

        protected val rotationOffset = arrayOf(
            //         y0  y1  y2  y3  y4
            intArrayOf( 0, 36,  3, 41, 18), // x0
            intArrayOf( 1, 44, 10, 45,  2), // x1
            intArrayOf(62,  6, 43, 15, 61), // x2
            intArrayOf(28, 55, 25, 21, 56), // x3
            intArrayOf(27, 20, 39,  8, 14), // x4
        )

        protected val roundConstants = longArrayOf(
            0x0000000000000001L,
            0x0000000000008082L,
            0x800000000000808AuL.toLong(),
            0x8000000080008000uL.toLong(),
            0x000000000000808BL,
            0x0000000080000001L,
            0x8000000080008081uL.toLong(),
            0x8000000000008009uL.toLong(),
            0x000000000000008AL,
            0x0000000000000088L,
            0x0000000080008009L,
            0x000000008000000AL,
            0x000000008000808BL,
            0x800000000000008BuL.toLong(),
            0x8000000000008089uL.toLong(),
            0x8000000000008003uL.toLong(),
            0x8000000000008002uL.toLong(),
            0x8000000000000080uL.toLong(),
            0x000000000000800AL,
            0x800000008000000AuL.toLong(),
            0x8000000080008081uL.toLong(),
            0x8000000000008080uL.toLong(),
            0x0000000080000001L,
            0x8000000080008008uL.toLong()
        )
    }
}