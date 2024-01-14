package org.angproj.crypto.c


/**
 * Block cipher engines are expected to conform to this interface.
 */
public interface BlockCipher {
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
     * Return the block size for this cipher (in bytes).
     *
     * @return the block size for this cipher in bytes.
     */
    public val blockSize: Int

    /**
     * Process one block of input from the array in and write it to
     * the out array.
     *
     * @param input the array containing the input data.
     * @param inOff offset into the in array the data starts at.
     * @param output the array the output data will be copied into.
     * @param outOff the offset into the out array the output will start at.
     * @exception DataLengthException if there isn't enough data in input , or
     * space in out.
     * @exception IllegalStateException if the cipher isn't initialised.
     * @return the number of bytes processed and produced.
     */
    //@Throws(DataLengthException, IllegalStateException)
    public fun processBlock(input: ByteArray, inOff: Int, output: ByteArray, outOff: Int): Int

    /**
     * Reset the cipher. After resetting the cipher is in the same state
     * as it was after the last init (if there was one).
     */
    public fun reset()
}
