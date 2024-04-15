package org.angproj.crypt.sec

import org.angproj.crypt.Hash
import org.angproj.crypt.ec.NistPrime
import org.angproj.crypt.sha.Sha1Hash
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class Prime224Test: AbstractNistPTest() {

    override val hash: Hash = Sha1Hash
    override val curve: Curves<PrimeDomainParameters> = NistPrime.P_224.curve

    /**
     * From NIST SP.800-186, 3.2.1.2, P-224, p.10
     * */
    @Test
    fun testOutputOfBigInteger () {
        val dp = Secp224Random1.domainParameters
        assertEquals(
            BigInteger(dp.a.toByteArray()).toString(),
            "26959946667150639794667015087019630673557916260026308143510066298878"
        )
        assertEquals(
            BigInteger(dp.b.toByteArray()).toString(),
            "18958286285566608000408668544493926415504680968679321075787234672564"
        )
        assertEquals(
            BigInteger(dp.n.toByteArray()).toString(),
            "26959946667150639794667015087019625940457807714424391721682722368061"
        )
        assertEquals(
            BigInteger(dp.p.toByteArray()).toString(),
            "26959946667150639794667015087019630673557916260026308143510066298881"
        )
        assertEquals(
            BigInteger(dp.G.x.toByteArray()).toString(),
            "19277929113566293071110308034699488026831934219452440156649784352033"
        )
        assertEquals(
            BigInteger(dp.G.y.toByteArray()).toString(),
            "19926808758034470970197974370888749184205991990603949537637343198772"
        )
    }

    @Test
    fun testKeyPair() {
        testKeyPair(P_224_KeyPair.testVectors)
    }

    @Test
    fun testPkv() {
        testPkv(P_224_PKV.testVectors)
    }

    @Test
    fun testSigGen() {
        testSigGen(P_224_SigGen.testVectors)
    }

    @Test
    fun testSigVer() {
        testSigVer(P_224_SigVer.testVectors)
    }

    object P_224_KeyPair {
        val testVectors: String = """#  CAVS 11.1
#  "Key Pair" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Thu May 05 09:19:12 2011


[P-224]

d = 981fb5f1fc871d7dde1e0164099be71b9fad63dd3301d15080935030
Qx = 95479944298f5139e253ec79b04dde871a7654d596b87a6df41c2c87
Qy = 915fd531dd24e578d908248a4999ec55f282b3c4b73368e424a91282

d = 483dd6576b910550da94ce1b02e92fbe0a25211616aebd2ccf80654b
Qx = 7481b29379a05e54d8a7e2b67497c5abe995c2123b6142434c41ebda
Qy = 363316ce0dede8a63890d6d704eff6d2bd11f56204460d8c750916f7

d = bf046469cfc90d60ff2d10a524cf1fd210aa1262423cc91f96f24491
Qx = 31648e0614437611bef4294f4adaf5042a3f1d38a77e4485c64c557a
Qy = 6c58f787c0f218ea53425b3f7ac9751cebfebdee35f4e6237b2e41ce

d = be499d329a0de5ed37663eac134ca34b18e15ab39535eb5af2d54ae5
Qx = 6cd134f1f425e74dee020e1be2cd84af91480ae182818cde9a57289b
Qy = 67eaf89259cb3a7211485f8cef376749a6fb36083639943cc27988df

d = 42e81b5620655dfaef745a96939ccde37ea5abafa3562187353d3941
Qx = 6da57c46a81675bcd6c3a89cea1961658122724e633a339e2e8c02af
Qy = 579265079c92392be4dfc3ce73bb41ba4bc878a91b6b179698210e76

d = f0d6dcc2e6edb2b71ba2fa8759c38b764533fe91911067b404e5e46e
Qx = 6d781e3fbd883d532e4c6797c4a808ab63a1bcad55008b064071d95c
Qy = f337f4824a96d84fdef1a7e0bb1eda41f9561a2f07032fa33d44c309

d = fb1c2993b7c3883e0446919b6f5cfdd02a353e3c754d7f94a8d6ef9b
Qx = ad5277e7390d6c50d0b5dd25a2c67b5f5429213b58496a28e8186721
Qy = 274d6d1e3896fc61d87965d9bdd9d5aeab571362bbfb5e6f223a106a

d = a204d0b91e82b0b474fbcf1b9207a9b793eba9fd8852f728fd45abb9
Qx = 829d59956a536e5dece589acc61a35998f37f099f459b31e9db38ab4
Qy = 9b7aa4d51f9ca09a4cd21529a992d06f2322280a777b32bcd624d9d0

d = 0709986187f108c4adba66688ff521c4b484edaf08738bcc7598fa6c
Qx = 8e66d57b457fbf2e5bd08e63d9b1528c99e0391a91f9d7a76bd0910a
Qy = 17fb3098d1e13d2b42d0581e6b7718dda58510ca1996225b2626071c

d = 6bdb055db1268843c5447bb50a3dcc65a715fd1f851f38921dfbfc71
Qx = a4c3902f5d2d545c5f1ef5380f9019c9d5561e0976e4ddaac5b2eb59
Qy = 9904eaa7656c6aadc88fc795e5a9a394bfc27a08b21b54761a92d12c"""
    }

    object P_224_PKV {
        val testVectors: String = """#  CAVS 11.0
#  "PKV" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:01 2011


[P-224]

Qx = 3913b7c347f0d56bdda1244a973378ae1a23b6c05f6ea276491e75d8
Qy = c5c9086cb4704540d566a9f2cc461488fb80b7dd7384cefea4616c15
Result = F (2 - Point not on curve)

Qx = 2b27eeb74e93b92f423e8d1bdb6869811746af14c2887a54338f3982
Qy = ca92f56341ce049bf0300a1cc5f57be96cdc1703512c28b1e07ab6c4
Result = F (2 - Point not on curve)

Qx = c32bc4bee87df6478f76cc74552c337fbc00026d74f22068e6a98e2a
Qy = 9c618bec3f89628a61638d69d61824d36070379fa0d2c6d7a63a62e7
Result = F (2 - Point not on curve)

Qx = c0d570b903d8f1743b3235af72c0772abd5209e96b7d6d43f305d3f8
Qy = 11fe6013787c8a8dcc19ca6be51aec3dddc5b92d9540a047af860e76c
Result = F (1 - Q_x or Q_y out of range)

Qx = 6619b5bbba54a6bb3438a25ff8737112aadd4e571375a79435aa1cdb
Qy = 8f0dea2f30ee960d4089dcc852afc974911c0b79c304552a4b302009
Result = P (0 )

Qx = 00c3f626b0c14c23f3160825254fc9fc6252e4311bd01a9b97d0d531
Qy = a29feb79a8de5131ffc2e1ab045fbaa70b22075642b8e333d9506024
Result = P (0 )

Qx = 7ed8ceb65fc7d06dc6f4976b33f2611ef0da9913900c1073cabd3836
Qy = e2594a63469d0b84fdd3e29cec8a08427e71c585d9653ab1322dfad1
Result = F (2 - Point not on curve)

Qx = 0e924ace2fd3066e9d6797b1c637890ed11e6df71928709421d8c805
Qy = 508bab846a1662f9441e2a9a762d8e66fdb513f6800e3a6047696bdb
Result = P (0 )

Qx = 56579986c148519adb29e8d2d374e7ceddafc85448612a297f0f0f46
Qy = 1d6ac5f9a38354875f1ed2973aa44d7b8ca5e5ad7249ee3bc648b20b3
Result = F (1 - Q_x or Q_y out of range)

Qx = 35b2cca7f60d0f7bd06497d512ca31b29c0dbcf1dd8b4bc994f3d632
Qy = 6d570820cffeab75468e740e61f82882ea3d857caa64f09ca4577138
Result = P (0 )

Qx = 1e2412382d3c1b0683bdd152a64a0e1ee06359146872a6fc26584b666
Qy = 125591c446520d0dcae5c9287c2ce4fb69a1f82827d41f9fe4f29744
Result = F (1 - Q_x or Q_y out of range)

Qx = 847ac5c23e0f100fcd9451ab948eaf78eb38aab98060d1539cb485d0
Qy = 170ca182475dd56eda14e3eaf3f2fbd17926d41175ea272e475e8732d
Result = F (1 - Q_x or Q_y out of range)"""
    }

    object P_224_SigGen {
        val testVectors: String = """#  CAVS 11.2
#  "SigGen" information for "ecdsa_values"
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Aug 16 16:05:58 2011



[P-224]

Msg = 66e98a165854cd07989b1ee0ec3f8dbe0ee3c2fb0051ef53a0be03457c4f21bce7dc50ef4df37486c3207dfee26bde4ed62340cbb2da784906b1b783b4d601bdff4ae1a7e5e85a85afa3208dc60f0990c823bedddb3db663426665152ed7b093d6bda506c93a694b83ac71553f31f5cc0d6ba2fa248090e8796573c4915d1586
Qx = 56fb6538f1723d2bef3c764134320b44ba615f663db804e54050b95a
Qy = 9514a442eb66dbf2b450746f66d54101877a50d4bc2910c61d005add
R = 9660bfffc173431d29f83fa2af0ba581791be3f43625316f395d27ff
S = 9e8c3b82bca2a4467c9694c666dff7f0e79d279bd64eb83bce2e3018

Msg = d39ad56135bec4c3c4362d59d3d9175acb386670c5db0a1757ce7646ad5d5352dc1b760f7429103854b42511c3c0404abc24642788d645de9369b84178d4699c5e75cce18756560226aeec9f71ab9ce1f86e8ba635582ede6484bd349594e5f2ffb1be1e97cdfce9e12b694b062293e7281ec134f2e72cde73266c6a2c25311a
Qx = 391de80590dfbc60bb41cc9f3d93706d1d0083f1ef31d3a031fdc604
Qy = 4dd1928e557636f0a5df082aa0b185d08f57d1d5bf7857fceeb8c4c1
R = fc9a2058b2a7b64c3c48f40a3643b18dbf418f579929bf52a5dea773
S = 05653b946b270aaf01351b21de753508610b1a76cf42366f1a6c1d62

Msg = 477101daa282a5a55b48c5313290c8da65b07cc4d41a5a1463300c60a05a2c63a6564ff641d0423c5233931c75be53f4e2da1b8b91ab4d48a2e59ca2fbe1cfd833f0c1e2afefada70a1ba870ba276f9df18c6397c221d20585eb78437c36460fb7e4628634066e504ba90d1749f2a33d5c6e5dceafa372b3d5eba8296b821972
Qx = 993473a7d4b333e34c17ebfaa86ed929c8645ccfd79759c084cdb174
Qy = a26ad50e57347965b42d5a159ff53a28957a5f3b57a844dcd0ee6d54
R = 259fda3143c584131ab8e49feaf93dad3b86575f2789962406bf8092
S = cea1cc15e28c094c3bc557b3f5a5172b62910cd7cfbb6193ae6d8dd2

Msg = 85bcd406f4cdc8f30e19553ba92f9c0894b2833d7bb0b5650a30ebe8550a698578fccbfc7af9010b78c4999cffbe3c3db9fd7cd04c9dcd847f5bd0049f5fd8ee215aa78688d178ac89b9430b6d433c94b8f99c2b080aa59f3fe2e0e0e4feaa6cc7ffee0be4fd34bc8a1ddbefbd66a2f4d8d06eb4779e479f93eea3e5ef8fe7e9
Qx = b8c33944ff61c5600904ca2ae0aefae6d21bf3c74aa21fafd824ca69
Qy = ed93a6edb3f3c3c4997ebfbc97664965449e5ed7eaae875fc328f0ee
R = 6b4bc7a907090db66e3a02258f043c40330403b22dea131bad0dbb86
S = ecea70cd6921e9342d49b88191c020d2bfaffaa67df59388db4a3ea5

Msg = c58cc5e0eba51a42e3a988c2432481db2e01537284effc5c56093f219db5f7ff2bccac32e1fc6473236bbbbf899d3659d798d5123046e7c8861a222d55f6f3c9914d1343dd855bdcfd9fd93c8e192371f413eb3595b2d46f45c284fd009ffbb1df5523994506a884598c09c1ba60f02a6ebabf621bfa6b5a79478d1a0776b6be
Qx = 3b55be3a8cc5c47085ab5d29e3b6c28e51e74b79d1a874b5d7bbbb20
Qy = aeba7d2ff67e2f6861e4de552116bdc1cfa4f187149d19abd1acd758
R = 3da522ea72e2532aa43949299101d3125cecc1bf45747ee1fd49ded8
S = f822638396a4c1d1d6a5eccf89bd26f4248bde681631a290de7db412

Msg = b52ada0e56c5d6f552fceacdd0af880ff4c2126554c3f069accde1b0d2ac8e07ec4165fce028a4a10c99b48c388cb922c8216716363e172cc07a1a435e3a4d92f719ed32d48eaddc55c906f5e3ddcfc725240d1277df0cc3981eaf03a2e00473407a1bb61b3d19319553880ebaeaf1877395ad61fdeef8b69a8147f887e5dc33
Qx = f3465da1238fa959b4b525b127b53abf0c9e295dc830874635c4ccd0
Qy = b42af0bb8feb22d4889eb3e4fe256d61beb50f02d70e875071fc87a7
R = d02011391fd353b772b3a3a72fc1e40f9be7d4a86a5428f1634d6a30
S = fc550f23ca67bb0874210fda9344d4853ba114f543697a94594c96c2

Msg = 24e3b449e3b648a4c37c048815d1638dda430cd48507b15202eb376bf68c17c349c90451e277ceb5b15686cbf344e380b1b1e4e48a53b781b31bdaec3c678099957936f35028c6ac28d55b5243a1e724b4ddc22344c3946c726374c4b8df517510db9159b730f93431e0cd468d4f3821eab0edb93abd0fba46ab4f1ef35d54fe
Qx = e5f58264c651f8981991cf8de0dbf90235c31c93a0a63ce1e484915b
Qy = f16234cb0166a73160b05c314cc7a0c9476e6fcf3bc1146ed12881de
R = 8f2856eba0024cc8afbf1705d0cab81071b3b2b62cac22a5861db0ad
S = 669365489e790763112af6288ea73c048102ef5f60f380b8fceab913

Msg = b2f141d96ad8a2b903a89ebc51f7473e94128612a09160bd3b2f77e2c7d9d4fe2105e356c845bf2d421cb039bb10f358f000b9804bb871f44fc34abe3780f459da79a7aed2ef6742ddba86ad83506a4efa23913d4630d181a4d4e58329e225c3a7f495cad0fe21609e2927fef2de36867b201632d92008ca020b4e1be527eea4
Qx = 04015992286133eef54e6e91ecc58c021c25806191bcf668f7f65127
Qy = a32a50f28e1a4f2f076498f50bf27c291103f993cb88ceb45ac550cb
R = 4a3b1d8f1d224b3491c7d3af160ace4da3b5da16e77d9f8bcd19592d
S = 4353c4d87053d78c821d29ba926bb00442169875b9d95618f9350152

Msg = bc64299165358e19e5ac855cdfabfbe6c7ae09b4ac506fe054113e808e56d4cbc4b10d7dd063b87d101f48bdbd11cc62781f2226266f67fafe29b1efe07e0156e9e9732644b4b89f43bfc62dae7dce8f5b3f6f38feb529f643b114d11a2fd2365293c2556a3176c7a41268745af5e8b4959559b4dc2a9d1c4a32ceeee4712fb1
Qx = cc602a858276f944bccca2acbbd8c9518b3cd9f062e4802e342780d7
Qy = 5650efeddbce1c00f506a3f88a8f57669405e344901cfb51ce54c15f
R = 7374f7df8e9e6b2bcb4e82258a0fa8b136b93aba18a53a79e8c8e897
S = a4a5da18cbb987e48a80a73bd69be378ce476fbbda6468c4a49b7219

Msg = 11d4344dcaa9f67c1679ac2fbf77056694c5f25eb29f0a6202d72ce4038166cccaef151047e04e85c9f47df5c4634c71d3efc7e8f917bdf92c8e690614eb518a10a26fac7a338f142ca1f8fd42264e8b2225b75fba603d6a096b74fee1615c2bebd58848eb0b80d045686570c128d35d8b4f0beb7ee2525ca15694f5f2494d58
Qx = 544bf08e35ffa3c960553c9723ee7097b7dfaae059adccf2da75299b
Qy = 999dae3a21782cc21ba3a50181dc7e67b8d7960f9d58d094a3b141ef
R = 5bf01553db59eb4530e72157d39f03dab9167ffd0df3abb74d822fdf
S = 0c104e52018dfbdd5386ddb9599b41abf2107708a3dc86b35f68dc1c

Msg = 003b648e92b1c17b00081c88b58be28516a32e2cc10231fa1222d73deceea76db6bfecbfa38912f83757527f07f492b7c0eb868b6ab21c4bee2317b6d62bf95855c62716872b4807f943ee856c607884d62c3723aae0cf8159b6e0d219a0e6b2e53a5eb8c5fd2b4f4fb637c16e375d95624d871b08f213455cc37cb83cb43623
Qx = b48b9585bdfa5ad902d3d91317cf1aae9a175d39364a18e5dd9e133d
Qy = b03426dc8660543e13bee6660b43c453fb1c7a13a847e98a5d9bb038
R = 707cf96de23f84dc987351599b63bc16cb6aa876fcebdefbdcc40eee
S = dd71fbb5bce18e0041c867d577271b67832c139901c7194c50aa94ed

Msg = 6453fe087d564e4b5a0e7ff5e705fb9602f4b033309b67478ef151080b7de3ebd504e46a1164c505cfc030873ddde38a1accbf3d6eca1c65796460a2e9dfd0f450d6a67dd4256389290666cbbc7abee931eb3d3c100fcdd1bb0f07b3a4e9026cbc1369c92f61d1de4e28494acca432bc2e9751b67d3322913e3a6fc2a464225b
Qx = dc20b984eb646343679e8299763416cb9af27fae39f297da3cdb934f
Qy = 0e63b6fd5e0a2f79acc5004c2120ecaa57f55b3993c7c27efaea581a
R = 1bf7ada14936a7411e29725d867e1f5c88ead2e4eea36e4a8e2e7b89
S = 9ee15ffedb03549eb2f5aab0ebfe335bb35ea325d0af71dedd82e90c

Msg = 33d876e7e56b0a8443f1dc284b41f30d5a13b94943c2abad9f5155ee65ad7e02bb31a0cd0b6f9bfd582ea933d3af9563a935287d20c3e54de228f6a0983965a8bb5e8fa1a750d6b5f220971dfb7c5c8ac62e52c307e5fd8f7c382ad580e82502de652dabe3942574b7d08c30ed2faf4958061d6220af5c3c1689dad6454126d9
Qx = 7516eeb4db631f0fd6fde701aa1893ad115591e37cdfcc52b714e778
Qy = 62e0164883b23cc4f4d5e06e766070e86fe377f12dbec46eafe5f4e2
R = d27ff094808787c76495af70419426149ec11059cdd39f2581b8d135
S = f450a12774d1a9c843e76ec1a24fe15064b21ad2bcda9dfb0cf6fd2c

Msg = 7210e879ebd15f29dd3f8e6ed639293ec7823b82c36f106e04e3520e2f7d7222d810492c495400c77fdb2d3a76463e4da7b81b5cce8b07a9f042c7f69dbf075473b7f936e3623fc938770b88d457850bbd886d1f5966011c761521738dbab18748eb3fbe4164e5d70dfb717e79d6c41d13db78f6ed68c66f9d44a6ac88d63763
Qx = 3e4409baa7817f25c694a4e65e7045e124ea0cef548269fdc9d73398
Qy = 9d91568f038a43ad6a0a91b42476369ccc67cdd88b16f71c09d9e00a
R = 676d6f6a3fa61bf0f3cf5f7447563df101361f9b09181334179ef6ce
S = 0686517815bdb6ece3bda257a821a6b7249931e5ccd575332f77d358

Msg = 228edfdefb8c9011d4e6130f81307f29577b731292279c4ff4f97de9af6d6f65854ee6f52e83f7f0fa2c1780b7ee269bd58aac34c8b562b2ae24f4a132e605a7b0f673ba9a11dd911ac56358f8cbada8bf962fb61b2cabcb8635f7f4235100faeaaf0f9406dc4bdc7bd880000c6f1e3a5cc6850253357f82c3540e4a4c596945
Qx = 252cfda1f1f2ba7a78eb7ab2adab3d12ee830cd0e827d60635a83614
Qy = ca7465c5fd9ad07fdf5460abcd94da23e2ee520cfbdef8eb66cc0bfc
R = a8176f8756a2fcc73ad799498cc3fa7cd34746650c7ffc15b2d0a4e0
S = bcdb2190028362be803d776dcb6dc2aafcd0bf9997e7425f3e05ddf6"""
    }

    object P_224_SigVer {
        val testVectors: String = """#  CAVS 11.0
#  "SigVer" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:17 2011



[P-224]

Msg = df76204710e9f57c952fddd2abe5146394930fa18cd40f98ffb7f773f9645a42de03d9fa5bd9acbd9cb47e5cc08a7e1b8a5db3b459618f9e5a61c88efc5f633fa47b126fa316bf2a6536694aea2c2f3f7a75aa0c95df80922aec3611097b46ec99a572accee65748567d45a413154e57cea49db12a940afdd621eb0307022cd1
Qx = 2b91097215ca2ac110594daae7d6516537db89095b293a8b43559e0c
Qy = a018e041a53350d369927a7be06e291915161d35db29eb924c0af08f
R = 509dceb682aaef0a7c1346862c3624d938a0033168137d60c731eefb
S = 84e5b395f00258b62ed3ee346733ef1b762d90f14718f83f4f9f4f3e
Result = F (4 - Q changed)

Msg = 789f833d91f8c9b5883bd0740da1f82aa6f006575c577655b3991398e06ec4353726a9a1e38f66bbd919bf0d0f38664fe80a102df33ace4bd6249b858539317bb8a5224d9b3123a3c762abf171ebbdf9e1420191268b1aa609be34813f06dc49e36715c923a4a8743960bba69d83a52d78df01085c14016e33b00613769b6da6
Qx = 0a5c65088321a28398c969a49478b91b8ee480e2628f6643711f1671
Qy = 3364c8e71f93f91b079662a3f3875993935e4af7dc1eeb1da0d7248f
R = 9e6dfedbd21c27ea3d727605624e1ff4d4ca7b304446719c98d40bcd
S = 64c5e69a3f001b56cd4768a253329954f5c66e2d9c2f5b86599da501
Result = F (1 - Message changed)

Msg = 2a3483bd4ae388be5eaa708e9642214935390588fd951627e7ba384c188ecca411da82f026f46ec1319b5f7f87226cb02bd1cf774288f75bc7a5d84a802886de311502df0f1a53087772d90ee009dfde0fe16f268ab46a184edbd86bc46e7ca2f2adab90ee9e5f1c99f888428940a25a47328c9dbb6298e5e423dfb4cda8d778
Qx = 4692471b53177d6aa18841b8c3258afd70f52cd585b5518557b6dea7
Qy = ec1220d0c1856acca9fa4026e44e3a4c1b9542a11793dad1fd44230a
R = e90b92446a1f5c0d95136ab5949178b4395db03c2134fdd168623583
S = b7114497e01b6ac52b032c545a6d709c7658510ff2b8577a160426cd
Result = F (3 - S changed)

Msg = 8783e336936e3b2dfa98e0120a67b21ab84f68ef91f425f9f48e4cdc7f929642c78f62da4c554ca1ac5d006016655175025b85d71f7cf3558dbdd11e4d6a4786f60dc707a991a18646a7d2d8e0691d856f3d5eccc5fe1ae7c4555a90cd6cbac37bc6bdc692696367323efe8551bb41d168bf9ad66bbdc237ff1d9238e8df1880
Qx = 3e6fed376475003d2809c7dc2de135ee294f8e82d9494f5064140f46
Qy = 30f2829773f9ccf9f7dcd7bc75aa9a17225467b75b41f84cdd654698
R = 1e6effab5c6120f187f5722e4dd91d0273140f6df777bd99dd123615
S = 221860f019640857437e60d6152e11b0c18dc0e2251fa73fedf3e659
Result = F (1 - Message changed)

Msg = 948765d3e25cec798bd564a588992d49d836eb90c75be2b77049a21e8184e99f3ecec51ec955f961a02ca22267f81a63e8ec1d0258f72a8ddb3b2c574bc25acc356c5c01252caf9024744aa27eee824dc96d2b21222300fdba14452792caaa9c824b73a58048d6ab1f97a44c56e42caf83a35d14af482aaf14bb33b6dc06a1b9
Qx = 85171dfb6fb8514a799482b2cb05064ac4a61b98562ea7b8aaaa93be
Qy = d588527afe3bb450f49f22c1926a45cb853c024eb83843e9a54dcd23
R = 9f667f7640ee27d7c1702b67266601d559f155407afbb34863550dd8
S = bd233f99fdf5b2286846b75613a02d50dcd42cde86b6ace311a67175
Result = F (2 - R changed)

Msg = 0199459986314d51282eede83e9bd9d3bab5265d0941539c3a2389a3632d22f94035694a1e6aa05df7e84846df899cdd9efe5f6e9f1fd7b209801afe2b4588bb1d379916a417d9f857323039003567114543e8cf8845c5cd0b81fca3586ffa9be0c75d65e2dffe0c8e342f296b464157f6aa4ae68c5a24d4894fb9f8b2527e81
Qx = 987b1c549f74958ce78668346ec25d45e1195b2e2c69a04f3f28ad3e
Qy = b2d7f497ce3adbb4f27d1962d6ca3e45b71846ffabc6d188011bfa2c
R = 0c790ed7ba02f3cb7c0eb026298f86f43ad322c91931aec9e0013ae5
S = 4bc94b0ee2dfe0e52b7a6b30d4f6c88423d9cd4168e2850ac9a04825
Result = P (0 )

Msg = 28e1a96c001deef2af897630b382befd9832d8790e5618f03e4411015e2cbfff839e0697b74c5bb34c12c29f7033f86a01e6f190a21185d50e546674ac495579640a956cb2f4a02ff8ab1a29195e03a5434d30740c88fcdac1f93ba8faa46fa5722e3f32169c242e5c5e6618d68c4773076f7d6f7543e6c5ff3b78e371c67749
Qx = 91f463be21bfe1870e5586f0fb01ecef7ae676d1e7c3a1170cbff2b9
Qy = 1e5ff8d893ddcc4e86724e8ba45553947ef5fd15d7ec340a16161814
R = 04d3b657d6611ef3d222a83718eaf6455ebd149aa0ab084e41009731
S = 12bb5341ff0d3aa6617953df465b7bfd04164df48f2e38a1a6cde9c7
Result = P (0 )

Msg = 5b6f4d548e5e242267445a05734efb2838b3f01e1039768437facfe2226837d3128f1a66ea9a84c33d99d912ac81c6986f74772331430ba92132452c46fb84e01215f5903b35073ec6161f0a65768e550261c0259cb1b5cb52d6b5c4bdd340de8389459b293894da294394b4453339a51aff31ecf88b5e34f16e897330f85513
Qx = 2a45b861d4872bf472d0d76e412f1bfe7b23f6d10ba87726fb2238ac
Qy = c02753c0064a1a9077f643519b7c0545b038eaac9c43e414531155fd
R = 815149a42b6e49d5c93c56c69ca63e052ef6c9d358fb24e6503493ee
S = 0e440b3a5532448ccb2bf122f969f63b0fc81a4d792410fa76f0cfca
Result = F (3 - S changed)

Msg = c968e41870be3e33e6462c1677290450c29bf6e11ab292aa2c788a21e23fba49883465e22e37c2dc4deab712ad08e43585f9e306e5a3cec9518202f6aefb233163ed617cb88af9ac8165fe1901cb599a09570fb160c1c20267f77b0afd6fa60da1bb5e50d65eeb89ccde89943c1a6b6794de004854b5753eb8fe7bc597424ac5
Qx = b15471b37296ee1fb12bf5166467fee53acc1a18738cedd309ddb824
Qy = ec71677c91ab19f0a484d7759143a4da2e2fea02f3ed0b44d543dc82
R = 94d5b81c5c55f8e6f50292a6609ed899acbd24613a8d1442fc454709
S = d6c13964b3a443bd8058c20374f97534950705f4ccf7477dda9bc298
Result = F (4 - Q changed)

Msg = f477d47ef132d6272d1f6b8dc240a3565be98e1fa16b4e50f6f0701566a67da5da20cbc68ff99992c7ba5b7bbffd538de0d361f339641cbd2dd521e0696fac7e1afd65685c757ce2f5dbf7a643634b2b73d62a26f4369d522ba5526c698337821ad7486e3ff0a49e1e4ce71637b8b4b77bb991c1b92bf8975928aac24c2b4739
Qx = b40f639f9d0fbb14618428610280ccc12e137a62b5bd718046d49c15
Qy = 36f2f43361d7ec6402f3156078dda7351702ec0b1280af52446b5958
R = f43cbc560f76e55ec392bfcd4f51cce61104e8f78c2ac550efba363c
S = 1a0fa2d99fce32150530b75e2c9e1355c7eab850502fef29799e216a
Result = F (1 - Message changed)

Msg = 4a21c31c4464ae80f01a9ad4ec7fa15ac008355107f76222d02c53da433e18dc88caa6d8e40b1ff725ab545841d989c03a9c788d446798ec2aed9e4cee364413b0f34a8fd00fbb2f578672f3937adc210284003a7248bc2c654c51c9ba0a22788a43b82a51970a904e53fb8c2c8a25e5e167d53ed7c1564949f07338f1df3774
Qx = 43a7c779c9d7dae06d20be94f1b01755af2afd847d60e77fa40ded9f
Qy = 70a409e0193478b8c5eacb9d018b825c5711d352528738b29af9a0e7
R = f32082c00ff6f6ef2e471630ca775fb3552f3e68fd31920bc810cd7e
S = 98b6d2613d7de6a6f9d2d705e2f8c9d029f74579be3190a12d21fca0
Result = F (4 - Q changed)

Msg = d987174cee78fdb6a2c611b81f066aa1c82021b61da25527df5a72427523b3e78fffe63c096670a112de90e78dcce8bcb78a396a0bb96ec263d9dc525f17c8df0a65290fbbd0b0653a0e06722457554a91770936f320d8a84284ff745623cf3c94e2692ccc5ec6e8627ffac3ac50b9329c0e1a809f7f9bc3c484650e21f2d2e8
Qx = a7667d569a4dadedc2c0c5ce9b3f6488cc03023ff1cc03f2b5d3927e
Qy = 268809130ca676ee0aa9fada9ff3d75bc2e904d4bcde77ccfe72d266
R = c3ddaf8ed0cc812bddefaf52d5b9c1234dc16e48db2dbe257bb6dc04
S = 06eadcf1871de1ad856aadf0b402128d63b1a63a3ef9432f5d435874
Result = F (3 - S changed)

Msg = 0353a0ad95df6d3190a251435f62c30ed6b9cc0dd024c3c316565cad83d2e17566b8be6828df432a2f25a6a80103474fad65387c67b8fd337244901343bca989e3133b45959242eab928bc0af001f55181590800fb93a39d1c850ae9f2175f13400c202b231ff1d9f5529c4f7283567c19404483d5dc3d6bddc2d218d90a8b7a
Qx = 5ed951625e64d211d26bff3d58bf448a10392b45ded0733ed9e4071f
Qy = 86ff22c0ae393d2838b046d5d70390b893771399b7a1c67e88a59b5e
R = e54978b5a50b77817379750b7da867f16fbfdf40b2a26f0ba3348c1f
S = b1df00aca3ef1febd9fa12bf0cda9a4e452e4c9a98f32409e7d518e8
Result = F (2 - R changed)

Msg = 295b45f1e0ae241190b02b166e8f6045b9610f074b6803476e95fc1cf04c5ea87a6727401d7d23cc2e64637154f4eb6edf0eab445bddb11a655c9c5fbe1954b19e38f8fe4bba18e9e900256f948a25caac05ab9570850732cf0fba697a5a2d513ed6a27491ee6a2e6b8a585657d97720c94df842b2e76e5652b43e804d985050
Qx = 2729b2b494598a4798fea9fed6de017fbfa83c421bf17579d9a66c91
Qy = 9df4613e6802c6bc0abc421117cef4046acacf0539b58401ae533e36
R = 28e9437575b39662d9dd75d308aa2632035294a10c6bb4477be3a56f
S = e132cd72724a194599c7605a2ab6cd6ecdcf9e43cac69671c874a16f
Result = F (2 - R changed)

Msg = d20675715223816479ef9ecb4aac14a064cf969a346117af6aa5e0d8a9fbe014830443afa40746c44d944901056725aa8b8ec1e00ecfe024a0443d17e948da831897959f1fb0fc8f207e622b223833010986849dc5e5ee08c94c9d8ebf10a4c47a0593ca2ce64cced9f612d10be8e3aec8fc0858c16cea63cc0627c6941ae5df
Qx = 85ea2d25a2cf0f8254f8b870144d073ce3c4ed0dbc2a5fe52b839416
Qy = cfbc8ae23e79743e1cd72791ed2d0b8c40a18065352dfde5ff35331f
R = 8a03fff024a476df60f65f5181d09a9302100c792d15d512bb5e655a
S = e5887fe85620dcdccb43c3f616b32768138d1b0836a0ae10969422d4
Result = P (0 )"""
    }
}