package org.bouncycastle.crypto.params

public open class ECKeyParameters
protected constructor(
    isPrivate: Boolean,
    parameters: ECDomainParameters?
) : AsymmetricKeyParameter(isPrivate) {
    private val parameters: ECDomainParameters

    init {
        if (null == parameters) {
            throw java.lang.NullPointerException("'parameters' cannot be null")
        }

        this.parameters = parameters
    }

    public fun getParameters(): ECDomainParameters {
        return parameters
    }
}
