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
package org.angproj.crypt.num

import org.angproj.aux.util.DslBlock

public object BigScope : DslBlock {
    public fun IntArray.revIdx(index: Int): Int = lastIndex - index
    public fun IntArray.revGet(index: Int): Int = this[lastIndex - index]
    public fun IntArray.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
    public fun IntArray.revSet(index: Int, value: Int) {
        this[lastIndex - index] = value
    }

    public fun IntArray.revSetL(index: Int, value: Long) {
        this[lastIndex - index] = value.toInt()
    }

    public fun <E : List<Int>> E.revIdx(index: Int): Int = lastIndex - index
    public fun <E : List<Int>> E.revGet(index: Int): Int = this[lastIndex - index]
    public fun <E : List<Int>> E.revGetL(index: Int): Long = this[lastIndex - index].toLong() and 0xffffffffL
    public fun <E : MutableList<Int>> E.revSet(index: Int, value: Int) {
        this[lastIndex - index] = value
    }

    public fun <E : MutableList<Int>> E.revSetL(index: Int, value: Long) {
        this[lastIndex - index] = value.toInt()
    }

    public fun <R> scope(block: () -> R): R = block()

}