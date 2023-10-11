package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Secp224Random1 : SecPRandom {
    public override val name: String = "secp224r1"
    public override val strength: Int = 112
    public override val size: Int = 224

    private val _p: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "00000000" +
                "00000000" +
                "00000001"
    )

    private val _a: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFE"
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        "B4050A85" +
                "0C04B3AB" +
                "F5413256" +
                "5044B0B7" +
                "D7BFD8BA" +
                "270B3943" +
                "2355FFB4"
    )

    private val _S: ByteArray = BinHex.decodeToBin(
        "BD713447" +
                "99D5C7FC" +
                "DC45B59F" +
                "A3B9AB8F" +
                "6A948BC5"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "02" +
                "B70E0CBD" +
                "6BB4BF7F" +
                "321390B9" +
                "4A03C1D3" +
                "56C21122" +
                "343280D6" +
                "115C1D21"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "04" +
                "B70E0CBD" +
                "6BB4BF7F" +
                "321390B9" +
                "4A03C1D3" +
                "56C21122" +
                "343280D6" +
                "115C1D21" +
                "BD376388" +
                "B5F723FB" +
                "4C22DFE6" +
                "CD4375A0" +
                "5A074764" +
                "44D58199" +
                "85007E34"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFF16A2" +
                "E0B8F03E" +
                "13DD2945" +
                "5C5C2A3D"
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