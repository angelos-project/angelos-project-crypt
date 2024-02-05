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

import org.angproj.aux.util.BinHex

public interface Curves<E: AbstractDomainParameters> {
    public fun octets(block: () -> String): Lazy<OctetString> = lazy { OctetString(BinHex.decodeToBin(block())) }

    public val name: String
    public val strength: Int
    public val size: Int

    public val a: OctetString

    public val b: OctetString

    public val Gc: OctetString

    public val G: OctetString

    public val n: OctetString

    public val h: OctetString

    public val domainParameters: E
}