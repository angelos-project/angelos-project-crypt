package org.angproj.crypt.dsa

import org.angproj.crypt.sec.*
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BigIntTest {

    fun compareInOut(data: ByteArray) {
        assertContentEquals(BigInt(data).toByteArray(padded = true), data)
    }

    fun runKoblitzP(dp: SecPKoblitz) {
        //println(dp.name)
        compareInOut(dp.p)
        compareInOut(dp.a)
        compareInOut(dp.b)
        compareInOut(dp.G)
        compareInOut(dp.Gc)
        compareInOut(dp.n)
        compareInOut(dp.h)
    }

    fun runKoblitzT(dp: SecTKoblitz) {
        //println(dp.name)
        compareInOut(dp.a)
        compareInOut(dp.b)
        compareInOut(dp.G)
        compareInOut(dp.Gc)
        compareInOut(dp.n)
        compareInOut(dp.h)
    }

    fun runRandomP(dp: SecPRandom) {
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

    fun runRandomT(dp: SecTRandom) {
        //println(dp.name)
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
        runKoblitzP(Secp192Koblitz1)
        runRandomP(Secp192Random1)
        runKoblitzP(Secp224Koblitz1)
        runRandomP(Secp224Random1)
        runKoblitzP(Secp256Koblitz1)
        runRandomP(Secp256Random1)
        runRandomP(Secp384Random1)
        runRandomP(Secp521Random1)

        runKoblitzT(Sect163Koblitz1)
        runRandomT(Sect163Random1)
        runRandomT(Sect163Random2)
        runKoblitzT(Sect233Koblitz1)
        runRandomT(Sect233Random1)
        runKoblitzT(Sect293Koblitz1)
        runKoblitzT(Sect283Koblitz1)
        runRandomT(Sect283Random1)
        runKoblitzT(Sect409Koblitz1)
        runRandomT(Sect409Random1)
        runKoblitzT(Sect571Koblitz1)
        runRandomT(Sect571Random1)
    }
}