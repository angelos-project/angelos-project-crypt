package org.angproj.crypt

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.BinHex
import org.angproj.crypt.number.plus
import org.angproj.crypt.number.pow
import java.math.BigInteger
import java.security.KeyPair
import java.security.Signature
import kotlin.test.Test
import kotlin.test.assertTrue

class BouncyTest {
    @Test
    fun testEcdsaSignAndVerify() {

        val polred = BigInt.two.pow(1024) + BigInt.two.pow(7) +
                BigInt.two.pow(2) + BigInt.two.pow(1) + BigInt.one

        println(BigInteger(polred.toByteArray()).toString())
        println(BinHex.encodeToHex(polred.toByteArray()))
        println(polred.toByteArray().size * 8)

        /*val plainText = "Apan Herbert!".encodeToByteArray()

        val pair: KeyPair = GenerateKeys()
        val ecdsaSign: Signature = Signature.getInstance("SHA256withECDSA", "BC")
        ecdsaSign.initSign(pair.private)
        ecdsaSign.update(plaintext.getBytes("UTF-8"))
        val signature: ByteArray = ecdsaSign.sign()

        val ecdsaVerify: Signature = Signature.getInstance("SHA256withECDSA", "BC")
        ecdsaVerify.initVerify(pair.getPublic())
        ecdsaVerify.update(plaintext.getBytes("UTF-8"))
        val result: Boolean = ecdsaVerify.verify(signature)
        assertTrue(result)*/
    }
}