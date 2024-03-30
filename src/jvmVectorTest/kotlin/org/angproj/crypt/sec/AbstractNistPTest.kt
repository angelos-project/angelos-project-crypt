package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex
import org.angproj.crypt.Hash
import org.angproj.crypt.dsa.EcdsaVerify
import org.angproj.crypt.ellipticcurve.*
import java.math.BigInteger
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractNistPTest {
    abstract val hash: Hash
    abstract val curve: Curve

    fun testKeyPair (file: String) {
        keyPairIter(file) { d, qX, qY ->
            val point = Point(
                BigInteger(qX, 16),
                BigInteger(qY, 16)
            )
            assertTrue(curve.contains(point))
            assertContains(
                qX,
                PrivateKey(curve, BigInteger(d, 16)).publicKey().point.x.toString(16)
            )
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

    fun testPkv (file: String) {
        pkvIter(file) { qX, qY, result ->
            val point = Point(
                BigInteger(qX, 16),
                BigInteger(qY, 16)
            )
            val valid = when(result[0]) {
                'P' -> true
                'F' -> false
                else -> error("")
            }
            assertEquals(curve.contains(point), valid)
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

    fun testSigGen (file: String) {
        sigGenIter(file) { msg, qX, qY, r, s ->
            val publicKey = PublicKey(
                Point(
                    BigInteger(qX, 16),
                    BigInteger(qY, 16)
                ),
                curve,
            )
            val signature = Signature(
                BigInteger(r, 16),
                BigInteger(s, 16)
            )

            val ecdsa = EcdsaVerify(curve, hash)
            ecdsa.update(msg)
            assertTrue(ecdsa.final(publicKey, signature))
        }
    }

    fun sigGenIter(
        file: String,
        process: (
            msg: ByteArray,
            qX: String,
            qY: String,
            r: String,
            s: String
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val msg = BinHex.decodeToBin(rows[0].substring(6).trim())
            val qX = rows[1].substring(5).trim()
            val qY = rows[2].substring(5).trim()
            val r = rows[3].substring(4).trim()
            val s = rows[4].substring(4).trim()
            process(msg, qX, qY, r, s)
        }
    }

    fun testSigVer (file: String) {
        sigVerIter(file) { msg, qX, qY, r, s, result ->
            val publicKey = PublicKey(
                Point(
                    BigInteger(qX, 16),
                    BigInteger(qY, 16)
                ),
                curve,
            )
            val signature = Signature(
                BigInteger(r, 16),
                BigInteger(s, 16)
            )
            val valid = when(result[0]) {
                'P' -> true
                'F' -> false
                else -> error("")
            }

            val ecdsa = EcdsaVerify(curve, hash)
            ecdsa.update(msg)
            assertEquals(ecdsa.final(publicKey, signature), valid)
        }
    }

    fun sigVerIter(
        file: String,
        process: (
            msg: ByteArray,
            qX: String,
            qY: String,
            r: String,
            s: String,
            result: String
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val msg = BinHex.decodeToBin(rows[0].substring(6).trim())
            val qX = rows[1].substring(5).trim()
            val qY = rows[2].substring(5).trim()
            val r = rows[3].substring(4).trim()
            val s = rows[4].substring(4).trim()
            val result = rows[5].substring(9).trim()
            process(msg, qX, qY, r, s, result)
        }
    }
}