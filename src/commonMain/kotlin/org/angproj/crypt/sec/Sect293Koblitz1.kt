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

public object Sect293Koblitz1 : SecTKoblitz {
    public override val name: String = "sect293k1"
    public override val strength: Int = 115
    public override val size: Int = 293

    private val _a: BigInt by fromHex {
        "0000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000"
    }

    private val _b: BigInt by fromHex {
        "0000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _G: BigInt by fromHex {
        "0329A0" +
                "B6A887A9" +
                "83E97309" +
                "88A68727" +
                "A8B2D126" +
                "C44CC2CC" +
                "7B2A6555" +
                "193035DC"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "29A0B6A8" +
                "87A983E9" +
                "730988A6" +
                "8727A8B2" +
                "D126C44C" +
                "C2CC7B2A" +
                "65551930" +
                "35DC7631" +
                "0804F12E" +
                "549BDB01" +
                "1C103089" +
                "E73510AC" +
                "B275FC31" +
                "2A5DC6B7" +
                "6553F0CA"
    }

    private val _n: BigInt by fromHex {
        "2000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "005A79FE" +
                "C67CB6E9" +
                "1F1C1DA8" +
                "00E478A5"
    }

    private val _h: BigInt by fromHex {
        "04"
    }

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