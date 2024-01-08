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

public object Secp224Koblitz1 : SecPKoblitz {
    public override val name: String = "secp224k1"
    public override val strength: Int = 112
    public override val size: Int = 224

    private val _p: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFE56D"
    }

    private val _a: BigInt by fromHex {
        "00000000" +
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
                "00000000" +
                "00000005"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "A1455B33" +
                "4DF099DF" +
                "30FC28A1" +
                "69A467E9" +
                "E47075A9" +
                "0F7E650E" +
                "B6B7A45C"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "A1455B33" +
                "4DF099DF" +
                "30FC28A1" +
                "69A467E9" +
                "E47075A9" +
                "0F7E650E" +
                "B6B7A45C" +
                "7E089FED" +
                "7FBA3442" +
                "82CAFBD6" +
                "F7E319F7" +
                "C0B0BD59" +
                "E2CA4BDB" +
                "556D61A5"
    }

    private val _n: BigInt by fromHex {
        "01" +
                "00000000" +
                "00000000" +
                "00000000" +
                "0001DCE8" +
                "D2EC6184" +
                "CAF0A971" +
                "769FB1F7"
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