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
package org.angproj.crypt.keccak

import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine
import org.angproj.crypt.sha.ShaHashEngine

// https://csrc.nist.gov/Projects/cryptographic-algorithm-validation-program/Secure-Hashing
// https://csrc.nist.gov/pubs/fips/202/final

internal class Keccak224Hash(private val b: KeccakPValues = KeccakPValues.P_200): AbstractKeccakHashEngine() {

    private val xIndices = listOf(3, 4, 0, 1, 2)
    private val yIndices = xIndices

    private val state = LongArray(b.bWidth)

    protected var lasting: ByteArray = ByteArray(0)

    protected var count: Int = 0

    private fun createState(): LongArray = LongArray(5 * 5 * b.wSize)
    private fun createPlane(): LongArray = LongArray(5 * b.wSize)
    private fun createSlice(): LongArray = LongArray(5 * 5)
    private fun createSheet(): LongArray = LongArray(5 * b.wSize)
    private fun createRow(): LongArray = LongArray(5)
    private fun createColumn(): LongArray = LongArray(5)
    private fun createLane(): LongArray = LongArray(b.wSize)

    private fun LongArray.getBitOfState(x: Int, y: Int, z: Int): Long = this[b.wSize * (y + 5 * x) + z]
    private fun LongArray.setBitOfState(x: Int, y: Int, z: Int, value: Long) { this[b.wSize * (y + 5 * x) + z] = value }

    private fun LongArray.getBitOfPlane(x: Int, z: Int): Long = this[b.wSize * x + z]
    private fun LongArray.setBitOfPlane(x: Int, z: Int, value: Long) { this[b.wSize * x + z] = value }

    private fun stepTheta(aState: LongArray): LongArray {
        val cPlane = createPlane()
        (0 until 5).forEach { idx ->
            (0 until b.wSize).forEach { idz ->
                cPlane.setBitOfPlane(idx, idz, aState.getBitOfState(idx, 0, idz
                    ) xor aState.getBitOfState(idx, 1, idz
                    ) xor aState.getBitOfState(idx, 2, idz
                    ) xor aState.getBitOfState(idx, 3, idz
                    ) xor aState.getBitOfState(idx, 4, idz)
                )
            }
        }

        val dPlane = createPlane()
        (0 until 5).forEach { idx ->
            (0 until b.wSize).forEach { idz ->
                dPlane.setBitOfPlane(idx, idz,
                    cPlane.getBitOfPlane((idx - 1).mod(5), idz
                    ) xor cPlane.getBitOfPlane((idx + 1).mod(5), (idz - 1).mod(b.wSize))
                )
            }
        }

        val adState = createState()
        (0 until 5).forEach { idx ->
            (0 until 5).forEach { idy ->
                (0 until b.wSize).forEach { idz ->
                    adState.setBitOfState(idx, idy, idz,
                        aState.getBitOfState(idx, idy, idz
                        ) xor dPlane.getBitOfPlane(idx, idz)
                    )
                }
            }
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
        (0 until 5).forEach { idx ->
            (0 until 5).forEach { idy ->
                (0 until b.wSize).forEach { idz ->
                    adState.setBitOfState(idx, idy, idz,
                        aState.getBitOfState((idx + 3 * idy).mod(5), idx, idz)
                    )
                }
            }
        }
        return adState
    }
    private fun stepChi(aState: LongArray): LongArray {
        val adState = createState()
        (0 until 5).forEach { idx ->
            (0 until 5).forEach { idy ->
                (0 until b.wSize).forEach { idz ->
                    adState.setBitOfState(idx, idy, idz,
                        aState.getBitOfState(idx, idy, idz
                        ) xor ((aState.getBitOfState((idx + 1).mod(5), idy, idz
                        ) xor 1) and aState.getBitOfState((idx+2).mod (5), idy, idz)
                    ))
                }
            }
        }
        return adState
    }

    private fun stepIota(aState: LongArray): LongArray {
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

    override fun update(messagePart: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun final(): ByteArray {
        TODO("Not yet implemented")
    }

    override val type: String
        get() = "KECCAK224"

    public companion object: Hash {

        public override val name: String = "${KeccakHashEngine.TYPE}-224"
        public override val blockSize: Int = 1152 / ShaHashEngine.byteSize
        public override val wordSize: Int = 64 / ShaHashEngine.byteSize
        public override val messageDigestSize: Int = 224 / ShaHashEngine.byteSize

        override fun create(): HashEngine {
            TODO("Not yet implemented")
        }
    }

}