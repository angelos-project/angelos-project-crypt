package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.aux.util.Epoch
import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.readULongAt
import org.angproj.crypt.kp.PaulssonHash
import org.angproj.crypt.kp.PaulssonRandom
import org.angproj.crypt.kp.SimpleRandom
import org.angproj.crypt.kp.StdCLibRandom
import kotlin.experimental.xor
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class PaulssonHashTest {

    val testVectorsDigest = listOf(
        "d0746597a56a36d65a828deeed71879ae47e173ed36d92b5bec6c41ac467b13fdb8974cdef65ea3ada8d6e2742553bb2d907294ca257d6f6af5b7ddf2782ea4c7b7070ca104fa6a5748c20e92e25a5f0d52dfddb1eaaf7194f7f2abb8580d9caa9ccf68d66b30836af4d60687ed027949e5c1db3a46bc9b7b398a6665cf97329",
        "f18f617b63126f01e43ca8c05d22de4afa4d07d1520d40b7fe6d43e45fc6530f50016612aa933b4964b15e39234afa211014ba7ce525a5e2ec133d4861a92b2c4737c9c37236ea64bd2e26e143c2f286052f07ffbfceff876bb1661ec00c7536ecaf8601aaa0d135d8fa6a5abc4d9669a7ef67489703bacbc0e3f426524dd85c",
        "5a594b57311b712c3584c31731798db0dbfbf32559bd786b39dcf0123cf9428a23bae1e4dbd33f6667a7281571aba0976d93d6193f757e1183b9640a098c841b4822c5ae08bd370b65202f34b7722b076de7a1d7e254cccba891f9823395072073e59c661434d5ba16e03f867b13ceb1c8cbf1ae7451243f70be44cd3ea37a81",
        "0466e0f2146af24b2450d8654498ae94486c75f003cb5d7cb1d4502132eeb743ec8c17df1cbd26f2c6a68c53b8644ddafff19486264ae2674253860c48178255538ae66aa1bdb599ca5b5ae8487ba35d4dd67a8c67a6ac8eed4479b076c58c8a63360cc3875a6a96d5aa2dc57d58c6610982f5768994e75d4c6d4fee6276d3fb",
        "04e802543a89988eae179c0efdafa82179c14c0c564a233d92afd9f85abc3411ce14e812d53bc84edc830426657ce97f6a92f49d78063f431ad573f294d389a6bf267d4eead5ca1cc104df50d03482f87d63a71fffaa6a48dc23fa7be81e012362abbc2c1cec355c633906078ae2b71063b004d2a5f9baeeb79f4e7255972bfa",
        "800e6c5821ef8edd62bcae8661314eb6efc04df6738bd5726be0c47b3f7315053fe5daa8023e0cf9bba918705a8030eef1581f851760705d1cb41837a201098b34962403f80cbcecbac85672e7f324209ab32cfe942047def48fff23794bfe4c30891f63a3ddd61d9383e59071e33d50c966c30d94fd4976af668d757e585c5d",
        "2a6b328040935e178bb172d723b97945928d9c61e37bbd88268f883ce2e66c8e2357958b4e793b4758c9555ee3a7ca95ef1f77a22d4e433da368c849eddeefe3deeae89850db902b6507faa16c5d6c7b0e2003b0fb54d62f47b6f4c013a5fd18c3316ef760ccf28d51c8a0040f231353286951b358f43bca6457ba0259b60d45",
        "ccc4952b6a05bead139ea9e8234bf2b60cc43a4fce5fcf99ecccabcacf7dc32004b0678265673da8dc2542613d04d06708391f1750c20a0491d45a3d7e4471116a3c612933f1727b176e84b2145a650378c0d6b67c5a51863ef3d478d10ee8c679678f9f99c00aec6a051fc598a193769092d7c44987e26b8e1a0ac330a22d3c",
        "89956378902832390b3cd7407f486e043f32bbda60b8d543d8ef4366f2231794cca47b231966385678b96bca1682871c710b1bd42d779c499cbdf754ade3bc55a2b276f9f810ccc3d9f8fbe1f3344aae1f7e28d901bd36059c52dce402d264f86775367723ec5322100d912507b1d625a1e92ec282565f9ddf0de20dda521900"
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