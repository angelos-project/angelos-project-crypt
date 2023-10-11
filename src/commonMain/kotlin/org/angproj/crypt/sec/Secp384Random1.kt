package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Secp384Random1 : SecPRandom {
    public override val name: String = "secp384r1"
    public override val strength: Int = 192
    public override val size: Int = 384

    private val _p: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "00000000" +
                "00000000" +
                "FFFFFFFF"
    )

    private val _a: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "00000000" +
                "00000000" +
                "FFFFFFFC"
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        "B3312FA7" +
                "E23EE7E4" +
                "988E056B" +
                "E3F82D19" +
                "181D9C6E" +
                "FE814112" +
                "0314088F" +
                "5013875A" +
                "C656398D" +
                "8A2ED19D" +
                "2A85C8ED" +
                "D3EC2AEF"
    )

    private val _S: ByteArray = BinHex.decodeToBin(
        "A335926A" +
                "A319A27A" +
                "1D00896A" +
                "6773A482" +
                "7ACDAC73"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "03" +
                "AA87CA22" +
                "BE8B0537" +
                "8EB1C71E" +
                "F320AD74" +
                "6E1D3B62" +
                "8BA79B98" +
                "59F741E0" +
                "82542A38" +
                "5502F25D" +
                "BF55296C" +
                "3A545E38" +
                "72760AB7"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "04" +
                "AA87CA22" +
                "BE8B0537" +
                "8EB1C71E" +
                "F320AD74" +
                "6E1D3B62" +
                "8BA79B98" +
                "59F741E0" +
                "82542A38" +
                "5502F25D" +
                "BF55296C" +
                "3A545E38" +
                "72760AB7" +
                "3617DE4A" +
                "96262C6F" +
                "5D9E98BF" +
                "9292DC29" +
                "F8F41DBD" +
                "289A147C" +
                "E9DA3113" +
                "B5F0B8C0" +
                "0A60B1CE" +
                "1D7E819D" +
                "7A431D7C" +
                "90EA0E5F"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "C7634D81" +
                "F4372DDF" +
                "581A0DB2" +
                "48B0A77A" +
                "ECEC196A" +
                "CCC52973"
    )

    private val _h: ByteArray = BinHex.decodeToBin(
        "01"
    )

    override val p: ByteArray
        get() = _p.copyOf()
    override val a: ByteArray
        get() = _a.copyOf()
    override val b: ByteArray
        get() = _b.copyOf()
    override val S: ByteArray
        get() = _S.copyOf()
    override val G: ByteArray
        get() = _G.copyOf()
    override val Gc: ByteArray
        get() = _Gc.copyOf()
    override val n: ByteArray
        get() = _n.copyOf()
    override val h: ByteArray
        get() = _h.copyOf()
}