package org.angproj.crypt

import org.angproj.aux.num.AbstractBigInt
import org.angproj.crypt.number.bitMask
import kotlin.test.*

class BigIntBitwiseTest {

    val exponentList = listOf(0, 1, 20, 30, 40, 128)

    @Test
    fun bitMaskTest() {
        exponentList.forEach { assertEquals(AbstractBigInt.bitMask(it).bitLength, it) }
        assertFailsWith<IllegalStateException> { AbstractBigInt.bitMask(-1) }
    }
}