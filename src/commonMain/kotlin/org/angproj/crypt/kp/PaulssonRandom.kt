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
package org.angproj.crypt.kp

import org.angproj.aux.util.writeLongAt

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
internal object PaulssonRandom: AbstractPaulssonSponge() {

    init { seed() }

    fun seed(salt: Long = 0) {
        state[0] = state[0] xor salt
        repeat(15) { cycle() }
    }

    fun nextBytes(rand: ByteArray): ByteArray {
        cycle()
        state.forEachIndexed { idx, value -> rand.writeLongAt(idx * Long.SIZE_BYTES, value) }
        return rand
    }
}