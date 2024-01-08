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

public object Sect163Koblitz1 : SecTKoblitz {
    public override val name: String = "sect163k1"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: BigInt by fromHex {
        "000" +
                "0000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _b: BigInt by fromHex {
        "00" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _G: BigInt by fromHex {
        "0302" +
                "FE13C053" +
                "7BBC11AC" +
                "AA07D793" +
                "DE4E6D5E" +
                "5C94EEE8"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "0402FE" +
                "13C0537B" +
                "BC11ACAA" +
                "07D793DE" +
                "4E6D5E5C" +
                "94EEE802" +
                "89070FB0" +
                "5D38FF58" +
                "321F2E80" +
                "0536D538" +
                "CCDAA3D9"
    }

    private val _n: BigInt by fromHex {
        "04" +
                "00000000" +
                "00000000" +
                "00020108" +
                "A2E0CC0D" +
                "99F8A5EF"
    }

    private val _h: BigInt by fromHex {
        "02"
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