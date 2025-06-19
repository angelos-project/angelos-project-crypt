package org.angproj.crypt.sec

import org.angproj.big.toByteArray
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class Prime256CurveTest {

    /**
     * From NIST SP.800-186, 3.2.1.3, P-256, p.10-11
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp256Random1.domainParameters
        assertEquals(
            BigInteger(dp.a.toByteArray()).toString(),
            "115792089210356248762697446949407573530" +
                    "086143415290314195533631308867097853948"
        )
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(),
            "41058363725152142129326129780047268409" +
                    "114441015993725554835256314039467401291"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "115792089210356248762697446949407573529" +
                    "996955224135760342422259061068512044369"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "115792089210356248762697446949407573530" +
                    "086143415290314195533631308867097853951"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(),
            "48439561293906451759052585252797914202" +
                    "762949526041747995844080717082404635286"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(),
            "36134250956749795798585127919587881956" +
                    "611106672985015071877198253568414405109"
        )
    }
}