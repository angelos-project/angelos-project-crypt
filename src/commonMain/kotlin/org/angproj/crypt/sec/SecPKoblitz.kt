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

import org.angproj.aux.util.floorMod
import org.angproj.aux.num.unsignedBigIntOf
import org.angproj.crypt.ec.EcPoint

public interface SecPKoblitz: Curves<PrimeDomainParameters> {
    public val p: ByteArray

    public companion object {
        public fun build(curve: SecPKoblitz): PrimeDomainParameters = PrimeDomainParameters(
            unsignedBigIntOf(curve.p)
        ).also { q ->
            q.setup(
                unsignedBigIntOf(curve.a),
                unsignedBigIntOf(curve.b),
                splitXY(curve.G),
                unsignedBigIntOf(curve.n),
                unsignedBigIntOf(curve.h)
            )
        }

        public fun splitXY(data: ByteArray): EcPoint {
            check((data.size - 1).floorMod(2) == 0)
            check(data[0].toInt() == 4)

            val size = (data.size - 1) / 2
            return EcPoint(
                unsignedBigIntOf(data.copyOfRange(1, 1 + size)),
                unsignedBigIntOf(data.copyOfRange(1 + size, data.size))
            )
        }
    }
}