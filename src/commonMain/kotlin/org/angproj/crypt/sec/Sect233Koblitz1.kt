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

public object Sect233Koblitz1 : SecTKoblitz {
    public override val name: String = "sect233k1"
    public override val strength: Int = 112
    public override val size: Int = 233

    private val _a: ByteArray by lazy { BinHex.decodeToBin(
        "0000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000"
    ) }

    private val _b: ByteArray by lazy { BinHex.decodeToBin(
        "0000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    ) }

    private val _G: ByteArray by lazy { BinHex.decodeToBin(
        "020172" +
                "32BA853A" +
                "7E731AF1" +
                "29F22FF4" +
                "149563A4" +
                "19C26BF5" +
                "0A4C9D6E" +
                "EFAD6126"
    ) }

    private val _Gc: ByteArray by lazy { BinHex.decodeToBin(
        "04" +
                "017232BA" +
                "853A7E73" +
                "1AF129F2" +
                "2FF41495" +
                "63A419C2" +
                "6BF50A4C" +
                "9D6EEFAD" +
                "612601DB" +
                "537DECE8" +
                "19B7F70F" +
                "555A67C4" +
                "27A8CD9B" +
                "F18AEB9B" +
                "56E0C110" +
                "56FAE6A3"
    ) }

    private val _n: ByteArray by lazy { BinHex.decodeToBin(
        "80" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00069D5B" +
                "B915BCD4" +
                "6EFB1AD5" +
                "F173ABDF"
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