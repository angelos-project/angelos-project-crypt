package org.angproj.crypt.sha

import kotlin.jvm.JvmStatic


enum class HexDigits(val digit: Char) {
    ZERO('0'),
    ONE('1'),
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),

    A_CAPITAL('A'),
    B_CAPITAL('B'),
    C_CAPITAL('C'),
    D_CAPITAL('D'),
    E_CAPITAL('E'),
    F_CAPITAL('F'),

    A_SMALL('a'),
    B_SMALL('b'),
    C_SMALL('c'),
    D_SMALL('d'),
    E_SMALL('e'),
    F_SMALL('f'),
}


enum class HexValues(val value: Int) {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    A(10),
    B(11),
    C(12),
    D(13),
    E(14),
    F(15),

    U_ZERO(ZERO.value shl 4),
    U_ONE(ONE.value shl 4),
    U_TWO(TWO.value shl 4),
    U_THREE(THREE.value shl 4),
    U_FOUR(FOUR.value shl 4),
    U_FIVE(FIVE.value shl 4),
    U_SIX(SIX.value shl 4),
    U_SEVEN(SEVEN.value shl 4),
    U_EIGHT(EIGHT.value shl 4),
    U_NINE(NINE.value shl 4),
    U_A(A.value shl 4),
    U_B(B.value shl 4),
    U_C(C.value shl 4),
    U_D(D.value shl 4),
    U_E(E.value shl 4),
    U_F(F.value shl 4),
}


object BinHex {

    private fun upperBits(byte: Int): Char = when (byte) {
        HexValues.U_ZERO.value -> HexDigits.ZERO.digit
        HexValues.U_ONE.value -> HexDigits.ONE.digit
        HexValues.U_TWO.value -> HexDigits.TWO.digit
        HexValues.U_THREE.value -> HexDigits.THREE.digit
        HexValues.U_FOUR.value -> HexDigits.FOUR.digit
        HexValues.U_FIVE.value -> HexDigits.FIVE.digit
        HexValues.U_SIX.value -> HexDigits.SIX.digit
        HexValues.U_SEVEN.value -> HexDigits.SEVEN.digit
        HexValues.U_EIGHT.value -> HexDigits.EIGHT.digit
        HexValues.U_NINE.value -> HexDigits.NINE.digit
        HexValues.U_A.value -> HexDigits.A_SMALL.digit
        HexValues.U_B.value -> HexDigits.B_SMALL.digit
        HexValues.U_C.value -> HexDigits.C_SMALL.digit
        HexValues.U_D.value -> HexDigits.D_SMALL.digit
        HexValues.U_E.value -> HexDigits.E_SMALL.digit
        HexValues.U_F.value -> HexDigits.F_SMALL.digit
        else -> error("Out of 4 bit range.")
    }

    private fun lowerBits(byte: Int): Char = when (byte) {
        HexValues.ZERO.value -> HexDigits.ZERO.digit
        HexValues.ONE.value -> HexDigits.ONE.digit
        HexValues.TWO.value -> HexDigits.TWO.digit
        HexValues.THREE.value -> HexDigits.THREE.digit
        HexValues.FOUR.value -> HexDigits.FOUR.digit
        HexValues.FIVE.value -> HexDigits.FIVE.digit
        HexValues.SIX.value -> HexDigits.SIX.digit
        HexValues.SEVEN.value -> HexDigits.SEVEN.digit
        HexValues.EIGHT.value -> HexDigits.EIGHT.digit
        HexValues.NINE.value -> HexDigits.NINE.digit
        HexValues.A.value -> HexDigits.A_SMALL.digit
        HexValues.B.value -> HexDigits.B_SMALL.digit
        HexValues.C.value -> HexDigits.C_SMALL.digit
        HexValues.D.value -> HexDigits.D_SMALL.digit
        HexValues.E.value -> HexDigits.E_SMALL.digit
        HexValues.F.value -> HexDigits.F_SMALL.digit
        else -> error("Out of 4 bit range.")
    }

    @JvmStatic
    fun encodeToHex(data: ByteArray): String {
        val hex = CharArray(data.size * 2)
        for (i in data.indices) {
            val j = i * 2
            val byte = data[i].toInt()
            hex[j] = upperBits(byte and 0xF0)
            hex[j + 1] = lowerBits(byte and 0x0F)
        }
        return hex.concatToString()
    }

    private fun upperHex(hex: Char): Int = when (hex) {
        HexDigits.ZERO.digit -> HexValues.U_ZERO.value
        HexDigits.ONE.digit -> HexValues.U_ONE.value
        HexDigits.TWO.digit -> HexValues.U_TWO.value
        HexDigits.THREE.digit -> HexValues.U_THREE.value
        HexDigits.FOUR.digit -> HexValues.U_FOUR.value
        HexDigits.FIVE.digit -> HexValues.U_FIVE.value
        HexDigits.SIX.digit -> HexValues.U_SIX.value
        HexDigits.SEVEN.digit -> HexValues.U_SEVEN.value
        HexDigits.EIGHT.digit -> HexValues.U_EIGHT.value
        HexDigits.NINE.digit -> HexValues.U_NINE.value

        HexDigits.A_CAPITAL.digit -> HexValues.U_A.value
        HexDigits.B_CAPITAL.digit -> HexValues.U_B.value
        HexDigits.C_CAPITAL.digit -> HexValues.U_C.value
        HexDigits.D_CAPITAL.digit -> HexValues.U_D.value
        HexDigits.E_CAPITAL.digit -> HexValues.U_E.value
        HexDigits.F_CAPITAL.digit -> HexValues.U_F.value

        HexDigits.A_SMALL.digit -> HexValues.U_A.value
        HexDigits.B_SMALL.digit -> HexValues.U_B.value
        HexDigits.C_SMALL.digit -> HexValues.U_C.value
        HexDigits.D_SMALL.digit -> HexValues.U_D.value
        HexDigits.E_SMALL.digit -> HexValues.U_E.value
        HexDigits.F_SMALL.digit -> HexValues.U_F.value

        else -> error("Unknown hexadecimal digit.")
    }

    private fun lowerHex(hex: Char): Int = when (hex) {
        HexDigits.ZERO.digit -> HexValues.ZERO.value
        HexDigits.ONE.digit -> HexValues.ONE.value
        HexDigits.TWO.digit -> HexValues.TWO.value
        HexDigits.THREE.digit -> HexValues.THREE.value
        HexDigits.FOUR.digit -> HexValues.FOUR.value
        HexDigits.FIVE.digit -> HexValues.FIVE.value
        HexDigits.SIX.digit -> HexValues.SIX.value
        HexDigits.SEVEN.digit -> HexValues.SEVEN.value
        HexDigits.EIGHT.digit -> HexValues.EIGHT.value
        HexDigits.NINE.digit -> HexValues.NINE.value

        HexDigits.A_CAPITAL.digit -> HexValues.A.value
        HexDigits.B_CAPITAL.digit -> HexValues.B.value
        HexDigits.C_CAPITAL.digit -> HexValues.C.value
        HexDigits.D_CAPITAL.digit -> HexValues.D.value
        HexDigits.E_CAPITAL.digit -> HexValues.E.value
        HexDigits.F_CAPITAL.digit -> HexValues.F.value

        HexDigits.A_SMALL.digit -> HexValues.A.value
        HexDigits.B_SMALL.digit -> HexValues.B.value
        HexDigits.C_SMALL.digit -> HexValues.C.value
        HexDigits.D_SMALL.digit -> HexValues.D.value
        HexDigits.E_SMALL.digit -> HexValues.E.value
        HexDigits.F_SMALL.digit -> HexValues.F.value

        else -> error("Unknown hexadecimal digit.")
    }

    @JvmStatic
    fun decodeToBin(hex: String): ByteArray {
        val data = ByteArray(hex.length / 2)
        for (i in data.indices) {
            val j = i * 2
            data[i] = ((upperHex(hex[j]) or lowerHex(hex[j + 1])) and 0xFF).toByte()
        }
        return data
    }
}