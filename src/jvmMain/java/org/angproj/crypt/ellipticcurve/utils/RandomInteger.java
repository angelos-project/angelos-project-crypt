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
import java.security.SecureRandom;
import java.util.Random;


public class RandomInteger {

    /**
     *
     * @param start start
     * @param end end
     * @return BigInteger
     */
    public static BigInteger between(BigInteger start, BigInteger end) {
        Random random = new SecureRandom();
        return new BigInteger(end.toByteArray().length * 8 - 1, random).abs().add(start);
    }
}
