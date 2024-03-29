package org.angproj.crypt.ripemd

import org.angproj.aux.util.BinHex
import kotlin.test.Test
import kotlin.test.assertEquals

class Ripemd160HashTest {

    /**
     * Test vectors from
     * https://homes.esat.kuleuven.be/~bosselae/ripemd160.html
     */

    val testVectorsDigest = listOf(
        "9c1185a5c5e9fc54612808977ee8f548b2258d31",
        "0bdc9d2d256b3ee9daae347be6f4dc835a467ffe",
        "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc",
        "5d0689ef49d2fae572b881b123a85ffa21595f36",
        "f71c27109c692c1b56bbdceb5b9d2865b3708dbc",
        "12a053384a9c0c88e405a06c27dcf49ada62eb2b",
        "b0e20b6e3116640286ed3a87a5713079b21f5189",
        "9b752e45573d4b39f4dbd3323cab82bf63326bfb",
        "52783243c1697bdbe16d37f97f68f08325dc1528"
    )

    @Test
    fun testRipemd160Msg() {
        RipemdMessages.testVectors.forEachIndexed { idx, msg ->
            val algo = Ripemd160Hash()
            algo.update(msg)
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}