package org.angproj.crypt.sec

import org.angproj.aux.num.AbstractBigInt
import org.angproj.aux.num.BigInt
import org.angproj.aux.util.BinHex
import org.angproj.crypt.number.*
import org.angproj.crypt.number.multiply

/*public abstract class ECFieldElement : org.bouncycastle.math.ec.ECConstants {
    public abstract fun toBigInteger(): AbstractBigInt<*>
    public abstract val fieldName: String
    public abstract val fieldSize: Int

    public abstract fun add(b: ECFieldElement): ECFieldElement
    public abstract fun addOne(): ECFieldElement
    public abstract fun subtract(b: ECFieldElement): ECFieldElement
    public abstract fun multiply(b: ECFieldElement): ECFieldElement
    public abstract fun divide(b: ECFieldElement): ECFieldElement
    public abstract fun negate(): ECFieldElement
    public abstract fun square(): ECFieldElement
    public abstract fun invert(): ECFieldElement
    public abstract fun sqrt(): ECFieldElement

    public open fun bitLength(): Int {
        return toBigInteger().bitLength
    }

    public open val isOne: Boolean
        get() = bitLength() == 1

    public open val isZero: Boolean
        get() = toBigInteger().sigNum.isZero()

    public fun multiplyMinusProduct(b: ECFieldElement, x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return multiply(b).subtract(x.multiply(y))
    }

    public fun multiplyPlusProduct(b: ECFieldElement, x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return multiply(b).add(x.multiply(y))
    }

    public fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return square().subtract(x.multiply(y))
    }

    public fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
        return square().add(x.multiply(y))
    }

    public open fun squarePow(pow: Int): ECFieldElement {
        var r = this
        for (i in 0 until pow) {
            r = r.square()
        }
        return r
    }

    public open fun testBitZero(): Boolean {
        return toBigInteger().testBit(0)
    }

    public override fun toString(): String {
        return BinHex.encodeToHex(toBigInteger().toByteArray())
    }

    public val encoded: ByteArray
        get() = org.bouncycastle.util.BigIntegers.asUnsignedByteArray((fieldSize + 7) / 8, toBigInteger())

    public abstract class AbstractFp : ECFieldElement()


    public class Fp internal constructor(q: AbstractBigInt<*>, r: AbstractBigInt<*>?, x: AbstractBigInt<*>) :
        AbstractFp() {
        public var q: AbstractBigInt<*> = q
        public var r: AbstractBigInt<*>? = r
        public var x: AbstractBigInt<*>

        init {
            this.x = x
        }

        public override fun toBigInteger(): AbstractBigInt<*> {
            return x
        }

        /**
         * return the field name for this field.
         *
         * @return the string "Fp".
         */
        public fun getFieldName(): String {
            return "Fp"
        }

        public fun getFieldSize(): Int {
            return q.bitLength
        }

        public fun getQ(): AbstractBigInt<*> {
            return q
        }

        public override fun add(b: ECFieldElement): ECFieldElement {
            return Fp(q, r, modAdd(x, b.toBigInteger()))
        }

        public override fun addOne(): ECFieldElement {
            var x2 = x.add(BigInt.one)
            if (x2.compareTo(q).isEqual()) {
                x2 = BigInt.zero
            }
            return Fp(q, r, x2)
        }

        public override fun subtract(b: ECFieldElement): ECFieldElement {
            return Fp(q, r, modSubtract(x, b.toBigInteger()))
        }

        public override fun multiply(b: ECFieldElement): ECFieldElement {
            return Fp(q, r, modMult(x, b.toBigInteger()))
        }

        public override fun multiplyMinusProduct(
            b: ECFieldElement,
            x: ECFieldElement,
            y: ECFieldElement
        ): ECFieldElement {
            val ax = this.x
            val bx = b.toBigInteger()
            val xx  = x.toBigInteger()
            val yx = y.toBigInteger()
            val ab = ax.multiply(bx)
            val xy = xx.multiply(yx)
            return Fp(q, r, modReduce(ab.subtract(xy)))
        }

        public override fun multiplyPlusProduct(
            b: ECFieldElement,
            x: ECFieldElement,
            y: ECFieldElement
        ): ECFieldElement {
            val ax = this.x
            val bx = b.toBigInteger()
            val xx = x.toBigInteger()
            val yx = y.toBigInteger()
            val ab = ax.multiply(bx)
            val xy = xx.multiply(yx)
            return Fp(q, r, modReduce(ab.add(xy)))
        }

        public override fun divide(b: ECFieldElement): ECFieldElement {
            return Fp(q, r, modMult(x, modInverse(b.toBigInteger())))
        }

        public override fun negate(): ECFieldElement {
            return if (x.sigNum.isZero()) this else Fp(q, r, q.subtract(x))
        }

        public override fun square(): ECFieldElement {
            return Fp(q, r, modMult(x, x))
        }

        public override fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
            val ax = this.x
            val xx = x.toBigInteger()
            val yx = y.toBigInteger()
            val aa = ax.multiply(ax)
            val xy = xx.multiply(yx)
            return Fp(q, r, modReduce(aa.subtract(xy)))
        }

        public override fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
            val ax = this.x
            val xx = x.toBigInteger()
            val yx = y.toBigInteger()
            val aa = ax.multiply(ax)
            val xy  = xx.multiply(yx)
            return Fp(q, r, modReduce(aa.add(xy)))
        }

        public override fun invert(): ECFieldElement {
            // TODO Modular inversion can be faster for a (Generalized) Mersenne Prime.
            return Fp(q, r, modInverse(x))
        }

        // D.1.4 91
        /**
         * return a sqrt root - the routine verifies that the calculation
         * returns the right value - if none exists it returns null.
         */
        public override fun sqrt(): ECFieldElement {
            if (this.isZero || this.isOne) // earlier JDK compatibility
            {
                return this
            }

            if (!q.testBit(0)) {
                error("not done yet")
            }

            // note: even though this class implements ECConstants don't be tempted to
            // remove the explicit declaration, some J2ME environments don't cope.
            if (q.testBit(1)) // q == 4m + 3
            {
                val e = q.shiftRight(2).add(BigInt.one)
                return checkSqrt(Fp(q, r, x.modPow(e, q)))
            }

            if (q.testBit(2)) // q == 8m + 5
            {
                val t1 = x.modPow(q.shiftRight(3), q)
                val t2 = modMult(t1, x)
                val t3 = modMult(t2, t1)

                if (t3 == BigInt.one) {
                    return checkSqrt(Fp(q, r, t2))
                }

                // TODO This is constant and could be precomputed
                val t4 = BigInt.two.modPow(q.shiftRight(2), q)

                val y = modMult(t2, t4)

                return checkSqrt(Fp(q, r, y))
            }

            // q == 8m + 1
            val legendreExponent  = q.shiftRight(1)
            if (x.modPow(legendreExponent, q) != BigInt.one) {
                return null
            }

            val X = this.x
            val fourX = modDouble(modDouble(X))

            val k = legendreExponent.add(BigInt.one)
            val qMinusOne = q.subtract(BigInt.one)

            var U: AbstractBigInt<*>
            var V: AbstractBigInt<*>
            val rand: java.util.Random = java.util.Random()
            do {
                var P: AbstractBigInt<*>
                do {
                    P = BigInt(q.bitLength, rand)
                } while (P.compareTo(q).isGreaterOrEqual() || modReduce(P.multiply(P).subtract(fourX)).modPow(legendreExponent, q) != qMinusOne)

                val result: Array<AbstractBigInt<*>> = lucasSequence(P, X, k)
                U = result[0]
                V = result[1]

                if (modMult(V, V) == fourX) {
                    return Fp(q, r, modHalfAbs(V))
                }
            } while (U == BigInt.one || U == qMinusOne)

            return null
        }

        private fun checkSqrt(z: ECFieldElement): ECFieldElement? {
            return if (z.square() == this) z else null
        }

        private fun lucasSequence(
            P: AbstractBigInt<*>,
            Q: AbstractBigInt<*>,
            k: AbstractBigInt<*>
        ): Array<AbstractBigInt<*>> {
            // TODO Research and apply "common-multiplicand multiplication here"

            val n: Int = k.bitLength
            val s: Int = k.getLowestSetBit()

            // assert k.testBit(s);
            var Uh = BigInt.one
            var Vl = BigInt.two
            var Vh = P
            var Ql = BigInt.one
            var Qh = BigInt.one

            for (j in n - 1 downTo s + 1) {
                Ql = modMult(Ql, Qh)

                if (k.testBit(j)) {
                    Qh = modMult(Ql, Q)
                    Uh = modMult(Uh, Vh)
                    Vl = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)))
                    Vh = modReduce(Vh.multiply(Vh).subtract(Qh.shiftLeft(1)))
                } else {
                    Qh = Ql
                    Uh = modReduce(Uh.multiply(Vl).subtract(Ql))
                    Vh = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)))
                    Vl = modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)))
                }
            }

            Ql = modMult(Ql, Qh)
            Qh = modMult(Ql, Q)
            Uh = modReduce(Uh.multiply(Vl).subtract(Ql))
            Vl = modReduce(Vh.multiply(Vl).subtract(P.multiply(Ql)))
            Ql = modMult(Ql, Qh)

            for (j in 1..s) {
                Uh = modMult(Uh, Vl)
                Vl = modReduce(Vl.multiply(Vl).subtract(Ql.shiftLeft(1)))
                Ql = modMult(Ql, Ql)
            }

            return arrayOf<AbstractBigInt<*>>(Uh, Vl)
        }

        protected fun modAdd(x1: AbstractBigInt<*>, x2: AbstractBigInt<*>): AbstractBigInt<*> {
            var x3 = x1.add(x2)
            if (x3.compareTo(q).isGreaterOrEqual()) {
                x3 = x3.subtract(q)
            }
            return x3
        }

        protected fun modDouble(x: AbstractBigInt<*>): AbstractBigInt<*> {
            var _2x = x.shiftLeft(1)
            if (_2x.compareTo(q).isGreaterOrEqual()) {
                _2x = _2x.subtract(q)
            }
            return _2x
        }

        protected fun modHalf(x_: AbstractBigInt<*>): AbstractBigInt<*> {
            var x = x_
            if (x.testBit(0)) {
                x = q.add(x)
            }
            return x.shiftRight(1)
        }

        protected fun modHalfAbs(x_: AbstractBigInt<*>): AbstractBigInt<*> {
            var x = x_
            if (x.testBit(0)) {
                x = q.subtract(x)
            }
            return x.shiftRight(1)
        }

        protected fun modInverse(x: AbstractBigInt<*>): AbstractBigInt<*> {
            return org.bouncycastle.util.BigIntegers.modOddInverse(q, x)
        }

        protected fun modMult(x1: AbstractBigInt<*>, x2: AbstractBigInt<*>): AbstractBigInt<*> {
            return modReduce(x1.multiply(x2))
        }

        protected fun modReduce(x_: AbstractBigInt<*>): AbstractBigInt<*> {
            var x = x_
            if (r != null) {
                val negative: Boolean = x.sigNum.isNegative()
                if (negative) {
                    x = x.abs()
                }
                val qLen: Int = q.bitLength
                val rIsOne = r == BigInt.one
                while (x.bitLength > (qLen + 1)) {
                    var u = x.shiftRight(qLen)
                    val v = x.subtract(u.shiftLeft(qLen))
                    if (!rIsOne) {
                        u = u.multiply(r)
                    }
                    x = u.add(v)
                }
                while (x.compareTo(q).isGreaterOrEqual()) {
                    x = x.subtract(q)
                }
                if (negative && x.sigNum.isNonZero()) {
                    x = q.subtract(x)
                }
            } else {
                x = x.mod(q)
            }
            return x
        }

        protected fun modSubtract(x1: AbstractBigInt<*>, x2: AbstractBigInt<*>): AbstractBigInt<*> {
            var x3  = x1.subtract(x2)
            if (x3.sigNum.isNegative()) {
                x3 = x3.add(q)
            }
            return x3
        }

        public override fun equals(other: Any?): Boolean {
            if (other === this) {
                return true
            }

            if (other !is Fp) {
                return false
            }

            val o = other
            return q == o.q && x == o.x
        }

        public override fun hashCode(): Int {
            return q.hashCode() xor x.hashCode()
        }

        public companion object {
            public fun calculateResidue(p: AbstractBigInt<*>): AbstractBigInt<*>? {
                val bitLength: Int = p.bitLength
                if (bitLength >= 96) {
                    val firstWord = p.shiftRight(bitLength - 64)
                    if (firstWord.toLong() == -1L) {
                        return BigInt.one.shiftLeft(bitLength).subtract(p)
                    }
                }
                return null
            }
        }
    }

    public abstract class AbstractF2m : ECFieldElement() {
        public open fun halfTrace(): ECFieldElement? {
            val m = this.fieldSize
            if ((m and 1) == 0) {
                error("Half-trace only defined for odd m")
            }

            //            ECFieldElement ht = this;
//            for (int i = 1; i < m; i += 2)
//            {
//                ht = ht.squarePow(2).add(this);
//            }
            val n = (m + 1) ushr 1
            var k: Int = 31 - org.bouncycastle.util.Integers.numberOfLeadingZeros(n)
            var nk = 1

            var ht: ECFieldElement = this
            while (k > 0) {
                ht = ht.squarePow(nk shl 1).add(ht)
                nk = n ushr --k
                if (0 != (nk and 1)) {
                    ht = ht.squarePow(2).add(this)
                }
            }

            return ht
        }

        public open fun hasFastTrace(): Boolean {
            return false
        }

        public open fun trace(): Int {
            val m = this.fieldSize

            //            ECFieldElement tr = this;
//            for (int i = 1; i < m; ++i)
//            {
//                tr = tr.square().add(this);
//            }
            var k: Int = 31 - org.bouncycastle.util.Integers.numberOfLeadingZeros(m)
            var mk = 1

            var tr: ECFieldElement = this
            while (k > 0) {
                tr = tr.squarePow(mk).add(tr)
                mk = m ushr --k
                if (0 != (mk and 1)) {
                    tr = tr.square().add(this)
                }
            }

            if (tr.isZero) {
                return 0
            }
            if (tr.isOne) {
                return 1
            }
            error("Internal error in trace calculation")
        }
    }

    /**
     * Class representing the Elements of the finite field
     * `F<sub>2<sup>m</sup></sub>` in polynomial basis (PB)
     * representation. Both trinomial (TPB) and pentanomial (PPB) polynomial
     * basis representations are supported. Gaussian normal basis (GNB)
     * representation is not supported.
     */
    public class F2m internal constructor(
        /**
         * The exponent `m` of `F<sub>2<sup>m</sup></sub>`.
         */
        public val m: Int, private val ks: IntArray, x: org.bouncycastle.math.ec.LongArray
    ) :
        AbstractF2m() {
        /**
         * @return the representation of the field
         * `F<sub>2<sup>m</sup></sub>`, either of
         * TPB (trinomial
         * basis representation) or
         * PPB (pentanomial
         * basis representation).
         */
        /**
         * TPB or PPB.
         */
        public val representation: Int

        /**
         * @return the degree `m` of the reduction polynomial
         * `f(z)`.
         */

        /**
         * The `LongArray` holding the bits.
         */
        public var x: org.bouncycastle.math.ec.LongArray

        init {
            this.representation = if ((ks.size == 1)) TPB else PPB
            this.x = x
        }

        public override fun bitLength(): Int {
            return x.degree()
        }

        public override fun isOne(): Boolean {
            return x.isOne()
        }

        public override fun isZero(): Boolean {
            return x.isZero()
        }

        public override fun testBitZero(): Boolean {
            return x.testBitZero()
        }

        public override fun toBigInteger(): AbstractBigInt<*> {
            return x.toBigInteger()
        }

        public override fun getFieldName(): String {
            return "F2m"
        }

        public override fun getFieldSize(): Int {
            return m
        }

        public override fun add(b: ECFieldElement): ECFieldElement {
            // No check performed here for performance reasons. Instead the
            // elements involved are checked in ECPoint.F2m
            // checkFieldElements(this, b);
            val iarrClone: org.bouncycastle.math.ec.LongArray = x.clone() as org.bouncycastle.math.ec.LongArray
            val bF2m = b as F2m
            iarrClone.addShiftedByWords(bF2m.x, 0)
            return F2m(m, ks, iarrClone)
        }

        public override fun addOne(): ECFieldElement {
            return F2m(m, ks, x.addOne())
        }

        public override fun subtract(b: ECFieldElement): ECFieldElement {
            // Addition and subtraction are the same in F2m
            return add(b)
        }

        public override fun multiply(b: ECFieldElement): ECFieldElement {
            // Right-to-left comb multiplication in the LongArray
            // Input: Binary polynomials a(z) and b(z) of degree at most m-1
            // Output: c(z) = a(z) * b(z) mod f(z)

            // No check performed here for performance reasons. Instead the
            // elements involved are checked in ECPoint.F2m
            // checkFieldElements(this, b);

            return F2m(m, ks, x.modMultiply((b as F2m).x, m, ks))
        }

        public override fun multiplyMinusProduct(
            b: ECFieldElement,
            x: ECFieldElement,
            y: ECFieldElement
        ): ECFieldElement {
            return multiplyPlusProduct(b, x, y)
        }

        public override fun multiplyPlusProduct(
            b: ECFieldElement,
            x: ECFieldElement,
            y: ECFieldElement
        ): ECFieldElement {
            val ax: org.bouncycastle.math.ec.LongArray = this.x
            val bx: org.bouncycastle.math.ec.LongArray = (b as F2m).x
            val xx: org.bouncycastle.math.ec.LongArray = (x as F2m).x
            val yx: org.bouncycastle.math.ec.LongArray = (y as F2m).x

            var ab: org.bouncycastle.math.ec.LongArray = ax.multiply(bx, m, ks)
            val xy: org.bouncycastle.math.ec.LongArray = xx.multiply(yx, m, ks)

            if (ab === ax || ab === bx) {
                ab = ab.clone() as org.bouncycastle.math.ec.LongArray
            }

            ab.addShiftedByWords(xy, 0)
            ab.reduce(m, ks)

            return F2m(m, ks, ab)
        }

        public override fun divide(b: ECFieldElement): ECFieldElement {
            // There may be more efficient implementations
            val bInv = b.invert()
            return multiply(bInv)
        }

        public override fun negate(): ECFieldElement {
            // -x == x holds for all x in F2m
            return this
        }

        public override fun square(): ECFieldElement {
            return F2m(m, ks, x.modSquare(m, ks))
        }

        public override fun squareMinusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
            return squarePlusProduct(x, y)
        }

        public override fun squarePlusProduct(x: ECFieldElement, y: ECFieldElement): ECFieldElement {
            val ax: org.bouncycastle.math.ec.LongArray = this.x
            val xx: org.bouncycastle.math.ec.LongArray = (x as F2m).x
            val yx: org.bouncycastle.math.ec.LongArray = (y as F2m).x

            var aa: org.bouncycastle.math.ec.LongArray = ax.square(m, ks)
            val xy: org.bouncycastle.math.ec.LongArray = xx.multiply(yx, m, ks)

            if (aa === ax) {
                aa = aa.clone() as org.bouncycastle.math.ec.LongArray
            }

            aa.addShiftedByWords(xy, 0)
            aa.reduce(m, ks)

            return F2m(m, ks, aa)
        }

        public override fun squarePow(pow: Int): ECFieldElement {
            return if (pow < 1) this else F2m(m, ks, x.modSquareN(pow, m, ks))
        }

        public override fun invert(): ECFieldElement {
            return F2m(
                this.m, this.ks, x.modInverse(
                    m,
                    ks
                )
            )
        }

        public override fun sqrt(): ECFieldElement {
            return if ((x.isZero() || x.isOne())) this else squarePow(m - 1)
        }

        public val k1: Int
            /**
             * @return TPB: The integer `k` where `x<sup>m</sup> +
             * x<sup>k</sup> + 1` represents the reduction polynomial
             * `f(z)`.<br></br>
             * PPB: The integer `k1` where `x<sup>m</sup> +
             * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
             * represents the reduction polynomial `f(z)`.<br></br>
             */
            get() = ks[0]

        public val k2: Int
            /**
             * @return TPB: Always returns `0`<br></br>
             * PPB: The integer `k2` where `x<sup>m</sup> +
             * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
             * represents the reduction polynomial `f(z)`.<br></br>
             */
            get() = if (ks.size >= 2) ks[1] else 0

        public val k3: Int
            /**
             * @return TPB: Always set to `0`<br></br>
             * PPB: The integer `k3` where `x<sup>m</sup> +
             * x<sup>k3</sup> + x<sup>k2</sup> + x<sup>k1</sup> + 1`
             * represents the reduction polynomial `f(z)`.<br></br>
             */
            get() = if (ks.size >= 3) ks[2] else 0

        public override fun equals(anObject: Any?): Boolean {
            if (anObject === this) {
                return true
            }

            if (anObject !is F2m) {
                return false
            }

            val b = anObject

            return ((this.m == b.m)
                    && (this.representation == b.representation)
                    && org.bouncycastle.util.Arrays.areEqual(this.ks, b.ks)
                    && (this.x == b.x))
        }

        public override fun hashCode(): Int {
            return x.hashCode() xor m xor org.bouncycastle.util.Arrays.hashCode(ks)
        }

        public companion object {
            /**
             * Indicates gaussian normal basis representation (GNB). Number chosen
             * according to X9.62. GNB is not implemented at present.
             */
            public const val GNB: Int = 1

            /**
             * Indicates trinomial basis representation (TPB). Number chosen
             * according to X9.62.
             */
            public const val TPB: Int = 2

            /**
             * Indicates pentanomial basis representation (PPB). Number chosen
             * according to X9.62.
             */
            public const val PPB: Int = 3
        }
    }
}*/