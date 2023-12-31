package crypt.kp

import kotlin.math.absoluteValue
import kotlin.math.sqrt

class Benchmark {
    private var r: Long = 0
    private var b: Long = 0

    val n: Long
        get() = r + b

    fun scatterPoint(x: Long, y: Long) {
        val a = x.toULong().toDouble() / longMax.toDouble()
        val b = y.toULong().toDouble() / longMax.toDouble()
        val c = sqrt(a * a + b * b)
        when (c < 1) {
            true -> this.r++
            else -> this.b++
        }
    }

    fun distribution(): Double {
        val n = (r + b).toDouble()
        return 4 * r / n
    }

    companion object {
        const val longMax = ULong.MAX_VALUE
    }
}