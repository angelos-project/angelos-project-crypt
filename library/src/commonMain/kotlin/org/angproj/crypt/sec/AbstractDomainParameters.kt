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

import org.angproj.big.BigInt
import org.angproj.crypt.ec.EcPoint

public abstract class AbstractDomainParameters {
    private var frozen: Boolean = false

    private var _a: BigInt = BigInt.nullObject
    public val a: BigInt
        get() = _a

    private var _b: BigInt = BigInt.nullObject
    public val b: BigInt
        get() = _b

    private var _G: EcPoint = EcPoint.nullEcPoint
    public val G: EcPoint
        get() = _G

    private var _n: BigInt = BigInt.nullObject
    public val n: BigInt
        get() = _n

    private var _h: BigInt = BigInt.nullObject
    public val h: BigInt
        get() = _h

    public fun setup(
        a: BigInt, b: BigInt, G: EcPoint,
        n: BigInt, h: BigInt
    ): Unit = when(frozen){
        false -> {
            _a = a
            _b = b
            _G = G
            _n = n
            _h = h
            frozen = true
        }
        else -> error("Parameters already set.")
    }
}