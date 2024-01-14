package org.angproj.crypto.c

public interface StagedAgreement : BasicAgreement {
    public fun calculateStage(pubKey: CipherParameters): AsymmetricKeyParameter
}
