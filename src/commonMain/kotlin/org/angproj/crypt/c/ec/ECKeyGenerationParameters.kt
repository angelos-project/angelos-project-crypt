package org.bouncycastle.crypto.params

import org.bouncycastle.crypto.KeyGenerationParameters

public class ECKeyGenerationParameters
    (
    domainParams: ECDomainParameters,
    random: java.security.SecureRandom?
) : KeyGenerationParameters(random, domainParams.getN().bitLength()) {
    private val domainParams: ECDomainParameters = domainParams

    public val domainParameters: ECDomainParameters
        get() = domainParams
}
