package org.angproj.crypt.dsa

import org.angproj.crypt.number.and
import org.angproj.crypt.sec.*
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntBinaryTest {

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
    fun compareAndTest() {
        dp.forEach {
            println(it.name)
            println("G ${it.G}")
            println("n ${it.n}")

            val jbi1 = BigInteger(it.G)
            val jbi2 = BigInteger(it.n)
            val cbi1 = BigInt.fromByteArray(it.G)
            val cbi2 = BigInt.fromByteArray(it.n)
            assertContentEquals(cbi1.and(cbi2).toByteArray(), jbi1.and(jbi2).toByteArray())
        }
    }
}