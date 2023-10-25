package org.angproj.crypt.number

import org.angproj.aux.util.BinHex
import org.angproj.aux.util.swapEndian
import org.angproj.aux.util.writeLongAt
import org.angproj.crypt.dsa.BigInt
import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

object Combinator {

    fun positiveMaxRange(): Long = Long.MAX_VALUE
    fun positiveLongRange(): Long = Random.nextLong(Int.MAX_VALUE.toLong()+1, Long.MAX_VALUE-1)
    fun positiveIntRange(): Long = Random.nextInt(0, Int.MAX_VALUE).toLong()
    fun zero(): Long = 0L
    fun negativeIntRange(): Long = Random.nextInt(Int.MIN_VALUE, -1).toLong()
    fun negativeLongRange(): Long = Random.nextLong(Long.MIN_VALUE+1, Int.MIN_VALUE.toLong()-1)
    fun negativeMinRange(): Long = Long.MIN_VALUE

    fun randomFiller(): Long = Random.nextLong()

    fun generateValueVector(): List<BigInteger>{
        val vector = mutableListOf<BigInteger>()

        (3 downTo 0).forEach {
            vector.add(produceFilledVector(positiveMaxRange(), it))
            vector.add(produceFilledVector(positiveLongRange(), it))
            vector.add(produceFilledVector(positiveIntRange(), it))
        }
        vector.add(BigInteger.valueOf(zero()))
        (0..3).forEach {
            vector.add(produceFilledVector(negativeIntRange(), it))
            vector.add(produceFilledVector(negativeLongRange(), it))
            vector.add(produceFilledVector(negativeMinRange(), it))
        }
        return vector.toList()
    }

    fun generateSizeVector(): List<Int> {
        val vector = mutableListOf<Int>()

        vector.add(Random.nextInt(1 until 8))
        vector.add(Random.nextInt(9 until 16))
        vector.add(Random.nextInt(17 until 24))
        vector.add(Random.nextInt(25 until 32))
        vector.add(Random.nextInt(33 until 40))
        vector.add(Random.nextInt(41 until 48))
        vector.add(Random.nextInt(49 until 56))
        vector.add(Random.nextInt(57 until 64))

        return vector.toList()
    }

    fun generateLongValueVector(): List<Long>{
        val vector = mutableListOf<Long>()

        //vector.add(Long.MAX_VALUE ushr 2-1)
        //vector.add(Random.nextLong(Int.MAX_VALUE.toLong(), Long.MAX_VALUE ushr 2 -1))
        vector.add(positiveMaxRange())
        vector.add(positiveLongRange())
            vector.add(positiveIntRange())
        vector.add(zero()) //-8388608..8388607
            vector.add(negativeIntRange())
        vector.add(negativeLongRange())
        vector.add(negativeMinRange())
        //vector.add(Random.nextLong(Int.MIN_VALUE.toLong(), Long.MIN_VALUE ushr 2+1))
        //vector.add(Random.nextLong(Long.MIN_VALUE ushr 2+1))
        return vector.toList()
    }

    fun produceFilledVector(significant: Long, fill: Int = 0): BigInteger {
        var output = BigInteger.valueOf(significant).toByteArray()
        (0 until fill).forEach { output += BigInteger.valueOf(randomFiller()).toByteArray() }
        return BigInteger(output)
    }

    fun doVectorTests(
        vector: List<BigInteger>,
        action: (cbi: BigInt, jbi: BigInteger) -> Pair<BigInt, BigInteger>
    ) {
        vector.forEach {
            println("H: ${BinHex.encodeToHex(it.toByteArray())}")
            val bi = BigInt.fromByteArray(it.toByteArray())
            val result: Pair<BigInt, BigInteger> = action(bi, it)
            println("D: ${result.second}")
            println("K: ${BinHex.encodeToHex(result.first.toByteArray())}")
            println("J: ${BinHex.encodeToHex( stripLeadingZeroBytesCorrection(result.second.toByteArray()))}\n")
            /*println("J: ${BinHex.encodeToHex(result.second.toByteArray())}\n")
            assertContentEquals(result.first.toByteArray(), stripLeadingZeroBytesCorrection(result.second.toByteArray()))*/
            assertContentEquals(result.first.toByteArray(), result.second.toByteArray())

        }
    }

    fun doLongVectorTests(
        vector: List<Long>,
        action: (cbi: BigInt, jbi: BigInteger) -> Pair<BigInt, BigInteger>
    ) {
        vector.forEach {
            println("H: ${BinHex.encodeToHex(ByteArray(8).also{ ba ->
                ba.writeLongAt(0, it.swapEndian())})}")
            val bi = BigInt.fromLong(it)
            val jbi = BigInteger.valueOf(it)
            val result: Pair<BigInt, BigInteger> = action(bi, jbi)
            println("D: ${result.second}")
            println("K: ${BinHex.encodeToHex(result.first.toByteArray())}")
            //println("J: ${BinHex.encodeToHex( stripLeadingZeroBytesCorrection(result.second.toByteArray()))}\n")
            println("J: ${BinHex.encodeToHex(result.second.toByteArray())}\n")
            assertContentEquals(result.first.toByteArray(), stripLeadingZeroBytesCorrection(result.second.toByteArray()))
            //assertContentEquals(result.first.toByteArray(), result.second.toByteArray())
            //assertEquals(result.first.longValueExact(), result.second.longValueExact())
        }
    }

    fun doMatrixTests(
        vector1: List<BigInteger>,
        vector2: List<BigInteger>,
        action: (xbi: BigInt, ybi: BigInt, x: BigInteger, y: BigInteger) -> Pair<BigInt, BigInteger>
    ) {
        vector1.forEach { x ->
            val xbi = BigInt.fromByteArray(x.toByteArray())
            vector2.forEach { y ->
                val ybi = BigInt.fromByteArray(y.toByteArray())
                println("Jx: ${BinHex.encodeToHex(x.toByteArray())}")
                println("Jy: ${BinHex.encodeToHex(y.toByteArray())}")
                println("Hx: ${BinHex.encodeToHex(xbi.toByteArray())}")
                println("Hy: ${BinHex.encodeToHex(ybi.toByteArray())}")
                val result: Pair<BigInt, BigInteger> = action(xbi, ybi, x, y)
                println("D: ${result.second}")
                println("K: ${BinHex.encodeToHex(result.first.toByteArray())}")
                println("J: ${BinHex.encodeToHex(result.second.toByteArray())}\n")
                /*assertContentEquals(result.first.toByteArray(), stripLeadingZeroBytesCorrection(result.second.toByteArray()))
                assertContentEquals(
                    stripLeadingZeroBytesCorrection(result.first.toByteArray()),
                    stripLeadingZeroBytesCorrection(result.second.toByteArray())
                )*/
                assertContentEquals(result.first.toByteArray(), result.second.toByteArray())
            }
        }
    }

    fun doMatrixIntTests(
        vector1: List<BigInteger>,
        vector2: List<BigInteger>,
        action: (xbi: BigInt, ybi: BigInt, x: BigInteger, y: BigInteger) -> Pair<Int, Int>
    ) {
        vector1.forEach { x ->
            val xbi = BigInt.fromByteArray(x.toByteArray())
            vector2.forEach { y ->
                val ybi = BigInt.fromByteArray(y.toByteArray())
                val result: Pair<Int, Int> = action(xbi, ybi, x, y)
                //println("D: ${result.second}")
                //println("K: ${BinHex.encodeToHex(result.first.toByteArray())}")
                //println("J: ${BinHex.encodeToHex( stripLeadingZeroBytesCorrection(result.second.toByteArray()))}\n")
                assertEquals(result.first, result.second)
            }
        }
    }

    fun doMatrixIntTests2(
        vector1: List<BigInteger>,
        vector2: List<Int>,
        action: (xbi: BigInt, x: BigInteger, s: Int) -> Pair<BigInt, BigInteger>
    ) {
        vector1.forEach { x ->
            val xbi = BigInt.fromByteArray(x.toByteArray())
            vector2.forEach {s ->
                println("J: ${BinHex.encodeToHex(x.toByteArray())}")
                println("H: ${BinHex.encodeToHex(xbi.toByteArray())}")
                println("S: $s")
                val result: Pair<BigInt, BigInteger> = action(xbi, x, s)
                println("D: ${result.second}")
                println("K: ${BinHex.encodeToHex(result.first.toByteArray())}")
                println("J: ${BinHex.encodeToHex(result.second.toByteArray())}\n")
                assertContentEquals(result.first.toByteArray(), result.second.toByteArray())
            }
        }
    }

    fun stripLeadingZeroBytesCorrection(value: ByteArray): ByteArray {
        val keep = when(val idx = value.indexOfFirst { it.toInt() != 0 }) {
            //-1 -> value.size
            //else -> idx
            -1 -> 0
            else -> idx
        }
        return value.copyOfRange(keep, value.size)
    }
}