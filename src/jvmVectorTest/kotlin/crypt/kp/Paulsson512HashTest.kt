package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.PaulssonHash
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "f2d1472e3b163677cb911b45a60d714896d0ff30b309604b597fab391aceeb7e6a62bf1ad5e5d4b951f129eed717217487addac350634f8d7e4ece7c9882eff50716c02c391868573d691d387b2629c69ba4232a246888a75c94a2d33b941869edb07c46976a8444c51c58e5704105644e9ed599de90e8adb6310c8885ae84e6",
        "35d5576b75ce2be8f131b6eca9a25f2efa1ed67e4962e12ebf6a4930fcad1d2ef3d0e1514c72a4856f5c0e18af97e19b07c89439d374618477706a2a0c4d7e56fff163a0a7b79a770519ba5097351c47e404680c0ee39400661e222cdf6aa109260126cd4fa880140822f002eee3b5bef5db67bcd611012e95f7703b0cf3e5dc",
        "54d6fad9e58d8ded036235670044a2484de91cab18337f8a48f318002935c2427ad55dda51d23dd465bf520ee5239b96f8a9296e2394271f9a1c111cbabca05cde5b68aadfd5ec11f5fe70ac65e649014ff3ac8939340a16d642a8278cd2660bb1606a10e0c888a7cb2fc61d9612be799094b8c9e3b7315bda1f33b4a31f8966",
        "61ef7e511b9b2374c659cac86f24e2f76ad6885287e893933ec3551649a3782d040d5c24994b2e38c4c72aa47eeaadab025cba122838a3f13c8e42ca180a0cc5ca279d8a02e0890e24b6752e65830a9653c8bf21a34e6f20fc851c028be9cf13a72d3af882ca7f8e44c1c8aecefcf3ff084915fdb6bd74c3efbb01bbb7cfa85f",
        "d9ea807e38fb23cd1ab5f46f4a3aae34f49f6c42189177b61f090e0548490981b2035ce41d23c4f520e93e21e66f31b2e544fe481ae7b036119a572cbbdea9ed5ab8a6b92dfbe945da686c19bc42bcc88bdaa65ab19d737d3719014d1ce4648d6317ef5fed44ace58fa4d3e9865cfa96e791d0276c6354bd1445c25bcdfe5d7f",
        "71e3c9cf344f09bbefe76fbbb012c0078b48c4e8557c619ae6d122c12257b5a8d778278f7720ff870368d7058d00b76cc37ef22fdd864df439cb78ee421c3d03d3beeb49ba6146c89694ea10fb1ee2dbc00e650ddf71e377ad7eae83a9cca8c838324d2026a98be1f42eb1c4cff51de4ab5e1a830f57f0f7ceced7ff42a283ec",
        "59e4efebef917ea378953938ef67f36aa74cdc045d4a0bc9c54df4fc94d731f3b3af4a9cceed6e0789a90d4a699138f2c705dc844cd61d9fa0f83d145cc27c26433141bf316849a3228d26f7029d49d5dc6d7762a63e9cd6192b9215a5cd09c9a9cd7ea20635cba64b75894336f520c9daa653378c2124c6bfdec3b5bdc95a28",
        "ca8bfbf66a13c0cc00111dce6705640ee6da638321a13ab248a44521c522c3081e1f7e3c838a3a2a9ef10d679693ab490ca9b6abdd8f37019c9de70254e7c133d8a3d2b3d36c500327837e7817123bb118ae9de1deddb3bd27a6aee456e22c0c89495d6fddbce55b2b80c6996b2640ebe9754ee98c4474660c40bd66f418e64e",
        "258f54039714d9291120ccfcf3b92f7d26caabeab55104b7f8eb20ac94aa752b5aeb1ecf12ec5b625fb3f6e7da87ddee9ebe042a4c51b4f352158974c0d220b64a98639b9b6fe52482e64e9986087654159c5e70406ae807f042b0686f51c631c4cdce543eb96ebbc391f0c59d9e6af74cd9dc734f8ad3f0272469cc920a9e45"
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