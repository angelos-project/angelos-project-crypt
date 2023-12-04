package org.angproj.crypt

import org.angproj.aux.util.BinHex
import org.angproj.crypt.dsa.*
import org.angproj.crypt.number.*
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals


public fun AbstractBigInt<*>.divideAndRemainder2(value: AbstractBigInt<*>): Pair<AbstractBigInt<*>, AbstractBigInt<*>> =
    when {
        value.sigNum.isZero() -> error { "Divisor is zero" }
        value.compareTo(BigInt.one) == BigCompare.EQUAL -> Pair(this, BigInt.zero)
        sigNum.isZero() -> Pair(BigInt.zero, BigInt.zero)
        else -> {
            val cmp = compareMagnitude(value)
            when {
                cmp.isLesser() -> Pair(BigInt.zero, this)
                cmp.isEqual() -> Pair(BigInt.one, BigInt.zero)
                else -> {
                    when {
                        value.mag.size == 1 -> {
                            val qr = divideOneWord(this.abs(), value.abs())
                            Pair(
                                of(qr.first.mag.toIntArray(), if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                                of(qr.second.mag.toIntArray(), this.sigNum)
                            )
                        }
                        else -> {
                            val r = MutableBigInteger.divideMagnitude(
                                MutableBigInteger(abs().toComplementedIntArray()),
                                MutableBigInteger(value.abs().toComplementedIntArray())
                            )
                            Pair(
                                of(r.first.toIntArray(), if (this.sigNum == value.sigNum) BigSigned.POSITIVE else BigSigned.NEGATIVE),
                                of(r.second.toIntArray(), this.sigNum)
                            )
                        }
                    }
                }
            }
        }
    }

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

    val testSubject1 = BigInteger(BinHex.decodeToBin("7fffffffffffffffac558342a6ebd1f01b63beb525401ca4b754d3bde084251a"))
    val testSubject2 = BigInteger(BinHex.decodeToBin("296c47bd9382d357be2bdea1f32ea78ac1b95f2138d5814816004ceb2009d127"))

    @Test
    fun divideAndRemainderTest() {
        //vectorList2.forEach { println(it) }
        /*Combinator.doMatrixPairTests(vectorList1.slice(0..7), listOf(vectorList2[8])) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder(ybi) , x.divideAndRemainder(y))
        }*/
        Combinator.doMatrixPairTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder(ybi) , x.divideAndRemainder(y))
        }
    }

    @Test
    fun divideAndRemainder2Test() {
        Combinator.doMatrixPairTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.divideAndRemainder2(ybi) , x.divideAndRemainder(y))
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