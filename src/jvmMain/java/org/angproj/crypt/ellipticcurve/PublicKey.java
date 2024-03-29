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
import org.angproj.crypt.ellipticcurve.utils.ByteString;
import org.angproj.crypt.ellipticcurve.utils.BinaryAscii;

import java.util.Arrays;
import static org.angproj.crypt.ellipticcurve.Curve.secp256k1;
import static org.angproj.crypt.ellipticcurve.Curve.supportedCurves;


public class PublicKey {

    public Point point;
    public Curve curve;

    /**
     *
     * @param point point
     * @param curve curve
     */
    public PublicKey(Point point, Curve curve) {
        this.point = point;
        this.curve = curve;
    }

    /**
     *
     * @return ByteString
     */
    public ByteString toByteString() {
        return toByteString(false);
    }

    /**
     *
     * @param encoded encoded
     * @return ByteString
     */
    public ByteString toByteString(boolean encoded) {
        ByteString xStr = BinaryAscii.stringFromNumber(point.x, curve.length());
        ByteString yStr = BinaryAscii.stringFromNumber(point.y, curve.length());
        xStr.insert(yStr.getBytes());
        if(encoded) {
            xStr.insert(0, new byte[]{0, 4} );
        }
        return xStr;
    }

    /**
     *
     * @return ByteString
     */
    public ByteString toDer() {
        long[] oidEcPublicKey = new long[]{1, 2, 840, 10045, 2, 1};
        ByteString encodeEcAndOid = Der.encodeSequence(Der.encodeOid(oidEcPublicKey), Der.encodeOid(curve.oid));
        return Der.encodeSequence(encodeEcAndOid, Der.encodeBitString(this.toByteString(true)));
    }

    /**
     *
     * @return String
     */
    public String toPem() {
        return Der.toPem(this.toDer(), "PUBLIC KEY");
    }

    /**
     *
     * @param string string
     * @return PublicKey
     */
    public static PublicKey fromPem(String string) {
        return PublicKey.fromDer(Der.fromPem(string));
    }

    /**
     *
     * @param string byteString
     * @return PublicKey
     */
    public static PublicKey fromDer(ByteString string) {
        ByteString[] str = Der.removeSequence(string);
        ByteString s1 = str[0];
        ByteString empty = str[1];
        if (!empty.isEmpty()) {
            throw new RuntimeException (String.format("trailing junk after DER pubkey: %s", BinaryAscii.hexFromBinary(empty)));
        }
        str = Der.removeSequence(s1);
        ByteString s2 = str[0];
        ByteString pointStrBitstring = str[1];
        Object[] o = Der.removeObject(s2);
        ByteString rest = (ByteString) o[1];
        o = Der.removeObject(rest);
        long[] oidCurve = (long[]) o[0];
        empty = (ByteString) o[1];
        if (!empty.isEmpty()) {
            throw new RuntimeException (String.format("trailing junk after DER pubkey objects: %s", BinaryAscii.hexFromBinary(empty)));
        }

        Curve curve = (Curve) Curve.curvesByOid.get(Arrays.hashCode(oidCurve));
        if (curve == null) {
            throw new RuntimeException(String.format("Unknown curve with oid %s. I only know about these: %s", Arrays.toString(oidCurve), Arrays.toString(supportedCurves.toArray())));
        }

        str = Der.removeBitString(pointStrBitstring);
        ByteString pointStr = str[0];
        empty = str[1];
        if (!empty.isEmpty()) {
            throw new RuntimeException (String.format("trailing junk after pubkey pointstring: %s", BinaryAscii.hexFromBinary(empty)));
        }
        return PublicKey.fromString(pointStr.substring(2), curve);
    }

    /**
     *
     * @param string byteString
     * @param curve curve
     * @param validatePoint validatePoint
     * @return PublicKey
     */
    public static PublicKey fromString(ByteString string, Curve curve, boolean validatePoint) {
        int baselen = curve.length();

        ByteString xs = string.substring(0, baselen);
        ByteString ys = string.substring(baselen);

        Point p = new Point(BinaryAscii.numberFromString(xs.getBytes()), BinaryAscii.numberFromString(ys.getBytes()));

        PublicKey publicKey = new PublicKey(p, curve);
        if (!validatePoint) {
            return publicKey;
        }
        if (p.isAtInfinity()) {
            throw new RuntimeException("Public Key point is at infinity");
        }
        if (!curve.contains(p)) {
            throw new RuntimeException(String.format("Point (%s,%s) is not valid for curve %s", p.x, p.y, curve.name));
        }
        if (!Math.multiply(p, curve.N, curve.N, curve.A, curve.P).isAtInfinity()) {
            throw new RuntimeException(String.format("Point (%s,%s) * %s.N is not at infinity", p.x, p.y, curve.name));
        }
        return publicKey;
    }

    /**
     *
     * @param string byteString
     * @param curve curve
     * @return PublicKey
     */
    public static PublicKey fromString(ByteString string, Curve curve) {
        return fromString(string, curve, true);
    }

    /**
     *
     * @param string byteString
     * @param validatePoint validatePoint
     * @return PublicKey
     */
    public static PublicKey fromString(ByteString string, boolean validatePoint) {
        return fromString(string, secp256k1, validatePoint);
    }

    /**
     *
     * @param string byteString
     * @return PublicKey
     */
    public static PublicKey fromString(ByteString string) {
        return fromString(string, true);
    }
}
