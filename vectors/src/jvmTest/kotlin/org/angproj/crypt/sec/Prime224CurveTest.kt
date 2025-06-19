package org.angproj.crypt.sec

import org.angproj.big.toByteArray
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class Prime224CurveTest {

    /**
     * From NIST SP.800-186, 3.2.1.2, P-224, p.10
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp224Random1.domainParameters
        assertEquals(
            BigInteger(dp.a.toByteArray()).toString(),
            "26959946667150639794667015087019630673557916260026308143510066298878"
        )
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(),
            "18958286285566608000408668544493926415504680968679321075787234672564"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "26959946667150639794667015087019625940457807714424391721682722368061"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "26959946667150639794667015087019630673557916260026308143510066298881"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(),
            "19277929113566293071110308034699488026831934219452440156649784352033"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(),
            "19926808758034470970197974370888749184205991990603949537637343198772"
        )
    }
}