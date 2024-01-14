package org.angproj.crypto.c

/**
 * the foundation class for the hard exceptions thrown by the crypto packages.
 */
public open class CryptoException(message: String = "", cause: Throwable = Throwable()) : Exception(message, cause)
