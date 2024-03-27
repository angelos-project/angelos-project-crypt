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
package org.angproj.crypt.ellipticcurve

import org.angproj.crypt.sec.*
import java.math.BigInteger

/**
 * Bridging NIST curve implementation from Angelos Project to Starkbank implementation for testing and certifying.
 * */

public fun nistP192From(): Curve {
    val dp = Secp192Random1.domainParameters
    return Curve(
        BigInteger(1,dp.a.value.toByteArray()),
        BigInteger(1, dp.b.value.toByteArray()),
        BigInteger(1, dp.p.value.toByteArray()),
        BigInteger(1, dp.n.value.toByteArray()),
        BigInteger(1, dp.G.x.value.toByteArray()),
        BigInteger(1, dp.G.y.value.toByteArray()),
        Secp192Random1.name,
        longArrayOf(0, 1, 2, 3, 4)
    )
}

public fun nistP224From(): Curve {
    val dp = Secp224Random1.domainParameters
    return Curve(
        BigInteger(1,dp.a.value.toByteArray()),
        BigInteger(1, dp.b.value.toByteArray()),
        BigInteger(1, dp.p.value.toByteArray()),
        BigInteger(1, dp.n.value.toByteArray()),
        BigInteger(1, dp.G.x.value.toByteArray()),
        BigInteger(1, dp.G.y.value.toByteArray()),
        Secp224Random1.name,
        longArrayOf(1, 1, 2, 3, 4)
    )
}

public fun nistP256From(): Curve {
    val dp = Secp256Random1.domainParameters
    return Curve(
        BigInteger(1,dp.a.value.toByteArray()),
        BigInteger(1, dp.b.value.toByteArray()),
        BigInteger(1, dp.p.value.toByteArray()),
        BigInteger(1, dp.n.value.toByteArray()),
        BigInteger(1, dp.G.x.value.toByteArray()),
        BigInteger(1, dp.G.y.value.toByteArray()),
        Secp256Random1.name,
        longArrayOf(2, 1, 2, 3, 4)
    )
}

public fun nistP384From(): Curve {
    val dp = Secp384Random1.domainParameters
    return Curve(
        BigInteger(1,dp.a.value.toByteArray()),
        BigInteger(1, dp.b.value.toByteArray()),
        BigInteger(1, dp.p.value.toByteArray()),
        BigInteger(1, dp.n.value.toByteArray()),
        BigInteger(1, dp.G.x.value.toByteArray()),
        BigInteger(1, dp.G.y.value.toByteArray()),
        Secp384Random1.name,
        longArrayOf(3, 1, 2, 3, 4)
    )
}

public fun nistP521From(): Curve {
    val dp = Secp521Random1.domainParameters
    return Curve(
        BigInteger(1,dp.a.value.toByteArray()),
        BigInteger(1, dp.b.value.toByteArray()),
        BigInteger(1, dp.p.value.toByteArray()),
        BigInteger(1, dp.n.value.toByteArray()),
        BigInteger(1, dp.G.x.value.toByteArray()),
        BigInteger(1, dp.G.y.value.toByteArray()),
        Secp521Random1.name,
        longArrayOf(4, 1, 2, 3, 4)
    )
}