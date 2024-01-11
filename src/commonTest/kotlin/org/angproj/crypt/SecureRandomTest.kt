package org.angproj.crypt

import kotlin.test.Test

class SecureRandomTest {
    @Test
    fun getSecureEntropy() {
        SecureRandom.getSecureEntropy(128).forEach { print("$it, ") }
    }
}