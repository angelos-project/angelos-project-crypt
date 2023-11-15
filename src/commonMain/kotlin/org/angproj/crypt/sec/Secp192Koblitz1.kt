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

public object Secp192Koblitz1 : SecPKoblitz {
    public override val name: String = "secp192k1"
    public override val strength: Int = 96
    public override val size: Int = 192

    private val _p: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFEE37"
    ) }

    private val _a: ByteArray by lazy { BinHex.decodeToBin(
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
                "00000003"
    ) }

    private val _G: ByteArray by lazy { BinHex.decodeToBin(
        "03" +
                "DB4FF10E" +
                "C057E9AE" +
                "26B07D02" +
                "80B7F434" +
                "1DA5D1B1" +
                "EAE06C7D"
    ) }

    private val _Gc: ByteArray by lazy { BinHex.decodeToBin(
        "04" +
                "DB4FF10E" +
                "C057E9AE" +
                "26B07D02" +
                "80B7F434" +
                "1DA5D1B1" +
                "EAE06C7D" +
                "9B2F2F6D" +
                "9C5628A7" +
                "844163D0" +
                "15BE8634" +
                "4082AA88" +
                "D95E2F9D"
    ) }

    private val _n: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "26F2FC17" +
                "0F69466A" +
                "74DEFD8D"
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