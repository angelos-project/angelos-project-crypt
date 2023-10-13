package org.angproj.crypt.sec

public interface SecDomainParameters: DomainParameters {
    public val strength: Int
    public val size: Int

    public val a: ByteArray
    public val b: ByteArray
    public val G: ByteArray
    public val Gc: ByteArray
    public val n: ByteArray
    public val h: ByteArray
}