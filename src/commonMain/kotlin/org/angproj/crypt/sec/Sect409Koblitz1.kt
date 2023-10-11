package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Sect409Koblitz1 : SecTKoblitz {
    public override val name: String = "sect409k1"
    public override val strength: Int = 192
    public override val size: Int = 409

    private val _a: ByteArray = BinHex.decodeToBin(
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000"
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "03" +
                "0060F05F" +
                "658F49C1" +
                "AD3AB189" +
                "0F718421" +
                "0EFD0987" +
                "E307C84C" +
                "27ACCFB8" +
                "F9F67CC2" +
                "C460189E" +
                "B5AAAA62" +
                "EE222EB1" +
                "B35540CF" +
                "E9023746"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "04" +
                "0060F05F" +
                "658F49C1" +
                "AD3AB189" +
                "0F718421" +
                "0EFD0987" +
                "E307C84C" +
                "27ACCFB8" +
                "F9F67CC2" +
                "C460189E" +
                "B5AAAA62" +
                "EE222EB1" +
                "B35540CF" +
                "E9023746" +
                "01E36905" +
                "0B7C4E42" +
                "ACBA1DAC" +
                "BF04299C" +
                "3460782F" +
                "918EA427" +
                "E6325165" +
                "E9EA10E3" +
                "DA5F6C42" +
                "E9C55215" +
                "AA9CA27A" +
                "5863EC48" +
                "D8E0286B"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "7FFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFE5F" +
                "83B2D4EA" +
                "20400EC4" +
                "557D5ED3" +
                "E3E7CA5B" +
                "4B5C83B8" +
                "E01E5FCF"
    )

    private val _h: ByteArray = BinHex.decodeToBin(
        "04"
    )

    override val a: ByteArray
        get() = _a.copyOf()
    override val b: ByteArray
        get() = _b.copyOf()
    override val G: ByteArray
        get() = _G.copyOf()
    override val Gc: ByteArray
        get() = _Gc.copyOf()
    override val n: ByteArray
        get() = _n.copyOf()
    override val h: ByteArray
        get() = _h.copyOf()
}