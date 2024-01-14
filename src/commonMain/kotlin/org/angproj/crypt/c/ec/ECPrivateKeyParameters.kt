package org.bouncycastle.crypto.params

public class ECPrivateKeyParameters
    (
    d: java.math.BigInteger?,
    parameters: ECDomainParameters
) : ECKeyParameters(true, parameters) {
    private val d: java.math.BigInteger = parameters.validatePrivateScalar(d)

    public fun getD(): java.math.BigInteger {
        return d
    }
}
