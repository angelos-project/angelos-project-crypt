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

import org.angproj.aux.util.EndianAware
import org.angproj.aux.util.readLongAt
import org.angproj.crypt.Hash
import org.angproj.crypt.HashEngine

// https://github.com/brainhub/SHA3IUF/blob/master/sha3.c
// https://github.com/komputing/KHash/blob/master/keccak/src/commonMain/kotlin/org/komputing/khash/keccak/Keccak.kt

// https://csrc.nist.gov/Projects/cryptographic-algorithm-validation-program/Secure-Hashing
// https://csrc.nist.gov/pubs/fips/202/final

// https://github.com/AdoptOpenJDK/openjdk-jdk11/blob/master/src/java.base/share/classes/sun/security/provider/SHA3.java

// https://github.com/mchrapek/sha3-java

public abstract class AbstractKeccakHashEngine: KeccakHashEngine, EndianAware {

    protected val state: Array<Long> = Array(25) { 0 }

    protected val w: ByteArray = ByteArray(permutationSize)

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

    protected abstract fun pad(size: Int): ByteArray

    protected fun stepTheta(a: Array<Long>) {
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

    protected fun stepPiAndRho(a: Array<Long>) {
        val tmp: Long = a[10].rotateLeft(3)
        prTo.indices.forEach { i -> a[prTo[i]] = a[prFrom[i]].rotateLeft(prLeft[i]) }
        a[7] = tmp
    }

    protected fun stepChi(a: Array<Long>) {
        val y = arrayOf<Long>(0, 0, 0, 0, 0)

        (a.indices step 5).forEach { i ->
            a.copyInto(y, 0, i, i + 5)
            i5Loop { j ->
                a[i + j] = y[j] xor (y[(j + 1).mod(5)].inv() and y[(j + 2).mod(5)])
            }
        }
    }

   protected fun stepIota(a: Array<Long>, i: Int) {
        a[0] = a[0] xor roundConstants[i]
    }

    protected fun keccak(): Unit = (0 until rounds).forEach { i ->
        stepTheta(state)
        stepPiAndRho(state)
        stepChi(state)
        stepIota(state, i)
    }

    protected fun absorb(): Unit = ioLoop(blockSize) { i ->
        state[i / 8] = state[i / 8] xor w.readLongAt(i).asLittle()
    }

    protected abstract fun squeeze(): ByteArray

    protected fun push(chunk: ByteArray) {
        chunk.copyInto(w)
        absorb()
    }

    protected fun transform() {
        keccak()
    }

    protected fun updateWithSize(messagePart: ByteArray, blockSize: Int) {
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

    protected fun finalWithSize(blockSize: Int): ByteArray {
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

    public companion object : Hash {
        override val name: String = "${KeccakHashEngine.TYPE}3-224"
        override val blockSize: Int = 1344.inByteSize //1152.inByteSize // ? Why doesn't 1152 work or why can this not be set to -1 and continue work. FIND OUT
        override val wordSize: Int = 64.inByteSize
        override val messageDigestSize: Int = -1
        public val permutationSize: Int = 1600.inByteSize
        public val rounds: Int = 24
        override fun create(): HashEngine { throw UnsupportedOperationException("Don't invoke on an abstract class") }

        protected val prTo: IntArray = intArrayOf(
            10, 1, 6, 9, 22, 14, 20, 2, 12, 13, 19, 23, 15, 4, 24, 21, 8, 16, 5, 3, 18, 17, 11
        )
        protected val prFrom: IntArray = intArrayOf(
            1, 6, 9, 22, 14, 20, 2, 12, 13, 19, 23, 15, 4, 24, 21, 8, 16, 5, 3, 18, 17, 11, 7
        )
        protected val prLeft: IntArray = intArrayOf(
            1, 44, 20, 61, 39, 18, 62, 43, 25, 8, 56, 41, 27, 14, 2, 55, 45, 36, 28, 21, 15, 10, 6
        )

        protected val roundConstants: LongArray = longArrayOf(
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