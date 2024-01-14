package org.angproj.crypto.c

/**
 * base interface for general purpose Mac based byte derivation functions.
 */
public interface MacDerivationFunction : DerivationFunction {
    /**
     * return the MAC used as the basis for the function
     *
     * @return the Mac.
     */
    public val mac: Mac
}
