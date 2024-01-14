package org.angproj.crypto.c


/**
 * An [AEADCipher] based on a [BlockCipher].
 */
public interface AEADBlockCipher : AEADCipher {
    /**
     * return the [BlockCipher] this object wraps.
     *
     * @return the [BlockCipher] this object wraps.
     */
    public val underlyingCipher: BlockCipher
}
