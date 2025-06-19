package org.angproj.crypt.sec

import org.angproj.big.toByteArray
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals


class Prime384CurveTest {

    /**
     * From NIST SP.800-186, 3.2.1.4, P-384, p.11-12
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp384Random1.domainParameters
        assertEquals(
            BigInteger(dp.a.toByteArray()).toString(),
            "3940200619639447921227904010014361380507973927046544666794" +
                    "8293404245721771496870329047266088258938001861606973112316"
        )
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(),
            "2758019355995970587784901184038904809305690585636156852142" +
                    "8707301988689241309860865136260764883745107765439761230575"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "3940200619639447921227904010014361380507973927046544666794" +
                    "6905279627659399113263569398956308152294913554433653942643"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "3940200619639447921227904010014361380507973927046544666794" +
                    "8293404245721771496870329047266088258938001861606973112319"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(),
            "2624703509579968926862315674456698189185292349110921338781" +
                    "5615900925518854738050089022388053975719786650872476732087"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(),
            "832571096148902998554675128952010817928785304886131559470" +
                    "9205902480503199884419224438643760392947333078086511627871"
        )
    }
}