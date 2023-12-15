package org.angproj.crypt.kp

import kotlin.random.Random

public class StdCLibRandom(salt: Long = 0): Random() {

    private var seed: Int = salt.toInt()

    public fun reseed(salt: Long) {
        seed += salt.toInt()
    }

    override fun nextBits(bitCount: Int): Int {
        var next: Int = seed

        next *= 1103515245
        next += 12345
        var result = (next / 65536) % 2048

        next *= 1103515245
        next += 12345
        result = result shl 10
        result = result xor (next / 65536) % 1024

        next *= 1103515245
        next += 12345
        result = result shl 10
        result = result xor (next / 65536) % 1024;

        seed = next

        return result shr (32 - bitCount)
    }
}