package org.angproj.crypt.sec

import org.angproj.aux.util.BinHex

public class Secp224Random1 {

    public companion object: Secp {
        public override val strength: Int = 112
        public override val size: Int = 224

        protected val p: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "00000000" +
                    "00000000" +
                    "00000001"
        )

        protected val a: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFE"
        )

        protected val b: ByteArray = BinHex.decodeToBin(
            "B4050A85" +
                    "0C04B3AB" +
                    "F5413256" +
                    "5044B0B7" +
                    "D7BFD8BA" +
                    "270B3943" +
                    "2355FFB4"
        )

        protected val S: ByteArray = BinHex.decodeToBin(
            "BD713447" +
                    "99D5C7FC" +
                    "DC45B59F" +
                    "A3B9AB8F" +
                    "6A948BC5"
        )

        protected val G: ByteArray = BinHex.decodeToBin(
            "02" +
                    "B70E0CBD" +
                    "6BB4BF7F" +
                    "321390B9" +
                    "4A03C1D3" +
                    "56C21122" +
                    "343280D6" +
                    "115C1D21"
        )

        protected val Gc: ByteArray = BinHex.decodeToBin(
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

        protected val n: ByteArray = BinHex.decodeToBin(
            "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFFFFFF" +
                    "FFFF16A2" +
                    "E0B8F03E" +
                    "13DD2945" +
                    "5C5C2A3D"
        )

        protected val h: ByteArray = BinHex.decodeToBin(
            "01"
        )
    }
}