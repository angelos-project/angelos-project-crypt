package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.Paulsson512Hash
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "f9ae9167544406a9b795a77daacb1406e6b4a944d797c42ea629f8502c4ff536d498eeede1b3039fe6f1023c5806b6dd7fd48c780e1f16b4400e4a649cdc440c",
        "2cc7e46617b9ccd058167e623afeb6becafc4fd895e080f28db684d53d50502905354d8345be6c207173e75d390c96ce5c9ccc98c9a0d3d5faa0e3ef03218743",
        "e22793c49b4844e05a33f0040ec0a89d66dc0b1566c8a8a8d1e763183c608fd2865e417bff7852f75011e81eff8c474f3770b0df06409f2aef64388d230ab657",
        "2f07c3c3714284a0e45ab472c72badf4f5972765b663b06170617437e9c30f235aefd2095bbfa1976eb8d960965f8e26c683b44b4ac019e3a48b30251516b0f2",
        "c17370671c92b8450733ae6a8d57bead3ccf3339fab1fe32a8843f761841b3954ed4f16cc88a6e8dda894bafeb189456f7b77c2236c3bb1bb144ff9e7f54386f",
        "f35b01aaa52518d172b8aa3e0a6cbb6ab68e35b519225a7ec5d8b5e1546d60e3c0471c8eab5f1beca9be9cd695a35cb2914698862b93b96103a6c382a1dc40d5",
        "72915614b85233b15ea87ca420079db6f424074473b1318bd49233dd8c21dd7dbfb8bcfca27a2e60a3e13ce04f11c7585bf5b1596eadc2f6b477f03232603c95",
        "6a1c3fd6c947c09c77d9ce8368a7ed8a0e17f2c74976cd7af3bd53c2e1b7f97ce391a098d831c8f69bd7a026f6bcf94f4976b46ac237c6651c760bb77b48be6d",
        "772f7a4a3cdeed5d8fc74024b2491aad6b3467cb6b7f783dbd02038c74bd684bcd38d236ec97f8a587f617232554eaf95056949a8e027a9007da2f773bc073fb"
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