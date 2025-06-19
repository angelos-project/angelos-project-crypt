package org.angproj.crypt.hmac

import org.angproj.crypt.ripemd.RipemdMessages
import org.angproj.aux.util.BinHex
import org.angproj.crypt.ripemd.Ripemd160Hash
import kotlin.test.Test
import kotlin.test.assertEquals

class Ripemd160KeyHashedMacTest {

    /**
     * Test vectors and keys from
     * https://homes.esat.kuleuven.be/~bosselae/ripemd160.html
     */

    val keys = listOf(
        "00112233445566778899aabbccddeeff01234567",
        "0123456789abcdeffedcba987654321000112233"
    )

    val testVectorsDigest = arrayListOf(
        listOf(
            "cf387677bfda8483e63b57e06c3b5ecd8b7fc055",
            "0d351d71b78e36dbb7391c810a0d2b6240ddbafc",
            "f7ef288cb1bbcc6160d76507e0a3bbf712fb67d6",
            "f83662cc8d339c227e600fcd636c57d2571b1c34",
            "843d1c4eb880ac8ac0c9c95696507957d0155ddb",
            "60f5ef198a2dd5745545c1f0c47aa3fb5776f881",
            "e49c136a9e5627e0681b808a3b97e6a6e661ae79",
            "31be3cc98cee37b79b0619e3e1c2be4f1aa56e6c",
            "c2aa88c6405658dc225e485488371fb2433fa735"
        ),
        listOf(
            "fe69a66c7423eea9c8fa2eff8d9dafb4f17a62f5",
            "85743e899bc82dbfa36faaa7a25b7cfd372432cd",
            "6e4afd501fa6b4a1823ca3b10bd9aa0ba97ba182",
            "2e066e624badb76a184c8f90fba053330e650e92",
            "07e942aa4e3cd7c04dedc1d46e2e8cc4c741b3d9",
            "b6582318ddcfb67a53a67d676b8ad869aded629a",
            "f1be3ee877703140d34f97ea1ab3a07c141333e2",
            "85f164703e61a63131be7e45958e0794123904f9",
            "82a504a002ba6e6c67f3cd67cedb66dc169bab7a"
        )
    )

    @Test
    fun testRipemd160Msg() {
        keys.forEachIndexed { num, key ->
            RipemdMessages.testVectors.forEachIndexed { idx, msg ->
                val hmac = KeyHashedMac.create(BinHex.decodeToBin(key), Ripemd160Hash)
                hmac.update(msg)
                assertEquals(BinHex.encodeToHex(hmac.final()), testVectorsDigest[num][idx].lowercase())
            }
        }
    }
}