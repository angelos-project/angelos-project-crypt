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

public object Secp256Random1 : SecPRandom {
    override val name: String = "secp256r1"
    override val strength: Int = 128
    override val size: Int = 256

    override val p: ByteArray by octets {
        "FFFFFFFF" + "00000001" + "00000000" + "00000000" +
                "00000000" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF"
    }

    override val a: ByteArray by octets {
        "FFFFFFFF" + "00000001" + "00000000" + "00000000" +
                "00000000" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFC"
    }

    override val b: ByteArray by octets {
        "5AC635D8" + "AA3A93E7" + "B3EBBD55" + "769886BC" +
                "651D06B0" + "CC53B0F6" + "3BCE3C3E" + "27D2604B"
    }

    override val S: ByteArray by octets {
        "C49D3608" + "86E70493" + "6A6678E1" + "139D26B7" + "819F7E90"
    }

    override val Gc: ByteArray by octets {
        "03" +
                "6B17D1F2" + "E12C4247" + "F8BCE6E5" + "63A440F2" +
                "77037D81" + "2DEB33A0" + "F4A13945" + "D898C296"
    }

    override val G: ByteArray by octets {
        "04" +
                "6B17D1F2" + "E12C4247" + "F8BCE6E5" + "63A440F2" +
                "77037D81" + "2DEB33A0" + "F4A13945" + "D898C296" +
                "4FE342E2" + "FE1A7F9B" + "8EE7EB4A" + "7C0F9E16" +
                "2BCE3357" + "6B315ECE" + "CBB64068" + "37BF51F5"
    }

    override val n: ByteArray by octets {
        "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF" +
                "BCE6FAAD" + "A7179E84" + "F3B9CAC2" + "FC632551"
    }

    override val h: ByteArray by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPRandom.build(this) }
}