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

public interface SecDomainParameters: DomainParameters {
    public val strength: Int
    public val size: Int

    public val a: BigInt
    public val b: BigInt
    public val G: BigInt
    public val Gc: BigInt
    public val n: BigInt
    public val h: BigInt
}