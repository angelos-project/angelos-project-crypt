package org.angproj.crypto.c

/**
 * the foundation class for the exceptions thrown by the crypto packages.
 */
public open class RuntimeCryptoException : RuntimeException {
    /**
     * base constructor.
     */
    public constructor()

    /**
     * create a RuntimeCryptoException with the given message.
     *
     * @param message the message to be carried with the exception.
     */
    public constructor(message: String) : super(message)
}
