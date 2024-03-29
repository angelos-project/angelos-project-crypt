/**
 * Copyright (c) 2019 Stark Bank S.A.
 *
 * This software is available under the terms of the MIT license.
 * The legal terms are attached to the LICENSE file and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Rafael Stark - original implementation
 *      Dalton Menezes - original implementation
 *      Caio Dottori - original implementation
 *      Thales Mello - original implementation
 *      Kristoffer Paulsson - adaption to Angelos Project
 */
package org.angproj.crypt.ellipticcurve.utils;
import java.math.BigInteger;
import java.util.Arrays;


public final class BinaryAscii {

    /**
     *
     * @param string byteString
     * @return String
     */
    public static String hexFromBinary(ByteString string) {
        return hexFromBinary(string.getBytes());
    }

    /**
     *
     * @param bytes byte[]
     * @return String
     */
    public static String hexFromBinary(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     *
     * @param string string
     * @return byte[]
     */
    public static byte[] binaryFromHex(String string) {
        byte[] bytes = new BigInteger(string, 16).toByteArray();
        int i = 0;
        while (i < bytes.length && bytes[i] == 0) {
            i++;
        }
        return Arrays.copyOfRange(bytes, i, bytes.length);
    }

    /**
     *
     * @param c c
     * @return byte[]
     */
    public static byte[] toBytes(int c) {
        return new byte[]{(byte) c};
    }

    /**
     * Get a number representation of a string
     *
     * @param string String to be converted in a number
     * @return Number in hex from string
     */
    public static BigInteger numberFromString(byte[] string) {
        return new BigInteger(BinaryAscii.hexFromBinary(string), 16);
    }

    /**
     * Get a string representation of a number
     *
     * @param number number to be converted in a string
     * @param length length max number of character for the string
     * @return hexadecimal string
     */
    public static ByteString stringFromNumber(BigInteger number, int length) {
        String fmtStr = "%0" + String.valueOf(2 * length) + "x";
        String hexString = String.format(fmtStr, number);
        return new ByteString(BinaryAscii.binaryFromHex(hexString));
    }
}
