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
import java.math.BigInteger;

public class Point {

    public BigInteger x;
    public BigInteger y;
    public BigInteger z;

    /**
     *
     * @param x x
     * @param y y
     */
    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
        this.z = BigInteger.ZERO;
    }

    /**
     *
     * @param x x
     * @param y y
     * @param z z
     */
    public Point(BigInteger x, BigInteger y, BigInteger z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isAtInfinity() {
        return this.y.equals(BigInteger.ZERO);
    }
}
