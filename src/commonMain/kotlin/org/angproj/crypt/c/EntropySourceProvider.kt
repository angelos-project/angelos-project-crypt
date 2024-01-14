package org.angproj.crypto.c

public interface EntropySourceProvider {
    public fun get(bitsRequired: Int): EntropySource
}
