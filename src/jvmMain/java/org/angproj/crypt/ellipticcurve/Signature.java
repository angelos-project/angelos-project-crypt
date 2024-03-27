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
package org.angproj.crypt.ellipticcurve;
import org.angproj.crypt.ellipticcurve.utils.Der;
import org.angproj.crypt.ellipticcurve.utils.Base64;
import org.angproj.crypt.ellipticcurve.utils.BinaryAscii;
import org.angproj.crypt.ellipticcurve.utils.ByteString;

import java.io.IOException;
import java.math.BigInteger;


public class Signature {

    public BigInteger r;
    public BigInteger s;

    /**
     *
     * @param r r
     * @param s s
     */
    public Signature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    /**
     *
     * @return ByteString
     */
    public ByteString toDer() {
        return Der.encodeSequence(Der.encodeInteger(r), Der.encodeInteger(s));
    }

    /**
     *
     * @return String
     */
    public String toBase64() {
        return Base64.encodeBytes(toDer().getBytes());
    }

    /**
     *
     * @param string byteString
     * @return Signature
     */
    public static Signature fromDer(ByteString string) {
        ByteString[] str = Der.removeSequence(string);
        ByteString rs = str[0];
        ByteString empty = str[1];
        if (!empty.isEmpty()) {
            throw new RuntimeException(String.format("trailing junk after DER sig: %s", BinaryAscii.hexFromBinary(empty)));
        }
        Object[] o = Der.removeInteger(rs);
        BigInteger r = new BigInteger(o[0].toString());
        ByteString rest = (ByteString) o[1];
        o = Der.removeInteger(rest);
        BigInteger s = new BigInteger(o[0].toString());
        empty = (ByteString) o[1];
        if (!empty.isEmpty()) {
            throw new RuntimeException(String.format("trailing junk after DER numbers: %s", BinaryAscii.hexFromBinary(empty)));
        }
        return new Signature(r, s);
    }

    /**
     *
     * @param string byteString
     * @return Signature
     */
    public static Signature fromBase64(ByteString string) {
        ByteString der = null;
        try {
            der = new ByteString(Base64.decode(string.getBytes()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Corrupted base64 string! Could not decode base64 from it");
        }
        return fromDer(der);
    }
}
