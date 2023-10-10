package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public class Secp256Koblitz1 {

    public companion object: Secp {
        public override val strength: Int = 128
        public override val size: Int = 256

        internal val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFFC2F"
        )

        internal val a: ByteArray = BinHex.decodeToBin(
            "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000"
        )

        internal val b: ByteArray = BinHex.decodeToBin(
            "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000000" +
                    "00000007"
        )

        internal val G: ByteArray = BinHex.decodeToBin(
            "02" +
                    "79BE667E" +
                    "F9DCBBAC" +
                    "55A06295" +
                    "CE870B07" +
                    "029BFCDB" +
                    "2DCE28D9" +
                    "59F2815B" +
                    "16F81798"
        )

        internal val Gc: ByteArray = BinHex.decodeToBin(
            "04" +
                    "79BE667E" +
                    "F9DCBBAC" +
                    "55A06295" +
                    "CE870B07" +
                    "029BFCDB" +
                    "2DCE28D9" +
                    "59F2815B" +
                    "16F81798" +
                    "483ADA77" +
                    "26A3C465" +
                    "5DA4FBFC" +
                    "0E1108A8" +
                    "FD17B448" +
                    "A6855419" +
                    "9C47D08F" +
                    "FB10D4B8"
        )

        internal val n: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "BAAEDCE6" +
                    "AF48A03B" +
                    "BFD25E8C" +
                    "D0364141"
        )

        internal val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}