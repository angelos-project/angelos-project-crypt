/**
 * Copyright (c) 2023 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.dsa

import org.angproj.aux.util.readIntAt
import org.angproj.aux.util.swapEndian
import org.angproj.aux.util.writeIntAt
import org.angproj.crypt.number.bitLengthForInt
import org.angproj.crypt.number.getIdx

public enum class BigSigned(public val state: Int, public val signed: Int) {
    POSITIVE(1, 0),
    ZERO(0, 0),
    NEGATIVE(-1, -1);

    public fun negate(): BigSigned = when (this) {
        POSITIVE -> NEGATIVE
        NEGATIVE -> POSITIVE
        else -> this
    }

    public fun isPositive(): Boolean = this == POSITIVE

    public fun isZero(): Boolean = this == ZERO

    public fun isNegative(): Boolean = this == NEGATIVE

    public fun isNonZero(): Boolean = when(this) {
        ZERO -> false
        else -> true
    }

    public fun isNonNegative(): Boolean = when(this) {
        NEGATIVE -> false
        else -> true
    }
}

public enum class BigCompare(public val state: Int) {
    GREATER(1),
    EQUAL(0),
    LESSER(-1)
}

/**
 * pow mod log sqrt abs ceil floor lt gt le ge ne odd even
 * pow2 log2 sqrt2
 * addition subtraction multiplication division
 */

public class BigInt(
    public val mag: IntArray,
    public val sigNum: BigSigned,
    public val fullSize: Int = 0
) {

    public var bitCountPlusOne: Int = 0

    public val bitCount: Int by lazy { bitCount(mag, sigNum) }
    public val bitLength: Int by lazy { bitLength(mag, sigNum) }
    public val firstNonZero: Int by lazy { firstNonZero(mag) }

    public val size: Int
        get() = mag.size

    public val indices: IntRange
        get() = mag.indices

    public operator fun get(index: Int): Int = when {
        index < 0 -> 0
        index >= mag.size -> sigNum.signed
        sigNum.isNonNegative() -> mag[mag.size - index - 1]
        index <= firstNonZero -> -mag[mag.size - index - 1]
        else -> mag[mag.size - index - 1].inv()
    }

    public fun compareTo(other: BigInt): BigCompare = when {
        sigNum.state > other.sigNum.state -> BigCompare.GREATER
        sigNum.state < other.sigNum.state -> BigCompare.LESSER
        sigNum == BigSigned.POSITIVE -> compareMagnitude(other)
        sigNum == BigSigned.NEGATIVE -> other.compareMagnitude(this)
        else -> BigCompare.EQUAL
    }

    public fun compareMagnitude(other: BigInt): BigCompare = when {
        mag.size < other.mag.size -> BigCompare.LESSER
        mag.size > other.mag.size -> BigCompare.GREATER
        else -> {
            var cmp = BigCompare.EQUAL
            mag.indices.indexOfFirst {
                val a = mag[it]
                val b = other.mag[it]
                if(a != b) {
                    cmp = if((a.toLong() and 0xffffffffL) < (b.toLong() and 0xffffffffL)
                        ) BigCompare.LESSER else BigCompare.GREATER
                    //cmp = if(a.toUInt() < b.toUInt()) BigCompare.LESSER else BigCompare.GREATER
                    true
                } else false
            }
            cmp
        }
    }

    public fun toByteArray0(): ByteArray {
        val cache = ByteArray(mag.size * Int.SIZE_BYTES).also {
            it.fill(sigNum.signed.toByte()) }
        mag.indices.forEach { idx ->
            val complimented = when (sigNum) {
                BigSigned.NEGATIVE -> mag[idx].swapEndian().inv()
                else -> mag[idx].swapEndian()
            }
            cache.writeIntAt(idx * Int.SIZE_BYTES, complimented)
        }
        if (sigNum == BigSigned.NEGATIVE) cache[cache.lastIndex] = (
                cache.last() + 1).toByte() // Could be dangerous
        return when(fullSize) {
           0 -> when (cache.size) {
                0 -> byteArrayOf(0)
                else -> cache.copyOfRange(unusedIntBytes(mag[0]), cache.size)
            }
            else -> {
                val value = when {
                    cache.size > fullSize -> cache.copyOfRange(unusedIntBytes(mag[0]), cache.size)
                    else -> cache
                }
                when {
                    value.size == fullSize -> value
                    value.size < fullSize -> ByteArray(fullSize - value.size).also {
                        it.fill(sigNum.signed.toByte()) } + value
                    else -> error("Can't pad value larger than full size")
                }
            }
        }
    }

    public fun toByteArray(): ByteArray {
        val byteLen: Int = bitLength / 8 + 1
        val byteArray = ByteArray(byteLen)
        var i = byteLen - 1
        var bytesCopied = 4
        var nextInt = 0
        var intIndex = 0
        while (i >= 0) {
            if (bytesCopied == 4) {
                nextInt = getIdx(intIndex++)
                bytesCopied = 1
            } else {
                nextInt = nextInt ushr 8
                bytesCopied++
            }
            byteArray[i] = nextInt.toByte()
            i--
        }
        return byteArray
    }

    public companion object {

        public inline fun stripLeadingZeroInts(value: IntArray): IntArray = value.copyOfRange(
            value.indexOfFirst { it != 0 }, value.size)

        public fun fromLong(value: Long): BigInt = when {
            value == 0L -> BigInt(intArrayOf(), BigSigned.ZERO)
            //value < 0 -> BigInt(long2IntArray(-value), BigSignedInt.NEGATIVE)
            value < 0 -> BigInt(long2IntArray(value.inv()), BigSigned.NEGATIVE)
            else -> BigInt(long2IntArray(value), BigSigned.POSITIVE)
        }

        public fun long2IntArray(value: Long): IntArray = when(value) {
            in Int.MIN_VALUE..Int.MAX_VALUE -> intArrayOf(value.toInt())
            else -> intArrayOf((value ushr 32).toInt(), value.toInt())
        }

        private fun unusedIntBytes(value: Int): Int = when(value) {
            0 -> 4
            in Byte.MIN_VALUE..Byte.MAX_VALUE -> 3
            in Short.MIN_VALUE..Short.MAX_VALUE -> 2
            in -8388608..8388607 -> 1
            else-> 0
        }

        private fun usedIntBits(value: Int): Int {
            var temp = value
            var used = 0

            while(temp != 0) {
                temp = temp ushr 1
                used += 1
            }
            return used
        }

        public fun fromByteArray(value: ByteArray, withLeadingZeros: Boolean = false): BigInt {
            val keep = keepIndex(value)
            val mag = when {
                value[0].toInt() < 0 -> makePositive(value, keep)
                else -> stripLeadingZeroBytes(value, keep)
            }
            val signed = signed(value, mag)
            return BigInt(mag, signed, if(withLeadingZeros) value.size else 0)
        }

        private fun keepIndex(data: ByteArray): Int = when {
            data[0].toInt() < 0 -> data.indexOfFirst { it.toInt() != -1 }
            else -> when(val idx = data.indexOfFirst { it.toInt() != 0 }) {
                -1 -> data.size
                else -> idx
            }
        }

        private fun signed(data: ByteArray, magnitude: IntArray): BigSigned = when {
            data[0].toInt() < 0 -> BigSigned.NEGATIVE
            magnitude.isEmpty() -> BigSigned.ZERO
            else -> BigSigned.POSITIVE
        }

        private fun makePositive(value: ByteArray, keep: Int): IntArray {
            val k = value.slice(keep until value.size).indexOfFirst { it.toInt() != 0 }
            val extraByte = if (k == value.size) 1 else 0
            val mag = IntArray((value.size - keep + extraByte + 3).floorDiv(Int.SIZE_BYTES))
            val cache = ByteArray(mag.size * Int.SIZE_BYTES - (value.size - keep)).also {
                it.fill(-1) } + value.copyOfRange(keep, value.size)
            var addCompliment = true
            mag.indices.reversed().forEach { idx ->
                val num = cache.readIntAt(idx * Int.SIZE_BYTES)
                val uncomplimented = when {
                    num < 0 -> num.inv().swapEndian()
                    else -> num.swapEndian().inv()
                }
                mag[idx] = when (addCompliment) {
                    true -> ((uncomplimented.toLong() and 0xffffffff) + 1).toInt().also {
                        if(it != 0) addCompliment = false }
                    else -> uncomplimented
                }
            }
            return mag
        }

        private fun stripLeadingZeroBytes(value: ByteArray, keep: Int): IntArray {
            val mag = IntArray((value.size - keep + 3).floorDiv(Int.SIZE_BYTES))
            val cache = ByteArray(mag.size * Int.SIZE_BYTES - (
                    value.size - keep)) + value.copyOfRange(keep, value.size)
            mag.indices.reversed().forEach { mag[it] = cache.readIntAt(it * Int.SIZE_BYTES).swapEndian() }
            return mag
        }
    }
}

public fun BigInt.Companion.bitLength(mag: IntArray, sigNum: BigSigned): Int = if (mag.isNotEmpty()) {
    val magSize: Int = bitLengthForInt(mag[0]) + ((mag.size - 1) * 32)
    if (sigNum.isNegative()) {
        if (mag.first().countOneBits() == 1) {
            if ((1 until mag.lastIndex).indexOfFirst { mag[it] != 0 } == -1) magSize - 1 else magSize
        } else magSize
    } else magSize
} else 0

public fun BigInt.Companion.bitCount(mag: IntArray, sigNum: BigSigned): Int = mag.sumOf {
    it.countOneBits() } + if (sigNum.isNegative()) {
    var i: Int = mag.lastIndex
    while (mag[i] == 0) i--
    32 * (mag.lastIndex - i) + mag[i].countLeadingZeroBits() - 1
} else 0

public inline fun BigInt.Companion.firstNonZero(mag: IntArray): Int = (
        mag.indices.reversed().indexOfFirst { mag[it] != 0 }).let { if(it == -1) 0 else it }