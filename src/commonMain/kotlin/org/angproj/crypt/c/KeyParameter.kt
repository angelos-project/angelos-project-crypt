package org.angproj.crypto.c

public class KeyParameter
private constructor(length: Int) : CipherParameters {
    public val key: ByteArray = ByteArray(length)

    public val keyLength: Int
        get() = key.size

    public constructor(key: ByteArray, keyOff: Int = 0, keyLen: Int = key.size) : this(keyLen) {
        key.copyInto(this.key, 0, keyOff, keyLen)
    }

    public fun copyTo(buf: ByteArray, off: Int, len: Int) {
        check (key.size == len) { "len" }
        key.copyInto(buf, 0, off, len)
    }

    public fun reverse(): KeyParameter = KeyParameter(key.reversedArray())
}
