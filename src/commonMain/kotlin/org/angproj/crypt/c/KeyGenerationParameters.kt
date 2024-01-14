package org.angproj.crypto.c

import org.angproj.crypt.SecureRandom

/**
 * The base class for parameters to key generators.
 */

/**
 * return the bit strength for keys produced by this generator,
 *
 * @return the strength of the keys this generator produces (in bits).
 */
public class KeyGenerationParameters
    (
    private val random: SecureRandom,
    public val strength: Int
) {

    /**
     * return the random source associated with this
     * generator.
     *
     * @return the generators random source.
     */
    public fun getRandom(): SecureRandom {
        return random
    }
}
