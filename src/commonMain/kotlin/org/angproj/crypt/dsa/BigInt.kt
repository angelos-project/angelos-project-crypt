package org.angproj.crypt.dsa

import org.angproj.aux.util.readIntAt
import org.angproj.aux.util.swapEndian
import org.angproj.aux.util.writeIntAt
import kotlin.jvm.JvmInline

public enum class BigSignedInt(public val num: Int, public val sign: Int) {
    POSITIVE(1, 0),
    ZERO(0, 0),
    NEGATIVE(-1, -1);
}

public enum class BigCompare(public val num: Int) {
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
    public val magnitude: IntArray,
    public val signedNumber: BigSignedInt,
    public val fullSize: Int = 0
) {

    public val bitSize: Int by lazy { usedIntBits(magnitude[0]) + (magnitude.size - 1) * Int.SIZE_BITS }

    public fun compareTo(other: BigInt): BigCompare = when {
        signedNumber.num > other.signedNumber.num -> BigCompare.GREATER
        signedNumber.num < other.signedNumber.num -> BigCompare.LESSER
        signedNumber == BigSignedInt.POSITIVE -> compareMagnitude(other)
        signedNumber == BigSignedInt.NEGATIVE -> other.compareMagnitude(this)
        else -> BigCompare.EQUAL
    }

    public fun compareMagnitude(other: BigInt): BigCompare = when {
        magnitude.size < other.magnitude.size -> BigCompare.LESSER
        magnitude.size > other.magnitude.size -> BigCompare.GREATER
        else -> {
            var cmp = BigCompare.EQUAL
            magnitude.indices.indexOfFirst {
                val a = magnitude[it]
                val b = other.magnitude[it]
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
        val cache = ByteArray(magnitude.size * Int.SIZE_BYTES).also {
            it.fill(signedNumber.sign.toByte()) }
        magnitude.indices.forEach {idx ->
            val complimented = when (signedNumber) {
                BigSignedInt.NEGATIVE -> magnitude[idx].swapEndian().inv()
                else -> magnitude[idx].swapEndian()
            }
            cache.writeIntAt(idx * Int.SIZE_BYTES, complimented)
        }
        if (signedNumber == BigSignedInt.NEGATIVE) cache[cache.lastIndex] = (
                cache.last() + 1).toByte() // Could be dangerous
        return when(fullSize) {
            0 -> cache.copyOfRange(unusedIntBytes(magnitude[0]), cache.size)
            else -> {
                val value = when {
                    cache.size > fullSize -> cache.copyOfRange(unusedIntBytes(magnitude[0]), cache.size)
                    else -> cache
                }
                when {
                    value.size == fullSize -> value
                    value.size < fullSize -> ByteArray(fullSize - value.size).also {
                        it.fill(signedNumber.sign.toByte()) } + value
                    else -> error("Can't pad value larger than full size")
                }
            }
        }
    }

    public companion object {

        public inline fun stripLeadingZeroInts(value: IntArray): IntArray = value.copyOfRange(
            value.indexOfFirst { it != 0 }, value.size)

        public fun fromLong(value: Long): BigInt = when {
            value == 0L -> BigInt(intArrayOf(0), BigSignedInt.ZERO)
            value < 0 -> BigInt(long2IntArray(-value), BigSignedInt.NEGATIVE)
            else -> BigInt(long2IntArray(value), BigSignedInt.POSITIVE)
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

        private fun signed(data: ByteArray, magnitude: IntArray): BigSignedInt = when {
            data[0].toInt() < 0 -> BigSignedInt.NEGATIVE
            magnitude.isEmpty() -> BigSignedInt.ZERO
            else -> BigSignedInt.POSITIVE
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


public inline fun BigInt.negate(): BigInt = when (signedNumber) {
    BigSignedInt.POSITIVE -> BigInt(magnitude, BigSignedInt.NEGATIVE)
    BigSignedInt.NEGATIVE -> BigInt(magnitude, BigSignedInt.POSITIVE)
    else -> this
}

public inline fun BigInt.abs(): BigInt = when(signedNumber) {
    BigSignedInt.NEGATIVE -> negate()
    else -> this
}