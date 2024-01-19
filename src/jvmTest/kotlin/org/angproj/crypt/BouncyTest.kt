package org.angproj.crypt

import java.security.KeyPair
import java.security.Signature
import kotlin.test.Test
import kotlin.test.assertTrue

class BouncyTest {
    @Test
    fun testEcdsaSignAndVerify() {

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