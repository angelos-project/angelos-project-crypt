package org.angproj.crypt.sec

import org.angproj.big.toByteArray
import org.angproj.crypt.ec.NistPrime
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class Prime192CurveTest {

    /**
     * From NIST SP.800-186, 3.2.1.2, P-192, p.10
     * Information missing, refers to FIPS 186-4.
     *
     * D.1.2.1 Curve P-192
     * "The integers p and n are given in decimal form; bit strings and field elements are given in
     * hexadecimal."
     *
     * field a is not given!
     * */
    @Test
    fun testOutputOfBigInteger() {
        val dp = NistPrime.P_192.curve.domainParameters
        /*assertEquals(
            Secp192r1.a.toString(),
            ""
        )*/
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(16),
            "64210519" + "e59c80e7" + "0fa7e9ab" + "72243049" + "feb8deec" + "c146b9b1"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "6277101735386680763835789423176059013767194773182842284081"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "6277101735386680763835789423207666416083908700390324961279"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(16),
            "188da80e" + "b03090f6" + "7cbf20eb" + "43a18800" + "f4ff0afd" + "82ff1012"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(16),
            "7192b95" + "ffc8da78" + "631011ed" + "6b24cdd5" + "73f977a1" + "1e794811"
        )
    }
}