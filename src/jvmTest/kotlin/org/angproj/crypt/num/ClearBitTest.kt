/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.num

import org.angproj.aux.num.*
import org.angproj.aux.sec.SecureRandom
import java.lang.ArithmeticException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import java.math.BigInteger as JavaBigInteger


class ClearBitTest {
    /**
     * Generally fuzzes and validates that "public fun BigInt.clearBit(pos: Int): BigInt" works
     * under all normal conditions.
     * */
    @Test
    fun testClearBit() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = bigIntOf(x)
            val xJbi = JavaBigInteger(x)
            if(xBi2.sigNum.isZero()) return@numberGenerator
            (0..64).forEach { pos ->

                Debugger.assertContentEquals(
                    x, pos,
                    xJbi,
                    xBi2,
                    xJbi.clearBit(pos),
                    xBi2.clearBit(pos) // <- Emulation
                )
            }
        }
    }

    /**
     * Validates that a position beyond the magnitude is properly handled with modulus.
     * */
    @Test
    fun testPosBeyondMag() {
        val x = SecureRandom.read(13)
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertTrue(xBi2.mag.size * 32 < 200)
        assertContentEquals(
            xBi2.clearBit(200).toByteArray(),
            xJbi.clearBit(200).toByteArray()
        )
    }

    /**
     * Validates that a BigMathException is thrown if a negative position is given, and mimics Java.
     * */
    @Test
    fun testNegPos() {
        val x = SecureRandom.read(23)
        val xBi2 = bigIntOf(x)
        val xJbi = JavaBigInteger(x)

        assertFailsWith<BigMathException> { xBi2.clearBit(-100) }
        assertFailsWith<ArithmeticException> { xJbi.clearBit(-100) }
    }
}