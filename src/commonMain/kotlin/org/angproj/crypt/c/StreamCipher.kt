package org.angproj.crypto.c

/**
 * the interface stream ciphers conform to.
 */
public interface StreamCipher {
    /**
     * Initialise the cipher.
     *
     * @param forEncryption if true the cipher is initialised for
     * encryption, if false for decryption.
     * @param params the key and other data required by the cipher.
     * @exception IllegalArgumentException if the params argument is
     * inappropriate.
     */
    //@Throws(IllegalArgumentException)
    public fun init(forEncryption: Boolean, params: CipherParameters)

    /**
     * Return the name of the algorithm the cipher implements.
     *
     * @return the name of the algorithm the cipher implements.
     */
    public val algorithmName: String

    /**
     * encrypt/decrypt a single byte returning the result.
     *
     * @param in the byte to be processed.
     * @return the result of processing the input byte.
     */
    public fun returnByte(input: Byte): Byte

    /**
     * process a block of bytes from in putting the result into out.
     *
     * @param in the input byte array.
     * @param inOff the offset into the in array where the data to be processed starts.
     * @param len the number of bytes to be processed.
     * @param out the output buffer the processed bytes go into.
     * @param outOff the offset into the output byte array the processed data starts at.
     * @return the number of bytes produced - should always be len.
     * @exception DataLengthException if the output buffer is too small.
     */
    //@Throws(DataLengthException)
    public fun processBytes(input: ByteArray, inOff: Int, len: Int, out: ByteArray, outOff: Int): Int

    /**
     * reset the cipher. This leaves it in the same state
     * it was at after the last init (if there was one).
     */
    public fun reset()
}
