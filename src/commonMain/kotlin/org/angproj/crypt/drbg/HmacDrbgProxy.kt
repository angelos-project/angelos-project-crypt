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

import org.angproj.aux.reg.RegistryProxy

public class HmacDrbgProxy(private val engine: HmacDrbgEngine): HmacDrbg, RegistryProxy {
    override val identifier: String
        get() = engine.identifier
    override val securityStrength: Int
        get() = engine.securityStrength
    override val predictionResistanceFlag: Boolean
        get() = engine.predictionResistanceFlag
    override val maxNumberOfBitsPerRequest: Int
        get() = engine.maxNumberOfBitsPerRequest
    override val maxAdditionalInputLength: Int
        get() = engine.maxAdditionalInputLength
    override val reseedRequiredFlag: Boolean
        get() = engine.reseedRequiredFlag
    override val reseedInterval: Int
        get() = engine.reseedInterval

    override fun reseed(predictionResistanceRequest: Boolean, additionalInput: ByteArray) {
        engine.reseed(predictionResistanceRequest, additionalInput)
    }

    override fun generate(
        requestedNumberOfBits: Int,
        requestedSecurityStrength: Int,
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    ): ByteArray {
        return engine.generate(
            requestedNumberOfBits, requestedSecurityStrength,
            predictionResistanceRequest, additionalInput
        )
    }

    override fun checkHealth() {
        TODO("Not yet implemented")
    }
}