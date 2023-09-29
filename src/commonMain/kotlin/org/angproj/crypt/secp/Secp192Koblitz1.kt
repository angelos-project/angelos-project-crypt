package org.angproj.crypt.secp

import org.angproj.aux.util.BinHex

public class Secp192Koblitz1 {

    public companion object : Secp {
        public override val strength: Int = 96
        public override val size: Int = 192

        protected val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFEE37"
        )

        protected val a: ByteArray = BinHex.decodeToBin(
            "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000"
        )

        protected val b: ByteArray = BinHex.decodeToBin(
            "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000003"
        )

        protected val G: ByteArray = BinHex.decodeToBin(
            "03" +
                    "DB4FF10E" +
                    "C057E9AE" +
                    "26B07D02" +
                    "80B7F434" +
                    "1DA5D1B1" +
                    "EAE06C7D"
        )

        protected val Gc: ByteArray = BinHex.decodeToBin(
            "04" +
                    "DB4FF10E" +
                    "C057E9AE" +
                    "26B07D02" +
                    "80B7F434" +
                    "1DA5D1B1" +
                    "EAE06C7D" +
                    "9B2F2F6D" +
                    "9C5628A7" +
                    "844163D0" +
                    "15BE8634" +
                    "4082AA88" +
                    "D95E2F9D"
        )

        protected val n: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "26F2FC17" +
                    "0F69466A" +
                    "74DEFD8D"
        )

        protected val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}