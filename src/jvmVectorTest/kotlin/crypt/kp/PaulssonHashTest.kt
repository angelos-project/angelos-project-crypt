package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.PaulssonHash
import kotlin.test.Test
import kotlin.test.assertEquals

class PaulssonHashTest {

    val testVectorsDigest = listOf(
        "c7d681dc07155b75bc2e61480454507eb1c6dc85894fb102a5d7767a8fe74eb29f78fecad15ded04ec73e39fbc83d36abb72c3ddddbef434964f8d5c219f28be1381770194f915a151b7cb0e53ba46ed3b1e21c1ab3ef75ea480e0c74d04b41214db1762a1f4415c80ee17377095c467c1d3ecc58d51b9cb37556ea972de9b9a",
        "eff64fc562a404b227774b597e0c1ca40f59f7a739ff3059293e61e16ff2c11e73410da023378818a43a8ef7c9cadcd88082c871f881381f3ace7e2088ee4860645b163287089c6b410e6b87d02faac738a70272e15e717c21b5022f410a686de43fc3033bd24131afedd42c32d0b401291de8f5085813689d8aa97a20eab9a4",
        "eeae387bd8639820dc6d3a5fadb2c3423caa7d4a6e54328d4e69abb0809f6da12bc019864f99ab81372318501236fde3def70dd1f46805e19f3163a28afe85047565f041275e18cb92a270f94db96bc16961d254dbf98cdd83e655b8e8c26dc0fc9b3ceeb4c15f06b6724d8a3e17e4d26259e4f76e6f696a1953dcf9f87133f1",
        "0e11ca7154ef0f9fa10c63f12950abbf14b1a3d1ed16f84c86f6249ef89f5b06305432fcc32c5c6589eb5afdef31fd0bf7e4a01060d0d339a918a9a94c2cd936b3080182b7981e0dc9c032bd9ec88303f79e35daab63f9d30da23156db9925ef260f3b5c79f1469a38da00a6e2300731202faed3a5cc811891f631f9ad76449a",
        "641a3800ebb2e99acc07a719c58912b78d0d8a4b8a9b8f86360d2c7cea4c749dbb49ff32c48a583b5ca909804e407465d78f67bbbb8b10db8d18a0928fe22dcfb1934ff2f37761907c14fa64d58d3ed2cefa6212d0767f90c3fd6b26a27a45e9278c9afc5361e66d5e7d25935d8110859eccbc66a9eb46e8b4d8b13faf5b6fca",
        "3d0f8842e63c78da193c2fdbf37197bb702d2c244fc02eb3150f83d13a4b1836cd4becec32361edc0e2b6baab91b6cf77b256552c46a4617c435845ac04ebf328cd03c31189d6a89af91aa0b6cc12d0ce73b87c340a8a9cfc1f1fc5e2e2fb8e941ac58ad5a9259be18077206a375a5efee43c1042da9ee55fa7da1155b5768f1",
        "38d033c1b83f9ecddff9e2f45caea9072b7cd7a89a2de7e9366ad14db71905f04384ab3764e679c23cec189b1853874f2ba7a2ea0a3362c1a5ec63f297a6d0f137a16724569cdc6fc787615e0d071138c674d5720195d1dd308ad71744088f025077e7f857d073267eb18015ef4f232aad865397db42693c138bf7775ed779b1",
        "78e58ebcab97c976267e3d3a8cd334c23f601e696e5ea3dbe4108061d50a16ac4e18481b647992e3eb30220524c6ece1e603eb19a672761852988c9d68501668215c037d2283a857603db61ec5c12fa0f68415aff4f619b6603049f809726172c202858495ed2b37d48fd6219b1f131537bf7004af63fbddd3eb1d4b8101459f",
        "a050980ce0b13a137814bc07862366bf2cebc3b14c08df44be66a27b1a8b0461704153af64c4bee54ba6e96a0c8660a353a6d6c8781981f7b4561b9e5a4cd0d83303fbea596d9d990b230631e4432197e342b60044be3cba52beb410ae812475bb8259a1addb97f89ae35f8a423719ff3ceefe68b2b756714d1e98281a26762f"
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
    fun testPaulssonHash() {
        testVectors.forEachIndexed { idx, msg ->
            val algo = PaulssonHash()
            algo.update(msg)
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}