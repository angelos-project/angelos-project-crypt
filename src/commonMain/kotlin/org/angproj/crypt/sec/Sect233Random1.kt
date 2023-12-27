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

import org.angproj.crypt.number.BigInt

public object Sect233Random1 : SecTRandom {
    public override val name: String = "sect233r1"
    public override val strength: Int = 112
    public override val size: Int = 233

    private val _a: BigInt by fromHex {
        "0000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _b: BigInt by fromHex {
        "0066" +
                "647EDE6C" +
                "332C7F8C" +
                "0923BB58" +
                "213B333B" +
                "20E9CE42" +
                "81FE115F" +
                "7D8F90AD"
    }

    private val _S: BigInt by fromHex {
        "74D59FF0" +
                "7F6B413D" +
                "0EA14B34" +
                "4B20A2DB" +
                "049B50C3"
    }

    private val _G: BigInt by fromHex {
        "0300FA" +
                "C9DFCBAC" +
                "8313BB21" +
                "39F1BB75" +
                "5FEF65BC" +
                "391F8B36" +
                "F8F8EB73" +
                "71FD558B"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "00FAC9DF" +
                "CBAC8313" +
                "BB2139F1" +
                "BB755FEF" +
                "65BC391F" +
                "8B36F8F8" +
                "EB7371FD" +
                "558B0100" +
                "6A08A419" +
                "03350678" +
                "E58528BE" +
                "BF8A0BEF" +
                "F867A7CA" +
                "36716F7E" +
                "01F81052"
    }

    private val _n: BigInt by fromHex {
        "0100" +
                "00000000" +
                "00000000" +
                "00000000" +
                "0013E974" +
                "E72F8A69" +
                "22031D26" +
                "03CFE0D7"
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