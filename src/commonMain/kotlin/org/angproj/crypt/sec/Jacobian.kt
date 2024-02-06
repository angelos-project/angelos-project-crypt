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

public interface Jacobian {
    public fun FieldElement.add(b: FieldElement): FieldElement

    public fun FieldElement.addOne(): FieldElement

    public fun FieldElement.subtract(b: FieldElement): FieldElement

    public fun FieldElement.multiply(b: FieldElement): FieldElement

    public fun FieldElement.divide(b: FieldElement): FieldElement

    public fun FieldElement.negate(): FieldElement

    public fun FieldElement.square(): FieldElement

    public fun FieldElement.invert(): FieldElement

    public fun FieldElement.sqrt(): FieldElement
}