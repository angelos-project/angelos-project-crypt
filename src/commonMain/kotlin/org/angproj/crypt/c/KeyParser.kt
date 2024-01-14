package org.angproj.crypto.c

import org.angproj.aux.io.Readable

public interface KeyParser {
    //@Throws(IOException)
    public fun readKey(stream: Readable): AsymmetricKeyParameter
}
