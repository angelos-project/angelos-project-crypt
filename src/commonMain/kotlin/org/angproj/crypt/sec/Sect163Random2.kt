package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Sect163Random2 : SecTRandom {
    public override val name: String = "sect163r2"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: ByteArray = BinHex.decodeToBin(
        "00" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        "02" +
                "0A601907" +
                "B8C953CA" +
                "1481EB10" +
                "512F7874" +
                "4A3205FD"
    )

    private val _S: ByteArray = BinHex.decodeToBin(
        "85E25BFE" +
                "5C86226C" +
                "DB12016F" +
                "7553F9D0" +
                "E693A268"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "0303" +
                "F0EBA162" +
                "86A2D57E" +
                "A0991168" +
                "D4994637" +
                "E8343E36"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "0403F0" +
                "EBA16286" +
                "A2D57EA0" +
                "991168D4" +
                "994637E8" +
                "343E3600" +
                "D51FBC6C" +
                "71A0094F" +
                "A2CDD545" +
                "B11C5C0C" +
                "797324F1"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "04" +
                "00000000" +
                "00000000" +
                "000292FE" +
                "77E70C12" +
                "A4234C33"
    )

    private val _h: ByteArray = BinHex.decodeToBin(
        "02"
    )

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