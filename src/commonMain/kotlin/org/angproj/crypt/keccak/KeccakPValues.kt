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
package org.angproj.crypt.keccak

internal enum class KeccakPValues (val bWidth: Int, val wSize: Int, val log2: Int, val wordMask: Long, val bSize: Int){
    P_25(25, 1, 0, 0x1, 0),
    P_50(50, 2, 1, 0x3, 0),
    P_100(100, 4, 2, 0x7, 0),
    P_200(200, 8, 3, 0xff, 25),
    P_400(400, 16, 4, 0xffff, 50),
    P_800(800, 32, 5, 0xffffffff, 100),
    P_1600(1600, 64, 6, 0xffffffffffffffffuL.toLong(), 200)
}