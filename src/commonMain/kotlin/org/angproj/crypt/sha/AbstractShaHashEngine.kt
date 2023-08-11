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
package org.angproj.crypt.sha

abstract class AbstractShaHashEngine: ShaHashEngine {
    protected abstract val h: Any

    protected abstract val w: Any

    protected var lasting: ByteArray = ByteArray(0)

    protected var count = 0

    protected abstract fun truncate(hash: ByteArray): ByteArray
}