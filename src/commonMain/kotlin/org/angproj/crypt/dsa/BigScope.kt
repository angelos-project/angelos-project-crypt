package org.angproj.crypt.dsa

public class BigScope {
    public inline fun IntArray.revIdx(index: Int): Int = lastIndex - index
    public inline fun IntArray.revGet(index: Int): Int = this[lastIndex - index]
    public inline fun IntArray.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
    public inline fun IntArray.revSet(index: Int, value: Int) { this[lastIndex - index] = value }
    public inline fun IntArray.revSetL(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }

    public inline fun <E: List<Int>> E.revIdx(index: Int): Int = lastIndex - index
    public inline fun <E: List<Int>> E.revGet(index: Int): Int = this[lastIndex - index]
    public inline fun <E: List<Int>> E.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
    public inline fun <E: MutableList<Int>> E.revSet(index: Int, value: Int) { this[lastIndex - index] = value }
    public inline fun <E: MutableList<Int>> E.revSetL(index: Int, value: Long) { this[lastIndex - index] = value.toInt() }
}