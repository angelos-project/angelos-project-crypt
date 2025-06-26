package org.angproj.crypt.drbg

import org.angproj.crypt.ripemd.Ripemd128Hash
import org.angproj.crypt.ripemd.Ripemd160Hash
import org.angproj.crypt.ripemd.Ripemd256Hash
import org.angproj.crypt.ripemd.Ripemd320Hash
import org.angproj.crypt.sha.*
import java.io.File
import kotlin.test.Test

class HmacDrgbTest {
    @Test
    fun testHmacDrbgSha1() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha1Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha1.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgSha224() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha224Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha224.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgSha256() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha256Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha256.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgSha384() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha384Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha384.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgSha512() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha512Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha512.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgRipemd128() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Ripemd128Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-ripemd128.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgRipemd160() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Ripemd160Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-ripemd160.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgRipemd256() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Ripemd256Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-ripemd256.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgRipemd320() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Ripemd320Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-ripemd320.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgSha3224() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha3224Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha3_224.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmacDrbgSha3256() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha3256Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha3_256.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmagDrbgSha3384() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha3384Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha3_384.bin", 4) {
            instance.generate(8192, 128, true)}
    }

    @Test
    fun testHmagDrbgSha3512() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha3512Hash, 128, true, byteArrayOf()))
        val instance = HmacDrbgManager.receive(handle)
        generateGibaByte("hmacdrbg-sha3_512.bin", 4) {
            instance.generate(8192, 128, true)}
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
    fun testApa() {
        val handle = HmacDrbgManager.register(HmacDrbgEngine(
            Sha512Hash, 256,
            true, byteArrayOf()))
        println(HmacDrbgManager.receive(handle).identifier)
    }
}