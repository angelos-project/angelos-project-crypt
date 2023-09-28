package org.angproj.crypt.hmac

import org.angproj.crypt.BinHex

interface HmacVectorParsing {

    fun msgIter(file: String, process: (msg: ByteArray, key: ByteArray, md: String, klen: Int, tlen: Int) -> Unit) {
        val data = file.split("]\n\n")[1]
        data.split("\n\n").forEach { entry ->
            val rows = entry.split("\n")
            // val count = rows[0].substring(8).trim().toInt()
            val klen = rows[1].substring(7).trim().toInt()
            val tlen = rows[2].substring(7).trim().toInt()
            val key = BinHex.decodeToBin(rows[3].substring(6).trim()).copyOfRange(0, klen)
            val message = BinHex.decodeToBin(rows[4].substring(6).trim())
            val mac = rows[5].substring(6).trim().lowercase()
            process(message, key, mac, klen, tlen)
        }
    }
}