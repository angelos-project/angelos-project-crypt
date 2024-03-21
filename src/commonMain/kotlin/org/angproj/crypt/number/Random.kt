/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.crypt.number

import org.angproj.aux.num.BigInt
import org.angproj.aux.sec.SecureRandom
import org.angproj.aux.util.bigIntOf

import org.angproj.crypt.drbg.HmacDrbgProxy
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

public fun BigInt.Companion.randomBetween(lower: BigInt, upper: BigInt): BigInt {
    /*var handle = HmacDrbgManager.lookup("HMAC_DRBG-SHA-512_256")
    val random = when {
        handle != 0 -> HmacDrbgManager.receive(handle)
        else -> {
            handle = HmacDrbgManager.register(HmacDrbgEngine(
                Sha512Hash, 256,
                true, byteArrayOf()))
            HmacDrbgManager.receive(handle)
        }
    }*/
    val new = IntArray(upper.mag.size) { SecureRandom.readInt() }
    new[0] = min(new[0].absoluteValue, max(upper.mag[0].absoluteValue-1, 1))
    return bigIntOf(new)

    //return bigIntOf(BigInt.randomBits(lower.toByteArray().size * 8 - 1, random)).abs().add(upper).toBigInt()
}

private fun BigInt.Companion.randomBits(numBits: Int, rnd: HmacDrbgProxy): ByteArray {
    if (numBits < 0) error("numBits must be non-negative")
    val numBytes = ((numBits + 7) / 8) // avoid overflow
    val randomBits = rnd.generate(numBits, 256, true)

    if (numBytes > 0) {
        val excessBits = 8 * numBytes - numBits
        randomBits[0] = (randomBits[0].toInt() and ((1 shl (8 - excessBits)) - 1)).toByte()
    }
    return randomBits
}