package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.Paulsson512Hash
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "8d5b5899d2d1845dec7e528c5c16303f99d6fd3d9c6edb380fe877bbb5a77611006b6f01c8a1e19ecd5ae00631e0ff7e1bd6e7873fc94c97724e1476c49ffa8f",
        "c047476af1c0ed80cf609d195a6c9466f4a5df1c04012c476a6f313f070672b3913464c782a75fc84f7ee39e6ffc6e02fd8044196e6651ea3e94662e2e978b64",
        "88bc610c22ca448e524b804389cedf9bada2053a4fe2285cf038313d7045925998e73ed5c2ecaaadaedb215016821fd15fce0f4121fdb04993b3f9576736d918",
        "bc3f1c176aaf637389bb036a2e58a8dc8d859e8bc086953a368d2c5a16a44a3bdcbffd6262ec4bb72a7233d8a6e7561ffeb65a56752805f72c83c093562a0a5f",
        "ba7687a5dc32da06c3286ad8f8e48832d61d8999cac58f6f5c959f19734d6735ccba0358f04ecc55103d79697b3b9d9230c8e06933f1dd9f564fd2df83ca1feb",
        "b2f31c5cab75633c99a854449e0c787b1172913095edf4692e4e31d2de317803d8b6f333d3437cd37575bf9a7828630e07496f75bfe2db50c7058f356bdaf655",
        "e3a6de6c052cdcb419223c7c37ec82aad8fe97f7483ebb25c472eb387103bdf6dd7b742b2bee0311597566054e8266032812ca41052323e0cc1cb05e8b1217e0",
        "006409e25ce86c1ef963e0b14a4378c6d3dae8209f6c3efb70232dde24794c1ecb364f7395503c0a589b5c3d42bf63e01b27ec3b85567f2cd1d94b532fd66fec",
        "46eea7c6078298e7ed847296de82108c450a8e556f4483eac7f7b4cbb492321d006341311ce2508b5c88e3feb241fcd63026456576f307f5780401116391e909"
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
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}