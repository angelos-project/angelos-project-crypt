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

public object Secp192Random1 : SecPRandom {
    override val name: String = "secp192r1"
    override val strength: Int = 96
    override val size: Int = 192
    override val digestSize: Int = 20

    override val p: ByteArray by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF"
    }

    override val a: ByteArray by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFC"
    }

    override val b: ByteArray by octets {
        "64210519" + "E59C80E7" + "0FA7E9AB" +
                "72243049" + "FEB8DEEC" + "C146B9B1"
    }

    override val S: ByteArray by octets {
        "3045AE6F" + "C8422F64" + "ED579528" + "D38120EA" + "E12196D5"
    }

    override val Gc: ByteArray by octets {
        "03" +
                "188DA80E" + "B03090F6" + "7CBF20EB" +
                "43A18800" + "F4FF0AFD" + "82FF1012"
    }

    override val G: ByteArray by octets {
        "04" +
                "188DA80E" + "B03090F6" + "7CBF20EB" + "43A18800" +
                "F4FF0AFD" + "82FF1012" +
                "07192B95" + "FFC8DA78" +
                "631011ED" + "6B24CDD5" + "73F977A1" + "1E794811"
    }

    override val n: ByteArray by octets {
        "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" +
                "99DEF836" + "146BC9B1" + "B4D22831"
    }

    override val h: ByteArray by octets { "01" }

    override val domainParameters: PrimeDomainParameters by lazy { SecPRandom.build(this) }
}