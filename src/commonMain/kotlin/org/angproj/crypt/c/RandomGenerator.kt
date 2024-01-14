package org.angproj.crypto.c

/**
 * Generic interface for objects generating random bytes.
 */
public interface RandomGenerator {
    /**
     * Add more seed material to the generator.
     *
     * @param seed a byte array to be mixed into the generator's state.
     */
    public fun addSeedMaterial(seed: ByteArray)

    /**
     * Add more seed material to the generator.
     *
     * @param seed a long value to be mixed into the generator's state.
     */
    public fun addSeedMaterial(seed: Long)

    /**
     * Fill bytes with random values.
     *
     * @param bytes byte array to be filled.
     */
    public fun nextBytes(bytes: ByteArray)

    /**
     * Fill part of bytes with random values.
     *
     * @param bytes byte array to be filled.
     * @param start index to start filling at.
     * @param len length of segment to fill.
     */
    public fun nextBytes(bytes: ByteArray, start: Int, len: Int)
}
