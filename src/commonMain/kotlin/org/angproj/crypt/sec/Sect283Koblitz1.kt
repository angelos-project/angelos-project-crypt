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

public object Sect283Koblitz1 : SecTKoblitz {
    public override val name: String = "sect283k1"
    public override val strength: Int = 128
    public override val size: Int = 283

    private val _a: ByteArray by lazy { BinHex.decodeToBin(
        "00000000" +
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
                "00000000" +
                "00000001"
    ) }

    private val _G: ByteArray by lazy { BinHex.decodeToBin(
        "02" +
                "0503213F" +
                "78CA4488" +
                "3F1A3B81" +
                "62F188E5" +
                "53CD265F" +
                "23C1567A" +
                "16876913" +
                "B0C2AC24" +
                "58492836"
    ) }

    private val _Gc: ByteArray by lazy { BinHex.decodeToBin(
        "04" +
                "0503213F" +
                "78CA4488" +
                "3F1A3B81" +
                "62F188E5" +
                "53CD265F" +
                "23C1567A" +
                "16876913" +
                "B0C2AC24" +
                "58492836" +
                "01CCDA38" +
                "0F1C9E31" +
                "8D90F95D" +
                "07E5426F" +
                "E87E45C0" +
                "E8184698" +
                "E4596236" +
                "4E341161" +
                "77DD2259"
    ) }

    private val _n: ByteArray by lazy { BinHex.decodeToBin(
        "01FFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFE9AE" +
                "2ED07577" +
                "265DFF7F" +
                "94451E06" +
                "1E163C61"
    ) }

    private val _h: ByteArray = BinHex.decodeToBin(
        "04"
    )

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