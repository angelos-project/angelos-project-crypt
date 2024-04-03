package org.angproj.crypt

import junit.framework.TestCase.assertEquals
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.unsignedBigIntOf
import org.angproj.crypt.ec.EcPoint
import org.angproj.crypt.ellipticcurve.Point
import org.angproj.crypt.number.pow
import java.math.BigInteger
import kotlin.test.Test

/**
 * Pow Bug
 *
 * The regression test is needed because a bug was found even if the BigInt implementation has been fuzzed.
 *
 * The points to test for regression are chosen from NIST P-192 "Key Pair" information vector tests.
 * If there is no regression both should pass, otherwise number two will fail.
 *
 * The bug was found in the "pow" operation.
 * */
class BigIntPowRegressionTest {

    private fun reproduceErrorInCaseOfRegressionWithPow(qX: String, qY: String) {

        // Java BigInteger
        val biP = Point(
            BigInteger(qX, 16),
            BigInteger(qY, 16)
        )

        // Own BigInt
        val ecP = EcPoint(
            unsignedBigIntOf(BinHex.decodeToBin(qX)),
            unsignedBigIntOf(BinHex.decodeToBin(qY)),
        )
        val biRes = biP.y.pow(2) // Java BigInteger result
        val ecRes = ecP.y.pow(2) // Own BigInt result

        assertEquals(
            BinHex.encodeToHex(biRes.toByteArray()), // Java result
            BinHex.encodeToHex(ecRes.toByteArray()) // Own result
        )
    }

    @Test
    fun testPowWithCrypto() {
        // Always works
        val q1 = Pair(
            "e3338f21f02e36bc8519d09f9299d70356522a2b9f9b2a17",
            "110d18f6902e3b86234bd4fea97a2e29ca9bbf1b70172900"
        )
        // Fails in case of bug regression for pow.
        val q2 = Pair(
            "c1763de1fda85fda7be3dcd4ff77b78e41838e41ef0cc25b",
            "d4dcf3b2fc15df8ad103e5343e5f278f5da06705c5442758"
        )

        reproduceErrorInCaseOfRegressionWithPow(q1.first, q1.second) // May always make it
        reproduceErrorInCaseOfRegressionWithPow(q2.first, q2.second) // Will fail in case of regression
    }
}