package org.angproj.crypto.c

public interface Wrapper {
    public fun init(forWrapping: Boolean, param: CipherParameters)

    /**
     * Return the name of the algorithm the wrapper implements.
     *
     * @return the name of the algorithm the wrapper implements.
     */
    public val algorithmName: String

    public fun wrap(input: ByteArray, inOff: Int, inLen: Int): ByteArray

    //@Throws(InvalidCipherTextException)
    public fun unwrap(input: ByteArray, inOff: Int, inLen: Int): ByteArray
}
