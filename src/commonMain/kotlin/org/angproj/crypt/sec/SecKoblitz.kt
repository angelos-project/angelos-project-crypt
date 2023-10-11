package org.angproj.crypt.sec

public interface SecKoblitz: DomainParameters {
    public val p: ByteArray
    public val a: ByteArray
    public val b: ByteArray
    public val G: ByteArray
    public val Gc: ByteArray
    public val n: ByteArray
    public val h: ByteArray
}