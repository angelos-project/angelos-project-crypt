/**
 * Copyright (c) 2023-2024 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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

import org.angproj.aux.util.reg.RegistryProxy

public class HmacDrbgProxy(private val engine: HmacDrbgEngine): HmacDrbg, RegistryProxy {
    override val identifier: String
        get() = engine.identifier
    override val securityStrength: Int
        get() = engine.securityStrength
    override val predictionResistanceFlag: Boolean
        get() = engine.predictionResistanceFlag
    override fun reseed(predictionResistanceRequest: Boolean, additionalInput: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun generate(
        requestedNumberOfBits: Int,
        requestedSecurityStrength: Int,
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ): ByteArray {
        TODO("Not yet implemented")
    }

    override fun checkHealth() {
        TODO("Not yet implemented")
    }
}