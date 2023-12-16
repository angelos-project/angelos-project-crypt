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
public class PaulssonRandom(salt: Long = 0): AbstractPaulssonSponge(squeeze = true) {

    private var offset: Long = 0

    init {
        seed(salt)
    }

    protected fun seed(salt: Long) {
        state.first[0] = state.first[0] xor salt
        PaulssonSponge.scramble(state)
    }

    public fun nextLong(): Long {
        val index = offset.mod(16)
        if(index == 0) PaulssonSponge.squeeze(outBuf, state)
        return outBuf[index].also { offset++ }
    }
}