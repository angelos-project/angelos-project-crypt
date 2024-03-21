package org.angproj.crypt

import org.angproj.aux.sec.SecureRandom
import kotlin.test.Test

class SecureRandomTest {
    @Test
    fun getSecureEntropy() {
        SecureRandom.read(512).forEach { print("$it, ") }
    }
}