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

public enum class NistKoblitz(public val curve: SecTKoblitz) {
    K_163(Sect163Koblitz1),
    K_233(Sect233Koblitz1),
    K_283(Sect283Koblitz1),
    K_409(Sect409Koblitz1),
    K_571(Sect571Koblitz1)
}