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

import org.angproj.aux.num.BigInt

/**
 * sec1-v2.pdf -- 3.1.2 Elliptic Curve Domain Parameters over F2m.
 * */
public data class Char2DomainParameters(
    public val m: Int, /* An integer m specifying the finite field F2m. */
    public val x: BigInt /* An irreducible binary polynomial f(x) of degree m specifying the representation of F2m. */
) : AbstractDomainParameters()