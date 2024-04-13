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
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.writeIntAt
import org.angproj.aux.util.writeLongAt
import java.math.BigInteger as JavaBigInteger

object Debugger {

    private const val sigMagError = "sigNum and magnitude not equal, either bigger or not zero."
    private const val magSizeError = "Magnitude has leading zero integers."

    fun printValue(name: String, value: Int) {
        val arr = ByteArray(4)
        arr.writeIntAt(0, value)
        println("V$name: ${BinHex.encodeToHex(arr)}")
    }

    fun printValue(name: String, value: Long) {
        val arr = ByteArray(8)
        arr.writeLongAt(0, value)
        println("V$name: ${BinHex.encodeToHex(arr)}")
    }

    fun printValue(name: String, value: ByteArray) {
        println("V$name: ${BinHex.encodeToHex(value)}")
    }

    fun printDecimal(name: String, value: JavaBigInteger) {
        println("D$name: $value")
    }

    fun printJava(name: String, value: JavaBigInteger) {
        println("J$name: ${BinHex.encodeToHex(value.toByteArray())}")
    }

    fun printKotlin(name: String, value: BigInt) {
        println("K$name: ${BinHex.encodeToHex(value.toByteArray())}")
    }

    fun printJavaResult(value: JavaBigInteger) {
        println("Rj: ${BinHex.encodeToHex(value.toByteArray())}")
    }

    fun printKotlinRestult(value: BigInt) {
        println("Rk: ${BinHex.encodeToHex(value.toByteArray())}")
    }

    private fun keep(value: List<Int>, sigNum: BigSigned): Int {
        val keep = value.indexOfFirst { it != sigNum.signed }
        return when (keep) {
            -1 -> value.size
            else -> keep
        }
    }

    fun assertContentEquals(
        x: Long,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(rK.sigNum.isZero() and rK.mag.isEmpty() || rK.sigNum.isNonZero() and rK.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEquals(
        x: ByteArray,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(rK.sigNum.isZero() and rK.mag.isEmpty() || rK.sigNum.isNonZero() and rK.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }

    fun <E: Any> assertEquals(
        x: ByteArray,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: E,
        rK: E,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertEquals(
                rK,
                rJ
            )
            kotlin.test.assertTrue(kX.sigNum.isZero() and kX.mag.isEmpty() || kX.sigNum.isNonZero() and kX.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            println("Rj: $rJ")
            println("Rk: $rK")
            println("kM ${kX.mag.size}")
            println("kS ${kX.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }

    fun <E: Any> assertEquals(
        x: ByteArray,
        y: Int,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: E,
        rK: E,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertEquals(
                rK,
                rJ
            )
            kotlin.test.assertTrue(kX.sigNum.isZero() and kX.mag.isEmpty() || kX.sigNum.isNonZero() and kX.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            println("Vy: $y")
            printJava("x", jX)
            printKotlin("x", kX)
            println("Rj: $rJ")
            println("Rk: $rK")
            println("kM ${kX.mag.size}")
            println("kS ${kX.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }

    fun <E: Any> assertEquals(
        x: ByteArray,
        y: ByteArray,
        jX: JavaBigInteger,
        jY: JavaBigInteger,
        kX: BigInt,
        kY: BigInt,
        rJ: E,
        rK: E,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertEquals(
                rK,
                rJ
            )
            kotlin.test.assertTrue(kX.sigNum.isZero() and kX.mag.isEmpty() || kX.sigNum.isNonZero() and kX.mag.isNotEmpty(), sigMagError)
            kotlin.test.assertTrue(kY.sigNum.isZero() and kY.mag.isEmpty() || kY.sigNum.isNonZero() and kY.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printDecimal("x", jX)
            printValue("y", y)
            printJava("y", jY)
            printKotlin("y", kY)
            printDecimal("y", jY)
            println("Rj: $rJ")
            println("Rk: $rK")
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEquals(
        x: ByteArray,
        y: Int,
        jX: JavaBigInteger,
        kX: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(rK.sigNum.isZero() and rK.mag.isEmpty() || rK.sigNum.isNonZero() and rK.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            println("Vy: $y")
            printJava("x", jX)
            printKotlin("x", kX)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEquals(
        x: ByteArray,
        y: ByteArray,
        jX: JavaBigInteger,
        jY: JavaBigInteger,
        kX: BigInt,
        kY: BigInt,
        rJ: JavaBigInteger,
        rK: BigInt,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.toByteArray(),
                rJ.toByteArray()
            )
            kotlin.test.assertTrue(rK.sigNum.isZero() and rK.mag.isEmpty() || rK.sigNum.isNonZero() and rK.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printDecimal("x", jX)
            printValue("y", y)
            printJava("y", jY)
            printKotlin("y", kY)
            printDecimal("y", jY)
            printJavaResult(rJ)
            printKotlinRestult(rK)
            println("kM ${rK.mag.size}")
            println("kS ${rK.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }

    fun assertContentEqualsDouble(
        x: ByteArray,
        y: ByteArray,
        jX: JavaBigInteger,
        jY: JavaBigInteger,
        kX: BigInt,
        kY: BigInt,
        rJ: Array<JavaBigInteger>,
        rK: Pair<BigInt, BigInt>,
        fuzz: Boolean = false
    ) {
        try {
            kotlin.test.assertContentEquals(
                rK.first.toByteArray(),
                rJ.first().toByteArray()
            )
            kotlin.test.assertContentEquals(
                rK.second.toByteArray(),
                rJ.last().toByteArray()
            )
            kotlin.test.assertTrue(rK.first.sigNum.isZero() and rK.first.mag.isEmpty() || rK.first.sigNum.isNonZero() and rK.first.mag.isNotEmpty(), sigMagError)
            kotlin.test.assertTrue(rK.second.sigNum.isZero() and rK.second.mag.isEmpty() || rK.second.sigNum.isNonZero() and rK.second.mag.isNotEmpty(), sigMagError)
        } catch (e: AssertionError) {
            printValue("x", x)
            printJava("x", jX)
            printKotlin("x", kX)
            printDecimal("x", jX)
            printValue("y", y)
            printJava("y", jY)
            printKotlin("y", kY)
            printDecimal("y", jY)
            println("Rj A: ${BinHex.encodeToHex(rJ.first().toByteArray())}")
            println("Rk A: ${BinHex.encodeToHex(rK.first.toByteArray())}")
            println("Rj B: ${BinHex.encodeToHex(rJ.last().toByteArray())}")
            println("Rk B: ${BinHex.encodeToHex(rK.second.toByteArray())}")
            println("kM A ${rK.first.mag.size}")
            println("kS A ${rK.first.sigNum.state}")
            println("kM B ${rK.second.mag.size}")
            println("kS B ${rK.second.sigNum.state}")
            println()

            if(!fuzz) throw e
        }
    }
}