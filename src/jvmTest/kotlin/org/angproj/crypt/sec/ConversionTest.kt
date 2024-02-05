package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ConversionTest {

    val testPrimeCurve = Secp256Koblitz1

    @Test
    fun octetExport() {
        val q = testPrimeCurve.domainParameters

        assertContentEquals(Conversion.integer2octetSting(Integer(q.p), Convention.mlen(q)).value, testPrimeCurve.p.value)
        assertContentEquals(Conversion.integer2octetSting(Integer(q.a.value), Convention.mlen(q)).value, testPrimeCurve.a.value)
        assertContentEquals(Conversion.integer2octetSting(Integer(q.b.value), Convention.mlen(q)).value, testPrimeCurve.b.value)
        assertContentEquals(Conversion.ellipticCurvePoint2octetString(q.G, q, false).value, testPrimeCurve.G.value)
        assertContentEquals(Conversion.integer2octetSting(Integer(q.n), Convention.mlen(q)).value, testPrimeCurve.n.value)
        assertContentEquals(Conversion.integer2octetSting(Integer(q.h), 1).value, testPrimeCurve.h.value)
    }

    @Test
    fun octetString2ellipticCurvePoint() {
        val q = testPrimeCurve.domainParameters
        val base = Conversion.octetString2ellipticCurvePoint(testPrimeCurve.Gc, q)
        println(BinHex.encodeToHex(base.x.value.toByteArray()))
        println(BinHex.encodeToHex(base.y.value.toByteArray()))
        println()
        println(BinHex.encodeToHex(q.G.x.value.toByteArray()))
        println(BinHex.encodeToHex(q.G.y.value.toByteArray()))
    }
}