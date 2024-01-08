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

public object Secp192Random1 : SecPRandom {
    public override val name: String = "secp192r1"
    public override val strength: Int = 96
    public override val size: Int = 192

    private val _p: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "FFFFFFFF"
    }

    private val _a: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "FFFFFFFC"
    }

    private val _b: BigInt by fromHex {
        "64210519" +
                "E59C80E7" +
                "0FA7E9AB" +
                "72243049" +
                "FEB8DEEC" +
                "C146B9B1"
    }

    private val _S: BigInt by fromHex {
        "3045AE6F" +
                "C8422F64" +
                "ED579528" +
                "D38120EA" +
                "E12196D5"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "188DA80E" +
                "B03090F6" +
                "7CBF20EB" +
                "43A18800" +
                "F4FF0AFD" +
                "82FF1012"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "188DA80E" +
                "B03090F6" +
                "7CBF20EB" +
                "43A18800" +
                "F4FF0AFD" +
                "82FF1012" +
                "07192B95" +
                "FFC8DA78" +
                "631011ED" +
                "6B24CDD5" +
                "73F977A1" +
                "1E794811"
    }

    private val _n: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "99DEF836" +
                "146BC9B1" +
                "B4D22831"
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