package org.angproj.crypt.sec

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.unsignedBigIntOf
import org.angproj.crypt.Hash
import org.angproj.crypt.ec.EcPoint
import org.angproj.crypt.ec.NistPrime
import org.angproj.crypt.ellipticcurve.Curve
import org.angproj.crypt.ellipticcurve.Point
import org.angproj.crypt.number.*
import org.angproj.crypt.number.multiply
import org.angproj.crypt.sha.Sha1Hash
import java.math.BigInteger
import kotlin.test.Test

class Prime192DebugTest {

    val hash: Hash = Sha1Hash
    val curve: Curve = Curve.nistP192

    @Test
    fun testDebug() {
        val c = curve
        val dp = NistPrime.P_192.curve.domainParameters

        keyPairIter(P_192_PKV.testVectors) { d, qX, qY ->
            println("Q(x):    $qX")
            println("Q(y):    $qY")

            println()

            val biP = Point(
                BigInteger(qX, 16),
                BigInteger(qY, 16)
            )
            println("J. x: " + biP.x.signum() + ", " + BinHex.encodeToHex(biP.x.toByteArray()))
            println("J. y: " + biP.y.signum() + ", " + BinHex.encodeToHex(biP.y.toByteArray()))

            val ecP = EcPoint(
                unsignedBigIntOf(BinHex.decodeToBin(qX)),
                unsignedBigIntOf(BinHex.decodeToBin(qY)),
            )
            println("K. x: " + ecP.x.sigNum.state + ", " + BinHex.encodeToHex(ecP.x.toByteArray()))
            println("K. y: " + ecP.y.sigNum.state + ", " + BinHex.encodeToHex(ecP.y.toByteArray()))

            println()

            //println(biP.y.pow(2).subtract(biP.x.pow(3).add(c.A.multiply(biP.x)).add(c.B)).mod(c.P).toInt())
            val biRes = biP.y.pow(2).subtract(biP.x.pow(3).add(c.A.multiply(biP.x)).add(c.B)).mod(c.P)
            println("J. r: " + biRes.signum() + ", " + BinHex.encodeToHex(biRes.toByteArray()))

            //println(ecP.y.pow(2).subtract(ecP.x.pow(3).add(dp.a.multiply(ecP.x)).add(dp.b)).mod(dp.p).toLong())
            val ecRes = ecP.y.pow(2).subtract(ecP.x.pow(3).add(dp.a.multiply(ecP.x)).add(dp.b)).mod(dp.p)
            println("K. r: " + ecRes.sigNum.state + ", " + BinHex.encodeToHex(ecRes.toByteArray()))

            println()
            println("----------------------------------------------------------------------------------------")
            println()
        }
    }

    @Test
    fun testKeyPair() {
        keyPairIter(P_192_PKV.testVectors) { d, qX, qY ->
            val point = Point(
                BigInteger(qX, 16),
                BigInteger(qY, 16)
            )
            //println(point.x.signum())
            //println(point.y.signum())

            println(contains(Curve.nistP192, point))
            //assertTrue(contains(Curve.nistP192, point))
        }
    }

    fun pkvIter(
        file: String,
        process: (
            qX: String,
            qY: String,
            result: String
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val qX = rows[0].substring(5).trim()
            val qY = rows[1].substring(5).trim()
            val result = rows[2].substring(9).trim()
            process(qX, qY, result)
        }
    }

    fun contains(c: Curve, p: Point): Boolean {
        if (p.x.compareTo(BigInteger.ZERO) < 0) {
            println("X Lesser than 0")
            return false
        }
        if (p.x.compareTo(c.P) >= 0) {
            println("X Larger than prime")
            return false
        }
        if (p.y.compareTo(BigInteger.ZERO) < 0) {
            println("Y Lesser than 0")
            return false
        }
        if (p.y.compareTo(c.P) >= 0) {
            println("Y Larger than prime")
            return false
        }
        return p.y.pow(2).subtract(p.x.pow(3).add(c.A.multiply(p.x)).add(c.B)).mod(c.P).equals(BigInteger.ZERO)
    }

    @Test
    fun compare() {
        val dp = NistPrime.P_192.curve.domainParameters
        /*println(BinHex.encodeToHex(dp.a.toByteArray()))
        println(curve.A.toString(16))
        println(BinHex.encodeToHex(dp.b.toByteArray()))
        println(curve.B.toString(16))
        println(BinHex.encodeToHex(dp.p.toByteArray()))
        println(curve.P.toString(16))
        println(BinHex.encodeToHex(dp.n.toByteArray()))
        println(curve.N.toString(16))
        println(BinHex.encodeToHex(dp.G.x.toByteArray()))
        println(curve.G.x.toString(16))
        println(BinHex.encodeToHex(dp.G.y.toByteArray()))
        println(curve.G.y.toString(16))*/

        println(dp.a.sigNum.state)
        println(curve.A.signum())
        println(dp.b.sigNum.state)
        println(curve.B.signum())
        println(dp.p.sigNum.state)
        println(curve.P.signum())
        println(dp.n.sigNum.state)
        println(curve.N.signum())
        println(dp.G.x.sigNum.state)
        println(curve.G.x.signum())
        println(dp.G.y.sigNum.state)
        println(curve.G.y.signum())
    }

    @Test
    fun testPointOnCurve() {
        keyPairIter(P_192_PKV.testVectors) { qX, qY, r ->

            val point = EcPoint(
                unsignedBigIntOf(BinHex.decodeToBin(qX)),
                unsignedBigIntOf(BinHex.decodeToBin(qY)),
            )
            //println(point.x.sigNum.state)
            //println(point.y.sigNum.state)


            println(r)
            println(pointOnCurve(NistPrime.P_192.curve, point))
        }
    }

     fun pointOnCurve(curve: Curves<PrimeDomainParameters>, point: EcPoint): Boolean {
        val dp = curve.domainParameters
        return when {
            point.x.compareTo(BigInt.zero).isLesser() -> false
            point.x.compareTo(dp.p).isGreater() -> false
            point.y.compareTo(BigInt.zero).isLesser() -> false
            point.y.compareTo(dp.p).isGreater() -> false
            else -> point.y.pow(2).subtract(
                point.x.pow(3).add(dp.a.multiply(point.x)).add(dp.b)
            ).mod(dp.p).compareTo(BigInt.zero).isEqual()
        }
    }

    private fun keyPairIter(
        file: String,
        process: (
            d: String,
            qX: String,
            qY: String,
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val d = rows[0].substring(4).trim()
            val qX = rows[1].substring(5).trim()
            val qY = rows[2].substring(5).trim()
            process(d, qX, qY)
        }
    }

    object P_192_PKV {
        val testVectors: String = """#  CAVS 11.0
#  "PKV" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:01 2011


[P-192]

Qx = 491c0c4761b0a4a147b5e4ce03a531546644f5d1e3d05e57
Qy = 6fa5addd47c5d6be3933fbff88f57a6c8ca0232c471965de
Result = F (2 - Point not on curve)

Qx = 646c22e8aa5f7833390e0399155ac198ae42470bba4fc834
Qy = 8d4afcfffd80e69a4d180178b37c44572495b7b267ee32a9
Result = P (0 )

Qx = 4c6b9ea0dec92ecfff7799470be6a2277b9169daf45d54bb
Qy = f0eab42826704f51b26ae98036e83230becb639dd1964627
Result = F (2 - Point not on curve)

Qx = 0673c8bb717b055c3d6f55c06acfcfb7260361ed3ec0f414
Qy = ba8b172826eb0b854026968d2338a180450a27906f6eddea
Result = P (0 )

Qx = 82c949295156192df0b52480e38c810751ac570daec460a3
Qy = 200057ada615c80b8ff256ce8d47f2562b74a438f1921ac3
Result = F (2 - Point not on curve)

Qx = 284fbaa76ce0faae2ca4867d01092fa1ace5724cd12c8dd0
Qy = e42af3dbf3206be3fcbcc3a7ccaf60c73dc29e7bb9b44fca
Result = P (0 )

Qx = 1b574acd4fb0f60dde3e3b5f3f0e94211f95112e43cba6fd2
Qy = bcc1b8a770f01a22e84d7f14e44932ffe094d8e3b1e6ac26
Result = F (1 - Q_x or Q_y out of range)

Qx = 16ba109f1f1bb44e0d05b80181c03412ea764a59601d17e9f
Qy = 0569a843dbb4e287db420d6b9fe30cd7b5d578b052315f56
Result = F (1 - Q_x or Q_y out of range)

Qx = 1333308a7c833ede5189d25ea3525919c9bd16370d904938d
Qy = b10fd01d67df75ff9b726c700c1b50596c9f0766ea56f80e
Result = F (1 - Q_x or Q_y out of range)

Qx = 9671ec444cff24c8a5be80b018fa505ed6109a731e88c91a
Qy = fe79dae23008e46bf4230c895aab261a95845a77f06d0655
Result = P (0 )

Qx = 158e8b6f0b14216bc52fe8897b4305d870ede70436a96741d
Qy = fb3f970b19a313571a1a23be310923f85acc1cab0a157cbd
Result = F (1 - Q_x or Q_y out of range)

Qx = ace95b650c08f73dbb4fa7b4bbdebd6b809a25b28ed135ef
Qy = e9b8679404166d1329dd539ad52aad9a1b6681f5f26bb9aa
Result = F (2 - Point not on curve)"""
    }
}