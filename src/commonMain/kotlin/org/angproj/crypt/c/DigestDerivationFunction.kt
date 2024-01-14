package org.angproj.crypto.c

/**
 * base interface for general purpose Digest based byte derivation functions.
 */
public interface DigestDerivationFunction : DerivationFunction {
    /**
     * return the message digest used as the basis for the function
     */
    public val digest: Digest
}
