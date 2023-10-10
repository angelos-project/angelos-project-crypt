package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public class Secp192Random1 {

    public companion object: Secp {
        public override val strength: Int = 96
        public override val size: Int = 192

        protected val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFFFFF" +
                    "FFFFFFFF"
        )

        protected val a: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFFFFF" +
                    "FFFFFFFC"
        )

        protected val b: ByteArray = BinHex.decodeToBin(
            "64210519" +
                    "E59C80E7" +
                    "0FA7E9AB" +
                    "72243049" +
                    "FEB8DEEC" +
                    "C146B9B1"
        )

        protected val S: ByteArray = BinHex.decodeToBin(
            "3045AE6F" +
                    "C8422F64" +
                    "ED579528" +
                    "D38120EA" +
                    "E12196D5"
        )

        protected val G: ByteArray = BinHex.decodeToBin(
            "03" +
                    "188DA80E" +
                    "B03090F6" +
                    "7CBF20EB" +
                    "43A18800" +
                    "F4FF0AFD" +
                    "82FF1012"
        )

        protected val Gc: ByteArray = BinHex.decodeToBin(
            "04" +
                    "188DA80E" +
                    "B03090F6" +
                    "7CBF20EB" +
                    "43A18800" +
                    "F4FF0AFD" +
                    "82FF1012" +
                    "07192B95" +
                    "FFC8DA78" +
                    "631011ED" +
                    "6B24CDD5" +
                    "73F977A1" +
                    "1E794811"
        )

        protected val n: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "99DEF836" +
                    "146BC9B1" +
                    "B4D22831"
        )

        protected val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}