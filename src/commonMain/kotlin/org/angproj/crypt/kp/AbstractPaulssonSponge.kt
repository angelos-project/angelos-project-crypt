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

/**
 *  ===== WARNING! EXPERIMENTAL USE ONLY =====
 * */
public abstract class AbstractPaulssonSponge(entropy: LongArray): PaulssonSponge {

    init {
        require(entropy.size == PaulssonSponge.stateSize) { "Entropy is not 1024 bits long" }
        require(entropy.sum() != 0L) { "Entropy is zero" }
    }

    protected val state: LongArray = entropy.copyOf()
    protected val side: LongArray = LongArray(PaulssonSponge.sideSize)
}