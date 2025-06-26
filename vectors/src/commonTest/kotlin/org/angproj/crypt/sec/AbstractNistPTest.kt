package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex
import org.angproj.aux.util.floorMod
import org.angproj.big.toByteArray
import org.angproj.big.unsignedBigIntOf
import org.angproj.crypt.Hash
import org.angproj.crypt.dsa.Ecdsa
import org.angproj.crypt.dsa.EcdsaVerify
import org.angproj.crypt.ec.EcPoint
import org.angproj.crypt.ec.EcPublicKey
import org.angproj.crypt.ec.EcSignature
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

public fun String.fromHex(): ByteArray {
    val prefix = if(length.floorMod(2) == 1) "0" else ""
    return BinHex.decodeToBin(prefix + this)
}

abstract class AbstractNistPTest {
    abstract val hash: Hash
    abstract val curve: Curves<PrimeDomainParameters>

    fun testKeyPair (file: String) {
        keyPairIter(file) { d, qX, qY ->
            val point = EcPoint(
                unsignedBigIntOf(qX.fromHex()),
                unsignedBigIntOf(qY.fromHex())
            )
            assertTrue(Ecdsa.isPointOnCurve(curve, point))
            val privKey = Ecdsa.importPrivateKey(curve, unsignedBigIntOf(d.fromHex()))
            val pubKey = Ecdsa.derivePublicKeyFrom(privKey)
            assertContentEquals(
                point.x.toByteArray(),
                pubKey.point.x.toByteArray()
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
            val point = EcPoint(
                unsignedBigIntOf(qX.fromHex()),
                unsignedBigIntOf(qY.fromHex())
            )
            val valid = when(result[0]) {
                'P' -> true
                'F' -> false
                else -> error("")
            }
            assertEquals(Ecdsa.isPointOnCurve(curve, point), valid)
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
            val publicKey = EcPublicKey(
                EcPoint(
                    unsignedBigIntOf(qX.fromHex()),
                    unsignedBigIntOf(qY.fromHex())
                ),
                curve,
            )
            val signature = EcSignature(
                unsignedBigIntOf(r.fromHex()),
                unsignedBigIntOf(s.fromHex())
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
            val publicKey = EcPublicKey(
                EcPoint(
                    unsignedBigIntOf(qX.fromHex()),
                    unsignedBigIntOf(qY.fromHex())
                ),
                curve,
            )
            val signature = EcSignature(
                unsignedBigIntOf(r.fromHex()),
                unsignedBigIntOf(s.fromHex())
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