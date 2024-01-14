package org.angproj.crypto.c

/**
 * Generic signer interface for hash based and message recovery signers.
 */
public interface Signer {
    /**
     * Initialise the signer for signing or verification.
     *
     * @param forSigning true if for signing, false otherwise
     * @param param necessary parameters.
     */
    public fun init(forSigning: Boolean, param: CipherParameters)

    /**
     * update the internal digest with the byte b
     */
    public fun update(b: Byte)

    /**
     * update the internal digest with the byte array in
     */
    public fun update(`in`: ByteArray, off: Int, len: Int)

    /**
     * generate a signature for the message we've been loaded with using
     * the key we were initialised with.
     */
    //@Throws(CryptoException, DataLengthException)
    public fun generateSignature(): ByteArray

    /**
     * return true if the internal state represents the signature described
     * in the passed in array.
     */
    public fun verifySignature(signature: ByteArray): Boolean

    /**
     * reset the internal state
     */
    public fun reset()
}
