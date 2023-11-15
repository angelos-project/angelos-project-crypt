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
package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Secp256Koblitz1 : SecPKoblitz {
    public override val name: String = "secp256k1"
    public override val strength: Int = 128
    public override val size: Int = 256

    private val _p: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFC2F"
    ) }

    private val _a: ByteArray by lazy { BinHex.decodeToBin(
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000"
    ) }

    private val _b: ByteArray by lazy { BinHex.decodeToBin(
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000007"
    ) }

    private val _G: ByteArray by lazy { BinHex.decodeToBin(
        "02" +
                "79BE667E" +
                "F9DCBBAC" +
                "55A06295" +
                "CE870B07" +
                "029BFCDB" +
                "2DCE28D9" +
                "59F2815B" +
                "16F81798"
    ) }

    private val _Gc: ByteArray by lazy { BinHex.decodeToBin(
        "04" +
                "79BE667E" +
                "F9DCBBAC" +
                "55A06295" +
                "CE870B07" +
                "029BFCDB" +
                "2DCE28D9" +
                "59F2815B" +
                "16F81798" +
                "483ADA77" +
                "26A3C465" +
                "5DA4FBFC" +
                "0E1108A8" +
                "FD17B448" +
                "A6855419" +
                "9C47D08F" +
                "FB10D4B8"
    ) }

    private val _n: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "BAAEDCE6" +
                "AF48A03B" +
                "BFD25E8C" +
                "D0364141"
    ) }

    private val _h: ByteArray = BinHex.decodeToBin(
        "01"
    )

    override val p: ByteArray
        get() = _p.copyOf()
    override val a: ByteArray
        get() = _a.copyOf()
    override val b: ByteArray
        get() = _b.copyOf()
    override val G: ByteArray
        get() = _G.copyOf()
    override val Gc: ByteArray
        get() = _Gc.copyOf()
    override val n: ByteArray
        get() = _n.copyOf()
    override val h: ByteArray
        get() = _h.copyOf()
}