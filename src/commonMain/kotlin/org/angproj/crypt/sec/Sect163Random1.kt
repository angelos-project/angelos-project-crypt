package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Sect163Random1 : SecTRandom {
    public override val name: String = "sect163r1"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: ByteArray = BinHex.decodeToBin(
        "07" +
                "B6882CAA" +
                "EFA84F95" +
                "54FF8428" +
                "BD88E246" +
                "D2782AE2"
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        "07" +
                "13612DCD" +
                "DCB40AAB" +
                "946BDA29" +
                "CA91F73A" +
                "F958AFD9"
    )

    private val _S: ByteArray = BinHex.decodeToBin(
        "24B7B137" +
                "C8A14D69" +
                "6E676875" +
                "6151756F" +
                "D0DA2E5C"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "0303" +
                "69979697" +
                "AB438977" +
                "89566789" +
                "567F787A" +
                "7876A654"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "040369" +
                "979697AB" +
                "43897789" +
                "56678956" +
                "7F787A78" +
                "76A65400" +
                "435EDB42" +
                "EFAFB298" +
                "9D51FEFC" +
                "E3C80988" +
                "F41FF883"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "03" +
                "FFFFFFFF" +
                "FFFFFFFF" +
                "FFFF48AA" +
                "B689C29C" +
                "A710279B"
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