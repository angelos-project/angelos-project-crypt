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

public object Sect283Random1 : SecTRandom {
    public override val name: String = "sect283r1"
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
                "00000001"
    }

    private val _b: BigInt by fromHex {
        "027B680A" +
                "C8B8596D" +
                "A5A4AF8A" +
                "19A0303F" +
                "CA97FD76" +
                "45309FA2" +
                "A581485A" +
                "F6263E31" +
                "3B79A2F5"
    }

    private val _S: BigInt by fromHex {
        "77E2B073" +
                "70EB0F83" +
                "2A6DD5B6" +
                "2DFC88CD" +
                "06BB84BE"
    }

    private val _G: BigInt by fromHex {
        "03" +
                "05F93925" +
                "8DB7DD90" +
                "E1934F8C" +
                "70B0DFEC" +
                "2EED25B8" +
                "557EAC9C" +
                "80E2E198" +
                "F8CDBECD" +
                "86B12053"
    }

    private val _Gc: BigInt by fromHex {
        "04" +
                "05F93925" +
                "8DB7DD90" +
                "E1934F8C" +
                "70B0DFEC" +
                "2EED25B8" +
                "557EAC9C" +
                "80E2E198" +
                "F8CDBECD" +
                "86B12053" +
                "03676854" +
                "FE24141C" +
                "B98FE6D4" +
                "B20D02B4" +
                "516FF702" +
                "350EDDB0" +
                "826779C8" +
                "13F0DF45" +
                "BE8112F4"
    }

    private val _n: BigInt by fromHex {
        "03FFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFEF90" +
                "399660FC" +
                "938A9016" +
                "5B042A7C" +
                "EFADB307"
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