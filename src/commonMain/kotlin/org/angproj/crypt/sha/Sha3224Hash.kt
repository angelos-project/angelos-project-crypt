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

    protected var lasting: ByteArray = ByteArray(0)

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

    private fun stepIota(aState: LongArray): LongArray {
         TODO("!!!")
        return aState
    }

    private fun fromFlatToStructure(): Array<Array<LongArray>> = Array(5) { idx ->
        Array(5) { idy ->
            val ids = b.wSize * (idy + 5 * idx)
            println("x:$idx , y: $idy, z: 0-${b.wSize-1}; S: $ids-${ids + b.wSize-1};")
            state.copyOfRange(ids, ids + b.wSize)
        }
    }

    private fun absorb(value: ByteArray): Unit = value.forEachIndexed { idx, byte ->
        state[idx] = state[idx] xor (byte.toLong() and 0xff)
    }
    private fun rotateLeft64(x: Long, y: Int): Long = (x shl y) or (x shr (64 - y))

    override val type: String
        get() = "SHA3 Under developlment"

    override fun update(messagePart: ByteArray) {
    }

    override fun final(): ByteArray {
        return byteArrayOf(12, 43, 56, -78)
    }

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