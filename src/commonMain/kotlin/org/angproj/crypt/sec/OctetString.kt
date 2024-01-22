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
import org.angproj.aux.util.bigIntOf
import org.angproj.aux.util.encodeToHex
import kotlin.jvm.JvmInline

@JvmInline
public value class OctetString(internal val octets: ByteArray) {

    public val value: ByteArray
        get() = octets.copyOf()

    /**
     * Octet-String-to-Integer Conversion
     * Converts a wrapped ByteArray to a BigInt according to section 2.3.8 on p. 14 in
     * */
    /*public fun toInteger(): BigInt = bigIntOf(byteArrayOf(0) + octets)

    public fun asHex(): String = encodeToHex(octets)*/
}

/**
 * Integer-to-Octet-String Conversion
 * Converts a BigInt to a ByteArray value according to section 2.3.7 on p. 13 in
 * */

/*public fun BigInt.toRawOctetString(size: Int): ByteArray {
    check(this.sigNum.isNonNegative()) {
        "Can not convert a negative big integer, positive or zero is always assumed." }
    val octet = this.toZeroFilledByteArray(size)
    check(octet.size <= size) { "Value must be lesser than 2^(8 * $size)." }
    return octet
}*/

/**
 * Integer-to-Octet-String Conversion
 * Converts a BigInt to a OctetString according to section 2.3.7 on p. 13 in
 * */
//public fun BigInt.toOctetString(size: Int = 32): OctetString = OctetString(toRawOctetString(size))