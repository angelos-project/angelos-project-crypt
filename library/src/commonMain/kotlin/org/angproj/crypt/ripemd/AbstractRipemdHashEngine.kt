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
package org.angproj.crypt.ripemd

import org.angproj.io.buf.util.UtilityAware
import org.angproj.crypt.Crypto
import org.angproj.crypt.sha.RipemdHashEngine
import kotlin.jvm.JvmStatic

public abstract class AbstractRipemdHashEngine : RipemdHashEngine, UtilityAware {

    protected abstract val h: IntArray

    protected var lasting: ByteArray = ByteArray(0)

    protected var count: Int = 0

    private fun push(chunk: ByteArray): IntArray = IntArray(16).also {
        it.indices.forEach { idx -> it[idx] = chunk.readIntAt(idx * wordSize) }  // Little Endian
    }

    protected abstract fun transform(x: IntArray)
    override fun update(messagePart: ByteArray) {
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

            transform(push(chunk))
            count += blockSize
        }
    }

    override fun final(): ByteArray {
        count += lasting.size
        val bitCount: Long = count * 8L // WARNING! It's sensitive that bitCount is a Long.
        val buffer = lasting + byteArrayOf(128.toByte(), 0, 0, 0).copyOfRange(0, 4 - lasting.size.rem(4))

        val x = IntArray(16)
        val wordCount = buffer.size.floorDiv(wordSize)
        (0 until wordCount).forEach { idx -> x[idx] = buffer.readIntAt(idx * wordSize) }  // Little Endian

        if (wordCount > 14) {
            transform(x)
            x.fill(0)
        }

        x[14] = (bitCount and -0x1).toInt()
        x[15] = (bitCount ushr Int.SIZE_BITS).toInt()
        transform(x)

        val hash = ByteArray(h.size * wordSize)
        h.indices.forEach { hash.writeIntAt(it * wordSize, h[it]) }  // Little Endian
        return hash
    }

    public companion object : Crypto {
        public val blockSize: Int = 512.inByteSize
        public val wordSize: Int = 32.inByteSize

        @JvmStatic
        protected fun f(j: Int, x: Int, y: Int, z: Int): Int = when (j) {
            in 0..15 -> x xor y xor z
            in 16..31 -> (x and y) or (x.inv() and z)
            in 32..47 -> (x or y.inv()) xor z
            in 48..63 -> (x and z) or (y and z.inv())
            in 64..79 -> x xor (y or z.inv())
            else -> error("Can't happen")
        }

        @JvmStatic
        protected val r0: IntArray = intArrayOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
            4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
        )

        @JvmStatic
        protected val r1: IntArray = intArrayOf(
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
            12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
        )

        @JvmStatic
        protected val s0: IntArray = intArrayOf(
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
            9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
        )

        @JvmStatic
        protected val s1: IntArray = intArrayOf(
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
            8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
        )

        override fun create(): Any {
            TODO("Not yet implemented")
        }
    }
}