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
import kotlin.math.max

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

    public fun withSigned(sigNum: BigSigned): BigSigned = when (state == sigNum.state) {
        true -> BigSigned.POSITIVE
        else -> BigSigned.NEGATIVE
    }
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

    public fun intSize(): Int = bitLength.floorDiv(Int.SIZE_BITS) + 1

    public fun getIdx(index: Int): Int = when {
        index < 0 -> 0
        index >= mag.size -> sigNum.signed
        else -> {
            val num = mag.revGet(index)
            when {
                sigNum.isNonNegative() -> num
                index <= firstNonZero -> -num
                else -> num.inv()
            }
        }
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

    public fun negate(): BigInt = BigInt(mag, sigNum.negate())

    public fun abs(): BigInt = when (sigNum) {
        BigSigned.NEGATIVE -> negate()
        else -> this
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

    public fun toZeroFilledByteArray(totalSize: Int): ByteArray {
        val byteArray = toByteArray()
        return ByteArray(totalSize - byteArray.size).also { it.fill(sigNum.signed.toByte()) } + byteArray
    }

    public companion object {

        public val one: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.POSITIVE) }
        public val zero: BigInt by lazy { BigInt(intArrayOf(0), BigSigned.ZERO) }
        public val minusOne: BigInt by lazy { BigInt(intArrayOf(1), BigSigned.NEGATIVE) }

        public inline fun IntArray.revIdx(index: Int): Int = this.lastIndex - index
        public inline fun IntArray.revGet(index: Int): Int = this[lastIndex - index]
        public inline fun IntArray.revSet(index: Int, value: Int) { this[lastIndex - index] = value }

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

        public fun fromIntArray(value: IntArray): BigInt {
            check(value.isNotEmpty()) { "Zero length" }
            val negative = value.first() < 0

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

        private fun sigNumZeroAdjust(mag: IntArray, sigNum: BigSigned): BigSigned = when {
            mag.isEmpty() -> BigSigned.ZERO
            else -> sigNum
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

        private fun makePositive(value: IntArray): IntArray {
            val keep: Int = keep(value, BigSigned.NEGATIVE)
            val extra = (keep until value.size).indexOfFirst { value[it] != 0 }.let { if(it == -1) 1 else 0 }
            val result = IntArray(value.size - keep + extra)

            (keep until value.size).forEach { result[it - keep + extra] = value[it].inv() }

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

        public fun stripLeadingZeros(value: IntArray): IntArray {
            val keep = keep(value, BigSigned.POSITIVE)
            return if (keep == 0) value else value.copyOfRange(keep, value.size)
        }

        private fun keep(value: ByteArray, sigNum: BigSigned): Int {
            val keep = value.indexOfFirst { it.toInt() != sigNum.signed }
            return when (keep) {
                -1 -> value.size
                else -> keep
            }
        }

        private fun keep(value: IntArray, sigNum: BigSigned): Int {
            val keep = value.indexOfFirst { it != sigNum.signed }
            return when (keep) {
                -1 -> value.size
                else -> keep
            }
        }

        public fun bitSizeForInt(n: Int): Int = Int.SIZE_BITS - n.countLeadingZeroBits()

        public fun bitLength(mag: IntArray, sigNum: BigSigned): Int = if (mag.isNotEmpty()) {
            val size: Int = bitSizeForInt(mag[0]) + ((mag.size - 1) * Int.SIZE_BITS)
            if (sigNum.isNegative()) {
                if (mag.first().countOneBits() == 1) {
                    if ((1 until mag.lastIndex).indexOfFirst { mag[it] != 0 } == -1) size - 1 else size
                } else size
            } else size
        } else 0

        public fun bitCount(mag: IntArray, sigNum: BigSigned): Int = mag.sumOf {
            it.countOneBits() } + if (sigNum.isNegative()) {
            var i: Int = mag.lastIndex
            while (mag[i] == 0) i--
            Int.SIZE_BITS * (mag.lastIndex - i) + mag[i].countLeadingZeroBits() - 1
        } else 0

        public fun firstNonZero(mag: IntArray): Int = (
                mag.lastIndex downTo 0).indexOfFirst { mag[it] != 0 }.let { if(it == -1) 0 else it }

        internal fun fromLong(value: Long): BigInt = when {
            value == 0L -> BigInt(intArrayOf(), BigSigned.ZERO)
            //value < 0 -> BigInt(long2IntArray(-value), BigSignedInt.NEGATIVE)
            value < 0 -> BigInt(long2IntArray(value.inv()), BigSigned.NEGATIVE)
            else -> BigInt(long2IntArray(value), BigSigned.POSITIVE)
        }

        internal fun long2IntArray(value: Long): IntArray = when(value) {
            in Int.MIN_VALUE..Int.MAX_VALUE -> intArrayOf(value.toInt())
            else -> intArrayOf((value ushr 32).toInt(), value.toInt())
        }
    }
}

internal fun packMagSigNum(mag: IntArray, sigNum: BigSigned, cmp: BigCompare): BigInt = BigInt(
    BigInt.stripLeadingZeros(mag), cmp.withSigned(sigNum)
)

internal fun biggerFirst(x: IntArray, y: IntArray, block: (x: IntArray, y: IntArray) -> IntArray): IntArray =
    when (x.size < y.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

internal fun biggerFirst(x: BigInt, y: BigInt, block: (x: BigInt, y: BigInt) -> BigInt): BigInt =
    when (x.mag.size < y.mag.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

internal inline fun maxOfArrays(a: IntArray, b: IntArray, extra: Int = 1): IntArray =
    IntArray(max(a.size, b.size) + extra)

internal inline fun bigMask(pos: Int): Int = 1 shl (pos and Int.SIZE_BITS - 1)