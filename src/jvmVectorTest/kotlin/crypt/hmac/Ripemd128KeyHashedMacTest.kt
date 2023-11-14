package crypt.hmac

import crypt.ripemd.RipemdMessages
import org.angproj.aux.util.BinHex
import org.angproj.crypt.hmac.KeyHashedMac
import org.angproj.crypt.ripemd.Ripemd128Hash
import kotlin.test.Test
import kotlin.test.assertEquals

class Ripemd128KeyHashedMacTest {

    /**
     * Test vectors and keys from
     * https://homes.esat.kuleuven.be/~bosselae/ripemd160.html
     */

    val keys = listOf(
        "00112233445566778899aabbccddeeff",
        "0123456789abcdeffedcba9876543210"
    )

    val testVectorsDigest = arrayListOf(
        listOf(
            "ad9db2c1e22af9ab5ca9dbe5a86f67dc",
            "3bf448c762de00bcfa0310b11c0bde4c",
            "f34ec0945f02b70b8603f89e1ce4c78c",
            "e8503a8aec2289d82aa0d8d445a06bdd",
            "ee880b735ce3126065de1699cc136199",
            "794daf2e3bdeea2538638a5ced154434",
            "3a06eef165b23625247800be23e232b6",
            "9a4f0159c0952da43a8d466d46b0af58",
            "19b1b3af333b894dd86d09427116d0ad"
        ),
        listOf(
            "8931eeee56a6b257fd1ab5418183d826",
            "dbbcf169ea7419d5ba7bd8eb3673ff2d",
            "2c4cd07d3162d6a0e338004d6b6fbc9a",
            "75bfb25888f4bb77c77ae83ad0817447",
            "b1b5dc0fcb7258758855dd1840fcdce4",
            "670d0f7a697b18f1a8ab7d2a2a00dbc1",
            "54e315fdb34a61c0475392e5c7852998",
            "ad04354d8aa2a623e72e3594ee3535c0",
            "6f9b1c0fc06753618d6db4b007733795"
        )
    )

    @Test
    fun testRipemd128Msg() {
        keys.forEachIndexed { num, key ->
            RipemdMessages.testVectors.forEachIndexed { idx, msg ->
                val hmac = KeyHashedMac.create(BinHex.decodeToBin(key), Ripemd128Hash)
                hmac.update(msg)
                assertEquals(BinHex.encodeToHex(hmac.final()), testVectorsDigest[num][idx].lowercase())
            }
        }
    }
}