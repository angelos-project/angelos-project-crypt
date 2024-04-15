package org.angproj.crypt

import junit.framework.TestCase.assertEquals
import org.angproj.aux.num.unsignedBigIntOf
import org.angproj.aux.util.BinHex
import org.angproj.crypt.ec.EcPoint
import org.angproj.crypt.ec.NistPrime
import org.angproj.crypt.ellipticcurve.Curve
import org.angproj.crypt.ellipticcurve.Point
import org.angproj.crypt.num.*
import java.math.BigInteger
import kotlin.test.Test

/**
 * Modulus Bug
 *
 * The regression test is needed because a bug was found even if the BigInt implementation has been fuzzed.
 *
 * The points to test for regression are chosen from NIST P-192 "Key Pair" information vector tests.
 * If there is no regression both should pass, otherwise number two will fail.
 *
 * The bug was found in the "mod" operation.
 * */
class BigIntModRegressionTest {

    private fun reproduceErrorInCaseOfRegressionWith(qX: String, qY: String) {
        val c = Curve.nistP192
        val dp = NistPrime.P_192.curve.domainParameters

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
        val biRes = biP.y.pow(2).subtract(biP.x.pow(3)).mod(c.P) // Java BigInteger result
        val ecRes = ecP.y.pow(2).subtract(ecP.x.pow(3)).mod(dp.p) // Own BigInt result

        assertEquals(
            BinHex.encodeToHex(biRes.toByteArray()), // Java result
            BinHex.encodeToHex(ecRes.toByteArray()) // Own result
        )
    }

    @Test
    fun testPowWithCrypto() {
        // Fails in case of bug regression for pow.
        val q1 = Pair(
            "e3338f21f02e36bc8519d09f9299d70356522a2b9f9b2a17",
            "110d18f6902e3b86234bd4fea97a2e29ca9bbf1b70172900"
        )

        reproduceErrorInCaseOfRegressionWith(q1.first, q1.second) // Will fail in case of regression
    }
}