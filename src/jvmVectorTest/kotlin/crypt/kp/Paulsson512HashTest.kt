package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.PaulssonHash
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "f3066a4fed3dbace096d740c491696995ddcf71f7ec5028804a7b54b4fe9fa59f97a20ab4cc07c635ea05fa6b20e5c16ac7487e446c18e04822187152231db08f22e2e0f2a369d87ecf2e317b3c34968efd9bf549132a8b9e3a2a1756b283a769362b73fa98c8e8b931c9ce8ae5287036c9469e313483921b03493a76a991f47",
        "52a604bbd44b77d12872f7b00d80f4ed6d1238b9099245aaedee33f6da9ad809392761d9c4111f39693e0d3e55c1fc715c92d7b38516c2c1b09b4568cdb7e31c087fbde8a007269674fce35c65d1b8cb9558ed50e98dadc98886e9a5600b39d9c6cf31601c6eb3e9c37c78e9dc12f283590e59f4e9c2d1075210df8a48922052",
        "2d4957f91ff156363b897f3b8dce1637709bf503dafb42274117425c747be6ca1c339fb4209f38cb7f1da114ee51a82aa1501f482342a11fe1cbcfeb0e0c0e3ed1b79a3c15ce7d7b740d83d99819db8ff49162ccc868397d010755511bde6b9bf1680106310250cb130c55e4175937c6c5afeb8562de2d930733037973daeeb5",
        "5a6e4f5210fe9fb5f3dc739e09a89c99fbaabc23741a17339204b60fe9ad9d17bfa5afad6e9afc88c3a1e3ac637cfda0c3f56ffeb23a7b1e29068fffbb172818066e51736a168db021d4f49a9fe110d520a9eb1f8a96a9bf354a99a60041f17596499bcc61f396f84eeb0723fe6ac8e342eaeadbe823f64d3e0ef6ccff214d54",
        "0141bb7c4d5a86f5c62bf8b6e642c6766c951b2b8d86c981217cd9180d4b635cec5f5f7c39a7eb6872fd75bb82033910b70a1f15ad94c27085551e5a919aa438b8108f3849c017bfda1498e6ee227dab4e9568071b5f3fd4c17a3006f2177220d98cc6b6fe198023a169b03b091443070d4e892d5e666924d43d1038f3a63a54",
        "3311846012d55c92d5119b4400782e2ac52ac8ffdf9dd378c0a1a9b0371c127726bf26e3e6119a23e71bf290b255414cb937f3a816f2ad6461d16b6c6ea54b01807d977e9630c98972926691126976309430b3b10ca66199ecd129d28b461b7452ba68c96a2f9ea5a0db5dc1138a019ea27c2da3d792fd329410629af2e59e55",
        "e9a3d90dfb888d90730135d83da7b7e6e23ea9345da54a9722838cfc3932b147e231072d341b5e094af186caf07adb46a510f71548be5029895be705adf32c5dc8ef39fcd435cc2b6ceb7e77296bc9060196972bd79b3c334349ee88706951b28b16ec6bc9abd5c35ef8b1a814c0225c7c05aeec119a321dfc7026c2fa60eccb",
        "ef8fbb31551c0dc6e3d30c6ece528a952757916ca6061fa126ceeb30679706566ebee34ecabeaecce02e48b3d6e5db2907553fc29ec95ce515c394b09e9feaab1cf0cb701e117dcb35bfe24fceca530db33a6b62bfd232e0f6792202b0c54affe72f5bf480e7844634af9fd6bcb062c2b2b235994e61ae729a78315dfd7354c6",
        "a0f8c28626503a537c20275f04d6db0dc9dec91a8fb0e2313c69416e4e4d324520dc1ba69207313070f7521ea73430c994b4bffebb11b25b738938bf7c37a258d9d28a9b7a12f280793fdb4c6934e9970a24e5f922c48310ad2e17b19ebe6d39e89d5ea85354a271d79697f68a927477cf8d4498036792524c67d7049e351c7e"
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
            val algo = PaulssonHash()
            algo.update(msg)
            //println(BinHex.encodeToHex(algo.final()))
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}