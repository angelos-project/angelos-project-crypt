package org.angproj.crypt.sec

import org.angproj.aux.num.*
import org.angproj.aux.util.BinHex
import org.angproj.crypt.Hash
import org.angproj.crypt.dsa.Ecdsa
import org.angproj.crypt.ec.EcPoint
import org.angproj.crypt.ec.NistPrime
import org.angproj.crypt.ellipticcurve.Curve
import org.angproj.crypt.ellipticcurve.Point
import org.angproj.crypt.num.*
import org.angproj.crypt.sha.Sha1Hash
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
    fun testPointOnCurve() {
        pkvIter(P_192_PKV.testVectors) { qX, qY, r ->
            val point = EcPoint(
                unsignedBigIntOf(BinHex.decodeToBin(qX)),
                unsignedBigIntOf(BinHex.decodeToBin(qY)),
            )
            val valid = when(r[0]) {
                'P' -> true
                'F' -> false
                else -> error("")
            }
            assertEquals(valid, Ecdsa.isPointOnCurve(NistPrime.P_192.curve, point))
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

    @Test
    fun testKeyPair() {
        keyPairIter(P_192_KeyPair.testVectors) { d, qX, qY ->
            val privKey = Ecdsa.importPrivateKey(
                NistPrime.P_192.curve,
                unsignedBigIntOf(BinHex.decodeToBin(d))
            )
            println("----------------------------------------------------------------")
            val pubKey = Ecdsa.derivePublicKeyFrom(privKey)
            val point = EcPoint(
                unsignedBigIntOf(BinHex.decodeToBin(qX)),
                unsignedBigIntOf(BinHex.decodeToBin(qY)),
            )
            assertEquals(BinHex.encodeToHex(pubKey.point.x.toByteArray()), qX)
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