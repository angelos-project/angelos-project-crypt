package org.bouncycastle.crypto.params

import org.bouncycastle.math.ec.ECPoint

public class ECPublicKeyParameters
    (
    q: ECPoint?,
    parameters: ECDomainParameters
) : ECKeyParameters(false, parameters) {
    private val q: ECPoint = parameters.validatePublicPoint(q)

    public fun getQ(): ECPoint {
        return q
    }
}
