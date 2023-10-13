package org.angproj.crypt.sec

public interface SecTKoblitz: SecDomainParameters {
    //public val p: ByteArray
    public override val a: ByteArray
    public override val b: ByteArray
    public override val G: ByteArray
    public override val Gc: ByteArray
    public override val n: ByteArray
    public override val h: ByteArray
}