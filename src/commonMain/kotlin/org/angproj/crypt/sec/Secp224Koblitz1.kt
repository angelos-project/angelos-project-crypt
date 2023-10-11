package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Secp224Koblitz1 : SecPKoblitz {
    public override val name: String = "secp224k1"
    public override val strength: Int = 112
    public override val size: Int = 224

    private val _p: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFE56D"
    )

    private val _a: ByteArray = BinHex.decodeToBin(
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
                "00000005"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "03" +
                "A1455B33" +
                "4DF099DF" +
                "30FC28A1" +
                "69A467E9" +
                "E47075A9" +
                "0F7E650E" +
                "B6B7A45C"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "04" +
                "A1455B33" +
                "4DF099DF" +
                "30FC28A1" +
                "69A467E9" +
                "E47075A9" +
                "0F7E650E" +
                "B6B7A45C" +
                "7E089FED" +
                "7FBA3442" +
                "82CAFBD6" +
                "F7E319F7" +
                "C0B0BD59" +
                "E2CA4BDB" +
                "556D61A5"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "01" +
                "00000000" +
                "00000000" +
                "00000000" +
                "0001DCE8" +
                "D2EC6184" +
                "CAF0A971" +
                "769FB1F7"
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
    override val G: ByteArray
        get() = _G.copyOf()
    override val Gc: ByteArray
        get() = _Gc.copyOf()
    override val n: ByteArray
        get() = _n.copyOf()
    override val h: ByteArray
        get() = _h.copyOf()
}