package org.angproj.crypt.dsa

import org.angproj.crypt.sec.*
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntTest {

    fun compareInOut(data: ByteArray) {
        assertContentEquals(BigInt(data).toByteArray(padded = true), data)
    }

    fun runKoblitz(dp: SecKoblitz) {
        //println(dp.name)
        compareInOut(dp.p)
        compareInOut(dp.a)
        compareInOut(dp.b)
        compareInOut(dp.G)
        compareInOut(dp.Gc)
        compareInOut(dp.n)
        compareInOut(dp.h)
    }

    fun runRandom(dp: SecRandom) {
        //println(dp.name)
        compareInOut(dp.p)
        compareInOut(dp.a)
        compareInOut(dp.b)
        compareInOut(dp.S)
        compareInOut(dp.G)
        compareInOut(dp.Gc)
        compareInOut(dp.n)
        compareInOut(dp.h)
    }

    @Test
    fun impexp() {
        runKoblitz(Secp192Koblitz1)
        runRandom(Secp192Random1)
        runKoblitz(Secp224Koblitz1)
        runRandom(Secp224Random1)
        runKoblitz(Secp256Koblitz1)
        runRandom(Secp256Random1)
        runRandom(Secp384Random1)
        runRandom(Secp521Random1)
    }
}