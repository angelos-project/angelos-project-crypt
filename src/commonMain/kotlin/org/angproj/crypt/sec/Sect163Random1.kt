package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Sect163Random1 : SecTRandom {
    public override val name: String = "sect163r1"
    public override val strength: Int = 80
    public override val size: Int = 163

    private val _a: ByteArray = BinHex.decodeToBin(
        ""
    )

    private val _b: ByteArray = BinHex.decodeToBin(
        ""
    )

    private val _S: ByteArray = BinHex.decodeToBin(
        ""
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        ""
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        ""
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        ""
    )

    private val _h: ByteArray = BinHex.decodeToBin(
        ""
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