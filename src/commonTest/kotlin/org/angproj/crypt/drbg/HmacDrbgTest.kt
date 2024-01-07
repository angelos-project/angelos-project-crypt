package org.angproj.crypt.drbg

import org.angproj.aux.util.BinHex
import org.angproj.crypt.ripemd.Ripemd160Hash
import org.angproj.crypt.sha.Sha256Hash
import org.angproj.crypt.sha.Sha3512Hash
import kotlin.test.Test

class HmacDrbgTest {

    @Test
    fun testFacadeAndGenerate() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha256Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        repeat(10) {
            val random = instance.generate(128, 128, true)
            println(BinHex.encodeToHex(random))
        }
    }
}