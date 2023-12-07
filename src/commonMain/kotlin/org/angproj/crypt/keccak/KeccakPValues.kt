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

internal enum class KeccakPValues (val bWidth: Int, val wSize: Int, val log: Int){
    P_25(25, 1, 0),
    P_50(50, 2, 1),
    P_100(100, 4, 2),
    P_200(200, 8, 3),
    P_400(400, 16, 4),
    P_800(800, 32, 5),
    P_1600(1600, 64, 6)
}