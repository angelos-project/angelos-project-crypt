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

public object Secp224Koblitz1 : SecPKoblitz {
    override val name: String = "secp224k1"
    override val strength: Int = 112
    override val size: Int = 224

    override val p: OctetString by octets {
        "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFE" + "FFFFE56D"
    }

    override val a: OctetString by octets {
        "00000000" +
                "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000000"
    }

    override val b: OctetString by octets {
        "00000000" +
                "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000005"
    }

    override val Gc: OctetString by octets {
        "03" +
                "A1455B33" + "4DF099DF" + "30FC28A1" + "69A467E9" +
                "E47075A9" + "0F7E650E" + "B6B7A45C"
    }

    override val G: OctetString by octets {
        "04" +
                "A1455B33" + "4DF099DF" + "30FC28A1" + "69A467E9" + "E47075A9" +
                "0F7E650E" + "B6B7A45C" + "7E089FED" + "7FBA3442" + "82CAFBD6" +
                "F7E319F7" + "C0B0BD59" + "E2CA4BDB" + "556D61A5"
    }

    override val n: OctetString by octets {
        "01" +
                "00000000" + "00000000" + "00000000" + "0001DCE8" +
                "D2EC6184" + "CAF0A971" + "769FB1F7"
    }

    override val h: OctetString by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPKoblitz.build(this) }
}