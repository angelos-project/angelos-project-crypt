package crypt.kp

import org.angproj.aux.util.*
import org.angproj.crypt.kp.PaulssonSponge
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class PaulssonHashTest {

    val nonceGenerator = Random.receive(Random.lookup("NonceRandom-Standard"))

    val testVectorsDigest = listOf(
        "d382ee733f5225c5d4acdf24bd68bc44a0c72c7500a0abc468d4f1f6324b508fc0178df218cd2306b2002fe4b37d448d54db5695408c34d77321e6519cc509a4a219497e4b0852a391153d70930d276a433bc955d65b6d00bd5856c156ae6eddc8d4778eed3fa3408521a0f8c8d0810e33edf2f51d1ae94513e4f73ba3b0c32f",
        "bdffdaac44d842c9a4c597cba4e9db310c821ed198dba1df18004eed164c86edfabaab3874267f4df66563295b99eb264a15825dbe473a4cb37f56f4a9cf4003214ce4ace60ebffd4798098ff1f500f52e8de8397e9ecea7378dfc5f139ecb284c9504e0c078d85715843fbb3d7cfd30786eb7d5b3c051bcdf4d8b57390041fa",
        "1d6d8134a359b96bb90db02db83bb899810a1dad8a3e7f5929590161d7dd7857aa90a32875520959060418d48531e9efeb629df5da165b144869095ead20c653df808ef84130762203e1b117f7335c528231a9715f9725fe98b09ff6084e7b3b766a781cbab57ca2f57aeb6f78bf30d399ab9aa608595fed82247bc847d2eb5f",
        "36580fe18244f0658e3c4ba74774782a5c06de027378d0b681dfc85aae9764623263984c6c96d37ef270ae9c36bf2194e230ac746470cec2d66d8014716e67a4923dcee86d203895a3aa7e15c97da485856c61ef96d6745eac637f130453dda3f7912da9833a4c140c67141c0b51fcea24d8766bb7d694127cf0064ec6c848fc",
        "f22a4c6398b55ea7d0065d0346c9004a734f8475c0533aafd15400b7ac2901f430054ed027be1d9d2b7b951a229d51ed7b03bf09ff523a7a661b0edabb18b2905e62c28aabe7df198ef6baf0e20b2b34b49eaccafe42af8e21096c67d9fa6ae99469dc58be098dd8ffd4df42da398461ed1f9b25bedc0f2c53b3863ef6e033b0",
        "3781081099218afbc1851c02b9c9f5114305afb2b87f48a7e6520a5bb6e4719afcb2864a141f7135a6ad5b45b5b240d5672f3a744c6a2f2898bf30979b7955412c31e1a511ba6507bb188a0e20d2ba9cc82a34a308806e12a834b65231ba4a1dafcfc5d6fccca5fac70f39e90a90f89dff5ea6ff1e9d8bc8f84ad0a165ca1172",
        "ab3761e5428265cebcaf2e4e30c862690dc012999a577d07a95c9a522a2942fb13ef8b31d31cf956646d4110b489acc12c648437ca6760831eca3218bde91faa4b3f4be9b7c30400baa68ab201827935ff9effe53612037571e996cd83c7fe8c7c4966d81f5e4c4f5e4660c0a39fe839b7b56d6a5383575dee2f9781293757f7",
        "f6966928cfe7de655e47fd1664e140ee9e3f21cb1be721faed8d28eb64654d8403c4669487f80d5c121cd75fb95a8e2be437282dfcaab56d46e3c0eb6a5bc71b2cdd30b6f6874dac5296d9658d9101d024166d4ee66038ea8415922e2fdd47c1c5614c25dd3efc320ed35b69d7aa0748d16bc020073bef1a57fef233181ec121",
        "91b220b6b30a29459f96ea81367a99bc4fa0870455aa2786194cdbe5fb9553ddbab3c722daa4352f4879ffadd54ca849ed804d671868f55b9bbecbc84a144f5b773e95894afbf01d7dcc8cdfdc30eb40011ee814659d9fc0254b284e5c905f29029c6c41cea8c52e3858c718f2810ebf5694f1a0e1d6a21f80b1e8818dbe516a"
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

    /*@Test
    fun testPaulssonHash() {
        testVectors.forEachIndexed { idx, msg ->
            val algo = PaulssonHash()
            algo.update(msg)
            assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }

    @Test
    fun testPaulssonRandom() {
        val data = LongArray(16)
        repeat(1000) {
            val sponge = PaulssonSponge(nonceGenerator.getLongArray(16), preScramble = true)
            val monteCarlo = Benchmark()
            repeat(1_125_000) {
                sponge.squeeze(data)
                val bytes = data.toByteArray()
                (bytes.indices step ULong.SIZE_BYTES * 2).forEach {
                    monteCarlo.scatterPoint(bytes.readLongAt(it), bytes.readLongAt(it + ULong.SIZE_BYTES))
                }
            }
            println(monteCarlo.distribution())
        }
    }

    @Test
    fun testPaulssonRandomIntrospection() {
        repeat(1000) {
            Nonce.reseedWithTimestamp()
            val monteCarlo = Benchmark()
            repeat(5_000_000) {
                val nonce = Nonce.getFastNonce()
                monteCarlo.scatterPoint(nonce[0], nonce[1])
                monteCarlo.scatterPoint(nonce[2], nonce[3])
            }
            println(monteCarlo.distribution())
            //println(BinHex.encodeToHex(nonce.toByteArray()))
        }
    }

    @Test
    fun testPaulssonGenerateGigaByte() {
        val sponge = PaulssonSponge(nonceGenerator.getLongArray(16), preScramble = true)
        val data = LongArray(16)
        generateGibaByte("sponge.bin", 4) {
            sponge.squeeze(data)
            data.toByteArray()
        }
    }

    @Test
    fun testNonceGenerateGigaByte() {
        generateGibaByte("nonce.bin", 4) {
            nonceGenerator.getByteArray(4096)
        }
    }

    fun generateGibaByte(name: String, gigs: Long, block: () -> ByteArray) {
        val targetFile = File(name)
        val output = targetFile.outputStream()
        val times = gigs * 1024L * 1024L * 1024L / block().size.toLong() + 1
        repeat(times.toInt()) {
            output.write(block())
        }
        output.close()
    }

    @Test
    fun testPaulssonRandomIntrospection() {
        repeat(1000) {
            val monteCarlo = Benchmark()
            repeat(5_000_000) {
                val nonce = nonceGenerator.getLongArray(4)
                monteCarlo.scatterPoint(nonce[0], nonce[1])
                monteCarlo.scatterPoint(nonce[2], nonce[3])
            }
            println(monteCarlo.distribution())
            //println(BinHex.encodeToHex(nonce.toByteArray()))
        }
    }*/
}