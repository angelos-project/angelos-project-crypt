package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public class Secp384Random1 {

    public companion object: Secp {
        public override val strength: Int = 192
        public override val size: Int = 384

        protected val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFFFFF" +
                    "00000000" +
                    "00000000" +
                    "FFFFFFFF"
        )

        protected val a: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFFFFF" +
                    "00000000" +
                    "00000000" +
                    "FFFFFFFC"
        )

        protected val b: ByteArray = BinHex.decodeToBin(
            "B3312FA7" +
                    "E23EE7E4" +
                    "988E056B" +
                    "E3F82D19" +
                    "181D9C6E" +
                    "FE814112" +
                    "0314088F" +
                    "5013875A" +
                    "C656398D" +
                    "8A2ED19D" +
                    "2A85C8ED" +
                    "D3EC2AEF"
        )

        protected val S: ByteArray = BinHex.decodeToBin(
            "A335926A" +
                    "A319A27A" +
                    "1D00896A" +
                    "6773A482" +
                    "7ACDAC73"
        )

        protected val G: ByteArray = BinHex.decodeToBin(
            "03" +
                    "AA87CA22" +
                    "BE8B0537" +
                    "8EB1C71E" +
                    "F320AD74" +
                    "6E1D3B62" +
                    "8BA79B98" +
                    "59F741E0" +
                    "82542A38" +
                    "5502F25D" +
                    "BF55296C" +
                    "3A545E38" +
                    "72760AB7"
        )

        protected val Gc: ByteArray = BinHex.decodeToBin(
            "04" +
                    "AA87CA22" +
                    "BE8B0537" +
                    "8EB1C71E" +
                    "F320AD74" +
                    "6E1D3B62" +
                    "8BA79B98" +
                    "59F741E0" +
                    "82542A38" +
                    "5502F25D" +
                    "BF55296C" +
                    "3A545E38" +
                    "72760AB7" +
                    "3617DE4A" +
                    "96262C6F" +
                    "5D9E98BF" +
                    "9292DC29" +
                    "F8F41DBD" +
                    "289A147C" +
                    "E9DA3113" +
                    "B5F0B8C0" +
                    "0A60B1CE" +
                    "1D7E819D" +
                    "7A431D7C" +
                    "90EA0E5F"
        )

        protected val n: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "C7634D81" +
                    "F4372DDF" +
                    "581A0DB2" +
                    "48B0A77A" +
                    "ECEC196A" +
                    "CCC52973"
        )

        protected val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}