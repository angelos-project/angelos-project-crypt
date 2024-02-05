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

public object Sect409Random1 : SecTRandom {
    public val name: String = "sect409r1"
    public override val strength: Int = 192
    public override val size: Int = 409

    private val _a: BigInt by fromHex {
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _b: BigInt by fromHex {
        "0021A5C2" +
                "C8EE9FEB" +
                "5C4B9A75" +
                "3B7B476B" +
                "7FD6422E" +
                "F1F3DD67" +
                "4761FA99" +
                "D6AC27C8" +
                "A9A197B2" +
                "72822F6C" +
                "D57A55AA" +
                "4F50AE31" +
                "7B13545F"
    }

    private val _S: BigInt by fromHex {
        "4099B5A4" +
                "57F9D69F" +
                "79213D09" +
                "4C4BCD4D" +
                "4262210B"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "015D4860" +
                "D088DDB3" +
                "496B0C60" +
                "64756260" +
                "441CDE4A" +
                "F1771D4D" +
                "B01FFE5B" +
                "34E59703" +
                "DC255A86" +
                "8A118051" +
                "5603AEAB" +
                "60794E54" +
                "BB7996A7"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "015D4860" +
                "D088DDB3" +
                "496B0C60" +
                "64756260" +
                "441CDE4A" +
                "F1771D4D" +
                "B01FFE5B" +
                "34E59703" +
                "DC255A86" +
                "8A118051" +
                "5603AEAB" +
                "60794E54" +
                "BB7996A7" +
                "0061B1CF" +
                "AB6BE5F3" +
                "2BBFA783" +
                "24ED106A" +
                "7636B9C5" +
                "A7BD198D" +
                "0158AA4F" +
                "5488D08F" +
                "38514F1F" +
                "DF4B4F40" +
                "D2181B36" +
                "81C364BA" +
                "0273C706"
    }

    private val _n: BigInt by fromHex {
        "01000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "000001E2" +
                "AAD6A612" +
                "F33307BE" +
                "5FA47C3C" +
                "9E052F83" +
                "8164CD37" +
                "D9A21173"
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