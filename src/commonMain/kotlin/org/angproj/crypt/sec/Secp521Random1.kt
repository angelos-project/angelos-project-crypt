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

public object Secp521Random1 : SecPRandom {
    public override val name: String = "secp521r1"
    public override val strength: Int = 256
    public override val size: Int = 521

    private val _p: BigInt by fromHex {
        "01FF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFF"
    }

    private val _a: BigInt by fromHex {
        "01FF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFC"
    }

    private val _b: BigInt by fromHex {
        "0051" +
                "953EB961" +
                "8E1C9A1F" +
                "929A21A0" +
                "B68540EE" +
                "A2DA725B" +
                "99B315F3" +
                "B8B48991" +
                "8EF109E1" +
                "56193951" +
                "EC7E937B" +
                "1652C0BD" +
                "3BB1BF07" +
                "3573DF88" +
                "3D2C34F1" +
                "EF451FD4" +
                "6B503F00"
    }

    private val _S: BigInt by fromHex {
        "D09E8800" +
                "291CB853" +
                "96CC6717" +
                "393284AA" +
                "A0DA64BA"
    }

    private val _G: BigInt by fromHex {
        "0200C6" +
                "858E06B7" +
                "0404E9CD" +
                "9E3ECB66" +
                "2395B442" +
                "9C648139" +
                "053FB521" +
                "F828AF60" +
                "6B4D3DBA" +
                "A14B5E77" +
                "EFE75928" +
                "FE1DC127" +
                "A2FFA8DE" +
                "3348B3C1" +
                "856A429B" +
                "F97E7E31" +
                "C2E5BD66"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "00C6858E" +
                "06B70404" +
                "E9CD9E3E" +
                "CB662395" +
                "B4429C64" +
                "8139053F" +
                "B521F828" +
                "AF606B4D" +
                "3DBAA14B" +
                "5E77EFE7" +
                "5928FE1D" +
                "C127A2FF" +
                "A8DE3348" +
                "B3C1856A" +
                "429BF97E" +
                "7E31C2E5" +
                "BD660118" +
                "39296A78" +
                "9A3BC004" +
                "5C8A5FB4" +
                "2C7D1BD9" +
                "98F54449" +
                "579B4468" +
                "17AFBD17" +
                "273E662C" +
                "97EE7299" +
                "5EF42640" +
                "C550B901" +
                "3FAD0761" +
                "353C7086" +
                "A272C240" +
                "88BE9476" +
                "9FD16650"
    }

    private val _n: BigInt by fromHex {
        "01FF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFA" +
                "51868783" +
                "BF2F966B" +
                "7FCC0148" +
                "F709A5D0" +
                "3BB5C9B8" +
                "899C47AE" +
                "BB6FB71E" +
                "91386409"
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