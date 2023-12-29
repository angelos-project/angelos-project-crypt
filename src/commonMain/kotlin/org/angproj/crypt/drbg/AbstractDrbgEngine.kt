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
package org.angproj.crypt.drbg

public abstract class AbstractDrbgEngine: DeterministicRandomBitGenerator {
    //public val securityStrength: Any
    //public val workingState: Any

    /**
     * A count of the number of requests produced since the instantiation was seeded or
     * reseeded.
     */
    //public val requestCount: Long = 0
}