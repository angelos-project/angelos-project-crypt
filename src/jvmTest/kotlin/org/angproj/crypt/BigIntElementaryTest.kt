package org.angproj.crypt.dsa

import org.angproj.crypt.number.*
import kotlin.test.Test

class BigIntElementaryTest {

    val vectorList = Combinator.generateValueVector()
    val exponentList = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

    @Test
    fun powTest() {
        Combinator.doMatrixExpTests(vectorList, exponentList) { xbi, x, s ->
            Pair(xbi.pow(s), x.pow(s))
        }
    }
}