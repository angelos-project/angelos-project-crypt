package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Sect163Koblitz1 : SecTKoblitz {
    public override val name: String = "sect163k1"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: ByteArray = BinHex.decodeToBin(
        "000" +
                "0000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        "00" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "0302" +
                "FE13C053" +
                "7BBC11AC" +
                "AA07D793" +
                "DE4E6D5E" +
                "5C94EEE8"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "0402FE" +
                "13C0537B" +
                "BC11ACAA" +
                "07D793DE" +
                "4E6D5E5C" +
                "94EEE802" +
                "89070FB0" +
                "5D38FF58" +
                "321F2E80" +
                "0536D538" +
                "CCDAA3D9"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "04" +
                "00000000" +
                "00000000" +
                "00020108" +
                "A2E0CC0D" +
                "99F8A5EF"
    )

    private val _h: ByteArray = BinHex.decodeToBin(
        "02"
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