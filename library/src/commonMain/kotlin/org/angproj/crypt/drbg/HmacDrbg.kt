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

// https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-90Ar1.pdf

public interface HmacDrbg {

    public val securityStrength: Int

    public val predictionResistanceFlag: Boolean

    public val maxNumberOfBitsPerRequest: Int

    public val maxAdditionalInputLength: Int

    public val reseedRequiredFlag: Boolean

    public val reseedInterval: Int

    public fun reseed(
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray
    )

    public fun generate(
        requestedNumberOfBits: Int,
        requestedSecurityStrength: Int,
        predictionResistanceRequest: Boolean,
        additionalInput: ByteArray = byteArrayOf()
    ): ByteArray

    public fun checkHealth()
}

/**
 *
 * Security_strength_of_output = min(output_length, DRBG_security_strength).
 *
 * security_strength
 * state_handle
 * reseed_required_flag
 * reseed_interval
 * reseed_counter
 *
 * working_state
 *
 * highest_supported_security_strength
 * max_personalization_string_length
 * max_additional_input_length
 * max_number_of_bits_per_request
 *
 * ERROR_FLAG
 * CATASTROPHIC_ERROR_FLAG
 *
 * Get_entropy_input
 *
 * min_entropy
 * min_length
 * max_length
 * prediction_resistance_request
 *
 * status
 * entropy_input
 *
 * Instantiate_function
 *
 * requested_instantiation_security_strength
 * prediction_resistance_flag
 * personalization_string
 *
 * entropy_input
 * nonce
 *
 * status
 * state_handle
 *
 * Instantiate_algorithm
 *
 * entropy_input
 * nonce
 * personalization_string
 * security_strength
 *
 * initial_working_state
 *
 * Reseed_function
 *
 * state_handle
 * prediction_resistance_request
 * additional_input
 *
 * status
 *
 * Get_entropy_input
 *
 * security_strength
 * min_length
 * max_length
 *
 * status
 * entropy_input
 *
 * Get_entropy_input
 *
 * security_strength
 * min_length
 * max_length
 * prediction_resistance_request
 *
 * status
 * entropy_input
 *
 * Reseed_algorithm
 *
 * working_state
 * entropy_input
 * additional_input
 *
 * new_working_state
 *
 * Generate_function
 *
 * state_handle
 * requested_number_of_bits
 * requested_security_strength
 * prediction_resistance_request
 * additional_input
 *
 * status
 * pseudorandom_bits
 *
 * Reseed_function
 *
 * state_handle
 * additional_input
 *
 * status
 *
 * Generate_algorithm
 *
 * status
 * pseudorandom_bits
 * new_working_state
 *
 * working_state
 * requested_number_of_bits
 *
 * Reseed_function
 *
 * state_handle
 * prediction_resistance_request
 * additional_input
 *
 * status
 *
 * Generate_algorithm
 *
 * working_state
 * requested_number_of_bits
 * additional_input
 *
 * status
 * pseudorandom_bits
 * new_working_state
 *
 * Uninstantiate_function
 *
 * state_handle
 *
 * status
 *
 *  DRBG_HMAC
 *
 *  outlen
 *  seed_material
 *
 *  HMAC_DRBG_Update
 *
 *  provided_data
 *  K
 *  V
 *
 *  K
 *  V
 *
 *  HMAC_DRBG_Instantiate_algorithm
 *
 *  entropy_input
 *  nonce
 *  personalization_string
 *  security_strength
 *
 *  initial_working_state
 *
 *  HMAC_DRBG_Reseed_algorithm
 *
 *  working_state
 *  entropy_input
 *  additional_input
 *
 *  new_working_state:
 *
 *  HMAC_DRBG_Generate_algorithm
 *
 *  working_state
 *  requested_number_of_bits
 *  additional_input
 *
 *  status
 *  returned_bits
 *  new
 * _working_state
 *
 * */