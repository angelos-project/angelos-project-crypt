package org.angproj.crypt

import org.angproj.crypt.number.Combinator
import org.angproj.crypt.number.pow
import org.angproj.crypt.number.pow2
import org.angproj.crypt.number.sqrt
import kotlin.test.Test
import java.math.BigInteger as Bintiger

class BigIntElementaryTest {

    val vectorList = Combinator.generateValueVector()
    val exponentList = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    val positiveVector = Combinator.generatePositiveValueVector()

    @Test
    fun pow2Test() {
        Combinator.doVectorTests(vectorList) { xbi, x ->
            Pair(xbi.pow2(), x.pow(2))
        }
    }

    @Test
    fun powTest() {
        Combinator.doMatrixExpTests(vectorList, exponentList) { xbi, x, s ->
            Pair(xbi.pow(s), x.pow(s))
        }
    }

    @Test
    fun sqrtTest() {
        Combinator.doVectorTests(positiveVector) { xbi, x ->
            Pair(xbi.sqrt(), x.sqrt())
        }
    }


    // https://opensource.apple.com/source/gcc/gcc-1435/libjava/gnu/java/math/MPN.java.auto.html

    // https://developer.classpath.org/doc/java/math/BigInteger-source.html
    @Test
    fun modInverseTest() {
        /*Combinator.doVectorTests(positiveVector) { xbi, x ->
            Pair(xbi.modInverse(), x.modInverse())
        }*/
        /*var e = Bintiger("2")
        e = e.pow(256)
        val f = e.add(Bintiger.ONE)
        val d = e.modInverse(f)
        println(d.equals(Bintiger("115792089237316195423570985008687907853269984665640564039457584007913129639936")))*/

          /*positiveVector.forEach { println(it.toString())
            println(it.modInverse(Bintiger.valueOf(100)).toString()) }*/
    }

    @Test
    fun modPowTest() {
        /*Combinator.doVectorTests(positiveVector) { xbi, x ->
            Pair(xbi.modPow(), x.modPow())
        }*/
        var e = Bintiger("57896044618658097709090828437834002169383698139577177385661209641509512047202")
        var f = e.modPow(Bintiger.valueOf(20000), Bintiger.valueOf(200000))
        println(f.equals(Bintiger("109376")))

    }
}

