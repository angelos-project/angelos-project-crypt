package org.angproj.crypt.dsa

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

    /*@Test
    fun divideAndRemainderTest() {
        Combinator.doMatrixPairTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder(ybi) , x.divideAndRemainder(y))
        }
    }*/

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