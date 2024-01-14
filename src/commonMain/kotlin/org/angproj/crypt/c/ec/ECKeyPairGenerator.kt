package org.bouncycastle.crypto.generators

import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator
import org.bouncycastle.crypto.CryptoServicesRegistrar
import org.bouncycastle.crypto.KeyGenerationParameters
import org.bouncycastle.crypto.params.ECPrivateKeyParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters

public class ECKeyPairGenerator
protected constructor(private val name: String) : AsymmetricCipherKeyPairGenerator, ECConstants {
    public var params: ECDomainParameters? = null
    public var random: java.security.SecureRandom? = null

    public constructor() : this("ECKeyGen")

    public override fun init(
        param: KeyGenerationParameters
    ) {
        val ecP: ECKeyGenerationParameters = param as ECKeyGenerationParameters

        this.random = ecP.getRandom()
        this.params = ecP.getDomainParameters()

        CryptoServicesRegistrar.checkConstraints(
            DefaultServiceProperties(
                name, ConstraintUtils.bitsOfSecurityFor(
                    params.getCurve()
                ), ecP.getDomainParameters(), CryptoServicePurpose.KEYGEN
            )
        )
    }

    /**
     * Given the domain parameters this routine generates an EC key
     * pair in accordance with X9.62 section 5.2.1 pages 26, 27.
     */
    public override fun generateKeyPair(): AsymmetricCipherKeyPair {
        val n: java.math.BigInteger = params.getN()
        val nBitLength: Int = n.bitLength()
        val minWeight = nBitLength ushr 2

        var d: java.math.BigInteger
        while (true) {
            d = BigIntegers.createRandomBigInteger(nBitLength, random)

            if (isOutOfRangeD(d, n)) {
                continue
            }

            if (WNafUtil.getNafWeight(d) < minWeight) {
                continue
            }

            break
        }

        val Q: ECPoint = createBasePointMultiplier().multiply(params.getG(), d)

        return AsymmetricCipherKeyPair(
            ECPublicKeyParameters(Q, params),
            ECPrivateKeyParameters(d, params)
        )
    }

    protected fun isOutOfRangeD(d: java.math.BigInteger, n: java.math.BigInteger?): Boolean {
        return d.compareTo(ONE) < 0 || (d.compareTo(n) >= 0)
    }

    protected fun createBasePointMultiplier(): ECMultiplier {
        return FixedPointCombMultiplier()
    }
}
