package org.angproj.crypt.dsa

import org.angproj.aux.util.BinHex
import org.angproj.crypt.number.*
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntBasicTest {

    val vectorList1 = Combinator.generateValueVector()
    val vectorList2 = Combinator.generateValueVector()

    fun compareImport(value: ByteArray) {
        val jbi = BigInteger(value)
        val cbi = bigIntOf(value)
        assertContentEquals(cbi.toByteArray(), jbi.toByteArray())
    }

    @Test
    fun plusTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.add(ybi), x.add(y))
        }
    }

    @Test
    fun minusTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi - ybi, x.subtract(y))
        }
    }

    @Test
    fun timesTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi * ybi, x.multiply(y))
        }
    }

    val testSubject1 = BigInteger(BinHex.decodeToBin("7fffffffffffffff79faf9c67d318e19ded9fe7c82c98aefdba98d4275014c8e"))
    val testSubject2 = BigInteger(BinHex.decodeToBin("5ff66b9fa07ae36f9cfe8ccc"))

    @Test
    fun divideAndRemainderTest() {
        //vectorList2.forEach { println(it) }
        /*Combinator.doMatrixPairTests(vectorList1.slice(0..7), listOf(vectorList2[8])) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder(ybi) , x.divideAndRemainder(y))
        }*/
        Combinator.doMatrixPairTests(listOf(testSubject1), listOf(testSubject2)) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder(ybi) , x.divideAndRemainder(y))
        }
    }

    @Test
    fun divideOneWordTest() {
        Combinator.doMatrixPairTests(vectorList1, vectorList2.slice(11..13)) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder(ybi) , x.divideAndRemainder(y))
        }
    }

    @Test
    fun compareToTest() {
        Combinator.doMatrixIntTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.compareTo(ybi).state, x.compareTo(y))
        }
    }

    @Test
    fun negateTest() {
        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(-xbi, x.negate())
        }
    }

    @Test
    fun absTest() {
        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(xbi.abs(), x.abs())
        }
    }
}