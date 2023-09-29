package org.angproj.crypt.secp

import org.angproj.aux.util.BinHex

public class Secp256Random1 {

    public companion object: Secp {
        public override val strength: Int = 128
        public override val size: Int = 256

        protected val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "00000001" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF"
        )

        protected val a: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "00000001" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFC"
        )

        protected val b: ByteArray = BinHex.decodeToBin(
            "5AC635D8" +
                    "AA3A93E7" +
                    "B3EBBD55" +
                    "769886BC" +
                    "651D06B0" +
                    "CC53B0F6" +
                    "3BCE3C3E" +
                    "27D2604B"
        )

        protected val S: ByteArray = BinHex.decodeToBin(
            "C49D3608" +
                    "86E70493" +
                    "6A6678E1" +
                    "139D26B7" +
                    "819F7E90"
        )

        protected val G: ByteArray = BinHex.decodeToBin(
            "03" +
                    "6B17D1F2" +
                    "E12C4247" +
                    "F8BCE6E5" +
                    "63A440F2" +
                    "77037D81" +
                    "2DEB33A0" +
                    "F4A13945" +
                    "D898C296"
        )

        protected val Gc: ByteArray = BinHex.decodeToBin(
            "04" +
                    "6B17D1F2" +
                    "E12C4247" +
                    "F8BCE6E5" +
                    "63A440F2" +
                    "77037D81" +
                    "2DEB33A0" +
                    "F4A13945" +
                    "D898C296" +
                    "4FE342E2" +
                    "FE1A7F9B" +
                    "8EE7EB4A" +
                    "7C0F9E16" +
                    "2BCE3357" +
                    "6B315ECE" +
                    "CBB64068" +
                    "37BF51F5"
        )

        protected val n: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "00000000" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "BCE6FAAD" +
                    "A7179E84" +
                    "F3B9CAC2" +
                    "FC632551"
        )

        protected val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}