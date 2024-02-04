package org.angproj.crypt

import org.angproj.aux.num.BigInt
import org.angproj.aux.util.BinHex
import org.angproj.aux.util.bigIntOf
import org.angproj.crypt.kp.PaulssonDigest
import org.angproj.crypt.number.*
import org.angproj.crypt.sec.*
import org.angproj.crypt.sec.Integer
import java.math.BigInteger
import java.security.KeyPair
import java.security.Signature
import kotlin.math.absoluteValue
import kotlin.test.Test
import kotlin.test.assertTrue

class BouncyTest {

    /**
     * 3.1.3.1 Curve Selection
     * */
    fun generateB(S: OctetString, q: PrimeDomainParameters, hash: PaulssonDigest, a: FieldElement): FieldElement {
        val g = S.value.size * 8
        val m = Convention.primeLog2(q)
        val t = 128
        val s = ((m - 1) / t).absoluteValue
        val k =  m - s * t - 1 // Point 4, ODD
        val s0 = Convention.octetString2integer(S)
        var es = BigInt.zero
        val buf = ByteArray(128)
        (0..s).forEach { j ->
            val sj = Integer((s0.value + bigIntOf(j.toLong()).mod(BigInt.two.pow(g))).toBigInt())
            val Sj = Convention.integer2octetSting(sj, g/8)
            hash.update(Sj.value, 0, Sj.value.size)
            hash.doFinal(buf, t)
            val Hj = OctetString(buf)
            val ej = Convention.octetString2integer(Hj)
            es = (es + ej.value * BigInt.two.pow(t * (s-j))).toBigInt()
        }
        val e = Integer(es.mod(BigInt.two.pow(k + s * t)).toBigInt())
        val E = Convention.integer2octetSting(e, Convention.primeLog2(q) / 8)
        val r = Convention.octetString2fieldElement(E, q)
        // Point 10, ODD
        check(a.value.compareTo(BigInt.zero).isNotEqual()) { "a is zero." }
        check((bigIntOf(4) * r.value + bigIntOf(27)).compareTo(BigInt.zero).isNotEqual()) { "r doesn't satisfy." }
        check(Convention.primeIsFinite((a.value.pow(3) / r.value).sqrt().toBigInt(), q)) { "No sqrt within q." }
        return FieldElement((a.value.pow(3)/r.value).sqrt().toBigInt())
    }

    @Test
    fun testEcdsaSignAndVerify() {
        val p = (BigInt.two.pow(1279) - BigInt.one).toBigInt()
        val a = (p - BigInt.two - BigInt.one).toBigInt()
        val h = BigInt.one

        /*val q = emptyPrimeOf()
        q.p = (BigInt.two.pow(1279) - BigInt.one).toBigInt()
        val a = FieldElement((q.p.sqrt() - BigInt.one).toBigInt())
        val b = generateB(OctetString(SecureRandom.getSecureEntropy(16)), q, PaulssonDigest(), a)
        println(BinHex.encodeToHex(b.value.toByteArray()))*/
    }
}