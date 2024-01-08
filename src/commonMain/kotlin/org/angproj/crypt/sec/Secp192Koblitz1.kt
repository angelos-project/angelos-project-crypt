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

public object Secp192Koblitz1 : SecPKoblitz {
    public override val name: String = "secp192k1"
    public override val strength: Int = 96
    public override val size: Int = 192

    private val _p: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFEE37"
    }

    private val _a: BigInt by fromHex {
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000"
    }

    private val _b: BigInt by fromHex {
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000003"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "DB4FF10E" +
                "C057E9AE" +
                "26B07D02" +
                "80B7F434" +
                "1DA5D1B1" +
                "EAE06C7D"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
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
    }

    private val _n: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "26F2FC17" +
                "0F69466A" +
                "74DEFD8D"
    }

    private val _h: BigInt by fromHex {
        "01"
    }

    override val p: BigInt
        get() = _p.copyOf()
    override val a: BigInt
        get() = _a.copyOf()
    override val b: BigInt
        get() = _b.copyOf()
    override val G: BigInt
        get() = _G.copyOf()
    override val Gc: Pair<BigInt, BigInt>
        get() = _Gc.copy()
    override val n: BigInt
        get() = _n.copyOf()
    override val h: BigInt
        get() = _h.copyOf()
}