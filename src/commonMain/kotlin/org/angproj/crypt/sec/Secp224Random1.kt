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

public object Secp224Random1 : SecPRandom {
    public override val name: String = "secp224r1"
    public override val strength: Int = 112
    public override val size: Int = 224

    private val _p: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "00000000" +
                "00000000" +
                "00000001"
    }

    private val _a: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE"
    }

    private val _b: BigInt by fromHex {
        "B4050A85" +
                "0C04B3AB" +
                "F5413256" +
                "5044B0B7" +
                "D7BFD8BA" +
                "270B3943" +
                "2355FFB4"
    }

    private val _S: BigInt by fromHex {
        "BD713447" +
                "99D5C7FC" +
                "DC45B59F" +
                "A3B9AB8F" +
                "6A948BC5"
    }

    private val _G: BigInt by fromHex {
        "02" +
                "B70E0CBD" +
                "6BB4BF7F" +
                "321390B9" +
                "4A03C1D3" +
                "56C21122" +
                "343280D6" +
                "115C1D21"
    }

    private val _Gc: BigInt by fromHex {
        "04" +
                "B70E0CBD" +
                "6BB4BF7F" +
                "321390B9" +
                "4A03C1D3" +
                "56C21122" +
                "343280D6" +
                "115C1D21" +
                "BD376388" +
                "B5F723FB" +
                "4C22DFE6" +
                "CD4375A0" +
                "5A074764" +
                "44D58199" +
                "85007E34"
    }

    private val _n: BigInt by fromHex {
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFF16A2" +
                "E0B8F03E" +
                "13DD2945" +
                "5C5C2A3D"
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
    override val Gc: BigInt
        get() = _Gc.copyOf()
    override val n: BigInt
        get() = _n.copyOf()
    override val h: BigInt
        get() = _h.copyOf()
}