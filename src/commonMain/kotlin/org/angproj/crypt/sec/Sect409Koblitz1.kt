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

public object Sect409Koblitz1 : SecTKoblitz {
    public override val name: String = "sect409k1"
    public override val strength: Int = 192
    public override val size: Int = 409

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
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "0060F05F" +
                "658F49C1" +
                "AD3AB189" +
                "0F718421" +
                "0EFD0987" +
                "E307C84C" +
                "27ACCFB8" +
                "F9F67CC2" +
                "C460189E" +
                "B5AAAA62" +
                "EE222EB1" +
                "B35540CF" +
                "E9023746"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "0060F05F" +
                "658F49C1" +
                "AD3AB189" +
                "0F718421" +
                "0EFD0987" +
                "E307C84C" +
                "27ACCFB8" +
                "F9F67CC2" +
                "C460189E" +
                "B5AAAA62" +
                "EE222EB1" +
                "B35540CF" +
                "E9023746" +
                "01E36905" +
                "0B7C4E42" +
                "ACBA1DAC" +
                "BF04299C" +
                "3460782F" +
                "918EA427" +
                "E6325165" +
                "E9EA10E3" +
                "DA5F6C42" +
                "E9C55215" +
                "AA9CA27A" +
                "5863EC48" +
                "D8E0286B"
    }

    private val _n: BigInt by fromHex {
        "7FFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFE5F" +
                "83B2D4EA" +
                "20400EC4" +
                "557D5ED3" +
                "E3E7CA5B" +
                "4B5C83B8" +
                "E01E5FCF"
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