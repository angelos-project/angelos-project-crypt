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

import org.angproj.aux.num.BigInt

public object Sect163Random2 : SecTRandom {
    public val name: String = "sect163r2"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: BigInt by fromHex {
        "00" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _b: BigInt by fromHex {
        "02" +
                "0A601907" +
                "B8C953CA" +
                "1481EB10" +
                "512F7874" +
                "4A3205FD"
    }

    private val _S: BigInt by fromHex {
        "85E25BFE" +
                "5C86226C" +
                "DB12016F" +
                "7553F9D0" +
                "E693A268"
    }

    private val _G: BigInt by fromHex {
        "0303" +
                "F0EBA162" +
                "86A2D57E" +
                "A0991168" +
                "D4994637" +
                "E8343E36"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "0403F0" +
                "EBA16286" +
                "A2D57EA0" +
                "991168D4" +
                "994637E8" +
                "343E3600" +
                "D51FBC6C" +
                "71A0094F" +
                "A2CDD545" +
                "B11C5C0C" +
                "797324F1"
    }

    private val _n: BigInt by fromHex {
        "04" +
                "00000000" +
                "00000000" +
                "000292FE" +
                "77E70C12" +
                "A4234C33"
    }

    private val _h: BigInt by fromHex {
        "02"
    }

    override val a: BigInt
        get() = _a.copyOf()
    override val b: BigInt
        get() = _b.copyOf()
    override val S: BigInt
        get() = _S.copyOf()
    override val G: BigInt
        get() = _G.copyOf()
    override val Gc: Pair<BigInt, BigInt>
        get() = _Gc.copy()
    override val n: BigInt
        get() = _n.copyOf()
    override val h: BigInt
        get() = _h.copyOf()
}