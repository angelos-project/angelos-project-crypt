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

public object Sect163Random1 : SecTRandom {
    public val name: String = "sect163r1"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: BigInt by fromHex {
        "07" +
                "B6882CAA" +
                "EFA84F95" +
                "54FF8428" +
                "BD88E246" +
                "D2782AE2"
    }

    private val _b: BigInt by fromHex {
        "07" +
                "13612DCD" +
                "DCB40AAB" +
                "946BDA29" +
                "CA91F73A" +
                "F958AFD9"
    }

    private val _S: BigInt by fromHex {
        "24B7B137" +
                "C8A14D69" +
                "6E676875" +
                "6151756F" +
                "D0DA2E5C"
    }

    private val _G: BigInt by fromHex {
        "0303" +
                "69979697" +
                "AB438977" +
                "89566789" +
                "567F787A" +
                "7876A654"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "040369" +
                "979697AB" +
                "43897789" +
                "56678956" +
                "7F787A78" +
                "76A65400" +
                "435EDB42" +
                "EFAFB298" +
                "9D51FEFC" +
                "E3C80988" +
                "F41FF883"
    }

    private val _n: BigInt by fromHex {
        "03" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFF48AA" +
                "B689C29C" +
                "A710279B"
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