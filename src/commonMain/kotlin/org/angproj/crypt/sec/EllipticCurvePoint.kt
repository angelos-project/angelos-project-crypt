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
import org.angproj.crypt.number.log2

public class EllipticCurvePoint (
    public val x: BigInt,
    public val y: BigInt
) {
    /**
     *  sec1-v2.pdf -- 2.3.3 Elliptic-Curve-Point-to-Octet-String Conversion.
     * */
    public fun toOctetString(compress: Boolean = true): OctetString {
        /*if (isAtInfinity()) return OctetString(byteArrayOf(0x00))
        return when(compress) {
            true -> {
                val xB = x.toRawOctetString(x.log2() / 8)
            }
            else -> {}
        }*/
        return OctetString(byteArrayOf())
    }

    public fun isAtInfinity(): Boolean = y.equals(BigInt.zero)
}