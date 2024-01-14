package org.angproj.crypto.c

/**
 * a holding class for public/private parameter pairs.
 */
public class AsymmetricCipherKeyPair (
    private val publicParam: AsymmetricKeyParameter,
    private val privateParam: AsymmetricKeyParameter
) {
    /**
     * return the public key parameters.
     *
     * @return the public key parameters.
     */
    public val public: AsymmetricKeyParameter
        get() = publicParam

    /**
     * return the private key parameters.
     *
     * @return the private key parameters.
     */
    public val private: AsymmetricKeyParameter
        get() = privateParam
}
