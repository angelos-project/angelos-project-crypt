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

public object Secp384Random1 : SecPRandom {
    public override val name: String = "secp384r1"
    public override val strength: Int = 192
    public override val size: Int = 384

    private val _p: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "00000000" +
                "00000000" +
                "FFFFFFFF"
    }

    private val _a: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "00000000" +
                "00000000" +
                "FFFFFFFC"
    }

    private val _b: BigInt by fromHex {
        "B3312FA7" +
                "E23EE7E4" +
                "988E056B" +
                "E3F82D19" +
                "181D9C6E" +
                "FE814112" +
                "0314088F" +
                "5013875A" +
                "C656398D" +
                "8A2ED19D" +
                "2A85C8ED" +
                "D3EC2AEF"
    }

    private val _S: BigInt by fromHex {
        "A335926A" +
                "A319A27A" +
                "1D00896A" +
                "6773A482" +
                "7ACDAC73"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "AA87CA22" +
                "BE8B0537" +
                "8EB1C71E" +
                "F320AD74" +
                "6E1D3B62" +
                "8BA79B98" +
                "59F741E0" +
                "82542A38" +
                "5502F25D" +
                "BF55296C" +
                "3A545E38" +
                "72760AB7"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "AA87CA22" +
                "BE8B0537" +
                "8EB1C71E" +
                "F320AD74" +
                "6E1D3B62" +
                "8BA79B98" +
                "59F741E0" +
                "82542A38" +
                "5502F25D" +
                "BF55296C" +
                "3A545E38" +
                "72760AB7" +
                "3617DE4A" +
                "96262C6F" +
                "5D9E98BF" +
                "9292DC29" +
                "F8F41DBD" +
                "289A147C" +
                "E9DA3113" +
                "B5F0B8C0" +
                "0A60B1CE" +
                "1D7E819D" +
                "7A431D7C" +
                "90EA0E5F"
    }

    private val _n: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "C7634D81" +
                "F4372DDF" +
                "581A0DB2" +
                "48B0A77A" +
                "ECEC196A" +
                "CCC52973"
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