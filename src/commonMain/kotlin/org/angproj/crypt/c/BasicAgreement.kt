package org.angproj.crypto.c

import org.angproj.aux.num.BigInt

/**
 * The basic interface that basic Diffie-Hellman implementations
 * conforms to.
 */
public interface BasicAgreement {
    /**
     * initialise the agreement engine.
     */
    public fun init(param: CipherParameters)

    /**
     * return the field size for the agreement algorithm in bytes.
     */
    public val fieldSize: Int

    /**
     * given a public key from a given party calculate the next
     * message in the agreement sequence.
     */
    public fun calculateAgreement(pubKey: CipherParameters): BigInt
}
