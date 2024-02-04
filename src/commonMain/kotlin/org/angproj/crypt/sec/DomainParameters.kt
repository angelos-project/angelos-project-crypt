/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.num.BigInt

public interface DomainParameters {
    public var a: FieldElement /* two elements a, b ∈ Fp specifying an elliptic curve E(Fp). */
    public var b: FieldElement
    public var G: EllipticCurvePoint /* A base point G = (xG, yG) on E(Fp). */
    public var n: BigInt /* A prime n which is the order of G. */
    public var h: BigInt /* An integer h which is the cofactor h = #E(Fp)/n. */
}