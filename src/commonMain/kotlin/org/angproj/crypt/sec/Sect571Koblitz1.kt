package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public object Sect571Koblitz1 : SecTKoblitz {
    public override val name: String = "sect571k1"
    public override val strength: Int = 256
    public override val size: Int = 571

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
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000001"
    )

    private val _G: ByteArray = BinHex.decodeToBin(
        "02" +
                "026EB7A8" +
                "59923FBC" +
                "82189631" +
                "F8103FE4" +
                "AC9CA297" +
                "0012D5D4" +
                "60248048" +
                "01841CA4" +
                "43709584" +
                "93B205E6" +
                "47DA304D" +
                "B4CEB08C" +
                "BBD1BA39" +
                "494776FB" +
                "988B4717" +
                "4DCA88C7" +
                "E2945283" +
                "A01C8972"
    )

    private val _Gc: ByteArray = BinHex.decodeToBin(
        "04" +
                "026EB7A8" +
                "59923FBC" +
                "82189631" +
                "F8103FE4" +
                "AC9CA297" +
                "0012D5D4" +
                "60248048" +
                "01841CA4" +
                "43709584" +
                "93B205E6" +
                "47DA304D" +
                "B4CEB08C" +
                "BBD1BA39" +
                "494776FB" +
                "988B4717" +
                "4DCA88C7" +
                "E2945283" +
                "A01C8972" +
                "0349DC80" +
                "7F4FBF37" +
                "4F4AEADE" +
                "3BCA9531" +
                "4DD58CEC" +
                "9F307A54" +
                "FFC61EFC" +
                "006D8A2C" +
                "9D4979C0" +
                "AC44AEA7" +
                "4FBEBBB9" +
                "F772AEDC" +
                "B620B01A" +
                "7BA7AF1B" +
                "320430C8" +
                "591984F6" +
                "01CD4C14" +
                "3EF1C7A3"
    )

    private val _n: ByteArray = BinHex.decodeToBin(
        "02000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "00000000" +
                "131850E1" +
                "F19A63E4" +
                "B391A8DB" +
                "917F4138" +
                "B630D84B" +
                "E5D63938" +
                "1E91DEB4" +
                "5CFE778F" +
                "637C1001"
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