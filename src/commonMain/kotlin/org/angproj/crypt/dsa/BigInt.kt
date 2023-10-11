package org.angproj.crypt.dsa

import org.angproj.aux.util.readIntAt
import org.angproj.aux.util.swapEndian
import org.angproj.aux.util.writeIntAt

public enum class BigSignedInt(public val num: Int, public val sign: Int) {
    POSITIVE(1, 0),
    ZERO(0, 0),
    NEGATIVE(-1, -1)
}

public class BigInt(value: ByteArray) {

    public val fullSize: Int = value.size
    public val keepIndex: Int = calcKeep(value)

    protected val magnitude: IntArray = importBytes(value, keepIndex)
    protected var signedNumber: BigSignedInt = calcSignum(value, magnitude)

    public fun toByteArray(padded: Boolean = false): ByteArray {
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
        return when(padded) {
            false -> cache.copyOfRange(unusedIntSpace(magnitude[0]), cache.size)
            else -> {
                val value = when {
                    cache.size > fullSize -> cache.copyOfRange(unusedIntSpace(magnitude[0]), cache.size)
                    else -> cache
                }
                padToFullSize(value, fullSize, signedNumber.sign)
            }
        }
    }

    private companion object {

        fun padToFullSize(value: ByteArray, fullSize: Int, signum: Int): ByteArray = when {
            value.size == fullSize -> value
            value.size < fullSize -> ByteArray(fullSize - value.size).also {
                it.fill(signum.toByte()) } + value
            else -> error("Can't pad value larger than full size")
        }

        private fun unusedIntSpace(value: Int) = when(value) {
            0 -> 4
            in Byte.MIN_VALUE..Byte.MAX_VALUE -> 3
            in Short.MIN_VALUE..Short.MAX_VALUE -> 2
            in -8388608..8388607 -> 1
            in Int.MIN_VALUE..Int.MAX_VALUE -> 0
            else -> error("Can't happen!")
        }


        fun calcKeep(data: ByteArray): Int = when {
            data[0].toInt() < 0 -> data.indexOfFirst { it.toInt() != -1 }
            else -> when(val idx = data.indexOfFirst { it.toInt() != 0 }) {
                -1 -> data.size
                else -> idx
            }
        }

        fun importBytes(data: ByteArray, keep: Int): IntArray = when {
            data[0].toInt() < 0 -> makePositive(data, keep)
            else -> stripLeadingZeroBytes(data, keep)
        }

        fun calcSignum(data: ByteArray, magnitude: IntArray): BigSignedInt = when {
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
