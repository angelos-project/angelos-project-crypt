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

public object Sect571Random1 : SecTRandom {
    public override val name: String = "sect571r1"
    public override val strength: Int = 256
    public override val size: Int = 571

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
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _b: BigInt by fromHex {
        "02F40E7E" +
                "2221F295" +
                "DE297117" +
                "B7F3D62F" +
                "5C6A97FF" +
                "CB8CEFF1" +
                "CD6BA8CE" +
                "4A9A18AD" +
                "84FFABBD" +
                "8EFA5933" +
                "2BE7AD67" +
                "56A66E29" +
                "4AFD185A" +
                "78FF12AA" +
                "520E4DE7" +
                "39BACA0C" +
                "7FFEFF7F" +
                "2955727A"
    }

    private val _S: BigInt by fromHex {
        "2AA058F7" +
                "3A0E33AB" +
                "486B0F61" +
                "0410C53A" +
                "7F132310"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "0303001D" +
                "34B85629" +
                "6C16C0D4" +
                "0D3CD775" +
                "0A93D1D2" +
                "955FA80A" +
                "A5F40FC8" +
                "DB7B2ABD" +
                "BDE53950" +
                "F4C0D293" +
                "CDD711A3" +
                "5B67FB14" +
                "99AE6003" +
                "8614F139" +
                "4ABFA3B4" +
                "C850D927" +
                "E1E7769C" +
                "8EEC2D19"
    }

    private val _Gc: BigInt by fromHex {
        "04" +
                "0303001D" +
                "34B85629" +
                "6C16C0D4" +
                "0D3CD775" +
                "0A93D1D2" +
                "955FA80A" +
                "A5F40FC8" +
                "DB7B2ABD" +
                "BDE53950" +
                "F4C0D293" +
                "CDD711A3" +
                "5B67FB14" +
                "99AE6003" +
                "8614F139" +
                "4ABFA3B4" +
                "C850D927" +
                "E1E7769C" +
                "8EEC2D19" +
                "037BF273" +
                "42DA639B" +
                "6DCCFFFE" +
                "B73D69D7" +
                "8C6C27A6" +
                "009CBBCA" +
                "1980F853" +
                "3921E8A6" +
                "84423E43" +
                "BAB08A57" +
                "6291AF8F" +
                "461BB2A8" +
                "B3531D2F" +
                "0485C19B" +
                "16E2F151" +
                "6E23DD3C" +
                "1A4827AF" +
                "1B8AC15B"
    }

    private val _n: BigInt by fromHex {
        "03FFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "E661CE18" +
                "FF559873" +
                "08059B18" +
                "6823851E" +
                "C7DD9CA1" +
                "161DE93D" +
                "5174D66E" +
                "8382E9BB" +
                "2FE84E47"
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
    override val Gc: BigInt
        get() = _Gc.copyOf()
    override val n: BigInt
        get() = _n.copyOf()
    override val h: BigInt
        get() = _h.copyOf()
}