package org.angproj.crypto.c

/**
 * base interface for general purpose byte derivation functions.
 */
public interface DerivationFunction {
    public fun init(param: DerivationParameters)

    //@Throws(DataLengthException, IllegalArgumentException)
    public fun generateBytes(out: ByteArray, outOff: Int, len: Int): Int
}
