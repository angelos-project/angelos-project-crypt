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
import kotlin.test.*
import java.math.BigInteger as JavaBigInteger

class MutableBigIntTest {

    fun mutableBigIntOf(value: ByteArray): MutableBigInt {
        return BigMath.fromByteArray(value) { m, s -> MutableBigInt(m.toMutableList(), s ) }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger interprets a ByteArray of some random values
     * the same when importing from the said ByteArray and exporting to a new ByteArray.
     * */
    @Test
    fun testByteArray() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = mutableBigIntOf(x)
            val xJbi = JavaBigInteger(x)

            assertContentEquals(xBi2.toByteArray(), xJbi.toByteArray())
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the sigNum of the same underlying value similarly.
     * */
    @Test
    fun testSigNum() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = mutableBigIntOf(x)
            val xJbi = JavaBigInteger(x)

            assertEquals(xBi2.sigNum.state, xJbi.signum())
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitLength of the same underlying value similarly.
     * */
    @Test
    fun testBitLength() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = mutableBigIntOf(x)
            val xJbi = JavaBigInteger(x)

            assertEquals(xBi2.bitLength, xJbi.bitLength())
        }
    }

    /**
     * This test recognizes that BigInt and Java BigInteger calculates
     * the bitCount of the same underlying value similarly.
     * */
    @Test
    fun testBitCount() {
        Combinator.numberGenerator(-64..64) { x ->
            val xBi2 = mutableBigIntOf(x)
            val xJbi = JavaBigInteger(x)

            assertEquals(xBi2.bitCount, xJbi.bitCount())
        }
    }
}