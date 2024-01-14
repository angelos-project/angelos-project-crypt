package org.angproj.crypto.c

public interface RawAgreement {
    public fun init(parameters: CipherParameters)

    public val agreementSize: Int

    public fun calculateAgreement(publicKey: CipherParameters, buf: ByteArray, off: Int)
}
