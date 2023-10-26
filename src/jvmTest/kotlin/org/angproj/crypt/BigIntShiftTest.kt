package org.angproj.crypt.dsa

import org.angproj.crypt.number.*
import kotlin.test.Test

class BigIntShiftTest {

    val vectorList1 = Combinator.generateValueVector()
    val vectorListSizes = Combinator.generateSizeVector()

    @Test
    fun shiftLeftTest() {
        Combinator.doMatrixIntTests2(vectorList1, vectorListSizes) { xbi, x, s ->
            Pair(xbi.shiftLeft(s), x.shiftLeft(s))
        }
    }

    @Test
    fun shiftRightTest() {
        Combinator.doMatrixIntTests2(vectorList1, vectorListSizes) { xbi, x, s ->
            Pair(xbi.shiftRight(s), x.shiftRight(s))
        }
    }
}