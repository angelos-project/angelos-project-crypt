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

public object Secp224Random1 : SecPRandom {
    override val name: String = "secp224r1"
    override val strength: Int = 112
    override val size: Int = 224

    override val p: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "00000000" + "00000000" + "00000001"
    }

    override val a: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE" +
                "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE"
    }

    override val b: OctetString by octets {
        "B4050A85" + "0C04B3AB" + "F5413256" + "5044B0B7" +
                "D7BFD8BA" + "270B3943" + "2355FFB4"
    }

    override val S: OctetString by octets {
        "BD713447" + "99D5C7FC" + "DC45B59F" + "A3B9AB8F" + "6A948BC5"
    }

    override val Gc: OctetString by octets {
        "02" +
                "B70E0CBD" + "6BB4BF7F" + "321390B9" + "4A03C1D3" +
                "56C21122" + "343280D6" + "115C1D21"
    }

    override val G: OctetString by octets {
        "04" +
                "B70E0CBD" + "6BB4BF7F" + "321390B9" + "4A03C1D3" + "56C21122" +
                "343280D6" + "115C1D21" +
                "BD376388" + "B5F723FB" + "4C22DFE6" +
                "CD4375A0" + "5A074764" + "44D58199" + "85007E34"
    }

    override val n: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFF16A2" +
                "E0B8F03E" + "13DD2945" + "5C5C2A3D"
    }

    override val h: OctetString by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPRandom.build(this) }
}