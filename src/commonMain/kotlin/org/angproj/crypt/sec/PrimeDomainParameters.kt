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

/**
 * sec1-v2.pdf -- 3.1.1 Elliptic Curve Domain Parameters over Fp.
 * */
public class PrimeDomainParameters(
    public val p: BigInt, /* An integer p specifying the finite field Fp. */
    public val a: FieldElement, /* two elements a, b ∈ Fp specifying an elliptic curve E(Fp). */
    public val b: FieldElement,
    public val G: EllipticCurvePoint, /* A base point G = (xG, yG) on E(Fp). */
    public val n: BigInt, /* A prime n which is the order of G. */
    public val h: BigInt /* An integer h which is the cofactor h = #E(Fp)/n. */
): DomainParameters