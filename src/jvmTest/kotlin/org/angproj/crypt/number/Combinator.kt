package org.angproj.crypt.number

import org.angproj.crypt.dsa.BigInt
import java.math.BigInteger
import kotlin.random.Random
import kotlin.test.assertContentEquals

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
            val bi = BigInt.fromByteArray(it.toByteArray())
            val result: Pair<BigInt, BigInteger> = action(bi, it)
            assertContentEquals(result.first.toByteArray(), result.second.toByteArray())
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
                val result: Pair<BigInt, BigInteger> = action(xbi, ybi, x, y)
                assertContentEquals(result.first.toByteArray(), result.second.toByteArray())
            }
        }
    }
}