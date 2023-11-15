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

public object Secp256Random1 : SecPRandom {
    public override val name: String = "secp256r1"
    public override val strength: Int = 128
    public override val size: Int = 256

    private val _p: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "00000001" +
                "00000000" +
                "00000000" +
                "00000000" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF"
    ) }

    private val _a: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "00000001" +
                "00000000" +
                "00000000" +
                "00000000" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFC"
    ) }

    private val _b: ByteArray by lazy { BinHex.decodeToBin(
        "5AC635D8" +
                "AA3A93E7" +
                "B3EBBD55" +
                "769886BC" +
                "651D06B0" +
                "CC53B0F6" +
                "3BCE3C3E" +
                "27D2604B"
    ) }

    private val _S: ByteArray by lazy { BinHex.decodeToBin(
        "C49D3608" +
                "86E70493" +
                "6A6678E1" +
                "139D26B7" +
                "819F7E90"
    ) }

    private val _G: ByteArray by lazy { BinHex.decodeToBin(
        "03" +
                "6B17D1F2" +
                "E12C4247" +
                "F8BCE6E5" +
                "63A440F2" +
                "77037D81" +
                "2DEB33A0" +
                "F4A13945" +
                "D898C296"
    ) }

    private val _Gc: ByteArray by lazy { BinHex.decodeToBin(
        "04" +
                "6B17D1F2" +
                "E12C4247" +
                "F8BCE6E5" +
                "63A440F2" +
                "77037D81" +
                "2DEB33A0" +
                "F4A13945" +
                "D898C296" +
                "4FE342E2" +
                "FE1A7F9B" +
                "8EE7EB4A" +
                "7C0F9E16" +
                "2BCE3357" +
                "6B315ECE" +
                "CBB64068" +
                "37BF51F5"
    ) }

    private val _n: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "00000000" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "BCE6FAAD" +
                "A7179E84" +
                "F3B9CAC2" +
                "FC632551"
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
    override val S: ByteArray
        get() = _S.copyOf()
    override val G: ByteArray
        get() = _G.copyOf()
    override val Gc: ByteArray
        get() = _Gc.copyOf()
    override val n: ByteArray
        get() = _n.copyOf()
    override val h: ByteArray
        get() = _h.copyOf()
}