package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex
import org.angproj.crypt.ellipticcurve.Curve
import org.angproj.crypt.ellipticcurve.Ecdsa
import org.angproj.crypt.ellipticcurve.Point
import org.angproj.crypt.ellipticcurve.PrivateKey
import org.angproj.crypt.ellipticcurve.PublicKey
import org.angproj.crypt.ellipticcurve.Signature
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Prime192Test {

    /**
     * From NIST SP.800-186, 3.2.1.2, P-192, p.10
     * Information missing, refers to FIPS 186-4.
     *
     * D.1.2.1 Curve P-192
     * "The integers p and n are given in decimal form; bit strings and field elements are given in
     * hexadecimal."
     *
     * field a is not given!
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp192Random1.domainParameters
        /*assertEquals(
            Secp192r1.a.toString(),
            ""
        )*/
        assertEquals(
            BigInteger(dp.b.value.toByteArray()).toString(16),
            "64210519" + "e59c80e7" + "0fa7e9ab" + "72243049" + "feb8deec" + "c146b9b1"
        )
        assertEquals(
            BigInteger(dp.n.value.toByteArray()).toString(),
            "6277101735386680763835789423176059013767194773182842284081"
        )
        assertEquals(
            BigInteger(dp.p.value.toByteArray()).toString(),
            "6277101735386680763835789423207666416083908700390324961279"
        )
        assertEquals(
            BigInteger(dp.G.x.value.toByteArray()).toString(16),
            "188da80e" + "b03090f6" + "7cbf20eb" + "43a18800" + "f4ff0afd" + "82ff1012"
        )
        assertEquals(
            BigInteger(dp.G.y.value.toByteArray()).toString(16),
            "7192b95" + "ffc8da78" + "631011ed" + "6b24cdd5" + "73f977a1" + "1e794811"
        )
    }

    @Test
    fun testKeyPair () {
        val curve = Curve.nistP192
        keyPairIter(P_192_KeyPair.testVectors) { d, qX, qY ->
            val point = Point(
                BigInteger(qX, 16),
                BigInteger(qY, 16)
            )
            assertTrue(curve.contains(point))
            assertContains(
                qX,
                PrivateKey(curve, BigInteger(d, 16)).publicKey().point.x.toString(16)
            )
        }
    }

    fun keyPairIter(
        file: String,
        process: (
            d: String,
            qX: String,
            qY: String,
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val d = rows[0].substring(4).trim()
            val qX = rows[1].substring(5).trim()
            val qY = rows[2].substring(5).trim()
            process(d, qX, qY)
        }
    }

    object P_192_KeyPair {
        val testVectors: String = """#  CAVS 11.1
#  "Key Pair" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Thu May 05 09:19:12 2011


[P-192]

d = c691800ae3691ed451ae4fa3f66a78798320f510b76ad287
Qx = e3338f21f02e36bc8519d09f9299d70356522a2b9f9b2a17
Qy = 110d18f6902e3b86234bd4fea97a2e29ca9bbf1b70172900

d = 756270bc1c92cd7a586d7c808d75570cee966a42a61435ed
Qx = 8b64bda6551000bf416c1d14560afacedb720a87f06d8bdd
Qy = 10d5179a28bff54c1d885a9d068978c53b0096511a69551b

d = 982195f26694ca1cc68a09a5098b65bb79926eb07f2575e1
Qx = c1763de1fda85fda7be3dcd4ff77b78e41838e41ef0cc25b
Qy = d4dcf3b2fc15df8ad103e5343e5f278f5da06705c5442758

d = 578a58f71c8deb5ceff819f0f5f1b3d92b9b045a0a1bf5b4
Qx = 534007e5b736cdc0aa05902c7fc269a51e8543cf6c0315e7
Qy = 6beaccfd2e13d49baad46245de2be3b27f7797466885d12a

d = 05fb42a935e09a0f94f78f396705e1b9fa784019fd8263e5
Qx = 7f9ed5bc4af05caad90e802b79032742d93c4e9bb7ea6149
Qy = 51b851c914f778ec675fb2797e508cb1403ba299215f033c

d = 4f90490b6e62415879c329487f2f8d31720955dafcedc859
Qx = 31e1096f883c13314bdec397d3d3515fd0beec74a2a316dc
Qy = cafa81c4bec33ed3ce5cb2216a550e6615792735486894c4

d = 2fa1a90467dae40ada59dcf7e8700eb5a02f111b2620c3e1
Qx = 89b91cb3958c31683f32f5745e7542e4b1c543cbbe36a592
Qy = 4be6e3bef23bf12f7c945b0fe1e5e829b8ce7fc2d93daa3c

d = 84bc2eb3e6470e23c429b9521cb617138da2f462240a0dcc
Qx = 268e5ab7a80c96ec3b05cf7902ecbee224188958259c0f2d
Qy = ba444ecc3f1eadc53f27dc08e36663b4ea6846f761722a78

d = b9e820ac3e062622c62fd6cab097b18cdb404e5c5505825e
Qx = e749efc0a078d53e654d9cc66393eda2b729bd28f5b5946a
Qy = dc713df0fd53def63dc93921aecd866fca8cce31a5e2f914

d = f96154b47b40c675d7749fad59ffbbd45604f17c2491cd79
Qx = d23429f841f283d07a20f7fea6f0588476a20e934be92014
Qy = 0cd968ba262e03276f09765da0a358a045988fd9e5e7a13f"""
    }

    @Test
    fun testPkv () {
        val curve = Curve.nistP192
        pkvIter(P_192_PKV.testVectors) {qX, qY, result ->
            val point = Point(
                BigInteger(qX, 16),
                BigInteger(qY, 16)
            )
            val valid = when(result[0]) {
                'P' -> true
                'F' -> false
                else -> error("")
            }
            assertEquals(curve.contains(point), valid)
        }
    }

    fun pkvIter(
        file: String,
        process: (
            qX: String,
            qY: String,
            result: String
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val qX = rows[0].substring(5).trim()
            val qY = rows[1].substring(5).trim()
            val result = rows[2].substring(9).trim()
            process(qX, qY, result)
        }
    }

    object P_192_PKV {
        val testVectors: String = """#  CAVS 11.0
#  "PKV" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:01 2011


[P-192]

Qx = 491c0c4761b0a4a147b5e4ce03a531546644f5d1e3d05e57
Qy = 6fa5addd47c5d6be3933fbff88f57a6c8ca0232c471965de
Result = F (2 - Point not on curve)

Qx = 646c22e8aa5f7833390e0399155ac198ae42470bba4fc834
Qy = 8d4afcfffd80e69a4d180178b37c44572495b7b267ee32a9
Result = P (0 )

Qx = 4c6b9ea0dec92ecfff7799470be6a2277b9169daf45d54bb
Qy = f0eab42826704f51b26ae98036e83230becb639dd1964627
Result = F (2 - Point not on curve)

Qx = 0673c8bb717b055c3d6f55c06acfcfb7260361ed3ec0f414
Qy = ba8b172826eb0b854026968d2338a180450a27906f6eddea
Result = P (0 )

Qx = 82c949295156192df0b52480e38c810751ac570daec460a3
Qy = 200057ada615c80b8ff256ce8d47f2562b74a438f1921ac3
Result = F (2 - Point not on curve)

Qx = 284fbaa76ce0faae2ca4867d01092fa1ace5724cd12c8dd0
Qy = e42af3dbf3206be3fcbcc3a7ccaf60c73dc29e7bb9b44fca
Result = P (0 )

Qx = 1b574acd4fb0f60dde3e3b5f3f0e94211f95112e43cba6fd2
Qy = bcc1b8a770f01a22e84d7f14e44932ffe094d8e3b1e6ac26
Result = F (1 - Q_x or Q_y out of range)

Qx = 16ba109f1f1bb44e0d05b80181c03412ea764a59601d17e9f
Qy = 0569a843dbb4e287db420d6b9fe30cd7b5d578b052315f56
Result = F (1 - Q_x or Q_y out of range)

Qx = 1333308a7c833ede5189d25ea3525919c9bd16370d904938d
Qy = b10fd01d67df75ff9b726c700c1b50596c9f0766ea56f80e
Result = F (1 - Q_x or Q_y out of range)

Qx = 9671ec444cff24c8a5be80b018fa505ed6109a731e88c91a
Qy = fe79dae23008e46bf4230c895aab261a95845a77f06d0655
Result = P (0 )

Qx = 158e8b6f0b14216bc52fe8897b4305d870ede70436a96741d
Qy = fb3f970b19a313571a1a23be310923f85acc1cab0a157cbd
Result = F (1 - Q_x or Q_y out of range)

Qx = ace95b650c08f73dbb4fa7b4bbdebd6b809a25b28ed135ef
Qy = e9b8679404166d1329dd539ad52aad9a1b6681f5f26bb9aa
Result = F (2 - Point not on curve)"""
    }

    @Test
    fun testSigGen () {
        val curve = Curve.nistP192
        sigGenIter(P_192_SigGen.testVectors) { msg, qX, qY, r, s ->
            val publicKey = PublicKey(
                Point(
                    BigInteger("00" + qX, 16),
                    BigInteger("00" + qY, 16)
                ),
                curve,
            )
            val signature = Signature(
                BigInteger("00" + r, 16),
                BigInteger("00" + s, 16)
            )
            println(signature.r.toString(16))
            println(signature.s.toString(16))
            println(curve.contains(publicKey.point))
            assertTrue(Ecdsa.verify(msg, signature, publicKey))
        }
    }

    fun sigGenIter(
        file: String,
        process: (
            msg: ByteArray,
            qX: String,
            qY: String,
            r: String,
            s: String
        ) -> Unit
    ) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val msg = BinHex.decodeToBin(rows[0].substring(6).trim())
            val qX = rows[1].substring(5).trim()
            val qY = rows[2].substring(5).trim()
            val r = rows[3].substring(4).trim()
            val s = rows[4].substring(4).trim()
            process(msg, qX, qY, r, s)
        }
    }

    object P_192_SigGen {
        val testVectors: String = """#  CAVS 11.2
#  "SigGen" information for "ecdsa_values"
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Aug 16 16:05:58 2011



[P-192]

Msg = 66e98a165854cd07989b1ee0ec3f8dbe0ee3c2fb0051ef53a0be03457c4f21bce7dc50ef4df37486c3207dfee26bde4ed62340cbb2da784906b1b783b4d601bdff4ae1a7e5e85a85afa3208dc60f0990c823bedddb3db663426665152ed7b093d6bda506c93a694b83ac71553f31f5cc0d6ba2fa248090e8796573c4915d1586
Qx = 14f69738599689f5706ab71343becc886ef1569a2d1137fe
Qy = 0cf5a433909e33217fb4df6b9593f71d43fb1c2a5653b763
R = af1f749e3df6220ff04efd178618a977e0838b1b9dc126e3
S = 8990a04c6cc0ff26264ecf8f7831381a9dbc6e53cc8cc860

Msg = d39ad56135bec4c3c4362d59d3d9175acb386670c5db0a1757ce7646ad5d5352dc1b760f7429103854b42511c3c0404abc24642788d645de9369b84178d4699c5e75cce18756560226aeec9f71ab9ce1f86e8ba635582ede6484bd349594e5f2ffb1be1e97cdfce9e12b694b062293e7281ec134f2e72cde73266c6a2c25311a
Qx = 386afa71afc065019f3d2021ead531ed1d365887122d2d4b
Qy = bbfb6e9cdb32c2252015acfb4cfb049b08b4cb279c64928a
R = 337be42eebdcedd97678eeaae9d1b231b740a191a293c22a
S = 9d6766b391e95f649e05442453a85466da29eaa97ddcfc62

Msg = 477101daa282a5a55b48c5313290c8da65b07cc4d41a5a1463300c60a05a2c63a6564ff641d0423c5233931c75be53f4e2da1b8b91ab4d48a2e59ca2fbe1cfd833f0c1e2afefada70a1ba870ba276f9df18c6397c221d20585eb78437c36460fb7e4628634066e504ba90d1749f2a33d5c6e5dceafa372b3d5eba8296b821972
Qx = 3b1c19d73b6a4d7a12003530a54ae0f5ba18d75c98a0db95
Qy = afb8bd8c94c6e3d5dc050e3641c0fad771557ce97f5f3dba
R = e36d5dbb9560d959814cbd30aa6a405da9652fbd250da9ab
S = f2db3d62733f6d08b08ef0682f579ac527950117f39e474b

Msg = 85bcd406f4cdc8f30e19553ba92f9c0894b2833d7bb0b5650a30ebe8550a698578fccbfc7af9010b78c4999cffbe3c3db9fd7cd04c9dcd847f5bd0049f5fd8ee215aa78688d178ac89b9430b6d433c94b8f99c2b080aa59f3fe2e0e0e4feaa6cc7ffee0be4fd34bc8a1ddbefbd66a2f4d8d06eb4779e479f93eea3e5ef8fe7e9
Qx = 21933e6e8cdcfff62f36c45e83dece1280a888663a34608f
Qy = 565890e37fdf563595d24524ef1da9aae246892e75a74234
R = 1791e503645e53a8304f2a44715942c6aab315b0086f4147
S = 366309b7f79b2fd98d6c2f3a8424178b01235041f9869552

Msg = c8888ddf35fb43d262a5f4eebec73195f0fa79571b68a3c3613b7b7ca656b0909643ee06e96d47b6e590d75e256da8f19ac2d332cf79e882712090f60e9971ad1d1352613f39803bc4a870ec71a7a5f98012d456e9984737e0277f239fa31d4a65de0318bfc5b60a06d706c129dcf255acd3c6ebf12c868ab7378f2ae0dd7c0e
Qx = 4828ad4aff5d87dcca98a341344a462edf277850755c7277
Qy = 8a1bfc6b7b0424e15454ac198d4d697927f4eaaf9d14d88e
R = c438ca66c5d3b19e2bcd26a5c54742435f25ee0e9364b41c
S = ff194479695afdcc88645f2a309cb99717beac26794c0658

Msg = 637ac61c9a108a77d1d34f42676f9d314bedeb32c9341208a3cf023564f0b41ef1b5606a61abefab0e91b340ec5e567a1a8510fdb7771d69f7696f3a7658a0edcc4b2a6561d9da8c83ea823db5c2ef624a3b1a593351a29d16e47aa2a0badf001bc48ed443a23363556824016aa34f320397720ee5cb8aea27c88d34891d5b9f
Qx = 2f5d05cbd620145b41d0a1c656d676ead2fc651b3cf71cc2
Qy = 88d2386cfb812d02a1fa19578a7cd04b356920a2b2e69839
R = cde95d12d650102c535fdc1994fc468308b05e2af7af9c14
S = c2606a54e7592214a265270918322ae33d487268b9108cae

Msg = d154754899fcbfe2a48e2c70bc406cf2b13070a59d9eced46c5868f19c7549e44246cfa34e8a0f030fe175cefa84ab15a732bcc71c6e7a834ee71dabe5382cba40d1a1d7b93548aa2a67686eb09ea547ae4025d6150ef20be39c9f3bea052f73d3dc320b6266317efc064c7966f82231cf42f1fa11e8fe16346038ca334518b7
Qx = 47e8ec5e1edb1d23c04ebfeefd8cb8bf39cd48c348de3ea7
Qy = b1167b3a4e2fbe167bbd7b7b06bf1314d58d9e3976ffa47f
R = 539e285389399c785dfc4f3bfccbc825602f443ae0dca3d1
S = ed56e54fbaa0b7c0bc462920314031c7a3e7b2eb701cb9db

Msg = 264be37369846f67019fb31f94fc9e9ab03c5c2405a62ad8d1c968649b63ef0c2482a6f29be9509410b5a8962b41b0c21e46464dd847f85b7b2c19c6112d141b7bf101e6a9f776301136a045590092bb572cdcaf3c7460520c1c61a552d5dd8011e01dd9fd768a43eb68c2b58703eb8d453648d49af709c46be26887c623f142
Qx = a4db1303bf13696e26678286e27c166dd87be98cc8be2844
Qy = 09b4de84bbfa77e049ace272a12d0dd141d363e74ec12511
R = 6818d44aea421701e3f41600c867fb3d3749eadbf33820fa
S = 658b228b2edd8569f6940fee1c303626e48181094581987b

Msg = 66e69dc2a47b31515a71456e329cf02b08331b1543d57e5c66c9cb34e28a287a2f83e39b1a1761b261e458f07d493e41bf25022c9ee2f9fb50b31b1d97196fc8deacc4e38418a22278668073067d3799f0e8b716c1d9f94f5e6e35ebde34547c9beaa8618987dbebc77e25be7440c27ac402a49b53c1659eb9847f665ee2bfdd
Qx = 270d7bb9bfab54529240426a08508f1ed90dc7914cdf11f3
Qy = c91ed7f2eca39f563757c5b518d9c28d4d41e7bdc0c7f53e
R = db20f09b69ac79e224258800ebe6cc51a2e12536b69396c8
S = d31df9544277f4a79676fdc7a51faef73ee2a0d1057ff3a2

Msg = ed3f4b0c4b79ddd5b048b7b9f3f687fb578aca7560493f3ae57e9b7b65777ea79fd0a6d65cf4636890e04ae9a12a2e3807424ce915f9c6e7163f535e9c3134d815536aa69f1926811ead81718bec6dee8fb1eb4550577f3e8b79e1fce3fcf6247850fe42bcf20f688236c21858432c63175f1053b86d528769b3af1de0e1acd5
Qx = 6a5723e72f6270160f256ddd403c108503388d4320f7926c
Qy = f52b0e0e4c8871443cfc0c231e856f6adc2a7c3318a43578
R = 6505663a952b741c4583e755496740833d5901fc09433963
S = de0411972e27bfb80362b44650e687866cd202a4199d605c

Msg = 6a8b595076518880f008382eed3e51f2b3f06c4fce4c8c9261bb08fdbb1fcc252a59c299140f8ac443ac711581a8a15d650995786c2ad02f4bbbcf5d84093c51387f64743c6deb2b6f09beff4ab44afcb87eec5628eba853e1f81d649659b8953b05292817f4137d192708366feab498555658332b197bc5cb7adc5c1997aabb
Qx = 1c0e4fc6d8f4e7605f2bf57541395f5167d41d8bdd5c4f80
Qy = 775da73226020bad6ec4df5022a8c61aee064372ba535ae7
R = bc540a8f5ea81c6144698f5452556f17da76a52636461d70
S = 2de23f6cc9a45899a9f605dab9f28fddf9e376ea06c92db5

Msg = efd5d82000249ef168a04bc39f32356bbae83e0be19af1da03cdce23c8d7e191c8449aa9fa79d8986f97472f40a2ec2cc5e3febb078af83c7898ec47e2fc1aed9efcc14106d735182ca9320ccd8e70c7800a777e33a9b5bbd8e2ce605c4133e9d9c6f6f10c88ddf7099db5632d3ee8c45527525c4d3d51de8c731b761eee550a
Qx = 194e8fdd4b6b84f1955d1919387f96facf6feadd8105d8dd
Qy = 2eb47a2ed3aac4250be37f9e31d7ff87be4043ac1eac401f
R = 0bbd331b5cbcaef2b26e27bdc559d31c62d5c9b6b8979603
S = c1670a3b435805fa7ab20e6dd8ffa1012e9fb41b1d7d0176

Msg = f9e23934afaa64634c91b5a148c1fd85a9b5188ccaf0cf6b5beffda4a95cf1581f84145c0d0bcb8fa9de9f6a546a9cd81e0cf6488bc8f301983d04b6f9d8355ba549c83794331d39c728d68f8ad43875f678d34bb99b65eb1afcfc3937ca5bfc91179feccb8b220baf55a0397267980339e3f5bddbd0eed0e9122b54ec9929e2
Qx = 18b94fc7825bad91b1ca5a69cdf576377ad76965670d3967
Qy = 0a73253d6df54d5105f0ca2f948d0686706dbd064744f7c3
R = 1a813b607c0b4d71e7acdac040eb78b3795e53bc498bae85
S = 67fc1124c30966473dc6a1f14ae32ec2dd860b3667c61dec

Msg = aa0090570f6fa81ed02fa1ce2c124ffd3574f2f8d4285e570eb359d585b8455f42b4ff6bb1ec068beda3e0423e8fb916e18e4ee60796097e1fea6d695894b506620fb8df7544103595b87be01403815658845809df5304f67f77c27af359a7689379855a63600e444fd63c4fb554f0e424bcd906644897e6b6c020b15cf163ad
Qx = 3888a94ef288ebe32b4b9b53bf2d3a85b45976583251f0ec
Qy = 30156d8e0d212a66c071fb2e7b422ef1784a95dacffd9edb
R = 37033150afed029fd8390716eac6500223e72154ccff9fee
S = b4e978e8d499cc1be576dea74c0dd74cafe9e794a7563172

Msg = a3bbbad76671d8fdacbbee1f5ab241bd0178ff996e7677187ce888ea2d9c5aedf9f64d4f66382ff4d900b217ac131d3380584124d528af89d8c5286e7b275528cbffb87f7ff6e313cc35e8f8812b10a44f8ad00b6893f8084d942effe0af9bc1c1dce4afd7315c232d62780b008483d7161692965141bb5b835f82e684bb9480
Qx = b11956a29af6984043973e2de46d53d870e04687cae59728
Qy = c915f88aa0fa9822762cc4e60df759d189a10c486b901d5e
R = c459f7bcea050210e3369ac174ba89c823ca1b0d4c0964f6
S = 04715cba9ba31c4ed9bf0be07d194e2a709294472cc60bdf"""
    }

    object P_192_SigVer {
        val testVectors: String = """#  CAVS 11.0
#  "SigVer" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:17 2011



[P-192]

Msg = df76204710e9f57c952fddd2abe5146394930fa18cd40f98ffb7f773f9645a42de03d9fa5bd9acbd9cb47e5cc08a7e1b8a5db3b459618f9e5a61c88efc5f633fa47b126fa316bf2a6536694aea2c2f3f7a75aa0c95e080922aec3611097b46ec99a572accee65748567d45a413154e57cea49db12a940afdd621eb0307022cd1
Qx = 11e255bf74c988058b3a14b75449ad707b190943d7f48969
Qy = da1457970c6c6e516cd36a4d5ae27badde19b19b0f4f05ce
R = 57a9bd79d4badf061f64ee049bb4df10e5b5596127f38845
S = 5b8fe44e47500881a17280f8e87dfe0a40264756355869a0
Result = F (1 - Message changed)

Msg = 5b6e533af0fa055b26cf63e9c16e7a0f53f2846c0c985fb8d27e3dab94ab9c1adc507964a44dc00c6bf708a788197df9bd391d5b0f1c285fd58cd1b710583b53fa9d9fcae244d2fb36cce7f4e2493013d8ad6c6af0473b68f389d5b6f20efc60dddc2f3551e62170b0d5699877077ba4ccd8d7635721801b53ffb071e5d6ca88
Qx = 2ce86304cfb3faebabdc47fba6a314bf29bb6cd92a890bc3
Qy = 266bf27c2ea5998219ad3012c9f92f7031a7ba4ca3c5f6bd
R = 898b7c436eb061fe8a2a505d38a3c4858625bd5f1de5b530
S = 60dc313c47fda3c2ae652a9bb4321ac95614ee72b995581b
Result = F (1 - Message changed)

Msg = db0c3cd541468b318e7cd3dc4479c29ba2ceb7231a74d3db05b453e496441d570cb398cd2c06c125efa47c81786f279248347516b5457a9e9864029547e3277ec30a237d32b962198f4edc40a0c9a18b282fb5c45f2c1fa69ffb3de3608b5a6c66da82ec39a59595d6449b20a4d0570fa9861f36fe70eb798042abd7c982dea8
Qx = 3ce8768d3e94de0451d07731c74a6335ae456b5621a4ee7e
Qy = dfcd02c5184ca8c949f91ded3c25b092a1ae527e8b2fa4e0
R = 913a46c6411db9582cc0fe64b6eb58ab26134bf78920c15b
S = 19bd5b8f15fb8cb5c5bcc48f1efb98a69c1a97ebe94c286e
Result = P (0 )

Msg = 6df3c25fde0d6db224be15ccdfb4fef20e80c45dd64b40917fc5afeac05f11783cbad00d4c5fa293ffb0b0a35f1ff854f69df05191556c2f449bc1fdae1f2b1c7e676c51367cb61830e3cb3bdece63a45594ce6f0d5fda44e224f7e45fd999013ce2de5325cc71e8be3daebce32b0789344da6e0d1cf5379624a99c85bf324d0
Qx = 33cf3ccb9cc7ca7552bf203cec334ea96ba0d3a6b1519ca9
Qy = 6bf9b4851ddf094aa3865926282434b62a9086e2dc8bd22c
R = 6f74b619e4da6e11bc968e104f686766f028ecf5a485dcac
S = 0b81e5e7f851c76c47cc1fe5cf55b56591bbb4d15f0a97c3
Result = P (0 )

Msg = e08f1c97f5e0c02bf6e2e481ec50e8c95e64afa7679c52210bba16bbcb0e9ce077f7588444a844081f28d65e409c2f6d01c995b7f1d52339244dbf92ce6d4d68109b22081234bc4c528bb0c23c2fe30afa6dfd808d161b1c1d83c5968dd51fcd53816369870d8ebf39145af608906d68f059d04b64e8a2919d610e0c31d17ad6
Qx = 07c5a35e9b3d1ea220dff0dc46248faeb8181b45079df0b7
Qy = c3c63a7cd5f0d8c612916c0204f849403ce9ef6288099cb7
R = 78e19935385b6803f098eb785e7cdb559aacdd07539c4dee
S = 8492be5ee7063f7e0749988e6a61acc509539d64170eb959
Result = F (4 - Q changed)

Msg = 043b52c572ce4431b3c08f2d5e1ba2c563e403b69d5886d0a675c29eefbcc549103607ba5cb59dffb95caff0af612c12be12bcbbf516f777c0f6d4ab02dc6478639ac0e2eced4032d103ed988d3c3915342eee9805eb65683f86f8c137d3b9c2a66f658747009392a2be316f223331976e3e8096f65e3869cc2d162ab62b2eef
Qx = 1c470ce738d06e9432236e5adfa2a5c6629db3739d778fa2
Qy = 634400cfd1c26ed9fbf4e294b6e7c7a8c8d4c0c9e53e0f49
R = 341eb0fd96432a35f4c3e7b1765b32d26ed3530d2946d7bf
S = a3a6ae8ec61f3755ab01bd2d4dd17b2732a7d30c30032eee
Result = F (3 - S changed)

Msg = eff7e7f0795fddbc47a3481f7d297f5969d46e005c85fea2b8e2d2ad4b56348cdb584f1302f19e1d3d78a4733fa1def08bb6f6e3412339b538384bcc4e2b19aefc5c106a2edd8d345132944fad55c430f5fe8ae01c2096a26bb78bd6d1c1fb1d7a8a92bea22d9148ac587e796e4f1a5fd03494bd9585fcb078e6fa927b21c01f
Qx = ad84883d5feb1e0f942fd954b9d9da03c16f9f75fddb7101
Qy = 6cc7b11d9dd7d17e5230d94ebe8b5d70b3cc8f8a9ecba313
R = f00e339209507dcd8066a39c6b059be1c3456a68995e46fe
S = 0e295e8df49ac71a460fa6c3bdcb224fcd7bff61d9dc3ee0
Result = F (2 - R changed)

Msg = debeb86d16af548a2a715ccd93e045a2997ea4247ecef89bf9fdca43e6c7f9503b994f2e3ef3570df511739ddeca2916e1b8be7c839906215cf57a6bcb028bd7e8c4363eba78d958ab33457e38c714b4ede63de6918ae5729e9378e7f4b2f90ccd879625f4068071321ff9f7099eaf83a55048eb10673e96a51fed3fc7a2eaf7
Qx = b1b28e8741146a6a1cf9afa867cecac4cc93a7eb7393ff10
Qy = 9c5dfe8a0d6dd994f3321255b9b5c7bf9cef6b7cbd889c32
R = db39b6affb561e4374df02b3a2e40f876dbbf763bd1ae6ac
S = ea1543c5bc299308a1d3daf9ef2d95da2205abd15379249b
Result = P (0 )

Msg = 2084d0b136de308bf10ebdceab96412829b74df820e2db5ff771319acf94098fc18eeea56621e9d99d465d0f882230a5d8362b4e4bd6a41e72dc5ae1b89da0b1a01cbec84e7e949249be80067fa282378aee9e2631d782db32ba69b43f0e3ffc469806bff5dd78dd7eddee5e97fe8005c5476e28dbcebfbfbd7427ba6acf2e98
Qx = 26d88d50026ec246091c524669220224343b4ae6c13580ee
Qy = 4cb0cd0589967f271c4f4da567c22167d0fa6a48fe1606f3
R = a87da21a03c3168076c5243c31bc0f87438e76847b291a30
S = b349dfe049f64981577b74dc599392f75ff4579e1a2ad3c9
Result = F (4 - Q changed)

Msg = 4a25b06a1d9009d6b4797d26002023a5704a796913d9a985fd9f0a80eb74ea062a868bdeb4f16b1c9d90c898ad12649e4f5086e7a9dd41c80ddff8aa901e22baf85b4be2dce35bf706641b341cfef5e7809dfc6a74872c558231f2f21b57eff17e4bf95ddb8e62cfb89d7de2950d5fe6cbdae3d9bafdf193233cff40d346d713
Qx = f89e5799506c909dee00be2120bc2b47f628ed5c3c450857
Qy = 2093684f67edca970549542f3defb1b9b5137d6c45994027
R = dc4f2e39cace6c795cb44c18e7602fe964de79293c3e26f1
S = 35fe1de584b6674cab7e3706fa5aab6491ade34edad8c54e
Result = F (3 - S changed)

Msg = 59395ba101bab05cde354aa9e59aee8c15bea72d07a30339c7fa7e86e2e63ff6c003029d34a756265268087281dedf83d1aa89998eb6d69a15bf24b7e6111b4dfb929a095802f050e48150de77527fad34ded01819fc5240d46f48afd8b0d93bb1ef092d9454e8a33b8a0933f07e36d1e76462f3d0fcdff3ad13d2b4e14ce388
Qx = b836cea36d88f59eee849c365659108c3656543355ff05f6
Qy = 58ffbaa563b1afafc7fb62c641d277e69a8d8c96c3369e91
R = e1e81f92ab8907ede4f23d120d122efe5751fdb09db3c05a
S = f8d84df0f53369c94a386862dd59b4722191d6bb5a884558
Result = F (3 - S changed)

Msg = ab51f051a7aa408f9fe75912e6df4511e723e434ba3c29f8f5338654c557a59d3d3a59a7edfdd42db7cd1a72deaeb6296077ab92861eade7b93691f697b6030f46fc77200812621f102fa9a8958159e824299ab93a88de0e12b95e92bc5ec78c6fa030062071502e5314eefc32e09815dc4198f0ffff2437a9c3d5705bd75bdc
Qx = b3c29f8e9e7ab6ea33f8a548e92458b416770b5a24e68d2c
Qy = d3908b266d7469d05312d4ed70f24e785f9c8ee13db162c2
R = 3e95a61b8bc610303adce45581ae6b49bfc308386fe8913b
S = cb5a86c8a1ca489764b1330455b57388b5d861c364adaa3b
Result = F (4 - Q changed)

Msg = 124675f2090f51fc57225b52332d9bb2fa6b6fce21c822946d46c0b2669f295a092c6542bd0537f0886af534836196f68ba10d313aafd6401547ce5907fd47570f245cbc35ef82f6c2d660e1b03be18e604e87438b2e02c27b5101ce7b2548ec8cf70267591559966d2f4561a403a102df3a8409fc8ccea3062b011ba85f53a1
Qx = 545b6ddd74380f1af4c7872db4de1e926c06091e1c11ea4b
Qy = f38dddfc1792206c7b3f49ea97c3886db5131a798b8ddb85
R = b3b4d00eb4e44d97ba5f91118132709177d657fc0010f6ed
S = 7a71bab1ef0f5614c1fa8bc1b93e56208b10b83cfd63c8be
Result = F (2 - R changed)

Msg = f3703fafb9bdba5aa37a4b86bc9beed56eec07a6e9ecea8bc9dfa0fef21fe94fbfb37de15d2297967189b48801ec98fa91ca938e8cf402b8e69f187200b7df31cc7679183136ac6cb1839e494975dc93983ca12e44fd56729add55d9745bf2fb60e2bcbdfe9e00c5ab908f315bddc188cb6cbaf0d9afeca22bc1dd9c17b22b95
Qx = 0a89e23400ca41324a5e6590f0084d87bd187f17370687b0
Qy = 6783915e97c959ba03b66cdcc0ee2d21f07095cde53794c4
R = 94c64492871c7c0f679f97a497f3311d30dc66c302e57a4c
S = a13c2a931626570713fa7ddca031af2f7f0c2130c66db500
Result = F (1 - Message changed)

Msg = 611aa497fb457857450b76b083b59156ebac5d1f4e8ed1b4fb814b8ce1c8d41bf394a546d11ceb1ba4efc8bf49899aa7b3257f0b8ad3199818ca8e6666ffd329a2f72babc4d9ea7a72c77fdd206eb10d7bdc29a20892314c08dbbbc3a05ca50136eb3bae1ea5fedc763aa4f44e8c5ff00d7962cb0ff4a236935bd98dd504d109
Qx = 13f4a359f421d1ca2df0f1f8326e33df194005d7b97b8fe9
Qy = 2b50883ee068323a1b685a5e1a913639466e805a54a71780
R = fd42aa7073077a420afb0dd55c4ead0f817a5d131c351638
S = bb336d456f53fcc05fa6dcb6fbb004ab71bf3a65ee8e64c9
Result = F (2 - R changed)"""
    }
}