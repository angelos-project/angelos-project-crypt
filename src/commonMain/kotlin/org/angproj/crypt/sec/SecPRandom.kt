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

public interface SecPRandom: SecDomainParameters {
    public val p: BigInt
    public override val a: BigInt
    public override val b: BigInt
    public val S: BigInt
    public override val G: BigInt
    public override val Gc: BigInt
    public override val n: BigInt
    public override val h: BigInt
}