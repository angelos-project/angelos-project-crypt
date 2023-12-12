package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.Paulsson512Hash
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "e159dc381d9a6a20e8fad3a6bd9a285f1420dc2cbc76fa61f569f31c182bc1ec78c2a28615a5766eaebe9af4ba94ac2856a8063909d8b0875d57a273ee7ed813",
        "8151b277f03de7b0072cec32d7be9a37ea321d4aa3ad3e358a218d441fcb721ae5e04c2273917988530c0e3bb1f8af00879750d214583eeb1b681d7f2e0cb775",
        "ed437b314c93a68bfe5cf0eca3af7042fd304d8920803268b19eff4e577c1008f524134ea1ae22e2ec8f1d85fa9db86a1035e960fde560e2506328f07a4e6b00",
        "b173eea5e87c600f50c481203c71138640617d0f770fcebcc8405744cc2418fe63ae4fd676ae0d3ad47ddc134f8cb0634a627f6e05364711cb6eb73e2b3f770f",
        "2f531c8a64b8573c1e6d8da870f7245d2b7847cd19135757f9cdb99ce6614154c7f0dde3e55f6905c23ab6b43be64cd9c430163cc834b1a0860272ba31b9831c",
        "161d9fc830575ea3e46ac7fe7712f172b29157799aabf3085bec7a2fc4d50fd6c34fe2318de044d20031cf9c54059044f32c8387254f45fb0a68d6b9048432b7",
        "0baef8de31523cc7eed85dc2442ab5b86e9e10b23189892efa082bdfcba1edded94ff46fb69b5881698183632917f7199fd80b377d9eb0ee3ee90b14d7473ae7",
        "94f8e3519ebca9503c1fcdb5e977b2e5403887a60605d0722ca7e82eb3e6edb47c86fae94e8b39592faaa7afb61adeb5616551fcdae8107e5a76ee574cd4b9d2",
        "5fadfbae10df0d59e5e1a9439b86dd3c7865edd7e539e2d6783c159c64352edf913cc0cf6c6378c5161faf98a80bb40b1fdbf4fa267a210def9988149ca8f7d2"
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
            println(BinHex.encodeToHex(algo.final()))
            //assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}