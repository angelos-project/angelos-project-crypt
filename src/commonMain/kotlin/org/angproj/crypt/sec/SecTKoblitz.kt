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

public interface SecTKoblitz: SecDomainParameters {
    //public val p: ByteArray
    public override val a: ByteArray
    public override val b: ByteArray
    public override val G: ByteArray
    public override val Gc: ByteArray
    public override val n: ByteArray
    public override val h: ByteArray
}