package crypt.kp

import org.angproj.aux.util.BinHex
import org.angproj.aux.util.readLongAt
import org.angproj.aux.util.writeIntAt
import org.angproj.crypt.kp.Paulsson512Hash
import org.angproj.crypt.kp.PaulssonRandom
import org.angproj.crypt.sha.Sha512Hash
import kotlin.random.Random
import kotlin.random.Random.Default
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Paulsson512HashTest {

    val testVectorsDigest = listOf(
        "cdf26213a150dc3ecb610f18f6b38b46",
        "86be7afa339d0fc7cfc785e72f578d33",
        "c14a12199c66e4ba84636b0f69144c77",
        "9e327b3d6e523062afc1132d7df9d1b8",
        "fd2aa607f71dc8f510714922b371834e",
        "a1aa0689d0fafa2ddc22e88b49133a06",
        "d1e959eb179c911faea4624c60c5c702",
        "3f45ef194732c2dbb2c4a2c769795fa3",
        "4a7f5723f954eba1216c9d8f6320431f"
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
        testVectors.forEachIndexed { _, msg ->
            val algo = Paulsson512Hash()
            algo.update(msg)
            println(BinHex.encodeToHex(algo.final()))
            //assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }

    @Test
    fun testPaulssonRandom() {
        val buffer = ByteArray(128)
        println("Paulsson Random")

        val seed = Random.nextLong()
        val random = PaulssonRandom()
        repeat(1000) {
            val monteCarlo = Benchmark()
            repeat(1_250_000) {
                random.nextBytes(buffer).also { arr ->
                    (0 until 128 step 16).forEach { idx ->
                        monteCarlo.scatterPoint(arr.readLongAt(idx), arr.readLongAt(idx + 8))
                    }
                }
            }
            println(monteCarlo.distribution())
        }
        random.nextState().forEach { idx -> println(idx) }
    }

    @Test
    fun testKotlinRandom() {
        val buffer = ByteArray(128)
        println("Paulsson Random")

        repeat(1000) {
            val monteCarlo = Benchmark()
            repeat(1_250_000) {
                Random.nextBytes(buffer).also { arr ->
                    (0 until 128 step 16).forEach { idx ->
                        monteCarlo.scatterPoint(arr.readLongAt(idx), arr.readLongAt(idx + 8))
                    }
                }
            }
            println(monteCarlo.distribution())
        }
    }
}