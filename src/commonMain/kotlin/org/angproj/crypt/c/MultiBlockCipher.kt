package org.angproj.crypto.c

/**
 * Base interface for a cipher engine capable of processing multiple blocks at a time.
 */
public interface MultiBlockCipher : BlockCipher {
    /**
     * Return the multi-block size for this cipher (in bytes).
     *
     * @return the multi-block size for this cipher in bytes.
     */
    public val multiBlockSize: Int

    /**
     * Process blockCount blocks from input in offset inOff and place the output in
     * out from offset outOff.
     *
     * @param in input data array.
     * @param inOff start of input data in in.
     * @param blockCount number of blocks to be processed.
     * @param out output data array.
     * @param outOff start position for output data.
     * @return number of bytes written to out.
     * @throws DataLengthException
     * @throws IllegalStateException
     */
    //@Throws(DataLengthException, IllegalStateException)
    public fun processBlocks(input: ByteArray, inOff: Int, blockCount: Int, out: ByteArray, outOff: Int): Int
}
