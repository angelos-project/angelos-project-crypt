package org.angproj.crypt.sec

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.bigIntOf
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

        keyPairIter(P_192_KeyPair.testVectors) { d, qX, qY ->
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
            val biRes = biP.y.pow(2).subtract(biP.x.pow(3)).mod(c.P)
            println("J. r: " + biRes.signum() + ", " + BinHex.encodeToHex(biRes.toByteArray()))

            //println(ecP.y.pow(2).subtract(ecP.x.pow(3).add(dp.a.multiply(ecP.x)).add(dp.b)).mod(dp.p).toLong())
            val ecRes = ecP.y.pow(2).subtract(ecP.x.pow(3)).mod(dp.p)
            println("K. r: " + ecRes.sigNum.state + ", " + BinHex.encodeToHex(ecRes.toByteArray()))

            println()
            println("----------------------------------------------------------------------------------------")
            println()
        }
    }

    @Test
    fun testKeyPair() {
        keyPairIter(P_192_KeyPair.testVectors) { d, qX, qY ->
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
        println(p.y.pow(2).subtract(p.x.pow(3).add(c.A.multiply(p.x)).add(c.B)).mod(c.P).toInt())
        return p.y.pow(2).subtract(p.x.pow(3).add(c.A.multiply(p.x)).add(c.B)).mod(c.P).equals(BigInteger.ZERO)//.toInt() == 0
        //return p.y.pow(2).subtract(p.x.pow(3).add(c.A.multiply(p.x)).add(c.B)).mod(c.P).toInt() == 0
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
        keyPairIter(P_192_KeyPair.testVectors) { d, qX, qY ->

            val point = EcPoint(
                unsignedBigIntOf(BinHex.decodeToBin(qX)),
                unsignedBigIntOf(BinHex.decodeToBin(qY)),
            )
            //println(point.x.sigNum.state)
            //println(point.y.sigNum.state)


            println(pointOnCurve(NistPrime.P_192.curve, point))
        }
    }

    fun pointOnCurve(curve: Curves<PrimeDomainParameters>, point: EcPoint): Boolean {
        val dp = curve.domainParameters
        if (point.x.compareTo(BigInt.zero).state < 0) {
            println("X Lesser than 0")
            return false
        }
        if (point.x.compareTo(dp.p).state >= 0) {
            println("X Larger than prime")
            return false
        }
        if (point.y.compareTo(BigInt.zero).state < 0) {
            println("Y Lesser than 0")
            return false
        }
        if (point.y.compareTo(dp.p).state >= 0) {
            println("Y Larger than prime")
            return false
        }
        println(point.y.pow(2).subtract(point.x.pow(3).add(dp.a.multiply(point.x)).add(dp.b)).mod(dp.p).toLong().toInt())
        return point.y.pow(2).subtract(point.x.pow(3).add(dp.a.multiply(point.x)).add(dp.b)).mod(dp.p).compareTo(BigInt.zero).isEqual()//.toLong().toInt() == 0
        // p.y.pow(2).subtract(p.x.pow(3).add(A.multiply(p.x)).add(B)).mod(P).intValue() == 0;
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

    object P_192_KeyPair {
        val testVectors: String = """#  CAVS 11.1
#  "Key Pair" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Thu May 05 09:19:12 2011


[P-192]

d = c691800ae3691ed451ae4fa3f66a78798320f510b76ad287
Qx = e3338f21f02e36bc8519d09f9299d70356522a2b9f9b2a17
Qy = 110d18f6902e3b86234bd4fea97a2e29ca9bbf1b70172900

d = 756270bc1c92cd7a586d7c808d75570cee966a42a61435ed
Qx = 8b64bda6551000bf416c1d14560afacedb720a87f06d8bdd
Qy = 10d5179a28bff54c1d885a9d068978c53b0096511a69551b

d = 982195f26694ca1cc68a09a5098b65bb79926eb07f2575e1
Qx = c1763de1fda85fda7be3dcd4ff77b78e41838e41ef0cc25b
Qy = d4dcf3b2fc15df8ad103e5343e5f278f5da06705c5442758

d = 578a58f71c8deb5ceff819f0f5f1b3d92b9b045a0a1bf5b4
Qx = 534007e5b736cdc0aa05902c7fc269a51e8543cf6c0315e7
Qy = 6beaccfd2e13d49baad46245de2be3b27f7797466885d12a

d = 05fb42a935e09a0f94f78f396705e1b9fa784019fd8263e5
Qx = 7f9ed5bc4af05caad90e802b79032742d93c4e9bb7ea6149
Qy = 51b851c914f778ec675fb2797e508cb1403ba299215f033c

d = 4f90490b6e62415879c329487f2f8d31720955dafcedc859
Qx = 31e1096f883c13314bdec397d3d3515fd0beec74a2a316dc
Qy = cafa81c4bec33ed3ce5cb2216a550e6615792735486894c4

d = 2fa1a90467dae40ada59dcf7e8700eb5a02f111b2620c3e1
Qx = 89b91cb3958c31683f32f5745e7542e4b1c543cbbe36a592
Qy = 4be6e3bef23bf12f7c945b0fe1e5e829b8ce7fc2d93daa3c

d = 84bc2eb3e6470e23c429b9521cb617138da2f462240a0dcc
Qx = 268e5ab7a80c96ec3b05cf7902ecbee224188958259c0f2d
Qy = ba444ecc3f1eadc53f27dc08e36663b4ea6846f761722a78

d = b9e820ac3e062622c62fd6cab097b18cdb404e5c5505825e
Qx = e749efc0a078d53e654d9cc66393eda2b729bd28f5b5946a
Qy = dc713df0fd53def63dc93921aecd866fca8cce31a5e2f914

d = f96154b47b40c675d7749fad59ffbbd45604f17c2491cd79
Qx = d23429f841f283d07a20f7fea6f0588476a20e934be92014
Qy = 0cd968ba262e03276f09765da0a358a045988fd9e5e7a13f"""
    }
}