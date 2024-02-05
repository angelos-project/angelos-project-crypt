package org.angproj.crypt

import org.angproj.aux.util.bigIntOf
import org.angproj.crypt.number.*
import org.angproj.crypt.sec.*
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntBinaryTest {

    val vectorList1 = Combinator.generateValueVector()
    val vectorList2 = Combinator.generateValueVector()
    val vectorPositiveListSizes = Combinator.generatePositiveSizeVector()

    val vectcorListLong = Combinator.generateLongValueVector()

    /*val dp: List<SecCurves> = listOf(
        Secp192Koblitz1,
        Secp192Random1,
        Secp224Koblitz1,
        Secp224Random1,
        Secp256Koblitz1,
        Secp256Random1,
        Secp384Random1,
        Secp521Random1,

        Sect163Koblitz1,
        Sect163Random1,
        Sect163Random2,
        Sect233Koblitz1,
        Sect233Random1,
        Sect293Koblitz1,
        Sect283Koblitz1,
        Sect283Random1,
        Sect409Koblitz1,
        Sect409Random1,
        Sect571Koblitz1,
        Sect571Random1
    )*/

    fun compareImport(value: ByteArray) {
        val jbi = BigInteger(value)
        val cbi = bigIntOf(value)
        assertContentEquals(cbi.toByteArray(), jbi.toByteArray())
    }

    /*@Test
    fun compareImportTest() {
        dp.forEach { compareImport(it.G)}
    }*/

    /*@Test
    fun compareDataImportTest() {
        dp.forEach { compareImport(it.G)}

        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(xbi, x)
        }
    }*/

    @Test
    fun andTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi and ybi, x.and(y))
        }
    }

    @Test
    fun orTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi or ybi, x.or(y))
        }
    }

    @Test
    fun xorTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi xor ybi , x.xor(y))
        }
    }

    @Test
    fun notTest() {
        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(xbi.not(), x.not())
        }
    }

    @Test
    fun andNotTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.andNot(ybi), x.andNot(y))
        }
    }

    @Test
    fun clearBitTest() {
        Combinator.doMatrixIntTests2(vectorList1, vectorPositiveListSizes) { xbi, x, s ->
            Pair(xbi.clearBit(s), x.clearBit(s))
        }
    }

    @Test
    fun setBitTest() {
        Combinator.doMatrixIntTests2(vectorList1, vectorPositiveListSizes) { xbi, x, s ->
            Pair(xbi.setBit(s), x.setBit(s))
        }
    }

    @Test
    fun testBitTest() {
        Combinator.doMatrixIntTests3(vectorList1, vectorPositiveListSizes) { xbi, x, s ->
            Pair(xbi.testBit(s), x.testBit(s))
        }
    }

    @Test
    fun flipBitTest() {
        Combinator.doMatrixIntTests2(vectorList1, vectorPositiveListSizes) { xbi, x, s ->
            Pair(xbi.flipBit(s), (x.flipBit(s)))
        }
    }

    @Test
    fun fromLongTest() {
        Combinator.doLongVectorTests(vectcorListLong) { xbi, x ->
            Pair(xbi, x)
        }
    }

    @Test
    fun compareToTest() {
        Combinator.doMatrixIntTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.compareTo(ybi).state, x.compareTo(y))
        }
    }
}