package org.angproj.crypt.sec

import org.angproj.crypt.Hash
import org.angproj.crypt.ec.NistPrime
import org.angproj.crypt.sha.Sha1Hash
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class Prime521Test : AbstractNistPTest() {

    override val hash: Hash = Sha1Hash
    override val curve: Curves<PrimeDomainParameters> = NistPrime.P_521.curve

    /**
     * From NIST SP.800-186, 3.2.1.5, P-521, p.12-13
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp521Random1.domainParameters
        assertEquals(
            BigInteger(dp.a.toByteArray()).toString(),
            "686479766013060971498190079908139321726943530014330540939" +
                    "446345918554318339765605212255964066145455497729631139148" +
                    "0858037121987999716643812574028291115057148"
        )
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(),
            "1093849038073734274511112390766805569936207598951683748994" +
                    "5863944959531161507350160137087375737596232485921322967063" +
                    "13309438452531591012912142327488478985984"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "686479766013060971498190079908139321726943530014330540939" +
                    "446345918554318339765539424505774633321719753296399637136" +
                    "3321113864768612440380340372808892707005449"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "686479766013060971498190079908139321726943530014330540939" +
                    "446345918554318339765605212255964066145455497729631139148" +
                    "0858037121987999716643812574028291115057151"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(),
            "2661740802050217063228768716723360960729859168756973147706" +
                    "6713684188029449964278084915450806277719023520942412250655" +
                    "58662157113545570916814161637315895999846"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(),
            "37571800257700204635455072244911836035944551347697624866945" +
                    "67779615544477440556316691234405012945539562144444537289428" +
                    "522585666729196580810124344277578376784"
        )
    }

    @Test
    fun testKeyPair() {
        testKeyPair(P_521_KeyPair.testVectors)
    }

    @Test
    fun testPkv() {
        testPkv(P_521_PKV.testVectors)
    }

    @Test
    fun testSigGen() {
        testSigGen(P_521_SigGen.testVectors)
    }

    @Test
    fun testSigVer() {
        testSigVer(P_521_SigVer.testVectors)
    }

    object P_521_KeyPair {
        val testVectors: String = """#  CAVS 11.1
#  "Key Pair" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Thu May 05 09:19:12 2011


[P-521]

d = 00ecb8c48f273006782a83df41fd9d654257548edacfdbf56a0330d17b9693b5bb0e223272707cb31a14377e2048758569741c61b5f798e1f20cfd315f86130cc7c5
Qx = 00fa72969f28b863c80fde28f3fd2b15336d9d7ed3b9d5630c184a18ed850a6604b57c02797f60185fa1d01ffe6cc5ee5c3158b9bbd66765c25ea371f21094d2dc84
Qy = 007ed2303594b5ef4768a7579c1c9ff7854a1968a0c408aaddf355d7941c27502a317d44fcdc8901f2c719c3a7fa43c77fc3998bbadce75d4f6f5fb10735e4071db4

d = 009a90288bb61dfd1d027518b4cdc6acb8a632a321c9e7a95cba2f954c4b1c9c31136b97b6490672e92b6a963f8003d8372162f93025c23dd8e755bab36be99b65e4
Qx = 00f384fda776118ca0ee197954d909bf7e2a37d3ec41565a1f8a2f3d020262177de75774ef02e82f677c37ff6ec54e11839f6ff089a85930aa9cf69f22850256257a
Qy = 010da76a34efd5fb68376ba9ab655921810b1b35a358eacc6b580cb53b750af701a15b34fd1e5ff493c3b0b0e7691ba3c9daae1b12c8bc4e3c3b31367acc441b87f2

d = 014037920870300cc2b8e7762b270b70c98d8789110e9d5c79106d357b99bbfcef1c7fefdf14ccb2e5e5a672dc52acbeda385f7d24d9354eb6b7f58033b67cfb3694
Qx = 00144d6d59bce70471d162efd33ebb383570acb3c1f19d2e12389b163635dbe43f2e53ee8358bc27535c47d9b033fa0c48b3a4050a2397760370ceb821c8a357e2ea
Qy = 00894d5137d47f6bd734e60465fdafbd9775e63e7b479c7ba014c97a06dff61190fac7bdced928200d81013f9865ebe8ebbb52d0d6e06694fe65d1e18ee83b978a16

d = 00d429823150789d5e4427ffd19674a7ace0f36f8512bffe6fe5de60241029e81983992d40d0b5461c0889f1618fce14ace6d492437d12a023395447a8b802efad86
Qx = 01fcc2aa2bc5fcb541e320c42fb3a17cc8bca6a96f126c03f76c85aa455ca8680c06d22263c12a75145acd18a113aa1f2414f45075b64e443eccb52f6a57103d623a
Qy = 01be25db25550fa6aa60e33e9b05ef8d4e2365409f5363ac23a4906c3b9620a81016fcfed19bc6aae66bbb293ed8909bc749dc89cbae31c43bada8cfc0d5365e300e

d = 0157b08e4d6807caca293fb43f03e24ac8bbb0a607fde42bb3581fe664efca41008a9d4aab577b679405cc832c982eff4bb50547617e7eaad31a232dc538f7e535bb
Qx = 00d4e0e38227502af8556a20cb84d3f3a2698b9a66c2ca91d35c363425b35db31328f6b126d5d35df11e283afcd68ae24695727c5da51aed9c713bd3524cfe2ec0cb
Qy = 01248cfca2f2bd3cd554dbe4be6611b5020bc0dc33c9531989603e736f02da818a6f4cc8be1595727647cd604e9c8113bb29900ed44d5109bd99663ff3a6c05881a4

d = 01ee746001b0fa9853f77fe54f8835ce7f917fd80c323d82403b6678aaed6e30a4734cefe088236a43488c5025fa26371971d455355fc07b8b89d8b143921a075d7e
Qx = 0118d6d4daa1b1bce86d9f7ea4bfc354a546025e0874a4cfa9ebde1199321967c078aceed7853b18e833f08c1d1b3d5283ee60198089a688d548bdc0af812850c54e
Qy = 008f2d707f0a0aeb8df236ed0c33e68bf894b5ffa43bcbb09dc5f521b3ae3a352de1981e34d99dd36437f2a41f038f2475470b36a3bc3e0eccd26eb30487e31a759d

d = 01333c985613e31b97064aa7a5bd0308afc877d283750df579fb01e26f2ff7270f29b4178ce15ff94faff25b064f3db761f21dadd7a2b0f8feabfe30cfde06a7d6e1
Qx = 01d653bac8927c420111cfbb01d1b92a50986f48f43c9ee6caf0a7e997d69baebc496d8d950355821e0a990161d94f29e0ee301315efe3338011c33f1ffd015648b8
Qy = 0197399880981b6f97e0ee8fe306df1cc18e7b8a0eaf2f6d315bec38950ecc9d52aded578b3b61970d38de61c831831a04144508cf2492c65f0d832f74d09d2c1215

d = 00a29465c27ea8e3d05a1075d33f6a3cbd2ef35141f53d3b862301ed87cfdd22b08a5402995b5792874b45b89a0a5f43541c149ecc53c5d3b88744b299fa3a83732c
Qx = 01bdd7032afcae58e81c4125dd631f08c58626d9d54ec0c404c7af84b68558e0700260e84afc03ccba11c3b4579a3f8305cc504f6b52d52d881bda04ac5a69ea24c3
Qy = 01fb96b547ed6148f7c39f0b3f9064a4f7e1dc3a9ede02077e2ff613a6ea3e9190b58547a3c7f21f0d5eff7d50b022a693b455798eab272bed21cf2d232aa984b3b0

d = 00d63ebd700b040f3546f9e2640d31f80af41c1751bd23536f2621114fc9758499f099dbe2598376b1ca8665025173a4f8aca2fae03656d059646993a5844d5ec0b1
Qx = 015e22a20e8d4a4b8973ae6a786bbcb30e8f1ae4189f51abbd36e0f0847b38a790c30e75fb0da212316b1621ef6e4647b7e7e1f4ffab30cba1c5b88e4a37a420bdc9
Qy = 019131c0094d8bc60f843109a67374631e8cf9097d3f7e49b7c1f06c7d6fcbdae4fb0e3cfe9f8f33b8ad34c6cceb983471034f1fcf716c7b80c541cb7b9d21651baf

d = 01c14060704f0c676c5e8f348096fef44194e4febf43870411657b454dd4aa7882a989224df4e66864a78653de8c2969838739bfb88d8a86bbba1710dc7b523c885c
Qx = 005136ff6e47fa833732531396e1f9dea6227f4e3ed5587cc4f6a7d3bcd64a27ce2c18304f26e6415a3cbdf77460bc13bf4058ceb8788195f532055acfdfb9a0333a
Qy = 00aebc65776bbb3064b4e740dd8b48d8c21a155d2eaa09172eb3ebe57b323c1ff18c3fdd4cdbd04d52fefaf393a4438f8524f9a1c12d932dae04396632e09c63de04"""
    }

    object P_521_PKV {
        val testVectors: String = """#  CAVS 11.0
#  "PKV" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:01 2011


[P-521]

Qx = 07573e6115674bbffb65097f3dcb05597a35193bf0ed1a4b90a86006eabe5ed638d1e11adb769cd6ed7fba181dea42ffc38a611a6f162fe10b925b80ce9c419ac80
Qy = 2dbe133e4ff21af1aa50742fc1f7c74cbe8342fe5037c33b961f65b218f947a4acada6d53b1e0bf9ada5be979652275bce77194c8fae2066c5531196aa9997dd2bc
Result = F (1 - Q_x or Q_y out of range)

Qx = 014bc514f2b5664a35d1728cc096cc7de2b0391089c014f5f3e4d1048ec5d8497fbe6cfe008b634d708d12bac6aa616c78af1576c0a8676a17a16773ee5a827e3b5
Qy = 2c027dade98d1a7b433368075e19e8269d8465bbc91c4c3aebfdb2418115d331ea946663713d67f9226f4920c85b0d78c312e87826672f107bcd6c19ea17fff4331
Result = F (1 - Q_x or Q_y out of range)

Qx = 11062a3b6bc340c35104a406bedb3fb37baf5c745c378491ab136818a67f73763d31846cb837217442ce90c3ac16bee13031b2675a065746ea92e92871bc1a3de7f
Qy = 1dfc4dbf1dd62e1c22a936f209def208794af1a05abca30d0453143013aa755c81a12372eb54dcd89204ff1bc3203220e40e9eb784f0328e5a36b9fca50a62d5aa8
Result = P (0 )

Qx = 071e46a03739adee5238538306e794d13fcf7e456a156e21bc69cad0c8573b4aec7b0325a49212c50ee267d43697122701e59cbfed13f4c3dcb46fd85845b3e9f06
Qy = 16140ca52ad93a3aabe17ac22a1cac989e83529aa60880b4d43ca8fc7830f027cd4024eaa40798cabd671d37f1b9e4b7cdb15759fce3b14f1b85aaaabb328051eb1
Result = P (0 )

Qx = 259d237214e071f725c77674b38b8b6cc590bed12b7cfdb3d177162afb2a40ab0540f77386ab98ac049711287a5b80891ece13ea34c545c19593e87a15237e2a108
Qy = 1f33c8963f0981350d77615f0e04e24515a76efc690140272efddaa8cb85b140acad7fc42cf6da7ff5f3ce47183f46e7272b06eca4c74200536223fef2d74e095b1
Result = F (1 - Q_x or Q_y out of range)

Qx = 089eb672b023d7098bb864bd7789b9f6f4ee268aa9dca6ba3268023b2119be34ee035699d7f1f776ff6028a91824fbfefa22671ed2ce7ffb46ddcece33d1087985d
Qy = 07b7ea0588de385a35a5c0e7dba9cd86fd13d91f71a97a9769e483c2fb823cc3ae9fb9800a05814a25af676f780de1d805a174b70703b51e46455d0eae78b5d7e1d
Result = F (2 - Point not on curve)

Qx = 1859642073e648dd5580346ad5ba9daec8b60d5b574938b2f16ef2a48128ffcadaa46be1fa10ded234d72ec3c38d7cb898281d25264c00d83c2a14bab175ddb9d2b
Qy = 11ad23cb933fa28e00d7d9b0faf78297e4f2b026e6fff74e456b8f2df938e52ceaf98760070c8d22c7f742728434eb6ae6afe7193ee81bc730f5549eebacf7f9952
Result = F (2 - Point not on curve)

Qx = 18402a60a68ff20ba1144ff3571bcc889583c4d375b5797bff7450db77ad3729bacfe2510d508974aa897c54642c0ac53c8aab85d73edd4eb689253d09a0cd9df00
Qy = 1dd68f2242e7c3b9142919020f0a1f867dd691526bc86b92bd593ca9b58fdb362939cdf79b8b4161769a912e241a499b73d5f37a21c900951342c8bef37cc7988d9
Result = P (0 )

Qx = 176b36128e5a294876c57fac275f388155eb5715c8ace3d90ee4c31b755c8f867327b7e037e7be8f6b521b2674e1786d67294c1f5b098be16102ddd361d92505fd0
Qy = 0d01b3f053aecde9e0c534d6a518fe24c68ef246b4cff071a3ebbc742152c9d4e872b1acd5a76a42847fe98e9360e7c33ba8575cf75218e89564839ac9f13e6ef14
Result = F (2 - Point not on curve)

Qx = 01f26ff28f769521f232fb83e697c9cde606d11383115deef0af16fc05e4631850b57975ae91299b87133fc53bdb424fd8f21b28c7636055eca88f3417d80a3bde1
Qy = 2748bee6ea7c3b9790fb927c0e691436b946d35b7d52b98398cbcf433683138f48ebe93a007e611f00a73dcfadfd2b0bedbc48bf2de204969df04cebfc1018072f1
Result = F (1 - Q_x or Q_y out of range)

Qx = 0fcacf322f6be9da5342dae87cbc8cdcb22bc489ca6e97b186b97d2ac02610518b5ee72be37f22825278fb205895f2f823540b91b313abb54a6b41506152e0deec3
Qy = 187333ce6fe5e6dea5d08d8f5950b5207cb8eb34fa0de2cae5acad8bc8436ff617b45bd8f2975f2762982219b3136bffec3f6c58f8f2cd0d6eb2ebd46467219126f
Result = F (2 - Point not on curve)

Qx = 014bc514f2b5664a35d1728cc096cc7de2b0391089c014f5f3e4d1048ec5d8497fbe6cfe008b634d708d12bac6aa616c78af1576c0a8676a17a16773ee5a827e3b5
Qy = 0c027dade98d1a7b433368075e19e8269d8465bbc91c4c3aebfdb2418115d331ea946663713d67f9226f4920c85b0d78c312e87826672f107bcd6c19ea17fff4332
Result = P (0 )"""
    }

    object P_521_SigGen {
        val testVectors: String = """#  CAVS 11.2
#  "SigGen" information for "ecdsa_values"
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Aug 16 16:05:58 2011



[P-521]

Msg = cc92ca36a76760752b5a45ca5d7235947122a6002f1d4e7d9c6be570d7bd2c2941fe2e16e02ac637066361d22d420568266b93e773644921f1a78a7dbaf5e2ed49ee4520dfdf97f826db723e140d2395134cf5ac5ff0b3b8afe4682217fd697c2d8a95ba6b2ddc9fd4e9fe75da7b950180ee56b6bc6a94291f4d05c5b77cc9c0
Qx = 0f1dc7ccb09d61e6af379b89aca905b49779fbe43a94c8ef384ccbf660f4805c965a3a24ed5a962c24809415cdecfdfe50fd18f1266073154b62f355fe4c98af6e5
Qy = 1740eb95b8e31a0434c988f2edd550b8dc6c45c6f504309255370cce57e821fcb4f60bad17a8fb9a3f4dc67ed4860ae6dd3ed4b1f51b98451b7e7095cc87d4d6279
R = 0d314dde74cce6024518980ad85cc7d5a294e148fa26f0648486a6d2882ca7a92a1c934c4b01ee1f6cc1dcc5920d49719a1823cfa32a69cda710b0e95623bbb0451
S = 14b0b93bda137a5293900eb6cb6b151e3301b8e2944eaee5ce0f8df87c9841b61372a2d70e775c6758d29a7d24f62c69dc884b54ce67a8edb51a072e4797a9b036d

Msg = 7a32e2334f404d734d4c54223cd0162d63c738fb9c6b9350ae432972d65d5e8ba7d9654f15a7f8decc904cecc67b441f1ce9e5b81cc30a769d5558b78f9a75e1d6ba750059d195b155f22c3ef86cde0a2b4b1330af22a7676b08e42904e1ad14afdffb9043488eb29379dc60fa2b4eb637f5aba97b12d4eac165b30d5eba6eeb
Qx = 062a005d80ec3ee67c1c184eb2efe8d1063e81eb2f919357bb442052d977d143dd7ca9be43f624028b7f8c82422e5fdf63d19f2cea7e2a4f6678a57165a1c45aa34
Qy = 1724cb834d5383a189ccf7f372f16694af1dbafadb3f569d34d2ed54d70aab106bbbf041f539dae97c6efa35d41b390b1ad751eed2c0d838784111bb562de9beb72
R = 021bca796dafa1cb5bfbdc3fd70787ed6405d418a12e224b34d9516b89e29a486607bb7d51f6823b585579f33a86f6a6b31197e7dd16744e90716a7dc947ac96930
S = 0c426dde552bb1c56bba173930bd547639ac8bc752f762eaaf8924a8ae5f646ca93ae46488ead0f3758e787f23795920af0263950f83c73f5994b7f4ad68232866c

Msg = d7f34b6df5439df3355145c2b160ca56c6ea111418937edbd65b5cdd40177fad6622279832502839a82348486d42e9b38626e8f06317c4911c570319fa9dd10e39c4eb4f0cc5bba8e2e3c4a4e82541a2cf09092bcf77b683ecf4067e78188295d9425d398c8a9e686af7827d2af31b4c28fb3e6e649b9cdace9a2fb5c172d86c
Qx = 0d2ec1ed013cc9275c04a8ddbea6168d92e522aae42002c3b0ff10086b21671d6d5e085e95d411ac971fc574a93d994431d621e67fb3ee4599b7dd7d489d0c84ba6
Qy = 056453c783ab46f553c6eae03c46d4f4463c61c4caade1b583c3c82891f2fda1717351f7d24ff718ef7b3f02b39f2bd97f9d03ad08d05a2eaedb0590d941f776001
R = 0e77d338c3241620989a4d178b851bd6c8dd0667002c2fee603dd2bbee19f572d2d479c63ed41eeb95988cb8faa912342f546f0445edb158b0f2c6d7aee4b37a741
S = 102394e3536a9d4c59e8ffa1945c07beebf535cd3ea001a4c3109d7db6b11f3c89403c49e06d50db4aaa9e52e32b9dd4d3cf4fedb5e72808fdc5b14d6c4d2919d96

Msg = 05eba719fd162fa6276326673491deb623707c765ba0a8f9f79b892161f5f0c622e3d358f8fbfec6293a059e184b7d7516066b897a38fda5f4a278451e4472fb54db4111c6a8b370cb7300c079d0fcd172a1913a00dc6d40658ef8f96c2089b144d9d98c8ea5b86db515fe177ea6c592e046d5d115146091f168f08473139daf
Qx = 0e1d42554f06016c22024a057a1b9e06c6a422a85242d7ab691c5f878529b69bced42996a0dad508b4d887d33a323fa093a1090e317297af73860ad89563ca9d02b
Qy = 0bd54ce4b34bfd91c23f2fefa19bbaf2b365d4643032f2db03313aefd232dadefc39aa9670fcc75e6f6061faba62fc38c674a4ac8e85308d96d2b976ee1725878a9
R = 1f96cb2221597b9743696ad42de0ecf7645c63e490258c2bef6d12bde51b7d17efb98a970090330170e807f8149f2b278ff56723673a35c2bebba130e199516a2f3
S = 054a2ce361b3514270d411b46f1aba6bfd4c082c197d9d1a92cb04a77050a2fa6e0991337ce285de1a146650091969d17bbe589387a5d6e9a4d432f1cd0d86a2a28

Msg = 9347744634ed2681cd80910dfdb3f14a730808fdb18b08a210c0b30405dac10aa22d82d460e6c4061bfaf7645e5c8fa0e173aa6f50cbf3e24e0bc6f4ba27956031d68ab5d4476dab4b0fc9b80a674adfd2946ac97ba64c16f265e8f57e8969f24729d3101876e91864a94e30873aeaaf4f538f30bb84dde602b8632fd768066b
Qx = 1330127e2d19ca0a2cbecade2e086cc9153ddaae24aa6ef111ac0a99d82708e2b2b64d38c9dcc833bb091d39e2aff69cac60b2a2b49052044b2c7bf0934e9f22117
Qy = 17e82f5fa31eda69c1b78bf9ad80820fd470a77f773b0d4de4e35c8504540434f2d5048c92df639f0f3504c0b24a1a9be08f66aa1e9218a95c929915158c4563bc9
R = 1505b843460266affb130397279477cd1920393a4d78008b1f11bff53b46b0df05c783dae9a494aa12e48b6fd8671c2d58d8612ce019d94f08ed8ed40317d14dac7
S = 035998ebb40502baeb5ec86bcde0463d7cc9597d0ae17e64891680049efe39ba46d87425acf47f1622365975ea7968805e7d08aeb3c307792cdd130ce3bf42e1348

Msg = c3ebabf4ef29e3b0ce98bdf2d269efabfc2f131b594f4de6cdb12d6b0fbcd751f5826162f2377c9ddde85786f369c0c120908ddf7aa03a780e08d37ae04e14c02d46d0e7f3878f5db41f7bba76867d540ed1a30d9e28c5705d7a68fd966fe2c0f49fbf29fc016417dba6f23662a76d8b4f5c1e4dcece35f73f6fb39b3a098779
Qx = 12b13bcaec10e9b4bd4376ebc5413beb74265a839c8cd840e42393ff28cca74cab3dc2435fd209ab9d614a5af7156372f86fc784465d38c12a404225591f3f21996
Qy = 141fc895169d25f3373161d778b29ca13800e411c21552eb780fe32c8676b5bcbbc882d25c0aa598be47022422d71b18f0c24e2a198615e7edacb12d168f4391a5d
R = 07b89815afd850860f8d196354d73e50c761cb5b83231a294655e20c81ae38213c3337b450e053d7bf02aca6f3135e695704cc33718847129bab6b541ad113cf7eb
S = 131f6792c608300d42066c86ca8915af80e1675cda7e0f30649e4bd0833ef6f58b3c0a5b3195365f5835933f5a45492ca3a8f7032897e2d0ffcc8441d5c443dead1

Msg = ceb55a4a0e78c532221c8243587e88e97b1408e3602ea0a4bc7cd3f938d1b6edc826ebea38e2d3200859002a7992e9efe84de2c75b444be05ab207df001230ca51290825ce46335b47c92460f1e2d695b89bbcecd619fa4ca51ed537b1d368aa12c59944f92d4cab8874d72ce7f6b5a194077e98fa3316b146ca2548769dc854
Qx = 0c6ca7d88f5cd5ad1d1fcdc4d5c10877a9f5bd518b453234b402bcf9e162567a7c6b0477b72c27ebb79a4256eb3df6b89e7c6c500194b917ec0cfdb8db5232497fe
Qy = 1e706e7c6b9ee85a9f7f8e515a3a404e011f8cc1e920c59b0f657d795212f01975b1ec8065ab1c3cf6fd92d12eb2f546fa1b0b295b1d970f552fc3f8da0d21518d3
R = 14556d75ff541b915c585d80fedc1781672ce63df34777a4a3739c1e66d412fd7a28b5b029f6867d94ff0d13ea5a17127d93895d87381466110509f369d15f599ea
S = 0d9ced828d8dab125289893276beda8daa4660d2ff8a3f157b1b616d18e4a7983c7b8f879e423bfac3990905f84f1c9079e2399df5f4f1da947b22af8e0ed0a68f1

Msg = 74535464277968daf4c7973f3014eebd6d76bcb7930646ad33e3bc60177a6c371e0436e2474f403d0bc2eb77f09c05735b7c406ea526e2e37284bfab3eeb646939a09625559bfb379d8a1667c81979520e7135103afe961301760d5292176ea76e305b767ae2009052280257e115b1222f5d85e37917c80787c1909d3a85f0bb
Qx = 0c039c986f5d71bae5a157986fd80edd50ca15a0ac000d41530b8b0f3d9d8819f64eedea91cd8f00db1931e53e06cb5344b82c692f87311cfb1c47dc7318c06138f
Qy = 13b17f833fe30cece8ca54548b531f94fba92091569a32f6811e2315d8b0fc64a62f432ac0fd54277f81963f2b29c13110775da93eaa03a31cdd31dd0392047d43a
R = 1afb0936e04235bcdf27742067035a4ff673a0de8ffbb1259cd61ab07eef085b99d76e11b3cca1697bf93d73b3a72e8a00c61ae66eae96c951ad0b7fe8ef670b2e0
S = 101ce08ad3cecec9e5c426bf00bc99e2e471491b341e9ac6de20ca8f0d332c09c36b56ab05918f5dd0d168acab6c319cf1461fb61f8cc33fb961e0aa66884cece30

Msg = 07e3c89dc3d1d8205be44bfd63327782ff1bc2c8f7175c920eb22e7790d40d442b46349cff72f1e1f86a9e585c2cd387a025dae280f363a74f6f79078bebb586b4b8b2a45eecc0bd8661dd2e2201e1b9fb41f82bbfec553b522225b666a0c2d94b422afe08c5af7caad2f0c8dc3807e0b87a04e9dd899b16c2d219cd4a7b9c05
Qx = 11bda7768218b82ecb8b1bf1f082ac57d146a3671540d200ee4a29b5b01fd6fb7f46448aa46f640a6cc36319e6237a0fe26bf8a0c5bd6d14da10cee4b4d246612f8
Qy = 02bbd5c3d1392e4616d8df0856e18feb95ce5bd373b3a041f485200628c20e42abca59fd0dd936c643dc2dcd8b5d93314756eec63dd0137bf61bf2fcad1a06b4580
R = 1ce49a110b57ef36104953184ba303dc4f6f1bea3867357b57b073d2cfc6cebb6b3f5d07b3bcc21c4398108b4f0e2f4ab77afe601bddf3311558f99123329f273a9
S = 0dbe7bab49ee26f51c2e860907ec2194765005bad98b09b689fc58e227ff61e8eb29b79a5c54f5447e933a718025645d9156106dc7b3d4333f3a6e0670e73c77a15

Msg = d4a6509fbe6f9b0dcd7a1bed4502ea86a4a600823d77f69f235010a748667ba2e61fd34a1e28e9bfe0c2c27c0a5ce38ad467cbf36b66c27adc44d18934eb126be5122abb6c56131d318d1f2caf5739b3d4f56e1fab5fad343890cb00a425ca0d9a265bdbf024a18d8a8438e37b1a76686efaf1ed458a4135ffdc07f2df3b2ef0
Qx = 0f420228512ec74a145d472c413948c4a6dbffb42014d359a315e8c85a182d011d80803d907e07147a78a2e862dc81927c9953652fef7523ab0fb9e18c466b23c96
Qy = 0269f909ec33551b4ea92589e7294b272e3db1c3272efa9793ba9531df9d302bc3a4378b02e3d1b41207168620a0a84327c9f247beb6fecabca384108d1d1afcea7
R = 130edf4964a6bd5336ab52ce40f322c131a3c4563c99e5e72c4aff8d866f795518aa1edbc6cb78dd861dfa3730ac0c7e6f19468d7a6c643f8a2dd62009aec3cb4d3
S = 11599bff426cf80eed79f36a99d0d68f08ec791a2bab85d95d8bfc48e161ac806f00cb291470d4adfd1bf9ad404f38374ae34ce064fb44bed4deb4815f81661c31a

Msg = 65be2641eb2d1116e5103a69ca0d7f2a11d1a79a52a6a272a848126159f40bbcb9c317724b367e3e2a12c420a76e720d340a1ae681e295a7e4038fe1fe9e466a9c5b8e52dbef191a90f1f521e6e9565f49ab965583e37a4425f0b79e29f6b4bad52e92cbe3816a13de56d554cfa8b288195b23d1a2d0d9b333b792812224df15
Qx = 1a5e5b98ec176f75155ac9418f78097966df7c0976802770fbc47c9a160b45757dfc1ea1edac164d8f36406c7331ce0992a4128826b9d2a4ca6267c1692431cac43
Qy = 00d100c8ee893609edc9752b3df71477d70c47277ebbbd5600826291514928c33eb9b489452337d1e8ed1abc2b5f06d5d40f91320adcca8921ff532c0cab60e4ebe
R = 137ab307437bdd47e5cc04d514ca133a6b98c96a27c4bee6581b7597b8cd8dac69f06db84d9ab5835f46e8871a0c125a7f7b1594c532c3781debbf51ea2ce273223
S = 0b6213ceccd391d96e4f5468647798f7f4dbd65e8f540ae746f72ecf52b50d0629cd55f1d5901175e0dddf466ad1f6d64f2e129d3bfd1091800cff258a1b4fbc7bc

Msg = 30bbd8f6291a1f3861b9ad88c0ef5010185707600823096321b309924867b9675bb18d15186ec1763a769d45e471f88d43edfb6170ae27ce9345990e4ee58dfe597122d369d95d0857a76f9a7ed53b7865e213359e25c201ed60d0d7ad9801f870241f97e605f9d60075acf154c221b9c45dd7455a2ee3e4772b35552d291c3b
Qx = 162b910635a71ce4644a7062f4eb427c84b49e0891f0dd0ef56404f102dc4e7ff6fe9536f8406cde4171b0aed3ca820df75fd458568ecf7718606714ab31dd88987
Qy = 078a75f70b47ea42d4390f616676c1008530c1db4f8499c21af25a7599b10020f22e7bd940571eb82eb98bd412aee4907c0f112e3817b40d5373f0fc4152eb9dad5
R = 1239e1d43a3da16578c77ddcfc092ed53974a97d79b5ebf58ccf83626bbeb4a8dc4e6cfd7e91cac1972232480447a823b7bdc6f5e5b0ff3f803795cbc1091128e1b
S = 01f04202523b6c20ba2853912584391278af60e89226c01b71090ee952e76fe38835c8980d24087c9bd293b9dee79aeee6f80f0ffc369230ecf9d265527bbd7b846

Msg = 64f16a929199248d641af94abd9ff5f133387caa63da3bba88a85eef4d70033fdf79efac457c806bf4f0e3449e8a873c4c05d272ac1f1247ace4b6c1472ebe329f6d70144da44d38f5b443201ddd24a93f8257ec1c2cce6754d237583458776acf5554935019713d8eef80a4e19fb37f353773e25c0b5563a9fb4a7cbaf41630
Qx = 1c9290741f8d83e60505f90d1116bb5971b8227373c2ade7ac9f646e4b74fe5defad0b7a615cdac9d04153401fb5426ace56e28ac8f9d81f15da860c08221a6d5de
Qy = 1028db7ea9a29c7314248b93f214b7c65ff0554c0e8e50816d0032c9afe7de61f1f7e3ca7b34fe44aa99a92f0ccbca8ced9b7caba4db2b291cfaf3979667cf61474
R = 148832a670027a709822e3b9f251d57c1deced7464ef4bd8d168f80b360585453c85688aa096d4090cf3d7089e184db0cff97393266508d1ec8bea4c487978a8c46
S = 1ba3883ed441a49d39c66daf71a290540cf5086c0a4be76f3fbe3d1f0c9fabcabc34fb8180154bf79d681da2d0ad315df519d1305bf63bb1ace7db9ecdf369c03da

Msg = acc37a55f3f7019202bc1a4c055f70d124378cd4572a4a107c728e2ea7a133e9b9e3447034846f0b4db2b602438368bbf03a50229ec62c4d1b8b61611065ecaf8eea9c23eaf07edfcb50992381e9dc7098e755edad45a4098190ae624cee96e65460e937bd441854a31e2c776697b53945c603fc2faf998ad509dffa044517d4
Qx = 1b364fb8193df3eafb3928b0fb4cb85cb3bc70b412853ae1c8d725946c5b13f0a4f4395421628dddf162fc6fd6376161bf27b1571cd1861e524a866c23b075a0393
Qy = 14c444fc0069fa90f43643500f39bfdf00c174cdaa10bac565ca2de6798c106bf43411581bf24cdb8a15bfd8532a1a8fd3b465c201aeee90f12f58dfe7053984055
R = 186a82f8fcbf5cc8abd4e617771c37412c413bd51b9f2b97b76e56c99a41f1ca276c3fcd8bbeae0d7ccf182868102cecb8ad109d77ec777f50cc4a12152a3e30bfd
S = 1a297d1f748d8bd8e84b9402ba4c487a1d837428345ef7f8276394765c506ee6e2c521721454e2e71801d73d23e5a566dfb793490db3213ad509591074704fb27e4

Msg = 4bc883a672e4bf8855e3062561524313e9058966be5b4a1a00c2cbb1337ed41c95a7acdfaf7145712bb1b3e1e59ce2bf45d1893acb9a5a86eb6e7c811cbb3a952ce3fde0d684779296fd2297bc2ee977c07f247ad523ca0fc09ee26eeb843b7db5845df00da6bfb53fd13b8f0d90be010215446d037f047d2758e7c2c9623f9b
Qx = 1ab5a787de5f1c1c5b4228c0b4a3040e48ad340dbb7daa45ad1ab0a048ebec24fb4f3feeeb223b36adfe4e7d1c5aa42864174ea02ea815159eaa1568dae67e828b3
Qy = 02e1e35bf55fd6ed07a6d62cd30ebfc3f6d60d42311ba176b99c36871c033a2d9e5ac360c67f1e7aaa21a8a63f652daac013f189ef6166a6427601583ec9e191857
R = 1437a67474282fad0154bfca2995380ee44ef12492c8c78374b169d2a7d45e16b5b71f1843a287300be7b6cdda3b2fa8ba7d89d8477b73a0aa80b0cb0cf5b33b693
S = 1833bf3e458ed65fef40e59fab6fa16302daff0588be42a6be8a3a04301e4b4d9006b2ac568c99c7f52b3570c611ab9d8b6f83ca832f5158aa951d396a7c797ede3"""
    }

    object P_521_SigVer {
        val testVectors: String = """#  CAVS 11.0
#  "SigVer" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:17 2011



[P-521]

Msg = 1f2822a4c1af80ec00ee119147b13ee7f0fdc93964a40e028316885f467a96c5a2fe7fefb06bb41b0cfc56767b70f8d5d05ea6a510c92625d4f47dd3ef77b6235193de772bc3534c54933fb1ecb55ffe3f5209c2eb9e6dfd46af1b90fa8fc5f1f29046237adfe4e6a15cd22be2a9f9ca0481f9773f4c6af3a0f0677fafe94d96
Qx = 157bd20eaed760789981a1309a8e89cf914d8b8f4e0f76dfdd885b2902321158b005a6b0cdf2c1f5e1b8d0500f314d3e99c9b428ff0706bd9728331dda7dba9e08d
Qy = 05abb10e57f99f34a5befc7540e0b1594f3ec224e4bd9b6db7cfd85c1105f390f47b0c7d287ae8a4288fa4da83010994879f1ae3f2e610fdcaa3f92e465de09b9ac
R = 1a70528e0d53a9b8ecdcb2122a83ab00133d0db61e070cb9c1bb4a73f0c65b00c9d3e1f8e336987b067e6213c9ed3cc59d72bea8d90fea33f4a9460127651ba4cc5
S = 15de2211c1c275e9ef10e000b4a760d6c05414dc9e6b43116674c0d1810cad43bcdbe6756ab09d0050d0f6960cb1233e105dcec7d4ef57eb139fcba155a68131dcb
Result = F (4 - Q changed)

Msg = 7bafacdc01e2004f9a840f831aec632a80eb1449cb543edf0fbbb3c7c77cecdf6d00466a483b6bb386790df48a5bdb75871cadf84e1902556491ad6258a9cb1553b3f8edf1afc38d4f125fab66c79b289774138086fea8652c4f2fa72857665756502c2ca347f12520d86b7055398a8bd77aafa91d0a19578f7e9f5fe08ea5ed
Qx = 181e294c02eac7127b3327a0afcdb94cb3eed4cd10a4e6fbf820c2bd34bc2193fc4025c63603b857f353decbf4a46620b0bb8317a9e9e848034c054abafdb58787a
Qy = 0aff7c33871ed7197ee15874211b8b9bf5a508d03bb54004bb2314d5bcbd847fb6b437d726d8e67e05e8b358f7802dd3ba3021edafa28ef87646f792662b65fe935
R = 02385bcdcd76ddb8e0731cb57c8f57621f8390bc86e48b402a182c5d79e9633bce9cafec6b7e640df5d3887449ce7ad75d0465c3394255d1315635d1cdeca109681
S = 0f1f35563d5e63281eb9dd9beed24b80c8a48018e98cd263e3591549ad1039af2ba20363a6dc5fcbe2825fbc54b541b7308559173bf43104dcda457a143b8899deb
Result = F (1 - Message changed)

Msg = 215734d53a605ccc44b450645e04c806982ff6ad58160b51fdacfca7c34c8607e65070ad0bdf1025db061001f10a72cdf6fdd2450a90ebc566285d004034aef5efbd8e1f503e5fec68b7f4714bfd995c3c54542a153f6b9aea972661e9dbd61f84c71692d328510933ee851c3483469bb25b05c441cd92b9cf21e03184ea096d
Qx = 14b2634f04f8b0c834679aa2d552d71a0f4d58518801839661f92d319ae891c7fea5e36bdcef85ee8da430a10f9b68beb0e64c7c18139e2231595e3bdf6909efd90
Qy = 1f830742713248c8da0aa8dc56bfb1acc6a3aea3457c5dfc1166cb83f10689cf36395385fa8f3e0d7db17b7d6e8b1205e4393cd76c5911d74e1c315d864030b415b
R = 115fd4aebe5c27499ff4d63609d17f7fce015157089037e70fb78a038aa37e9d37fac6ba8dd9acb30f6c615d5bfb98a15ae28cd7ac366f39a54fb1dd669c75ea256
S = 10327708ecdfd7dfb660c13d11b5de0f37e5fc6c22f9829bab6cb9181a3aa47aca397bd6f2fcd611b64d0422b704cc0648bcb8a37f5f868abee4ead07ed3de78081
Result = P (0 )

Msg = 37153c35ed0e39bf0ac14377a104a8040adc24bf8795a86a2712591feb61dc862892d14b2cca9dcf24936d1e3e7678220acc841626d1ab3c1afa5fd38189ac446a5ebd9cfd10a23e623c50dade1de9993f5d3e9a7b2b93a2948ea20fbef9e20ec41f080ea808ce758a65f491ca2185d171385c35cdcce6035c762abcecda064e
Qx = 1535d2ed9d3e1ca0dd9c7f85704b3b641ab4f309fd96252bcf697766fdb1b5bbfb917b7e6bf6b8b28e06e1ad050604ebcf42a96164d1d034e2826d007202d650bb0
Qy = 130953fbfe9e049c64446ceb4627fdd74b668bb12800da718d884d3417c2f3eb71d8997bd6022d1a5d5b85e84e4970f4cfdfffce6de0377908be980b3bb4369a890
R = 16b5a41af3a5ddb936e3b06855d4afebb3642d7d20883be14759d470269bee0707b4e4ba2389e8969e166174d45d7be2eda2e4472b2898d01053027a8631cb35945
S = 1e4d7e101804ecb67e3aa75ae061232fa2c9dabf379d493d756dfd02703dc526a0d4a0164aadca966e459ba7458c698b6ca585f932d92d5f288d1f8162bf66821c5
Result = P (0 )

Msg = db6fd0445255748f0aa46467c7ddadf71a1b89de359bd5d4fe7bc59162c2d7ebeca1053e96f93f10df1c18f51dd8c0b115b2329dc96fae4c91d96a6d6dbf85f39a3e3c74633f60cf3a4107120759839bc076907d3aead1253f267e3de0c557d00ccddcaf6231bf14449a9a862d66defc44f10b45311427a72fec69c762dd35c8
Qx = 0b9e8f3f11ec5c1982af65777fc01f2682a78fe5300bdf3096b301ba8f409c9a10f6923e46de1e1e690c9fe18a8cdddd89a1683daaf13bc2f1f6c1b022b13372996
Qy = 1f23826245a7425a60643666300c1ac2b1034ffd12462a0ecd99be3f7721c2904ba3e1aff82ee0550ea4ed05a8b8f35d9dea97ed80703c0980aebedc494d29a1af3
R = 010322f7df3e2d42f437f75b14f98f3d9fded092a83f7b726635dfcd9ddd377a77c97feb8f9ef9022fdb41696b675c0b489f7d34313ed8b4877ab80f2e234141056
S = 18b74cae9e00c796f061eb098c6dcdc031a16300c865576550ef74b3e05839e03b26bb8621a75147af8662a297da5bcd7a2896efc8cf116cd9a82a451c5c4f48bcd
Result = F (3 - S changed)

Msg = dff8908ef49082ce321f3ab58d1871eaa8e300ae4780f5a9076ee6e94dcb85287cbc754d6b0f5c36161f3ab593f8f9d848bc456e5c469dc66b0caaba7e4ad0ec3a5d0450576cddeb544eedda69a85195be568775da0d80745e3ef635f33d004481a2e49ea2f02c745674fc2cf870a2a970796fccab1ec53a48ca726094ec3cb5
Qx = 0cf257421db2be0373fe589be0b96470b70c60ef636aee8abec930f08d158a1cd4c13dcbfa27aefa8d9620fe92eb5dde18164418dbcce103871bba47a3f86c919b4
Qy = 12dab3935dfc45c5b36f33e4d69d5ae2a85b41edc6026aa8eb352da961cd5574641593cb2eac50c25f63bb60508f0facbe3ce465ccf82a7b73074edf1da9fcf82a6
R = 0f6c5d26f7c808121c7b422a27183950ac92985b0becb522dfea10032847df589d52913a886c59d343ce332be93d44c387d7d87607825142d411f9fda0581e7cff9
S = 03b3d187a3ae410a51326f7e820908a9c4eb4bec284f9e4690f7aab6cd946d2fa6215d01f27a7f84def8fd540d08fbaa014789ce47d0cb67a57d87cb849caeac210
Result = F (4 - Q changed)

Msg = bd1529c5d3b7d25aada3e1f5ba1cda242729f546a5480de36c61b83d7ff9b82b32a89225eacd7c9c25807c8dbac8cf56610e88c875d2797df99d566bda3718ba7319a0d356259dba4a95af9d9032cdad6bc6e42cdfd3c0c7b8853027cee665c388519cf89f29fd5b24cac24ec5c09bd14c38519b68b3aa6ab0028e97e56e67ed
Qx = 15ff25057352c539bb796cb8ae74c4145ce2d1d8f61c32581de603be63becb18bd7729167e6d64dbe69fd28a833e20b6e9de7d19e2347af2cdda821a378c73a86b1
Qy = 1943742ca17f5e1e82c14a7487ec8296182312b378e0d25ce310f7df45f17fce463998d4166a2f290555a96be4deda1025c2b64cb0a1f789b72ebe1265b9eaa5ef5
R = 09111fa1786844e598d082647e7c2a56091ac78ef8384960c7bd6ce979d1c3acc2052e128861eb1976a36322244c9b082b9236876653414905c8d04546032541c94
S = 17263f5145324769da30c34e06494d469d6619a5bcd6fa2a25ba5c558880da1ccfd024be6d3290def486e2473f86b9b192c8275fb63e3320b20af72bbca5c615bc1
Result = F (1 - Message changed)

Msg = ab9d35d2bafbbe8ec4633469c3faf2dc972c8bc9f1fcc36a7198de880b311eedaf09871b29cee53e46261060388b6fdc3b59bb15792505672b51fb5e02dd6726c580f8433bf845824b9e0f30623dc68915244f73087a056d0ed2fec291ff8f02352a12b2b822e6431df2b84109b901cd6db6d04d65a3ca6260446f1b823f9f68
Qx = 17dedf47fc355e3569f19d3e4159c80fcc474879796d249e35ea762e44fe45b8d2846db8635f178575cadaefe98fc12d17902e274385ab0bacef17ca56cc7eab90a
Qy = 113fce6b2145c06637476b2559b49c0d1feb679cc99659ce3564d1dc61dc1444260f0442cce99f0dfa1e16af9dfef37a8b712144e718451e26c42849b73af5d8b6a
R = 0c5efab5d67694b189fe8e30c59ff3f6cd9e8f5946ff798437a6fbfc7ad97c8a7b2a8bb04ca155cdb08270aa9b12b14059ffb6900b7cfb6a9399d0868f4f5f8d99b
S = 0fef8c40e0d13ce7505c8d9f3d3b9248e62a7c53b88c9425bba9bbb3057108dcb84dc722a3091d8075eb0a8f1a9045cc805473d944434ea9c9ef2558217e49dd065
Result = F (2 - R changed)

Msg = 6fc61ac9ac6099a067af75f6e7e8d8db4bc56de2288f1fd5757c23a7a43c38aa12cf2d02a199a45aa3b817d6fb9518487eb9dda6a0e3fbbc4c57721e1378ea6189993c8aec1134c7078d965d56ab5ee26bc44cac9008a089aed123b06e3166f89dcd4e48d5efd04f5ad6fa7542513311d98cc3dc432996a663923c47fc8e35d9
Qx = 05c3b6d05f873a5d427bd25cf5ce9a0dab6c4059f639840226c60880c4c9be810a62b934d176a7ce51a766f12bf8b236b400f1b1bbcd6697169532e72e7abaa9815
Qy = 04795362a7553efbfc3c260f8672aeaca6e08ec69f566ef95daedc5f3e45785b05e084056ffbd465471bfeb9684c4662d1823b746306c9cf22d08c64ee78865a971
R = 0a598bc42f3b17d4c23b111196ad537d90f779b59610e6f55e696db129c50bed90bb2dbeee800ffad0311f0ae9747a12a7e5171a11991b0a6d4794ad1e57a5aa6e6
S = 1ea90538ab2b94f4d243e629942e226e94d0b4306f3b3ac04ced96a5dbc0c09cba566c790f73293828ed7d2a8a464f2b752e1366799cb8895cd626ea777e1ce8e14
Result = F (2 - R changed)

Msg = 4f2426cb4e1f92b63e7b3acc16e46a3434d0142ea1b32aee97724caa401ba3f2c1d30d4077107ad0800896b17e69a81d974ab906c161faf13c7760075f1a9169989203c5af1662274a3f3311b6bcb8cde4481302b2d9ad866c1ad973931cb57ddd4851827c991463a3085b13474762ed23b1684bfe820e54304301156389d9d4
Qx = 1e3e2c6e42a21f7faf02ab177e332592446a946c95e7e15dabdd294e4c7a41dde5debb100f5f245923162f75138ccb102c251a4d1d9dee3001c598b527177630a2a
Qy = 0814eed9338d9651ac66c321f71eea8344f8c1533d661175eb037c5036e8ab61503af4fb7fa8a649427f79cfb8462a74fe00a5f4262be10511258bdf0b7a822f8a1
R = 1d4b48005a7c7756613fb113e09c5ecac7b0cc6ee800eaedede97c49c433efc311c83609abac1e3cbe89d64bceb8e52e4d7e00b13729ef41996370222205e734a55
S = 06930b938b04fc561be8654e28cc57d90d27c8aff829fd7f8e259b19cc9d8a762c0c33ba4cac258b95e186cc80e0ca8792acdee8242fb1e6588643f50320b675d53
Result = F (1 - Message changed)

Msg = bd02c825ae24c3511a550e1327741fde0e270e38cdc4ebbe1c725569a18b48116745372f8d925be73b444a46da75cbc88526e91dba26462c5636b259c06afc0364d20851635a222f88ef92937fe371054193bb5ed347a5d030593026849afe4d9af164149845c230ea9d0f837c86b89e2cb6ab847feeaeb09225f65a54e2f5f7
Qx = 15c28d4e207607d19ff424aa9e8329c80b48cfc7b8b35b6bd0121003523891a3ce84f736a3634aebfa861c084cd7cacaf8107670b1b355459825c078559fb1f3acf
Qy = 08ceb6c0307b47142eb77ee143255c155caa56ef15478e7548e12ab4db2f0ffe04cd1408fd8bceab28b3dfce34c8ccd0d720b0debf6fe22aec590aa2b5720b38f62
R = 15c28d4e207607d19ff424aa9e8329c80b48cfc7b8b35b6bd0121003523891a3ce84f736a3634aebfa861c084cd7cacaf8107670b1b355459825c078559fb1f3acf
S = 015079462f1e30e81dfe96dd7b1a14896ccc9912ef24754c7ce6de2e262fc56e2705879f7168ae109378aeb4c916a7e4e9c6be197711ba36e12a2c38afbe1b1dffc
Result = P (0 )

Msg = 14095fea13e9691110e5dfb16678a03e92c0a5dbf32fe4b9ce8474a095f9f1cb69be22f9dda7d9c1fd221b268f6b816b9803de29225e229e3ddb7f4844facab261ca238f53a0d8056f0e874be51383764590799042fb54ebb5575db4e564a7cb0513aad2fe3c2e856f936b49f2d96e07a24fbe3ada7040473fdf580e009f51be
Qx = 10fb97e5a31130aa3208950d8108baf8e5a86cc0abe0cd3d4d078fc8f88c9613fe6793b8e7c21a9aaed87f9c8bf6c9f4c9715c8114858607f87616bfcdc02182b8d
Qy = 18347181d29bee18d8a22dbaaeebb1349ed082081656c272e250e126c5a808db76de7f6d96e394b73d9187ac5e810799eb887182f474e9692213dcc2e3ff92fc837
R = 018755459bba8841a4e36040a8e8ca5b679a777549c40f6149cc451c1bf79df3c9b15d4a350274412b28b10d83a7c8af15833ac8152927c1aca857c2f63a0ce2f84
S = 0db7d3e921f796d7284b31bf241e553c2e3daf61585d5ce6736db5cac79684ce09a747a070bbcae3a38571ecbc0e97422e31dd358c4ed8dba70b1ce3627d1d4582c
Result = F (3 - S changed)

Msg = 22553ac177aa7f647c094f685225633609ddedc805101e19a6b1ebda281e1167c7ad56f92788121df4bcef65c58ced5d470ba9c097eadeecef8e2c94856518b5b07b55cb4d48424461cdb33500dff44c6dc20b0682f93e21525dd5550e6d213f25e5bd9eaf9214c10c642340c8c581ebc2299c008ee645a117476f8489db33ed
Qx = 1060b8b39fc87df8482926dd67dbdb7970b75c52706a6904b344dd1e4d91026929e6a5bea13a3760c5d2116f2cac1ab544d18536f7646f78b2f0d8b130cf12f032a
Qy = 09a1c281c1159439722d235ee1b0d889fcb2b7a9d420c9dedc8c34b4c43cd62f63f84775361ee988d64a91a44715ee2b27991c504f29d3db1fda3fad8217c9fbbb8
R = 1fb26bd52683ad90caae40e7d539cec96e794eb0aa4aecf0610e337aff4800b6b57bcba6c6d76ff1ed5db607fb22cd77057fc32fdd09be562017ba19ed613e37245
S = 0d0bb58c54a7378c96509b830917cf03c0c3eeb0d9b53b22e40db33a8e6226e5cbb5de52fa7186a5316a7085d33369786fedc2a6397d6010a76d9eb5e90a5cb45b2
Result = F (4 - Q changed)

Msg = 97d947106a6ca8dc724b5c07607b2bc4e6eb0fbb6394f2b6c2c4b89d9fb780a4ac6827c354a60e679e6a1e675368093963e4d26685c78f3b1ef26ec164d2f6f0c3358f20805c45c09c51c94afb7bb876c56f20af0d0941c3b0bc90de802602b9a2e7860f35b4d35134a7659a9ed9c2e5d433027e42818eb641c828db3cda2c67
Qx = 1c631c1c855618546afe346c867039a7ea07a5b4a2a25f1e2982ef69ea35264e5c04431475c781ebf0e1983dac20554c172db3f2c460ad8e0a247f8f79c82ec8883
Qy = 06189060d952bf811582ea59b475ad985e7b64c561e8355b75996adc2349e03e6e16664b70388b4329e10fd37e7c47694777b5daf165722661867276642b4260b5c
R = 0d989a427fbaf2293bfcdb5b19830b61dd6cad86a3751177211d3c5b59888fda099bcd94376427cebaddf70b0b5430ad9e01c135252491f67509c406e9769e124cc
S = 1cd420a56acfcf66f0e3f2bfff61a0d838f61de3c86168fdcbc4818e1e795b068585dbf0500d3bd2834c339046f1920227af8d8d62c4bd8551799aebb0b306302a7
Result = F (3 - S changed)

Msg = 5c59f2a680c2791187177b457bcaf659e6cc62fa954992bc9c92dcae9b5b79313f262e0a0bff2cb5586a36fc9a9a01c9e6e3e039b172a1f1db3cb1b5ec3487379dc1402c9a6f87a586e5b89d3f1745ce64bc58ca1503ba46fa81f3b9876eb3267e127035b57a5189196f4b4ef3a98a116a3f12d54962a663034be8e1f572c3a6
Qx = 155ede3452fc6b0241a30d1c8484a5bdba89b5c9254715ccb0e8916d73c4b695e428d9c17ddc6bd030acaa0d18db31bb4882cc084c2020f08c42b773007c3a308f1
Qy = 04fb18ffcaed0c94578985e0440ae24fc4c260f60b3572dcf7043524d9359d862e32f26bc4880cf331576ccd13d7df03e77913483a779839cc63452f1dc61765f46
R = 094071286fe3b8b3b6c10cd23303b0a87e601b93ed221ae4cf59bf63b45bf350db9fbea13fdb1970adc5276a39445daae1f5429ccf6765749530179a9b9e5ac68f7
S = 03d56c8b8faaedce7eec40dad4e0b1421e2361495e580c1fcbc4e2a28f5e7acc95794418c983bb300408aedc9e7f7a6affb00076ead3a63c8e8bcf0df4370a459b5
Result = F (2 - R changed)"""
    }
}