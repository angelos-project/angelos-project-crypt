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
import org.angproj.aux.util.BinHex
import java.math.BigInteger

public fun secp256k1From(): Curve {

    val dp = Secp256Koblitz1.domainParameters
    return Curve(
        BigInteger(1, dp.a.toByteArray()),
        BigInteger(1, dp.b.toByteArray()),
        BigInteger(1, dp.p.toByteArray()),
        BigInteger(1, dp.n.toByteArray()),
        BigInteger(1, dp.G.x.toByteArray()),
        BigInteger(1, dp.G.y.toByteArray()),
        Secp256Koblitz1.name,
        longArrayOf(0, 1, 2, 3, 4),
        32
    )
}

/**
 * Bridging NIST curve implementation from Angelos Project to Starkbank implementation for testing and certifying.
 * */

public fun nistP192From(): Curve {
    val dp = Secp192Random1.domainParameters
    return Curve(
        BigInteger(1, dp.a.toByteArray()),
        BigInteger(1, dp.b.toByteArray()),
        BigInteger(1, dp.p.toByteArray()),
        BigInteger(1, dp.n.toByteArray()),
        BigInteger(1, dp.G.x.toByteArray()),
        BigInteger(1, dp.G.y.toByteArray()),
        Secp192Random1.name,
        longArrayOf(0, 1, 2, 3, 4),
        20
    )
}

public fun nistP224From(): Curve {
    val dp = Secp224Random1.domainParameters
    return Curve(
        BigInteger(1, dp.a.toByteArray()),
        BigInteger(1, dp.b.toByteArray()),
        BigInteger(1, dp.p.toByteArray()),
        BigInteger(1, dp.n.toByteArray()),
        BigInteger(1, dp.G.x.toByteArray()),
        BigInteger(1, dp.G.y.toByteArray()),
        Secp224Random1.name,
        longArrayOf(1, 1, 2, 3, 4),
        28
    )
}

public fun nistP256From(): Curve {
    val dp = Secp256Random1.domainParameters
    return Curve(
        BigInteger(1, dp.a.toByteArray()),
        BigInteger(1, dp.b.toByteArray()),
        BigInteger(1, dp.p.toByteArray()),
        BigInteger(1, dp.n.toByteArray()),
        BigInteger(1, dp.G.x.toByteArray()),
        BigInteger(1, dp.G.y.toByteArray()),
        Secp256Random1.name,
        longArrayOf(2, 1, 2, 3, 4),
        32
    )
}

public fun nistP384From(): Curve {
    val dp = Secp384Random1.domainParameters
    return Curve(
        BigInteger(1, dp.a.toByteArray()),
        BigInteger(1, dp.b.toByteArray()),
        BigInteger(1, dp.p.toByteArray()),
        BigInteger(1, dp.n.toByteArray()),
        BigInteger(1, dp.G.x.toByteArray()),
        BigInteger(1, dp.G.y.toByteArray()),
        Secp384Random1.name,
        longArrayOf(3, 1, 2, 3, 4),
        48
    )
}

public fun nistP521From(): Curve {
    val dp = Secp521Random1.domainParameters
    return Curve(
        BigInteger(1, dp.a.toByteArray()),
        BigInteger(1, dp.b.toByteArray()),
        BigInteger(1, dp.p.toByteArray()),
        BigInteger(1, dp.n.toByteArray()),
        BigInteger(1, dp.G.x.toByteArray()),
        BigInteger(1, dp.G.y.toByteArray()),
        Secp521Random1.name,
        longArrayOf(4, 1, 2, 3, 4),
        64
    )
}

public fun pointOnCurve2(c: Curve, p: Point): Boolean {
    if (p.x.compareTo(BigInteger.ZERO) < 0) {
        return false
    }
    if (p.x.compareTo(c.P) >= 0) {
        return false
    }
    if (p.y.compareTo(BigInteger.ZERO) < 0) {
        return false
    }
    if (p.y.compareTo(c.P) >= 0) {
        return false
    }

    println(BinHex.encodeToHex(p.x.toByteArray()))
    println(BinHex.encodeToHex(p.y.toByteArray()))


    val Amul: BigInteger = c.A.multiply(p.x)
    println(BinHex.encodeToHex(Amul.toByteArray()))

    val Xpow = p.x.pow(3)
    println(BinHex.encodeToHex(Xpow.toByteArray()))

    val Add1 = Xpow.add(Amul)
    println(BinHex.encodeToHex(Add1.toByteArray()))

    val Add2 = Add1.add(c.B)
    println(BinHex.encodeToHex(Add2.toByteArray()))


    val Ypow = p.y.pow(2)
    println(BinHex.encodeToHex(Ypow.toByteArray()))

    val Sub1 = Ypow.subtract(Add2)
    println(BinHex.encodeToHex(Sub1.toByteArray()))

    val modP = Sub1.mod(c.P)
    println(BinHex.encodeToHex(modP.toByteArray()))
    println("")

    return modP.toInt() == 0

    //return p.y.pow(2).subtract(p.x.pow(3).add(A.multiply(p.x)).add(B)).mod(P).intValue() == 0;
}