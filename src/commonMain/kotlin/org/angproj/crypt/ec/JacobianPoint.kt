/**
 * Copyright (c) 2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 * Copyright (c) 2019 Stark Bank S.A.
 *
 * This software is available under the terms of the MIT license.
 * The legal terms are attached to the LICENSE file and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Rafael Stark - original implementation
 *      Dalton Menezes - original implementation
 *      Caio Dottori - original implementation
 *      Thales Mello - original implementation
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.crypt.ec

import org.angproj.aux.num.BigInt
import org.angproj.aux.num.bigInt
import org.angproj.aux.util.NullObject

public data class JacobianPoint(
    override val x: BigInt,
    override val y: BigInt,
    public val z: BigInt
): Point

public fun JacobianPoint.isNull(): Boolean = NullObject.jacobianPoint === this

private val nullJacobianPoint = JacobianPoint(NullObject.bigInt, NullObject.bigInt, NullObject.bigInt)
public val NullObject.jacobianPoint: JacobianPoint
    get() = nullJacobianPoint