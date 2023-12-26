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
package org.angproj.crypt.keccak

import org.angproj.aux.util.EndianAware

// https://github.com/brainhub/SHA3IUF/blob/master/sha3.c
// https://github.com/komputing/KHash/blob/master/keccak/src/commonMain/kotlin/org/komputing/khash/keccak/Keccak.kt

// https://csrc.nist.gov/Projects/cryptographic-algorithm-validation-program/Secure-Hashing
// https://csrc.nist.gov/pubs/fips/202/final

// https://github.com/AdoptOpenJDK/openjdk-jdk11/blob/master/src/java.base/share/classes/sun/security/provider/SHA3.java

// https://github.com/mchrapek/sha3-java

internal abstract class AbstractKeccakHashEngine: KeccakHashEngine, EndianAware {
}