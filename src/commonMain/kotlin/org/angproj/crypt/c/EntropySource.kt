package org.angproj.crypto.c

/**
 * Base interface describing an entropy source for a DRBG.
 */
public interface EntropySource {
    /**
     * Return whether or not this entropy source is regarded as prediction resistant.
     *
     * @return true if it is, false otherwise.
     */
    public val isPredictionResistant: Boolean

    /**
     * Return a byte array of entropy.
     *
     * @return  entropy bytes.
     */
    public fun getEntropy(): ByteArray

    /**
     * Return the number of bits of entropy this source can produce.
     *
     * @return size in bits of the return value of getEntropy.
     */
    public fun entropySize(): Int
}
