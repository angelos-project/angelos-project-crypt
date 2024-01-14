package org.bouncycastle.crypto.params

import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.math.ec.ECConstants
import org.bouncycastle.math.ec.ECCurve
import org.bouncycastle.math.ec.ECPoint

public class ECNamedDomainParameters

    : ECDomainParameters {
    private var name: ASN1ObjectIdentifier?

    public constructor(name: ASN1ObjectIdentifier?, curve: ECCurve?, G: ECPoint?, n: java.math.BigInteger?) : this(
        name,
        curve,
        G,
        n,
        ECConstants.ONE,
        null
    )

    public constructor(
        name: ASN1ObjectIdentifier?,
        curve: ECCurve?,
        G: ECPoint?,
        n: java.math.BigInteger?,
        h: java.math.BigInteger?
    ) : this(name, curve, G, n, h, null)

    public constructor(
        name: ASN1ObjectIdentifier?,
        curve: ECCurve?,
        G: ECPoint?,
        n: java.math.BigInteger?,
        h: java.math.BigInteger?,
        seed: ByteArray?
    ) : super(curve, G, n, h, seed) {
        this.name = name
    }

    public constructor(
        name: ASN1ObjectIdentifier?,
        domainParameters: ECDomainParameters
    ) : super(
        domainParameters.getCurve(),
        domainParameters.getG(),
        domainParameters.getN(),
        domainParameters.getH(),
        domainParameters.getSeed()
    ) {
        this.name = name
    }

    public constructor(name: ASN1ObjectIdentifier?, x9: X9ECParameters?) : super(x9) {
        this.name = name
    }

    public fun getName(): ASN1ObjectIdentifier? {
        return name
    }
}
