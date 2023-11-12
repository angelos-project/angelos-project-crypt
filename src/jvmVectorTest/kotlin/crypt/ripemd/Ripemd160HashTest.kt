package org.angproj.crypt.ripemd

import org.angproj.aux.util.BinHex
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test vectors from
 * https://homes.esat.kuleuven.be/~bosselae/ripemd160.html
 */

val testVectorsRipemd160 = mapOf(
    "".encodeToByteArray() to "9c1185a5c5e9fc54612808977ee8f548b2258d31",
    "a".encodeToByteArray() to "0bdc9d2d256b3ee9daae347be6f4dc835a467ffe",
    "abc".encodeToByteArray() to "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc",
    "message digest".encodeToByteArray() to "5d0689ef49d2fae572b881b123a85ffa21595f36",
    "abcdefghijklmnopqrstuvwxyz".encodeToByteArray() to "f71c27109c692c1b56bbdceb5b9d2865b3708dbc",
    "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray() to "12a053384a9c0c88e405a06c27dcf49ada62eb2b",
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".encodeToByteArray() to "b0e20b6e3116640286ed3a87a5713079b21f5189",
    "1234567890".repeat(8).encodeToByteArray() to "9b752e45573d4b39f4dbd3323cab82bf63326bfb",
    "a".repeat(1_000_000).encodeToByteArray() to	"52783243c1697bdbe16d37f97f68f08325dc1528"
)

// 8ba478f28a09f1a589ff676eddc46edd36284264
// 1637539be8c3df62ee7d9f560343efea1e2d3289


class Ripemd160HashTest {

    @Test
    fun testRipemd160Short() {
        testVectorsRipemd160.forEach{ (msg, md) ->
            val algo = Ripemd160Hash()
            algo.update(msg)
            assertEquals(BinHex.encodeToHex(algo.final()), md.lowercase())
            println(md.uppercase())
        }
    }

}