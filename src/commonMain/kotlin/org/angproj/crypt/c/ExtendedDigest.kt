package org.angproj.crypto.c

public interface ExtendedDigest : Digest {
    /**
     * Return the size in bytes of the internal buffer the digest applies it's compression
     * function to.
     *
     * @return byte length of the digests internal buffer.
     */
    public val byteLength: Int
}
