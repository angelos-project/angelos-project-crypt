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

/**
 * sec1-v2.pdf -- 3.1.1 Elliptic Curve Domain Parameters over Fp.
 * */
public class PrimeDomainParameters(
    public val p: Integer /* An integer p specifying the finite field Fp. */
) : AbstractDomainParameters(), Jacobian {

    override fun FieldElement.add(b: FieldElement): FieldElement = Convention.add(this, b, this@PrimeDomainParameters)

    override fun FieldElement.addOne(): FieldElement = Convention.addOne(this, this@PrimeDomainParameters)

    override fun FieldElement.subtract(b: FieldElement): FieldElement = Convention.subtract(this, b, this@PrimeDomainParameters)

    override fun FieldElement.multiply(b: FieldElement): FieldElement = Convention.multiply(this, b, this@PrimeDomainParameters)

    override fun FieldElement.divide(b: FieldElement): FieldElement {
        TODO("Not yet implemented")
    }

    override fun FieldElement.negate(): FieldElement {
        TODO("Not yet implemented")
    }

    override fun FieldElement.square(): FieldElement {
        TODO("Not yet implemented")
    }

    override fun FieldElement.invert(): FieldElement {
        TODO("Not yet implemented")
    }

    override fun FieldElement.sqrt(): FieldElement {
        TODO("Not yet implemented")
    }
}