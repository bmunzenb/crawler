package com.munzenberger.crawler.core.processor

import java.io.InputStream

interface DownloadWriter {
    val name: String

    fun write(inStream: InputStream): Long
}

fun interface DownloadWriterFactory {
    fun newWriter(
        url: String,
        referer: String,
    ): DownloadWriter
}
