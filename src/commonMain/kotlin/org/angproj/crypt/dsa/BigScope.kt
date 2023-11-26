package org.angproj.crypt.dsa

import org.angproj.aux.util.DslBlock

public object BigScope: DslBlock {
    public fun IntArray.revIdx(index: Int): Int = lastIndex - index
    public fun IntArray.revGet(index: Int): Int = this[lastIndex - index]
    public fun IntArray.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
    public fun IntArray.revSet(index: Int, value: Int) { this[lastIndex - index] = value }
    public fun IntArray.revSetL(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }

    public fun <E: List<Int>> E.revIdx(index: Int): Int = lastIndex - index
    public fun <E: List<Int>> E.revGet(index: Int): Int = this[lastIndex - index]
    public fun <E: List<Int>> E.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
    public fun <E: MutableList<Int>> E.revSet(index: Int, value: Int) { this[lastIndex - index] = value }
    public fun <E: MutableList<Int>> E.revSetL(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }

    public fun <R> scope(block: () -> R): R = block()

}