package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.URLQueueEntry
import java.io.InputStream

interface DownloadWriter {
    val name: String

    fun write(inStream: InputStream): Long
}

fun interface DownloadWriterFactory {
    fun newWriter(entry: URLQueueEntry): DownloadWriter
}
