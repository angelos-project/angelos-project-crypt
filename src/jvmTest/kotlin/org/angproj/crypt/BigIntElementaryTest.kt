package org.angproj.crypt

import org.angproj.crypt.number.*
import kotlin.test.Test

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

    @Test
    fun modPowTest() {
        Combinator.doVectorTests(positiveVector) { xbi, x ->
            Pair(xbi.modPow(), x.modPow())
        }
    }
}

