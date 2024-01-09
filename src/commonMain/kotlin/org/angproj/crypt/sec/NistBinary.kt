/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angproj.crypt.sec

public enum class NistBinary(public val curve: SecTRandom) {
    //B_163(),
    B_233(Sect233Random1),
    B_283(Sect283Random1),
    B_409(Sect409Random1),
    B_571(Sect571Random1),
}