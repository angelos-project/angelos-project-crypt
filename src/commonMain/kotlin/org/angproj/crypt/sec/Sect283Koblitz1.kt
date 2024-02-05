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

public object Sect283Koblitz1 : SecTKoblitz {
    public val name: String = "sect283k1"
    public override val strength: Int = 128
    public override val size: Int = 283

    private val _a: BigInt by fromHex {
        "00000000" +
                "00000000" +
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
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _G: BigInt by fromHex {
        "02" +
                "0503213F" +
                "78CA4488" +
                "3F1A3B81" +
                "62F188E5" +
                "53CD265F" +
                "23C1567A" +
                "16876913" +
                "B0C2AC24" +
                "58492836"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "0503213F" +
                "78CA4488" +
                "3F1A3B81" +
                "62F188E5" +
                "53CD265F" +
                "23C1567A" +
                "16876913" +
                "B0C2AC24" +
                "58492836" +
                "01CCDA38" +
                "0F1C9E31" +
                "8D90F95D" +
                "07E5426F" +
                "E87E45C0" +
                "E8184698" +
                "E4596236" +
                "4E341161" +
                "77DD2259"
    }

    private val _n: BigInt by fromHex {
        "01FFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFE9AE" +
                "2ED07577" +
                "265DFF7F" +
                "94451E06" +
                "1E163C61"
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