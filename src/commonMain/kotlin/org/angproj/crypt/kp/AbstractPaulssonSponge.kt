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
public abstract class AbstractPaulssonSponge(
    absorb: Boolean = false,
    squeeze: Boolean = false
): PaulssonSponge {
    protected val inBuf: StateBuffer = if(absorb) PaulssonSponge.buffer() else longArrayOf()
    protected val outBuf: StateBuffer = if(squeeze) PaulssonSponge.buffer() else longArrayOf()
    protected val state: Sponge = PaulssonSponge.sponge()
}