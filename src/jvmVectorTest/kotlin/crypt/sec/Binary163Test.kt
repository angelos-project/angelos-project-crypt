package crypt.sec

import org.angproj.aux.util.toByteArray
import org.angproj.crypt.dsa.EcdsaSign
import org.angproj.crypt.dsa.EcdsaVerify
import org.angproj.crypt.ecc.EccPrivateKey
import org.angproj.crypt.sec.*
import kotlin.random.Random
import kotlin.test.Test

class Binary163Test {

    @Test
    fun justTestIt() {
        val message = LongArray(20) { Random.nextLong() }.toByteArray()
        val privKey = EccPrivateKey(Secp256Koblitz1)

        val signer = EcdsaSign()
        signer.update(message)
        val signature = signer.final(privKey)

        val verifier = EcdsaVerify()
        verifier.update(message)
        println(verifier.final(privKey.publicKey(), signature))
    }

    object B_163_KeyPair {
        val testVectors: String = """#  CAVS 11.1
#  "Key Pair" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Thu May 05 09:19:12 2011


[B-163]

d = 023011611e2e970c86e6862858ece5cd2a2e7c80e2
Qx = 06646ffb3589f73dd6035aed914ab15bab1bf6ce4c
Qy = 03a303e046ba3d90c464b149de8144af7ab3055204

d = 0286d20e123f82db7c3eb92cc4295d7b0f1535a4d7
Qx = 06242f2ded4fe151cf554476a71c9cd854c5efbaac
Qy = 01fcddad199de186317a6cafca8536ab5f5daa29dd

d = 02f1265fcf33c4b5528af88c44acf9b0dffb1210a8
Qx = 03dddbe132791d4faa69981f87da71c0f969b5d0bb
Qy = 01743275dddffbf06dc44889e4d7e87f9f6f2d9229

d = f44bcff532034843f3c859ad9395249e1e6c7dcf
Qx = 03e018373f016817e13c691fabbd68a80ecc72b59a
Qy = 0230f1ebba2cdfb64057bc2717ffc48ff108d8e3c3

d = 32af480d6d29b9b53c648bde582bd830135b2438
Qx = 04d31df9be1a961dbaea600fc90645378637fb74fb
Qy = 075fd1ce1089c60536bc6ebee7826231343546a716

d = 01fcbf67b00d0ee854d49305ac6338f2f7bc709cdc
Qx = 01dcddb1f56c5ed56005c54da83fe2aef51c66f023
Qy = 04ef515ecae9874a485a9424bf084e97a3d810eeb6

d = 751046156be88d7d31ca6c3abd5e369411d6deab
Qx = 057217375b5339bae8a474dbb3f192d479f667a9ae
Qy = 023c8c0eb9cd27df7caa05c9af23baa35ebb548ac9

d = 3f47b6771ec7f7dba51f7e36ad0d458023a07d60
Qx = 0791954e41cbb00db92867beaee42e28a3c07b6a3b
Qy = 0669bdb2dec2709bdc6d83168a1877cfea50cbff29

d = 013f7f0fee96cf4afb178f578b7ae7eea455d3c33c
Qx = 0441d1a130a4531658b11ef53909667387478b0a6b
Qy = 019c57216e88c766f8a45c7ef2fd7fe9e36c6625d6

d = 9432bb55b1873b33656aa9f014f926afef52277b
Qx = 02f3e74b6dff6d1daf43bb2675ed9fadaf07448df8
Qy = 05efaf5464e0c1332b3ae72a389404ade6c286ddce"""
    }

    object B_163_PKV {
        val testVectors: String = """#  CAVS 11.0
#  "PKV" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:01 2011


[B-163]

Qx = 7bad47be106751fb88ea4ada7d9fbad33a9c93b09
Qy = 4c02421de07d1e6eba541db7a766c9827017eefa1
Result = F (1 - Point not on curve)

Qx = 1871c97908ba61c5bc27f522662002ba1feb75d5b
Qy = 0d4dff10e374dadb254cb2ced2bfe747cb7f4f952
Result = P (0 )

Qx = 4dc9a8a1493fb07650a6b2ff8b4c8064b96f96ad9
Qy = 6bfdf91c1c036397b7f7c185dfd84ae9f939f747c
Result = P (0 )

Qx = 05d47d14e477d8478a74f714dc01d0a4d91996a37
Qy = 07d4dbddea253549ceab438c9545374cee1897d33
Result = P (0 )

Qx = 1d41ea60a3a96d413bdbbf644fec8f6bf4fdd3e3b
Qy = 7ffe9fa64de8128b544c1039b974bdf7fc6cb6cc5
Result = P (0 )

Qx = 223b589a851faa4088205596c3e46e7117d065b03
Qy = 1ef94be38fbd33f451b9489901184f8d44294eda9
Result = F (2 - Added PT of order 2)

Qx = 120607cb7d530b47dad7a2d739c848286c22ee513
Qy = 7a3000f409dc9cef8f277694ba38d499031e24b2f
Result = F (2 - Added PT of order 2)

Qx = 5703f057b92cf89e17b10a64ce965294f31866587
Qy = 395257e9164bac6174a81a1cb1d079a45bd5bbcf7
Result = F (1 - Point not on curve)

Qx = 2aea93f6499de431a3675d788f14df962b2e5188d
Qy = 2943070cbff0f4bdf81488d82de574e5fd46c7216
Result = F (2 - Added PT of order 2)

Qx = 5b7f1ebc0670e1e636a533bae1fe658412a122cd1
Qy = 116e536b30aa0efffc0dc8140c1d3e81776127e5c
Result = F (1 - Point not on curve)

Qx = 471b1c1704c0a3f9dc23b861b1971b238d2885edb
Qy = 3d7354a08d1ff9f729c6cd5b3973ade6e3d688a77
Result = F (2 - Added PT of order 2)

Qx = 310463a875a33df81fea126103138fc5020fd431a
Qy = 2b17d855a80acb554bba075d7a95084d38e4c0537
Result = F (1 - Point not on curve)"""
    }

    object B_163_SigGen {
        val testVectors: String = """#  CAVS 11.2
#  "SigGen" information for "ecdsa_values"
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Aug 16 16:05:58 2011



[B-163]

Msg = c2d1afa40b3318016de5b64291fe1b45325e73157f870a5dc0af0e233d1395b25b8de1d80969e3525a75ff8573570f6fa823aadce22da6dd441cdb760e402a97c126bf4d8469923ac6ca34432583c8888ed94f99a6f12ae769e4d978111509df3ac3ce6c43da2da0f70fdde36d2b4e792ca7ebb3937a9d62792f9091103a3974
Qx = 1e16c18b800d4d55cea0e7722467311d898e4654e
Qy = 69ec2e95a75a59f01414ced5ceb72b8f347a96209
R = 26606f4abd3b58b9f68d64a55bf68a22cf73d026d
S = 020da664d9119813607afbb17f4c3cb555e1bc0d0

Msg = f01c31543a4ac199cabd42c767ac61ecbab0a0890d7844e952bf6eb0677b88b79857e21766649d84860bc9dd2d47ca008b1100e3951ba7e108c0fb51ab3ba05dd41842122de979b3b83aef5311c7c4a2f4ec3108fe5c6cf0b816cc67d1952ee5ab47976a1a411bcd6831fd519c94177afb297c1fd0a55dbf69a8291962f0f8f6
Qx = 2bbcf0aa90aead1768b54b4b4f66450d18fd3c396
Qy = 0e16e24f601d7dd5d53220f86383c5d934b576bfb
R = 2e84a2cef9d8e2469e105936d9f52239fdf7c680c
S = 1b78ca4e6e8e634081f9d1a1f50aadce2646a0c58

Msg = d0fabb6f60954cd136596d6abb1c6ae242f8c34be9a1300ec0f5e3252ed50d8b9034a3f1226d62b323da110a34a2068302d2ebb024b0c21d474c27858857d54621386c13485d72e954cf9744220a3d92b70b37e826aad1f4b42a11333b0f3d63cc2f9d3c980c825b1161887f4cfb17f7aaca305bb738667b52b92a950fa44ccb
Qx = 5783104a82a81f2e977a7de8d8cdded90238141e6
Qy = 54d41ba6ac3c4ec5e491c964a9de5eb042ab86cca
R = 1da8aa8d71f1e4f417076d35ea09d281f78bbd315
S = 14c6888c24eb317583276b82171340c83723cb01e

Msg = a4a372cfcb4e50d6149941cf02898314537367c1866ff7c59b0adb391a3b7ef156996c51f3caff62a63f3a253c1ef528de803a5693ddf01306c4b6898ad0123c5bd7cbe52664937dcc68e998642c3ef47f8a5752af5df2844a36acc86b5122543ac757326d04564e0f7c18ce809aa7a1bef9845be2d74640eec7859ec5a5f661
Qx = 1817cb0476e955dad768f77ba52233db1a58c035d
Qy = 618a85d441931110ddd2edc5b134ea10634e1795a
R = 086eb7a665976a5d58aa36cbb50fd2aa46a118b2d
S = 0f99a5dac0b35c59f87035f20bbba30baaa99b879

Msg = d1b416836f446bb19ecdd0c5dce3551d5efd6afbb083a1369d28b52f4e7acc07b6b7f90f2de305805a176f6d571cb1f51039c5bdec48e89eb00f9e9aa0cef615e7150f4138d8dbdbb6c5231511d5cbd3f22258199af8afc4c62e1f79d5b7a2dc2c746002da4c7528787ff4a553372c1dce612af801ba7f12a62d43b4365c59c4
Qx = 1aa217c6115e2600d97dec6645587648bf66b69f2
Qy = 6c2f30537ebbb442df69edf9fcde8a273c3e026cc
R = 0602f250e6171ac1ab85f13ad6b0c7519e7aac3e0
S = 1458fb5f288a6524dec620676d7d3c01c22fea6cb

Msg = 18abee60c809c6412d00b7334b0f8de0ee6317f1ee0686dfb2531706f3adf2fcc2705120cd28e476de0175b2a47fc892b66fc571d51ed81a71c899da6f53a8d92a3ddba9b8d49c206ea58c63a6366d44bff83ece6677a71d14d49874d8752e8957fbd6231174b4e7e6d2466fd89d449d12e4cb2f2a79bb93a35e5efcb67a4ab5
Qx = 1058a0ab461e8065334c54c80723190938cac8157
Qy = 07b511eadd250395d968a0a9594b2809d886a36c4
R = 2ae2d90d573a82e2c704f1a1d6c1bb336b308d00e
S = 17efb4e03e30aee08916aaae803f93b9fa89e8897

Msg = a9d626a21a5a4c54d8e4a99162c2ddcb7490a617dbb953d6873a7c94e2a9f1306076f03d57729ba9128da89c21e45ba3f2fbb10ea646df971deffa4453d3f88ef0ced7c21a381ca88eaf7f63ef17e5985de08ed24a87902589608412a8927fab806344f70ab6b79875d195ab756463f0ddc7a42b70844fcaf41580c789ef6ed0
Qx = 0bb205e46cf73d76a065ffddbdc2c557cd8ba63f4
Qy = 3339253cdc82ccf24580533895959bceb467adad0
R = 2a775fafeb816be660fcd7f1a94a945505255a8d9
S = 268bd5edbb6432746a2fde7a5fbcc37df6f04bfd4

Msg = 3283a85c8430f2ca981b00745ebe73479c1c50079ba8544b216288c99e4315d3aadb5822af3939d3e30bb9af7a06cbaf198865bf8d7cd6c3bd7f5ad8dd40ecdce52d5b30424ca0bccb666f34f66b0c9a4c1260051ac04ca06aab3df5313f016ccac1d90ea332cf8f3776498c3cb5df111d026a5eaa3a5deb0f2f59a90206fb8b
Qx = 6a3be2c25997e756de9e2286e7ac033acb1bd9078
Qy = 5edfa21576558bb32e6bb7c4e4aeddf57f217d489
R = 3a691e5be8cbe8b5ed3eb8e2ebb6eeef1a564aed2
S = 2d864aa8620163408e5ff81525e16f277ec4e4c40

Msg = c2dc36a2a3c22ffdfea9ce615462b7af936f70258dad7326a936813ed9853a61ccdbeb7364e2a70e6a90997f814f9811585bfc8f246bb0044e9389c56869d6b1666739b80b90b59aa15607f1edbc3a7262b8767095db19a1f1330825ac7148c4f947c8788014812b0bc3c1c182f0c459194d85d0c845d387fcdfa7678ce4b522
Qx = 274b43230b8b74c554bf4f40a9430b5c9df1624e8
Qy = 235c4f1fae772ccca08a4e7b700c1be5480fa8438
R = 174f2baf3f7b31adc85db396cf2cc8b3450b80c40
S = 3ec94cb19f4f2b0295a14273deae9d6a15acca80f

Msg = f4841cb246374d7b0ac64ac1c43a4de41d23ef232cd1926df5c700732f6d50024b050a90b1e08b154dd2aa1c4ae39b191d4714fdc691af08522390dea5e947de7647f7af9b4fb81b87383b21110bad4577a0dd2ba2c9a064abcd75889f1865857f302d368beb9f38c0e046f9a01b4911311591d648e32e2a07adf798a56271a0
Qx = 2fde9a63f79c8c85b959e8966d2a52521a639c2d0
Qy = 1c18487ede8815498c0fa1a756a950fef033d523b
R = 2715e195444800106b956cbcb8f258ad835214b22
S = 164a1d82d2ac3cd5b761e588dd4c5c6838badfd43

Msg = 56526f998e82992fd1f0f37926936550561fa32a51daaf8dceaca0ca3276a1e7b3a53cddbd07d90494dd81cb3c31e8ac966aab5fcb1dd64eed26a94adcf89c7c6731a09eb472a86be26d6853ec3f5c74d1010dc6f2999095bbd64f3585bd0cf1bb1d22443c6a0e4c01c72f7b0065ca90500c51c58df7e54a7ee519f3f56df92e
Qx = 4d0fa3ac735cf1cc156f04770ff3f71489ecb444f
Qy = 4543cb64cfae5ed2cd81e5470e589881e32af7616
R = 1a00aa7cec4f1bf5dc0865cf2f66b2a236f92f0f6
S = 3657cdd4f17fe592fa0173c57c8554ef6b1f63b90

Msg = 0c128f1c5f0438e8e205da4e08c733a619885a388fe447abc1ed96c1be7d88417ccb4b34323d7b53027de8c1dbcf9af69ea212fb7f64d3ddaf9b215c383209e32810a1bdf1f09185106cd2deed5ab665025c29e440b336c9ae54cdd6543ec319570a1a1f65c4836553f9210f051190dc8fdf265a9977785608402097db4abecc
Qx = 53c8766957fa8790ade8abbe0e743fe97075ee0d4
Qy = 154177a8fb182d8b1df5312c49021dc8d231cb787
R = 050b4701fbffa98ad363c2b2aafa3dbeb7182de3f
S = 31065d4b420e1ace09d5bd95702bf8b764ed30448

Msg = f176ec2d74b034a938f202cd68d062fc28e84de3c175e2aefe21f37cd61bc3104a6a0ec991814ad473132da0f3286f923071d1fb8dd85e47105cf177081cf7d1f3b176d59ec14f748ed1c07d9f5b2c58eb211ff6edc08d08e7885a6f808b1ff4b2f42c3ae39bc9f052d6c24bb59122b421a13ebe2955f55a4a4e374e8791c4e3
Qx = 0602f250e6171ac1ab85f13ad6b0c7519e7aac3e0
Qy = 082bb2ddaaf24cae0e6b06b8c6adfd5ccb452a993
R = 31e5880e9d73dbba80ec7687c562cf10f7a21f357
S = 0b9965d7db0f4929e5ee7f3bd8da8c3feb94e773c

Msg = 8a9e5509575cea1bea88ff5518acc6aa5484d8e68c5731a04e877c692f68924a6d4e3d6ed3ea411bddd69de71d6e0e28dde2d5ac0689baaa417851f95b547c80ee6431ee3eea7dab61b4c90ea3ee5dd0283a22c0d303acb9670d77d1ec5171d45509b9f15f07a7b2bc4a0c3271d3bb8a7db5488cbb6c0fb4096f785baa097a2e
Qx = 64e52df28fad775cc62d57539384b8a0cf2252c0b
Qy = 4b7d272ad99b41b8850682692d3e1bc350e47b8c5
R = 3d6f49ec350af2d0d5d4ebf3760b3d85dcd39efc0
S = 1e68491f642ab6ff6fc60f08dccb071515d097821

Msg = a07853ccb3b77025d9f3fa14a8f5dfdd2054afe3e9a4ff79dceb0bf6c5549f076728374ac1f96518902173a2156b493f180f92042de41b9636d17b3c2984f9381467aa585ed0dac84f79ad2ad22f06e87acea8c9c46c9cc39cd5d21122380b85ad1ff51e10a498dd1de2f2d568abde824e793b5b35a649aa14df569c37d69b94
Qx = 621119bcfbbcdfff88a826cc8bb54bba6b4a39b6e
Qy = 43ebc3ad38b323a368ae0bbb29fbcc484bf7671eb
R = 3d8534525b3cef40cc4a551fe77815083b08b6f1f
S = 215622fc7a0597f3eda19d3c6e67b76fb3622ba00"""
    }

    object B_163_SigVer {
        val testVectors: String = """#  CAVS 11.0
#  "SigVer" information 
#  Curves selected: P-192 P-224 P-256 P-384 P-521 K-163 K-233 K-283 K-409 K-571 B-163 B-233 B-283 B-409 B-571
#  Generated on Tue Mar 01 23:36:17 2011



[B-163]

Msg = f15936930f8d2b3ac5b2310ddef01ba16e9c3f051766d69a85d4a6243befde22923c415bfeed86ba9459ff5bc9aa7ae67dd5651c38d035afb3ff08579aef88229046130c74fa1acfe2c2c7fcfdcae34b6cabf62fa39ab0ae64df8770858aa216f361f6cdf71caf22b8d5d11802ba1f7745053c68e12e103fa05e6d988428f2a5
Qx = 0111ec474bf078eece607badb773aab29d6c35e2f
Qy = 1dd49385987e6c13f6117deb35c08e9e13d215921
R = 252e2ab231cdfe0903c03a153d6134a579e93b2ba
S = 1933774f14ef5e4f886f8bf53ce7433668f078f93
Result = P (0 )

Msg = ea22e125c3d4517ef9ad977adad30e9b2b03519a12e0455c348a5a211a730dffd96eb28ff20970e6771d8dc66fa3eb167cc9d390bb46f2baeceaa117d3796a9eddb9c76b07ca2cb5fcb648007d4989930623de2daf0bc0f4508673aee8a88933402ba922cbbe8ed4d19f528ecc6cedfb1337f9d9b5d8b8f2a887574e23aa74a7
Qx = 3e79c7309bbe4a7461344952d92a5a2ce23f86ad2
Qy = 2238da8fe0e043d3df18c42cfffb7ae8b66d068c4
R = 36c93e7ebb6c05b31946bfb204b83546857d1ed3f
S = 2b35f644b40507400cc66d06472fa188fcf911a2c
Result = F (2 - R changed)

Msg = b4266c77b317154dfc14d13ef8f7e315920c3a90e1223a1407dcbf9e59525ffdc0d4f03f5e0c4a69754a4e6b85182852b514d0c074ffd79960116000678f6d822ca05c90ac5db133212715fb273d9f391d8d536dcecbe80250c1cf392de67064c131b754e32fcf14c8542dd8238fd1309120c88135aef6b69f941233f37df20a
Qx = 284faf16ab5214bd79151e5983350ef39395e993f
Qy = 630a3aadf13c6cb1fe2b338b683d73d1918ab6111
R = 2168f969758f44b882db89bde7943ce8c520869fa
S = 14f11adea6d42fca0ce7ace3deadca56f6cbb8019
Result = F (1 - Message changed)

Msg = a0cf9f086fc2e12f7774ab6c2a5f20aad669209a7965bc56d6dbed45c80c853ed73165f00904f181fa81c1752fee99d2a8199c81b4e1bcf80af7751614e6bb4e8e513108507c9b44680f96816c1687dd0f4bd17df1984b2fbcd8f14e6cded9e7eeb86919ed34c8c2e9a4925889dfd01432882756d8934f451404c7b577fac74f
Qx = 7348b1b4de971be07d381ccf622776708e0c4ec6a
Qy = 78c5c0b2dc87755d576dbfb775b62e524d946c717
R = 13391b5ff546912a4b49241b3f26ff8503d8c9440
S = 069ecd0ba3da959356220dadb0a3b878fa1e3903b
Result = F (3 - S changed)

Msg = 8f55b2019350f0fbc78afce32860d492b71aac3a721285a6841901c3b916766034f0413264ae5d1b34dd34ab8232b4e8d371a64a458d3b0ac511e89d81be747ba076502431592665622db4d7f303dbaaea17f3a087f9625c7aea6ddd54894e463caf86e0e3977461ba42276f6219716063d53c44229c21250d6c378d19bd2949
Qx = 32f1759e81f5d9de0fbc5d4768f3aabfc7f001d2a
Qy = 1abba3fff787362033f10fde166ee6d013f6cbb31
R = 0c3471a45d764a7515b2881ce8fdce44a5e9ea4dd
S = 3352aafe845e751485c56a5a78d8d77e6edd9e92c
Result = F (4 - Q changed)

Msg = aa311379d3139b9b7e208dba8710d2e74c21c5e21ab646e0e644098b3e6e344b7f6f2ac447b9a1a02ddbfb8eb0a8d38a6e96d6609764d46ba00016a33ca7f3d7884e9e3685414214cc0a52eb7d9419a079909808d11891ab7e92dfc852d80a992e9618d776f3a581c27336dad61bcbb1d5d7b3e4b11aa427e6c40b026848a119
Qx = 6412bd977d30f7148a2643ea116422cecd4d4ccc5
Qy = 612feb95d0ff51ea244c9c520fd65bb6455ee00a6
R = 39a38cfdc0de46be1c241a1785c785d5de5199b9d
S = 2bac6766e7c50fd667375484e845bc73d67d645b6
Result = F (1 - Message changed)

Msg = f6ba40692cdadfd844a7745f1c1b69d401de0e537102848f25f0db674fa277ba2013b87b8df4da0853a119b7385319b29497860611fff7f6af1719950b9a0994e0e60158ceae374f5aef028537253f72c4bef317d5cf3249b6063f4c6de0f4a3401b6427776fc057637977ef9ff8a1bc963b9eae45dafa2f43a594b3feda0090
Qx = 7b29ed8410e38e65679147e981b6bd1ee83696c32
Qy = 0105a35781ee2a4b55f1e3f082ceb04c5d5de7fd9
R = 2b1f07a015fd786e9c9726431ce35f97511259678
S = 0ccfa10693311f7edc6cb941bb5b490bb8b74d7a1
Result = F (2 - R changed)

Msg = d489a1e5e23b32779efa5ff2634007c9ae2b2322de2da92b3805b0ad2eeed4e3e116dc8ce4519a3f430f13ffc48ce00f6c46d40745b4df285f3532ed1547a4e291f955b993a3919b6dd4f7bc538cda32d1abe7079daa088d8f8989f1ee087a13ed1a54957597df332531525298a9bcdbe643a4496d77ec01d1e9fce2419b1c1e
Qx = 4ab59240206e672a777f7f1cea3081a7bf411ff8e
Qy = 621a4c34e6963663e64585b725f7e1fc515ee031a
R = 0a6335e8a6b1e13c15018f0189cb40bea4c12f659
S = 37719ed138843293309072fa3738e2773fc9db24a
Result = P (0 )

Msg = 09dc22056b793f0985f89f9a051c81425e5e10d71ced4da374f449c1201b97ca6a5001b286c77aec243f54d54688b34f4f4242ac5de724cfd90c2e8d4de3535975055f21986ee97318c982e449ef33b27faa27a86bbea442a72a40128fab51625382a51b4a7773e6891e9290b559e9b3151e2793eb7ab44605d6e7188f7a792d
Qx = 16b7208ab7b4eca1ea98462f48d244599f410f00a
Qy = 6087285a519826341a77cad6262ded464c9660a1f
R = 33e250b0d1f42e2c6b6a7c86fefd59a9654968031
S = 0faf565bc1030a719e30e08f98e0bc30f703746bb
Result = F (2 - R changed)

Msg = b69e0feeaaa0af63905b7b474e283ad57b57ca087cc762c6ecb51b5842d2af1ead98808143dddc285ba97f6f37455915e794923469a6cc77158ba88739658bc600c1a240982823df93f0d6de8e58d57f11d59001b7a613cc8fae8cf0c18307cd75c37d13f0a70dee00da897a9b8b347dc3c1ca55aceec12f21c8cf7a2454ac9c
Qx = 361125043da38abd6d367e42163b34910d108e4fe
Qy = 5c43917eb2552f8f8ad85f51aa98beec55f57560f
R = 081bf1a3e9b5ab42c36905e8d65d7b9a3ad33a999
S = 3d9761ca180a7824b791de7070597b1dc9710c790
Result = F (4 - Q changed)

Msg = df98f533a73677d87ea0aa261592ec5dbee837d77f60e0e865a085081c7b4134bb702191e473dc530df1bedd190ee6292b31b16bbbc276f1ea32744e123c08b7a26b24ad29ddfcddb04602444d371596f5a1f5e19295fb6d554d07e84e70b9bf3a92e4cf9cfa3f3d0fa95ea2da98e16cc540ec812b5fd72551fa261df69ebb14
Qx = 52a56d8602e3d9623ef556dda1a75bbae5cf733c2
Qy = 636e1f2196ef604bd83a12ab1c670f3b5c989c30b
R = 0d532ecd20f52f0bc7c0dd75dc4e17ab90215daad
S = 096cdec553068cb1ad95ea7ff93a7713bf52fbbc6
Result = F (1 - Message changed)

Msg = ad13e02f13203adc1634e755674aca849ab7af8f8802d451f2f5a80a67f4d255678e755bc852b24759e46ffac65057ffbb9d22c14ca617fb9525cf719d113bde812ae92dced282eb2e1e98fd472d9e8ee0b8384c45ebb6d4ed73da6a5bd9e2ee420ec20bec47b2b2dc0ae8c7b389a71a503078983b7ab8c34408525f67f4ce6e
Qx = 353417ee05b28ddaab29616979fb61b0f3e74fe39
Qy = 26009383448c0781df3f220aef4ae6a127134de8c
R = 142ae7594b8249e03e72974ebaa2b49dd0b6c70fb
S = 298347a8897067694fc191da21ebc609e9fef0bb9
Result = P (0 )

Msg = 50bda50e64e44e02fef3c6ef7edffe8d0b41fe5b7aaae29171372256cc6617343f28bbc93d38b1bf19aa85fe6cc7341fb618ef2448c11c6761119ed19756564c78f6e97b72bbc4a8f06f5efec6a9bdf8cfe63c19d4db8bdeb00af7f1b294ec9f434eb88cc91691a5cb68da57552ca2fe9ed26ea1e8a138dca84cb71de7c7e114
Qx = 33d7762e1e4005201adbeae6aca2ca2146b70b9c4
Qy = 2ab515a3731075c5893af0286d13747cf28cb8b41
R = 3a2f147e35d8a7eb6dc3d791ede8d577047a2ffa9
S = 0bfcfbd6ce3477ba7f59fff041f013b43f928bf48
Result = F (3 - S changed)

Msg = 15d3296a0c635b6a88bc29b8b627458efc455b348a54476c32ae850e7992e8f0ee278f08babe16d5795809fa48689e2d1fdaa34f6253b5dcb2bbb38609f9721d06fdb2b2850451fc7317bffbe1d5b2da56731b1a6a0158ed90efabe034cfebbd065c79b095bc76a81f023300a7f7e95f491aabddcbe5986774525564219ebb98
Qx = 67af4c86e7c7b26ae7d89478cce3db5a7d408b9aa
Qy = 5fddceeb71f4aa89b2c4982ddff3833a3740787d1
R = 0c374db3d4d8deef2591fbf13e2df6eaa9df238ec
S = 2c110389d33b8243de6ce247ce235966e0bb1eded
Result = F (3 - S changed)

Msg = 9af2fe8dfad3984c5c19c7fbd84695cde7c84b3d2d7ce1aa9570b844a3ed8c2c06b5a4d76d894bb49cee840ebf31fa33fea936d6513c14b296247870e1f33dcb13d56ef2ae16b92de7e420c4fd55f21dcaacfa2498d8f6ef353d4ba34ab7f691e8dcc6a56482c801658203e53bbb66d27259ffefa7576eab9b4298a48640a0c8
Qx = 5d11a2d1fac1cc7099d4d8d6642bd03b3c6608fd5
Qy = 4e055d2ec05e4b7908c87d58b2c773399f660be94
R = 108c465676608372cb0c9ee5ce7ece6e484f8eb02
S = 0cf76bdd99e9c3cd8ffa320f481331aa708928f25
Result = F (4 - Q changed)"""
    }
}