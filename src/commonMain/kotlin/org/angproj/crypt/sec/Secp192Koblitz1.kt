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

public object Secp192Koblitz1 : SecPKoblitz {
    override val name: String = "secp192k1"
    override val strength: Int = 96
    override val size: Int = 192

    override val p: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFE" + "FFFFEE37"
    }

    override val a: OctetString by octets {
        "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000000"
    }

    override val b: OctetString by octets {
        "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000003"
    }

    override val Gc: OctetString by octets {
        "03" +
                "DB4FF10E" + "C057E9AE" + "26B07D02" +
                "80B7F434" + "1DA5D1B1" + "EAE06C7D"
    }

    override val G: OctetString by octets {
        "04" +
                "DB4FF10E" + "C057E9AE" + "26B07D02" + "80B7F434" +
                "1DA5D1B1" + "EAE06C7D" + "9B2F2F6D" + "9C5628A7" +
                "844163D0" + "15BE8634" + "4082AA88" + "D95E2F9D"
    }

    override val n: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE" +
                "26F2FC17" + "0F69466A" + "74DEFD8D"
    }

    override val h:OctetString by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPKoblitz.build(this) }
}