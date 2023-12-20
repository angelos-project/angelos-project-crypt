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

internal class Sha3224Hash(private val b: KeccakPValues = KeccakPValues.P_1600): AbstractKeccakHashEngine() {

    protected var string = ByteArray(permutationWidth.inByteSize)

    protected var lasting: ByteArray = byteArrayOf()

    protected var count: Int = 0

    private fun m(i: Int) = when(i) {
        0 -> 3
        1 -> 4
        2 -> 0
        3 -> 1
        4 -> 2
        else -> Int.MAX_VALUE
    }

    private fun createState() = Array(5) { Array(5) { BooleanArray(laneSize) } } // [x, y, z]
    private fun createPlane() = Array(5) { BooleanArray(laneSize) } // [x, z]

    private fun loopState(block: (idx: Int, idy: Int, idz: Int) -> Unit): Unit {
        for (x in 0 until 5) for (y in 0 until 5) for(z in 0 until laneSize) block(x, y, z)
    }

    private fun loopPlane(block: (idx: Int, idz: Int) -> Unit): Unit {
        for (x in 0 until 5) for(z in 0 until laneSize) block(x, z)
    }

    private fun loopAbsorb(block: (idx: Int, idy: Int, idz: Int) -> Unit): Unit {
        for (y in 0 until 5) for (x in 0 until 5) for(z in 0 until laneSize) block(x, y, z)
    }

    private fun loopPull(block: (idi: Int, idj: Int, ids: Int) -> Unit): Unit {
        for (j in 0 until 5) for (i in 0 until 5) block(i, j, i + j * 5)
    }

    private fun stepTheta(a: Array<Array<BooleanArray>>): Array<Array<BooleanArray>> {
        // In 3.2.1, p. 11
        val c = createPlane()
        loopPlane { idx, idz ->
            c[m(idx)][idz] = a[m(
                idx)][m(0)][idz] xor a[m(
                idx)][m(1)][idz] xor a[m(
                idx)][m(2)][idz] xor a[m(
                idx)][m(3)][idz] xor a[m(
                idx)][m(4)][idz]
        }

        val d = createPlane()
        loopPlane{ idx, idz ->
            d[m(idx)][idz] = c[(
                    m(idx) - 1).floorMod(5)][idz] xor c[(
                    m(idx)+1).floorMod(5)][(idz - 1).floorMod(5)]
        }

        val ad = createState()
        loopState { idx, idy, idz ->
            ad[m(idx)][m(idy)][idz] = a[m(idx)][m(idy)][idz] xor d[m(idx)][idz]
        }

        return ad
    }

    private fun stepRho(a: Array<Array<BooleanArray>>): Array<Array<BooleanArray>> {
        // In 3.2.2, p. 12
        val ad = createState()
        a[m(0)][m(0)].copyInto(ad[m(0)][m(0)])

        var idx = 1
        var idy = 0

        (0..23).forEach { idt ->
            (0 until laneSize).forEach { idz ->
                ad[m(idx)][m(idy)][idz] = a[m(idx)][m(idy)][(idz - (idt + 1) * (idt + 2) / 2).floorMod(laneSize)]
            }

            val tmp = idx
            idx = idy
            idy = (2 * tmp + 3 * idy).floorMod(5)
        }

        return ad
    }

    // FORTSÄTT HÄR !!!
    private fun stepPi(a: Array<Array<BooleanArray>>): Array<Array<BooleanArray>> {
        // In 3.2.3, p. 14
        val adState = createState()
        loopState { idx, idy, idz ->
            adState.setBitOfState(idx, idy, idz,
                aState.getBitOfState((idx + 3 * idy).floorMod(5), idx, idz)
            )
        }
        return adState
    }
    
    private fun stepChi(aState: LongArray): LongArray {
        val adState = createState()
        loopState { idx, idy, idz ->
            adState.setBitOfState(idx, idy, idz,
                aState.getBitOfState(idx, idy, idz
                ) xor ((aState.getBitOfState((idx + 1).floorMod(5), idy, idz
                ) xor true) and aState.getBitOfState((idx+2).floorMod(5), idy, idz)
                        ))
        }
        return adState
    }

    private fun roundConstant(t: Int): Boolean {
        if(t.floorMod(255) == 0) return true
        var r = booleanArrayOf(true, false, false, false, false, false, false, false)
        for(i in 1 until t.floorMod(255)) {
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
        /*val j = (-m-2).floorMod(x)
        val p = ByteArray(j / 8) */
        val p = ByteArray(x - m)
        p[0] = p[0].flipOnFlag7() // Normal non domain specific padding
        p[p.lastIndex] = p[p.lastIndex].flipOnFlag0()
        return p
    }

    private fun keccakRnd(aState: LongArray, i: Int): LongArray = stepIota(
        stepChi(stepPi(stepRho(stepTheta(aState)))), i)

    private fun keccakPRound(s: ByteArray, n: Int): ByteArray {
        var aState = absorb(s)

        for(i in(12 + 2 * b.log2 - n) until (12 + 2 * b.log2 - 1))
            aState = keccakRnd(aState, i)

        return squeeze(aState)
    }*/

    internal fun absorb(value: ByteArray): Array<Array<BooleanArray>> {
        // Should comply with 3.1.2, p. 9
        val aState = createState()
        loopAbsorb { idx, idy, idz ->
            val bitPos = laneSize * (5 * idy + idx) + idz
            //println("A[$idx, $idy, $idz] = S[$bitPos]")
            val ids = bitPos.floorDiv(8)
            val bit = when(bitPos.floorMod(8)) {
                7 -> value[ids].checkFlag0()
                6 -> value[ids].checkFlag1()
                5 -> value[ids].checkFlag2()
                4 -> value[ids].checkFlag3()
                3 -> value[ids].checkFlag4()
                2 -> value[ids].checkFlag5()
                1 -> value[ids].checkFlag6()
                0 -> value[ids].checkFlag7()
                else -> false
            }
            aState[m(idx)][m(idy)][idz] = bit
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

    private fun push(chunk: ByteArray): Unit {
        val p = chunk + ByteArray(capacitySize)
        string.indices.forEach { ids -> string[ids] = (string[ids].toInt() xor p[ids].toInt()).toByte() }
    }

    private fun transform() {
        //string = keccakPRound(string, rounds)
    }

    override fun update(messagePart: ByteArray) {
        val buffer = lasting + messagePart
        lasting = ByteArray(0) // Setting an empty array

        (0..buffer.size step rateSize).forEach { i ->

            // Slicing the buffer in ranges of 64, if too small it's lasting.
            val chunk = try {
                messagePart.copyOfRange(i, i + rateSize)
            } catch (_: IndexOutOfBoundsException) {
                lasting = buffer.copyOfRange(i, buffer.size)
                return
            }

            push(chunk)
            transform()

            count += rateSize
        }
    }

    public override fun final(): ByteArray {
        count += lasting.size

        if(lasting.size >= rateSize) {
            val first = lasting.copyOfRange(0, rateSize)
            lasting = lasting.copyOfRange(rateSize, lasting.size)
            push(first)
            transform()
        }

        /*val last = lasting.copyOfRange(0, lasting.size) + pad10_1(rateSize, count)
        push(last)
        transform()*/

        var z = byteArrayOf()
        while(true) {
            z += string.copyOfRange(0, rateSize)
            if(messageDigestSize < z.size) break
            transform()
        }
        return z.copyOfRange(0, messageDigestSize)
    }

    override val type: String
        get() = "SHA3 Under developlment"

    public companion object: Hash {
        public override val name: String = "${KeccakHashEngine.TYPE}3-224"
        public override val blockSize: Int = 1152.inByteSize
        public override val wordSize: Int = 64.inByteSize
        public override val messageDigestSize: Int = 224.inByteSize // d
        public val permutationWidth: Int = 1600 // b
        public val laneSize: Int = permutationWidth / 25 // w = b/25
        public val log2: Int = 6 // log2(b / 25) = log2(w)
        public val capacitySize: Int = messageDigestSize * 2 // d2
        public val rateSize: Int = permutationWidth.inByteSize - capacitySize // r
        public val rounds: Int = 24 // n

        //     P_1600(1600, 64, 6, 0xffffffffffffffffuL.toLong(), 200)

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