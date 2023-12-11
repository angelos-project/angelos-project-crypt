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
    fun testPaulssonPrintRandom() {
        val buffer = ByteArray(128)
        testVectors.forEachIndexed { _, _ ->
            PaulssonRandom.nextBytes(buffer)
            println(BinHex.encodeToHex(buffer))
            //assertEquals(BinHex.encodeToHex(algo.final()), testVectorsDigest[idx].lowercase())
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testSha512Random() {
        val count = LongArray(256)
        val input = ByteArray(8)

        val duration = measureTime {
            (0 until 16_000_000).forEach {
                val algo = Sha512Hash()
                input.fill(0)
                input.writeIntAt(0, it)
                algo.update(input)
                algo.final().forEach { count[it.toInt() + 128]++ }
            }
        }

        val sorted = count.sortedArray()
        val avarage = sorted.average()

        println("Sha512 Random")
        println("+/-${(sorted.last() - sorted.first()) / avarage / 2 * 100} %")
        println(duration)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testPaulsson512Random() {
        val count = LongArray(256)
        val input = ByteArray(8)

        val duration = measureTime {
            (0 until 16_000_000).forEach {
                val algo = Paulsson512Hash()
                input.fill(0)
                input.writeIntAt(0, it)
                algo.update(input)
                algo.final().forEach { count[it.toInt() + 128]++ }
            }
        }

        val sorted = count.sortedArray()
        val avarage = sorted.average()

        println("Paulsson512 Random")
        println("+/-${(sorted.last() - sorted.first()) / avarage / 2 * 100} %")
        println(duration)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testPaulssonRandom() {
        val monteCarlo = Benchmark()
        val buffer = ByteArray(128)
        val random = PaulssonRandom(Random.nextLong())

        repeat(1_250_000) {
            random.nextBytes(buffer).also { arr ->
                (0 until 128 step 16).forEach { idx ->
                    monteCarlo.scatterPoint(arr.readLongAt(idx), arr.readLongAt(idx + 8))
                }
            }
        }

        println("Paulsson Random")
        println(monteCarlo.n)
        println(monteCarlo.deviation())
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testKotlinRandom() {
        val monteCarlo = Benchmark()
        val buffer = ByteArray(128)

        repeat(1_250_000) {
            Random.nextBytes(buffer).also { arr ->
                (0 until 128 step 16).forEach { idx ->
                    monteCarlo.scatterPoint(arr.readLongAt(idx), arr.readLongAt(idx + 8))
                }
            }
        }

        println("Kotlin Random")
        println(monteCarlo.n)
        println(monteCarlo.deviation())
    }
}