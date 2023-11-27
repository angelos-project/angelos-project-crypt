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
import kotlin.math.max


public abstract class AbstractBigInt<E: List<Int>>(
    public val mag: E,
    public val sigNum: BigSigned,
){
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

    public fun getIdxL(index: Int): Long = getIdx(index).toLong() and 0xffffffffL

    public fun getUnreversedIdx(index: Int): Int {
        val num = mag[index]
        return when {
            sigNum.isNonNegative() -> num
            index <= firstNonZero -> -num
            else -> num.inv()
        }
    }

    public fun getUnreversedIdxL(index: Int): Long = getUnreversedIdx(index).toLong() and 0xffffffffL

    public fun compareTo(other: AbstractBigInt<*>): BigCompare = when {
        sigNum.state > other.sigNum.state -> BigCompare.GREATER
        sigNum.state < other.sigNum.state -> BigCompare.LESSER
        sigNum == BigSigned.POSITIVE -> compareMagnitude(other)
        sigNum == BigSigned.NEGATIVE -> other.compareMagnitude(this)
        else -> BigCompare.EQUAL
    }

    public fun compareMagnitude(other: AbstractBigInt<*>): BigCompare = when {
        mag.size < other.mag.size -> BigCompare.LESSER
        mag.size > other.mag.size -> BigCompare.GREATER
        else -> {
            mag.indices.forEach { idx ->
                val xNum = mag[idx]
                val yNum = other.mag[idx]
                if (xNum != yNum) return@compareMagnitude if (xNum xor -0x80000000 < yNum xor -0x80000000
                ) BigCompare.LESSER else BigCompare.GREATER
            }
            BigCompare.EQUAL
        }
    }

    public abstract fun negate(): AbstractBigInt<E>

    public fun abs(): AbstractBigInt<E> = when (sigNum) {
        BigSigned.NEGATIVE -> negate()
        else -> this
    }

    public fun toByteArray(): ByteArray {
        if(sigNum.isZero()) return byteArrayOf(0)

        val output = ByteArray(mag.size * 4)
        mag.indices.forEach { output.writeIntAt(it * 4, getIdx(mag.lastIndex - it).swapEndian()) }
        val keep = keep(output, sigNum)

        if(keep == output.size) return byteArrayOf(sigNum.signed.toByte())

        val prepend = sigNum.isNonNegative() == output[keep] < 0
        return when {
            keep == 0 && prepend -> byteArrayOf(sigNum.signed.toByte()) + output
            keep == 1 && prepend -> output.also{ it[0] = sigNum.signed.toByte() }
            keep > 1 && prepend -> byteArrayOf(sigNum.signed.toByte()) + output.copyOfRange(keep, output.size)
            keep > 0 -> output.copyOfRange(keep, output.size)
            else -> output
        }
    }

    public fun toReversedComplementedIntArray(): IntArray = IntArray(mag.size) { getIdx(it) }
    public fun toComplementedIntArray(): IntArray = IntArray(mag.size) { getUnreversedIdx(it) }
    public fun toRawIntArray(): IntArray = mag.toIntArray()

    public fun toZeroFilledByteArray(totalSize: Int): ByteArray {
        val byteArray = toByteArray()
        return ByteArray(totalSize - byteArray.size).also { it.fill(sigNum.signed.toByte()) } + byteArray
    }

    public abstract fun copyOf(): AbstractBigInt<E>

    public abstract fun of(value: IntArray): AbstractBigInt<E>
    public abstract fun of(value: IntArray, sigNum: BigSigned): AbstractBigInt<E>
    public abstract fun of(value: Long): AbstractBigInt<E>

    public abstract fun toMutableBigInt(): MutableBigInt
    public abstract fun toBigInt(): BigInt

    public companion object {

        public inline fun IntArray.revIdx(index: Int): Int = lastIndex - index
        public inline fun IntArray.revGet(index: Int): Int = this[lastIndex - index]
        public inline fun IntArray.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
        public inline fun IntArray.getL(index: Int): Long = this[index].toLong() and 0xffffffffL
        public inline fun IntArray.revSet(index: Int, value: Int) { this[lastIndex - index] = value }
        public inline fun IntArray.revSetL(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }
        public inline fun IntArray.setL(index: Int, value: Long) { this[index] = value.toInt() }

        public inline fun <E: List<Int>> E.revIdx(index: Int): Int = lastIndex - index
        public inline fun <E: List<Int>> E.revGet(index: Int): Int = this[lastIndex - index]
        public inline fun <E: List<Int>> E.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
        public inline fun <E: MutableList<Int>> E.revSet(index: Int, value: Int) { this[lastIndex - index] = value }
        public inline fun <E: MutableList<Int>> E.revSetL(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }

        public fun <T: AbstractBigInt<*>> fromByteArray(value: ByteArray, build: (IntArray, BigSigned) -> T): T {
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

            return build(mag, sigNumZeroAdjust(mag, sigNum))
        }

        public fun <T: AbstractBigInt<*>> fromIntArray(value: IntArray, build: (IntArray, BigSigned) -> T): T  {
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

            return build(mag, sigNumZeroAdjust(mag, sigNum))
        }

        public fun <T: AbstractBigInt<*>> fromLong(value: Long, build: (IntArray, BigSigned) -> T): T  {
            val negative = value < 0

            val sigNum = when(negative) {
                true -> BigSigned.NEGATIVE
                else -> BigSigned.POSITIVE
            }
            val tmp =  intArrayOf((value ushr 32).toInt(), value.toInt())
            val mag = when(negative) {
                true -> makePositive(tmp)
                else -> stripLeadingZeros(tmp)
            }

            return build(mag, sigNumZeroAdjust(mag, sigNum))
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

        public fun <E: List<Int>> bitLength(mag: E, sigNum: BigSigned): Int = if (mag.isNotEmpty()) {
            val size: Int = bitSizeForInt(mag[0]) + ((mag.size - 1) * Int.SIZE_BITS)
            if (sigNum.isNegative()) {
                if (mag.first().countOneBits() == 1) {
                    if ((1 until mag.lastIndex).indexOfFirst { mag[it] != 0 } == -1) size - 1 else size
                } else size
            } else size
        } else 0

        public fun <E: List<Int>> bitCount(mag: E, sigNum: BigSigned): Int = mag.sumOf {
            it.countOneBits() } + if (sigNum.isNegative()) {
            var i: Int = mag.lastIndex
            while (mag[i] == 0) i--
            Int.SIZE_BITS * (mag.lastIndex - i) + mag[i].countLeadingZeroBits() - 1
        } else 0

        public fun <E: List<Int>> firstNonZero(mag: E): Int = (
                mag.lastIndex downTo 0).indexOfFirst { mag[it] != 0 }.let { if(it == -1) 0 else it }
    }
}

internal fun biggerFirst(x: IntArray, y: IntArray, block: (x: IntArray, y: IntArray) -> IntArray): IntArray =
    when (x.size < y.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

internal fun biggerFirst(x: AbstractBigInt<*>, y: AbstractBigInt<*>, block: (x: AbstractBigInt<*>, y: AbstractBigInt<*>) -> AbstractBigInt<*>): AbstractBigInt<*> =
    when (x.mag.size < y.mag.size) {
        true -> block(y, x)
        else -> block(x, y)
    }

internal inline fun <A: List<Int>, B: List<Int>> maxOfArrays(x: A, y: B, extra: Int = 1): IntArray =
    IntArray(max(x.size, y.size) + extra)

internal inline fun bigMask(pos: Int): Int = 1 shl (pos and Int.SIZE_BITS - 1)