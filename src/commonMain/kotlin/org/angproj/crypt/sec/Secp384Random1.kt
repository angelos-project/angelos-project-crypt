/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

public object Secp384Random1 : SecPRandom {
    override val name: String = "secp384r1"
    override val strength: Int = 192
    override val size: Int = 384

    override val p: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE" +
                "FFFFFFFF" + "00000000" + "00000000" + "FFFFFFFF"
    }

    override val a: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE" +
                "FFFFFFFF" + "00000000" + "00000000" + "FFFFFFFC"
    }

    override val b: OctetString by octets {
        "B3312FA7" + "E23EE7E4" + "988E056B" + "E3F82D19" +
                "181D9C6E" + "FE814112" + "0314088F" + "5013875A" +
                "C656398D" + "8A2ED19D" + "2A85C8ED" + "D3EC2AEF"
    }

    override val S: OctetString by octets {
        "A335926A" + "A319A27A" + "1D00896A" + "6773A482" + "7ACDAC73"
    }

    override val Gc: OctetString by octets {
        "03" +
                "AA87CA22" + "BE8B0537" + "8EB1C71E" + "F320AD74" +
                "6E1D3B62" + "8BA79B98" + "59F741E0" + "82542A38" +
                "5502F25D" + "BF55296C" + "3A545E38" + "72760AB7"
    }

    override val G: OctetString by octets {
        "04" +
                "AA87CA22" + "BE8B0537" + "8EB1C71E" + "F320AD74" +
                "6E1D3B62" + "8BA79B98" + "59F741E0" + "82542A38" +
                "5502F25D" + "BF55296C" + "3A545E38" + "72760AB7" +
                "3617DE4A" + "96262C6F" + "5D9E98BF" + "9292DC29" +
                "F8F41DBD" + "289A147C" + "E9DA3113" + "B5F0B8C0" +
                "0A60B1CE" + "1D7E819D" + "7A431D7C" + "90EA0E5F"
    }

    override val n: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFF" + "C7634D81" + "F4372DDF" +
                "581A0DB2" + "48B0A77A" + "ECEC196A" + "CCC52973"
    }

    override val h: OctetString by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPRandom.build(this) }
}