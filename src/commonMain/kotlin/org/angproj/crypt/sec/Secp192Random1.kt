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

public object Secp192Random1 : SecPRandom {
    public override val name: String = "secp192r1"
    public override val strength: Int = 96
    public override val size: Int = 192

    private val _p: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "FFFFFFFF"
    ) }

    private val _a: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "FFFFFFFC"
    ) }

    private val _b: ByteArray by lazy { BinHex.decodeToBin(
        "64210519" +
                "E59C80E7" +
                "0FA7E9AB" +
                "72243049" +
                "FEB8DEEC" +
                "C146B9B1"
    ) }

    private val _S: ByteArray by lazy { BinHex.decodeToBin(
        "3045AE6F" +
                "C8422F64" +
                "ED579528" +
                "D38120EA" +
                "E12196D5"
    ) }

    private val _G: ByteArray by lazy { BinHex.decodeToBin(
        "03" +
                "188DA80E" +
                "B03090F6" +
                "7CBF20EB" +
                "43A18800" +
                "F4FF0AFD" +
                "82FF1012"
    ) }

    private val _Gc: ByteArray by lazy { BinHex.decodeToBin(
        "04" +
                "188DA80E" +
                "B03090F6" +
                "7CBF20EB" +
                "43A18800" +
                "F4FF0AFD" +
                "82FF1012" +
                "07192B95" +
                "FFC8DA78" +
                "631011ED" +
                "6B24CDD5" +
                "73F977A1" +
                "1E794811"
    ) }

    private val _n: ByteArray by lazy { BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "99DEF836" +
                "146BC9B1" +
                "B4D22831"
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