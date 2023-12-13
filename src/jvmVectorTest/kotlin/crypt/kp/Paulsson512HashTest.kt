package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.crypt.kp.PaulssonHash
import org.angproj.crypt.kp.PaulssonSponge
import kotlin.test.Test
import kotlin.test.assertEquals

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "7c579be5605dd675029a41b2c8f2eab84c400235c3e5cc3300e1d0ad13b965e27a83e29a6720e6d68d6c4430b97d6ce4878f86ea57f45a3c4bc4e8e450c8a1cc6032d1808b2e5f28da41c951337c789595f93359d237bcf4f20425ee30b94f37d572bf2733359a1167d058aab9558ea80e3c5ecba5448fbf8b57574e63608ac6",
        "6bb408f51c666939cb5e78c96cdfc78da92f95260004cd1bac6c5d9e6455dd9be3557e49928217e9e095f7aed0b485ea8c9d1c56552a4520ca08ea89747a8fe9b589916820e3492f443ff04d412c828e681ef7208b5c24cfef6d92780582016418efb371ee7d89fb2f7ea5eabd0a69d8cc31c7ffa9235bfaae4ad894f76fd467",
        "0c25452073779cff946470eb85de0b6854432364aa70fd9c8dd7a527dc425ece8d3e599615b6a88cd16a53c63a341ba0d7cd31cf7c0b6cb900b13b18aadc2902ef555c9ccfded08be9c60752927c7adc7b4a6dc0bf1d1be2d2688e8abe7f0d37c26c1be5997b327b86e957462854da85978f93c801f6e97f6145528463eda55b",
        "e666e088dc5cdb65789f1babf44fb0f40927012955ef19fb0a37f2b23546021173740208711344a182e66bed3d9c610d3ec7ed0a519cb3ab02993a36e7f6d51cd6c4335cb0febb0ec6888b1780176aca11afbbc239cd5d6293a62b627caf13063f38b7d65fcfcc6d5236a91e2538c481c6131b47ca658ae59299b864f453380f",
        "b57bdad36271233a3888c0ebd6793e9efd6a0173a27e2642bcc74e2d558c733094742fe276b9113e8e32b4915607aee9335e0f70056a5daaf57804f709b84030d1090b8a0c8e5cb4ed63f0928b228fb0144678367057ef882c8a87f65d175f4956f7249de0e2143cc934e17a6fa0d8d416b8b7726196a7c7dda6739c94b8f1c2",
        "46db7690127543cfa920ed28b06c51d83451e651299ba4a2b26ece080fc963ba4197570072ab921e915307078bcc9dc64ae6a1f3c2afa9796f0c3a44dc51c35002465ae303137e1f8b212f6d921395986dae2ae1c6212b25567cc6076bbd5f4f6dd4272e9d2cdef46d44eccb38bb2d20574f244709bf20242c7cec5ef4807d0a",
        "0b19960403a664df30bcd358fe23365915da5bd1f02af7aa745ae2e7a13333b89e052ae3ea38ebdb34dc700a4eabd3d4e81372c5074111e5ba7772a2049aa0e1f73d65f1c412ffa8ef0bc870f15ab1cfab79f237ce3557aa97d0456c9e4a183913c46f9f3ee0918d59bd87266d8bff56c254a49620438ab85f6ab2e8a014666d",
        "f32362f297356879403f160c6ff69581be1533adb22eee54526d5dc32454befe6b18f3085f59c0fd5cf8e8c6dad649800e8474856630c8bd2d89df5984ba60e005a55af79e3c348b9b08311d0ee8c97cd55514a41e85eed23970f94b5db56df51fe5763a1f5760872c4f3c736265789c2b5798857da455d0ccd90161b34438c2",
        "b8d35583cc994ca927f1e540856f2897729264d6cf7bcc91c8c6116a138d0f0a98e80a8c3d8454760616240f5214b0d04d519928c5d3e3ab910b9c2112941aefd79afb1524b772b9a15a7d7c3ecf4ab45eb60dc9cd7ec369f0bcc94bd334a8f18703ecd344f06cc23673e23eff069baddb7ced743f40e7446b214eeff4f80281"
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
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }
}