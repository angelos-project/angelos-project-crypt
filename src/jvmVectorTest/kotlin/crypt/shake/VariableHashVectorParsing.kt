package org.angproj.crypt.shake

import org.angproj.aux.util.BinHex


interface VariableHashVectorParsing {

    fun msgIter(file: String, process: (msg: ByteArray, md: String) -> Unit) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            val size = rows[0].substring(6).trim().toInt() / 8
            val message = BinHex.decodeToBin(rows[1].substring(6).trim()).copyOfRange(0, size)
            val digest = rows[2].substring(9).trim().lowercase()
            process(message, digest)
        }
    }
}