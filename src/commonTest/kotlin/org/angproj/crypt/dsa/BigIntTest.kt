package org.angproj.crypt.dsa

import org.angproj.crypt.sec.Secp256Koblitz1
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntTest {

    @Test
    fun impexp() {
        assertContentEquals(BigInt(Secp256Koblitz1.p).toByteArray(), Secp256Koblitz1.p)
        assertContentEquals(BigInt(Secp256Koblitz1.a).toByteArray(), Secp256Koblitz1.a) // OK
        assertContentEquals(BigInt(Secp256Koblitz1.b).toByteArray(), Secp256Koblitz1.b)  // OK
        assertContentEquals(BigInt(Secp256Koblitz1.G).toByteArray(), Secp256Koblitz1.G) // OK
        assertContentEquals(BigInt(Secp256Koblitz1.Gc).toByteArray(), Secp256Koblitz1.Gc) // OK
        assertContentEquals(BigInt(Secp256Koblitz1.n).toByteArray(), Secp256Koblitz1.n)
        assertContentEquals(BigInt(Secp256Koblitz1.h).toByteArray(), Secp256Koblitz1.h) // OK
    }
}