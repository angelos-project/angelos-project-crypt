package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.Paulsson512Hash
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "8b2249284f789b2fe31c7c7e71bcd471bd8135a8fa49195069f040fa34fedceec7644700022ffa7719c3ece91622b60daf7590a9d0c51a5b82d7ee9350169e4d",
        "59f8ec0d4e10b5b84b47e990ab9d90cd8f120ff57ad5a759ac16cfe9f8722c81e8f950507c59f70215e1c0e5bc718fe2a863b4cb9c77aeca04b3c42668c6c73f",
        "1879aa890baa843a8cdd2622f0c2b7b1cbc668ed660cefec383377ee14ffc5ac025d7d315dded3f9dfe82bfbd9abb80c94e84bbecd8f8add71911c8a8fbd0161",
        "3661b15ffea794d873679e239d4a25e53a05b32d921efc12b20ad1d647373998f435f744cb9fa861bd19bd22085192131d7728b42f4f0dc71c8cc27b340190fd",
        "c704ccbf26c5e2248e10b0b05903bec4b9ef3accd1474d15252e3676fc52b6b092b4afa331c93b017b35ed834af3c1f239c755c28cc663a558269b35cf51c3ce",
        "b9d867b025843484e5a3f04c78bffd26d91abd56280bc3e29f5d4b58946f27b871dd7251ce4938bc3f54bc34de2eebb4b7628271d8885b71fb9f8b85877cbf34",
        "719ef5b37a78a8bf85c7701706f569f4305d445ab267494412fb9125c6eff4f6cdf3d0cf1809110878256c346fb4b9faa3ef43ae05baa093058b4078fda9084c",
        "4548fedb3849f57d1ff2446c3c5d2ec3d070eaf94a78b2c893ee35e365ee833edd0770fa6a47db919f29daf9b7e0d7e69207960430de28e68948ec9d23c663b3",
        "f1b7ac0a0451c201b0897ab3077a46a39d716174e92fcedc6e873dbe71facf4247af6265ce98a54c98c9a8e6ce44d236bce423872a220d769c29c880c918b5a8"
    )

    val testVectors = listOf(
        "".encodeToByteArray(),
        "a".encodeToByteArray(),
        "abc".encodeToByteArray(),
        "message digest".encodeToByteArray(),
        "abcdefghijklmnopqrstuvwxyz".encodeToByteArray(),
        "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray(),
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".encodeToByteArray(),
        "1234567890".repeat(8).encodeToByteArray(),
        "a".repeat(1_000_000).encodeToByteArray()
    )

    @Test
    fun testPaulsson512Msg() {
        testVectors.forEachIndexed { idx, msg ->
            val algo = Paulsson512Hash()
            algo.update(msg)
            //println(BinHex.encodeToHex(algo.final()))
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}