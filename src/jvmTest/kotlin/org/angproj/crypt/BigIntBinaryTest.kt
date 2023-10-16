package org.angproj.crypt.dsa

import org.angproj.aux.util.BinHex
import org.angproj.crypt.number.Combinator
import org.angproj.crypt.number.and
import org.angproj.crypt.number.bitLength
import org.angproj.crypt.sec.*
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntBinaryTest {

    val vectorList1 = Combinator.generateValueVector()
    val vectorList2 = Combinator.generateValueVector()
    val vectcorListLong = Combinator.generateLongValueVector()

    val dp: List<SecDomainParameters> = listOf(
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
    )

    fun compareImport(value: ByteArray) {
        val jbi = BigInteger(value)
        val cbi = BigInt.fromByteArray(value)
        assertContentEquals(cbi.toByteArray(), jbi.toByteArray())
    }

    @Test
    fun compareImportTest() {
        dp.forEach { compareImport(it.G)}
    }

    @Test
    fun compareDataImportTest() {
        dp.forEach { compareImport(it.G)}

        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(xbi, x)
        }
    }

    @Test
    fun andTest() {
        Combinator.doMatrixTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.and(ybi), x.and(y))
        }
    }

    @Test
    fun fromLongTest() {
        Combinator.doLongVectorTests(vectcorListLong) { xbi, x ->
            println(xbi.bitLength())
            Pair(xbi, x)
        }
    }

    @Test
    fun compareToTest() {
        Combinator.doMatrixIntTests(vectorList1, vectorList2) { xbi, ybi, x, y ->
            Pair(xbi.compareTo(ybi).num, x.compareTo(y))
        }
    }

    @Test
    fun negateTest() {
        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(xbi.negate(), x.negate())
        }
    }

    @Test
    fun absTest() {
        Combinator.doVectorTests(vectorList1) { xbi, x ->
            Pair(xbi.abs(), x.abs())
        }
    }
}