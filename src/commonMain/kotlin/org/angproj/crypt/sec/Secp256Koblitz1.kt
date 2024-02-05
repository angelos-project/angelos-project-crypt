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

public object Secp256Koblitz1 : SecPKoblitz {
    override val name: String = "secp256k1"
    override val strength: Int = 128
    override val size: Int = 256

    override val p: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE" + "FFFFFC2F"
    }

    override val a: OctetString by octets {
        "00000000" + "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000000" + "00000000"
    }

    override val b: OctetString by octets {
        "00000000" + "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000000" + "00000007"
    }

    override val Gc: OctetString by octets {
        "02" +
                "79BE667E" + "F9DCBBAC" + "55A06295" + "CE870B07" +
                "029BFCDB" + "2DCE28D9" + "59F2815B" + "16F81798"
    }

    override val G: OctetString by octets {
        "04" +
                "79BE667E" + "F9DCBBAC" + "55A06295" + "CE870B07" +
                "029BFCDB" + "2DCE28D9" + "59F2815B" + "16F81798" +
                "483ADA77" + "26A3C465" + "5DA4FBFC" + "0E1108A8" +
                "FD17B448" + "A6855419" + "9C47D08F" + "FB10D4B8"
    }

    override val n: OctetString by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFE" +
                "BAAEDCE6" + "AF48A03B" + "BFD25E8C" + "D0364141"
    }

    override val h: OctetString by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPKoblitz.build(this) }
}