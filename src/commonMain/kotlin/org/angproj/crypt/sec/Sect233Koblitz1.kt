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

public object Sect233Koblitz1 : SecTKoblitz {
    public override val name: String = "sect233k1"
    public override val strength: Int = 112
    public override val size: Int = 233

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
        "020172" +
                "32BA853A" +
                "7E731AF1" +
                "29F22FF4" +
                "149563A4" +
                "19C26BF5" +
                "0A4C9D6E" +
                "EFAD6126"
    }

    private val _Gc: Pair<BigInt, BigInt> by xyFromHex {
        "04" +
                "017232BA" +
                "853A7E73" +
                "1AF129F2" +
                "2FF41495" +
                "63A419C2" +
                "6BF50A4C" +
                "9D6EEFAD" +
                "612601DB" +
                "537DECE8" +
                "19B7F70F" +
                "555A67C4" +
                "27A8CD9B" +
                "F18AEB9B" +
                "56E0C110" +
                "56FAE6A3"
    }

    private val _n: BigInt by fromHex {
        "80" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00069D5B" +
                "B915BCD4" +
                "6EFB1AD5" +
                "F173ABDF"
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