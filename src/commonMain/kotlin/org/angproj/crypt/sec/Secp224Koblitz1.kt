package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public class Secp224Koblitz1 {

    public companion object: Secp {
        public override val strength: Int = 112
        public override val size: Int = 224

        protected val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFE56D"
        )

        protected val a: ByteArray = BinHex.decodeToBin(
            "00000000" +
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
                    "00000000" +
                    "00000005"
        )

        protected val G: ByteArray = BinHex.decodeToBin(
            "03" +
                    "A1455B33" +
                    "4DF099DF" +
                    "30FC28A1" +
                    "69A467E9" +
                    "E47075A9" +
                    "0F7E650E" +
                    "B6B7A45C"
        )

        protected val Gc: ByteArray = BinHex.decodeToBin(
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

        protected val n: ByteArray = BinHex.decodeToBin(
            "01" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "0001DCE8" +
                    "D2EC6184" +
                    "CAF0A971" +
                    "769FB1F7"
        )

        protected val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}