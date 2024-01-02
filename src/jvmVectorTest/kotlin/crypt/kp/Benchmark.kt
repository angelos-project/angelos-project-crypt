package crypt.kp

import kotlin.math.sqrt

class Benchmark {
    private var r: Long = 0
    private var b: Long = 0

    val n: Long
        get() = r + b

    fun scatterPoint(x: Long, y: Long) {
        val a = x.toDouble() / longMax
        val b = y.toDouble() / longMax
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
        val longMax: Double = Long.MAX_VALUE.toDouble()
    }
}