package org.angproj.crypt.sec

import org.angproj.big.toByteArray

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class Prime256Koblitz1CurveTest {

    /**
     * Aren't available from authoritative open sources, presumably deleted for convenience.
     * Exists only based on already generated secp256k1 values which is tested through recycled
     * test-vectors, which presumably are deprecated NIST test-vectors for unofficial testing.
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp256Koblitz1.domainParameters
        assertEquals(
            BigInteger(dp.a.toByteArray()).toString(),
            "0"
        )
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(),
            "7"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "1157920892373161954235709850086879078528" +
                    "37564279074904382605163141518161494337"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "115792089237316195423570985008687907853" +
                    "269984665640564039457584007908834671663"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(),
            "55066263022277343669578718895168534326" +
                    "250603453777594175500187360389116729240"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(),
            "32670510020758816978083085130507043184" +
                    "471273380659243275938904335757337482424"
        )
    }
}