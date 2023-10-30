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
import org.angproj.crypt.number.bitLengthForInt
import org.angproj.crypt.number.getIdx
import kotlin.math.min

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

    /*public fun toByteArray1(): ByteArray {
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
    }*/

    public fun toByteArray0(): ByteArray {
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

        // Compatability adjustment compared to JAVA implementation, is it really necessary?
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

        private fun keep(value: ByteArray, sigNum: BigSigned): Int {
            val keep = value.indexOfFirst { it.toInt() != sigNum.signed }
            return when (keep) {
                -1 -> value.size
                else -> keep
            }
        }

        public fun fromByteArray(value: ByteArray): BigInt {
            check(value.isNotEmpty()) { "Zero length" }
            val negative = value[0].toInt() < 0

            val sigNum = when(negative) {
                true -> BigSigned.NEGATIVE
                else -> BigSigned.POSITIVE
            }
            val mag = when(negative) {
                true -> makePositive(value)
                else -> stripLeadingZeroBytes(value)
            }

            return BigInt(mag, sigNumZeroAdjust(mag, sigNum))
        }

        private fun makePositive(value: ByteArray): IntArray {
            println("MAKE POSITIVE")
            var keep: Int
            var k: Int
            val indexBound: Int = value.size

            // Find first non-sign (0xff) byte of input

            // Find first non-sign (0xff) byte of input
                keep = 0
                while (keep < indexBound && value.get(keep).toInt() == -1) {
                    keep++
                }

            /* Allocate output array.  If all non-sign bytes are 0x00, we must
         * allocate space for one extra output byte. */
                k = keep
                while (k < indexBound && value.get(k).toInt() == 0) {
                    k++
                }

            val extraByte = if (k == indexBound) 1 else 0
            val intLength = indexBound - keep + extraByte + 3 ushr 2
            val result = IntArray(intLength)


            /* Copy one's complement of input into output, leaving extra
         * byte (if it exists) == 0x00 */
            var b = indexBound - 1
            for (i in intLength - 1 downTo 0) {
                result[i] = value.get(b--).toInt() and 0xff
                var numBytesToTransfer = min(3.0, (b - keep + 1).toDouble()).toInt()
                if (numBytesToTransfer < 0) numBytesToTransfer = 0
                var j = 8
                while (j <= 8 * numBytesToTransfer) {
                    result[i] = result[i] or (value.get(b--).toInt() and 0xff shl j)
                    j += 8
                }

                // Mask indicates which bits must be complemented
                val mask = -1 ushr 8 * (3 - numBytesToTransfer)
                result[i] = result[i].inv() and mask
            }

            // Add one to one's complement to generate two's complement

            // Add one to one's complement to generate two's complement
            for (i in result.indices.reversed()) {
                result[i] = ((result[i].toLong() and 0xffffffffL) + 1).toInt()
                if (result[i] != 0) break
            }

            return result
        }

        private fun stripLeadingZeroBytes(value: ByteArray): IntArray {
            val indexBound: Int = value.size
            var keep: Int
            // Find first nonzero byte
                keep = 0
                while (keep < indexBound && value.get(keep).toInt() == 0) {
                    keep++
                }

            // Allocate new array and copy relevant part of input array
            val intLength = indexBound - keep + 3 ushr 2
            val result = IntArray(intLength)
            var b = indexBound - 1
            for (i in intLength - 1 downTo 0) {
                result[i] = value.get(b--).toInt() and 0xff
                val bytesRemaining = b - keep + 1
                val bytesToTransfer = min(3.0, bytesRemaining.toDouble()).toInt()
                var j = 8
                while (j <= bytesToTransfer shl 3) {
                    result[i] = result[i] or (value.get(b--).toInt() and 0xff shl j)
                    j += 8
                }
            }
            return result
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
        mag.indices.reversed().indexOfFirst { mag[it] != 0 }).let { if(it == -1) 0 else it }

public fun sigNumZeroAdjust(mag: IntArray, sigNum: BigSigned): BigSigned = when {
    mag.isEmpty() -> BigSigned.ZERO
    else -> sigNum
}