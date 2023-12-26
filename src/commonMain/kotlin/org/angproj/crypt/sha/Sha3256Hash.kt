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

import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.writeLongAt
import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine
import org.angproj.crypt.keccak.AbstractKeccakHashEngine
import org.angproj.crypt.keccak.KeccakHashEngine

internal class Sha3256Hash : AbstractKeccakHashEngine() {

    private val state = Array<Long>(25) { 0 }

    private val w = ByteArray(permutationSize)

    protected var lasting: ByteArray = byteArrayOf()

    protected var count: Int = 0

    protected fun ioLoop(breakAt: Int, block: (i: Int) -> Unit) {
        for (i in 0 until permutationSize step wordSize) {
            block(i)
            if (i >= breakAt) break
        }
    }

    protected fun i5Loop(block: (i: Int) -> Unit) {
        for (i in 0 until 5) block(i)
    }

    protected fun pad(size: Int): ByteArray = when (size) {
        1 -> byteArrayOf(-122)
        else -> ByteArray(size).also {
            it[0] = 6
            it[size - 1] = -128
        }
    }

    private fun stepTheta(a: Array<Long>) {
        val c = arrayOf<Long>(0, 0, 0, 0, 0)
        val d = arrayOf<Long>(0, 0, 0, 0, 0)

        i5Loop { i ->
            c[i] = a[i + 0] xor a[i + 5] xor a[i + 10] xor a[i + 15] xor a[i + 20]
        }

        i5Loop { i ->
            d[i] = c[(i + 4).mod(5)] xor c[(i + 1).mod(5)].rotateLeft(1)
        }

        a.indices.forEach { i ->
            a[i] = a[i] xor d[i.mod(5)]
        }
    }

    private fun stepPiAndRho(a: Array<Long>) {
        val tmp: Long = a[10].rotateLeft(3)
        prTo.indices.forEach { i -> a[prTo[i]] = a[prFrom[i]].rotateLeft(prLeft[i]) }
        a[7] = tmp
    }

    private fun stepChi(a: Array<Long>) {
        val y = arrayOf<Long>(0, 0, 0, 0, 0)

        (a.indices step 5).forEach { i ->
            a.copyInto(y, 0, i, i + 5)
            i5Loop { j ->
                a[i + j] = y[j] xor (y[(j + 1).mod(5)].inv() and y[(j + 2).mod(5)])
            }
        }
    }

    private fun stepIota(a: Array<Long>, i: Int) {
        a[0] = a[0] xor roundConstants[i]
    }

    fun keccak(): Unit = (0 until rounds).forEach { i ->
        stepTheta(state)
        stepPiAndRho(state)
        stepChi(state)
        stepIota(state, i)
    }


    fun absorb(): Unit = ioLoop(blockSize) { i ->
        state[i / 8] = state[i / 8] xor w.readLongAt(i).asLittle()
    }

    fun squeeze(): ByteArray {
        w.fill(0)
        ioLoop(messageDigestSize) { i ->
            w.writeLongAt(i, state[i / 8].asLittle())
        }
        return w.copyOfRange(0, messageDigestSize)
    }

    private fun push(chunk: ByteArray) {
        chunk.copyInto(w)
        absorb()
    }

    private fun transform() {
        keccak()
    }

    override fun update(messagePart: ByteArray) {
        val buffer = lasting + messagePart
        lasting = byteArrayOf(0) // Setting an empty array

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

    override fun final(): ByteArray {
        when (lasting.size != blockSize) {
            true -> {
                push(lasting + pad(blockSize - lasting.size))
                transform()
            }

            else -> {
                push(lasting)
                transform()
                push(pad(blockSize))
                transform()
            }
        }

        return squeeze()
    }

    override val type: String
        get() = "SHA3 Under developlment"

    companion object : Hash {
        override val name: String = "${KeccakHashEngine.TYPE}3-256"
        override val blockSize: Int = 1088.inByteSize
        override val wordSize: Int = 64.inByteSize
        override val messageDigestSize: Int = 256.inByteSize
        val permutationSize: Int = 1600.inByteSize
        val rounds: Int = 24

        override fun create(): HashEngine {
            TODO("Not yet implemented")
        }

        protected val prTo = intArrayOf(
            10, 1, 6, 9, 22, 14, 20, 2, 12, 13, 19, 23, 15, 4, 24, 21, 8, 16, 5, 3, 18, 17, 11
        )
        protected val prFrom = intArrayOf(
            1, 6, 9, 22, 14, 20, 2, 12, 13, 19, 23, 15, 4, 24, 21, 8, 16, 5, 3, 18, 17, 11, 7
        )
        protected val prLeft = intArrayOf(
            1, 44, 20, 61, 39, 18, 62, 43, 25, 8, 56, 41, 27, 14, 2, 55, 45, 36, 28, 21, 15, 10, 6
        )

        protected val roundConstants = longArrayOf(
            1L,
            32898L,
            -9223372036854742902L,
            -9223372034707259392L,
            32907L,
            2147483649L,
            -9223372034707259263L,
            -9223372036854743031L,
            138L,
            136L,
            2147516425L,
            2147483658L,
            2147516555L,
            -9223372036854775669L,
            -9223372036854742903L,
            -9223372036854743037L,
            -9223372036854743038L,
            -9223372036854775680L,
            32778L,
            -9223372034707292150L,
            -9223372034707259263L,
            -9223372036854742912L,
            2147483649L,
            -9223372034707259384L
        )
    }
}