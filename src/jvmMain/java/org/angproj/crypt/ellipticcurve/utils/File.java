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

import java.io.IOException;
import java.nio.file.*;

/*
 *This class handles the fileinput types as filename string
 *If using bytearray file as "signatureBinary.txt"
 *Use the readByte method
 **/
public class File {

    /**
     *
     * @param fileName fileName
     * @return String
     */
    public static String read(String fileName)
    {
        String content = "";
        try
        {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    /**
     *
     * @param fileName fileName
     * @return byte[]
     */
    public static byte[] readBytes(String fileName)
    {
        byte[] content = null;
        try
        {
            content = Files.readAllBytes(Paths.get(fileName));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return content;
    }

}