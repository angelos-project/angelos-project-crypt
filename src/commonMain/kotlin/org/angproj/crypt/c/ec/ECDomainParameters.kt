package org.bouncycastle.crypto.params

import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.math.ec.ECAlgorithms
import org.bouncycastle.math.ec.ECConstants
import org.bouncycastle.math.ec.ECCurve
import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.BigIntegers
import kotlin.jvm.Synchronized

public open class ECDomainParameters
    (
    curve: ECCurve?,
    G: ECPoint?,
    n: java.math.BigInteger?,
    h: java.math.BigInteger?,
    seed: ByteArray?
) : ECConstants {
    private val curve: ECCurve
    private val seed: ByteArray
    private val G: ECPoint?
    private val n: java.math.BigInteger?
    private val h: java.math.BigInteger?

    @get:Synchronized
    public var hInv: java.math.BigInteger? = null
        get() {
            if (field == null) {
                field = BigIntegers.modOddInverseVar(n, h)
            }
            return field
        }
        private set

    public constructor(x9: X9ECParameters) : this(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed())

    public constructor(
        curve: ECCurve?,
        G: ECPoint?,
        n: java.math.BigInteger?
    ) : this(curve, G, n, ONE, null)

    public constructor(
        curve: ECCurve?,
        G: ECPoint?,
        n: java.math.BigInteger?,
        h: java.math.BigInteger?
    ) : this(curve, G, n, h, null)

    init {
        if (curve == null) {
            throw java.lang.NullPointerException("curve")
        }
        if (n == null) {
            throw java.lang.NullPointerException("n")
        }

        // we can't check for h == null here as h is optional in X9.62 as it is not required for ECDSA
        this.curve = curve
        this.G = validatePublicPoint(curve, G)
        this.n = n
        this.h = h
        this.seed = Arrays.clone(seed)
    }

    public fun getCurve(): ECCurve {
        return curve
    }

    public fun getG(): ECPoint? {
        return G
    }

    public fun getN(): java.math.BigInteger? {
        return n
    }

    public fun getH(): java.math.BigInteger? {
        return h
    }

    public fun getSeed(): ByteArray {
        return Arrays.clone(seed)
    }

    public override fun equals(
        obj: Any?
    ): Boolean {
        if (this === obj) {
            return true
        }

        if (obj !is ECDomainParameters) {
            return false
        }

        val other = obj

        return (curve.equals(other.curve)
                && G.equals(other.G)
                && (this.n == other.n))
    }

    public override fun hashCode(): Int {
//        return Arrays.hashCode(new Object[]{ curve, G, n });
        var hc = 4
        hc *= 257
        hc = hc xor curve.hashCode()
        hc *= 257
        hc = hc xor G.hashCode()
        hc *= 257
        hc = hc xor n.hashCode()
        return hc
    }

    public fun validatePrivateScalar(d: java.math.BigInteger?): java.math.BigInteger {
        if (null == d) {
            throw java.lang.NullPointerException("Scalar cannot be null")
        }

        if (d.compareTo(ECConstants.ONE) < 0 || (d.compareTo(getN()) >= 0)) {
            throw java.lang.IllegalArgumentException("Scalar is not in the interval [1, n - 1]")
        }

        return d
    }

    public fun validatePublicPoint(q: ECPoint?): ECPoint? {
        return validatePublicPoint(getCurve(), q)
    }

    public companion object {
        public fun validatePublicPoint(c: ECCurve?, q: ECPoint?): ECPoint? {
            var q: ECPoint = q ?: throw java.lang.NullPointerException("Point cannot be null")

            q = ECAlgorithms.importPoint(c, q).normalize()

            if (q.isInfinity()) {
                throw java.lang.IllegalArgumentException("Point at infinity")
            }

            if (!q.isValid()) {
                throw java.lang.IllegalArgumentException("Point not on curve")
            }

            return q
        }
    }
}
