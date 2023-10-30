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
import org.angproj.crypt.number.revGet

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
    LESSER(-1);

    public fun isGreater(): Boolean = this == GREATER

    public fun isEqual(): Boolean = this == EQUAL

    public fun isLesser(): Boolean = this == LESSER
}

/**
 * pow mod log sqrt abs ceil floor lt gt le ge ne odd even
 * pow2 log2 sqrt2
 * addition subtraction multiplication division
 */

public class BigInt(
    public val mag: IntArray,
    public val sigNum: BigSigned,
) {
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

    public fun toByteArray(): ByteArray {
        var byteLen = bitLength / 8 + 1
        val byteArray = ByteArray(byteLen)

        var bytesCopied=4
        var nextInt = 0
        var intIndex = 0
        for(i in byteLen-1 downTo 0) {
            if (bytesCopied == 4) {
                nextInt = getIdx(intIndex++)
                bytesCopied = 1
            } else {
                nextInt = nextInt ushr 8
                bytesCopied++
            }
            byteArray[i] = nextInt.toByte()
        }

        // Compatibility adjustment compared to JAVA implementation, is it really necessary?
        return when(byteArray[0].toInt() == 127 && sigNum.isNegative()) {
            true -> byteArrayOf(-1) + byteArray
            else -> byteArray
        }
        //return byteArray
    }

    public fun toByteArrayPaddedWithLeadingZeros(totSize: Int): ByteArray {
        val byteArray = toByteArray()
        return ByteArray(totSize - byteArray.size).also { it.fill(sigNum.signed.toByte()) } + byteArray
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

        private fun keep(value: ByteArray, sigNum: BigSigned): Int {
            val keep = value.indexOfFirst { it.toInt() != sigNum.signed }
            return when (keep) {
                -1 -> value.size
                else -> keep
            }
        }

        public fun fromByteArray(value: ByteArray): BigInt {
            check(value.isNotEmpty()) { "Zero length" }
            val negative = value.first().toInt() < 0

            val sigNum = when(negative) {
                true -> BigSigned.NEGATIVE
                else -> BigSigned.POSITIVE
            }
            val mag = when(negative) {
                true -> makePositive(value)
                else -> stripLeadingZeros(value)
            }

            return BigInt(mag, sigNumZeroAdjust(mag, sigNum))
        }

        private fun makePositive(value: ByteArray): IntArray {
            val keep = keep(value, BigSigned.NEGATIVE)
            val extra = (keep downTo 0).indexOfFirst {
                value[it].toInt() != 0 }.let { if(it == value.size) 1 else 0 }
            val result = IntArray((value.size - keep + extra + 3).floorDiv(Int.SIZE_BYTES))
            val cache = ByteArray(result.size * Int.SIZE_BYTES - (value.size - keep)).also {
                it.fill(BigSigned.NEGATIVE.signed.toByte()) } + value.copyOfRange(keep, value.size)

            (result.lastIndex downTo 0).forEach {
                val num = cache.readIntAt(it * Int.SIZE_BYTES)
                result[it] = when {
                    num < 0 -> num.inv().swapEndian()
                    else -> num.swapEndian().inv()
                }
            }

            (result.lastIndex downTo 0).indexOfFirst {
                result[it] = ((result[it].toLong() and 0xffffffffL) + 1).toInt()
                result[it] != 0
            }
            return result
        }

        private fun stripLeadingZeros(value: ByteArray): IntArray {
            val keep = keep(value, BigSigned.POSITIVE)
            val result = IntArray((value.size - keep + 3).floorDiv(Int.SIZE_BYTES))
            val cache = ByteArray(result.size * Int.SIZE_BYTES - (
                    value.size - keep)) + value.copyOfRange(keep, value.size)

            (result.lastIndex downTo 0).forEach {
                result[it] = cache.readIntAt(it * Int.SIZE_BYTES).swapEndian() }
            return result
        }
    }
}

public fun BigInt.Companion.bitLength(mag: IntArray, sigNum: BigSigned): Int = if (mag.isNotEmpty()) {
    val size: Int = bitLengthForInt(mag[0]) + ((mag.size - 1) * Int.SIZE_BITS)
    if (sigNum.isNegative()) {
        if (mag.first().countOneBits() == 1) {
            if ((1 until mag.lastIndex).indexOfFirst { mag[it] != 0 } == -1) size - 1 else size
        } else size
    } else size
} else 0

public fun BigInt.Companion.bitCount(mag: IntArray, sigNum: BigSigned): Int = mag.sumOf {
    it.countOneBits() } + if (sigNum.isNegative()) {
    var i: Int = mag.lastIndex
    while (mag[i] == 0) i--
    Int.SIZE_BITS * (mag.lastIndex - i) + mag[i].countLeadingZeroBits() - 1
} else 0

public inline fun BigInt.Companion.firstNonZero(mag: IntArray): Int = (
        mag.lastIndex downTo 0).indexOfFirst { mag[it] != 0 }.let { if(it == -1) 0 else it }

public fun sigNumZeroAdjust(mag: IntArray, sigNum: BigSigned): BigSigned = when {
    mag.isEmpty() -> BigSigned.ZERO
    else -> sigNum
}