package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import java.net.URLConnection
import java.util.function.Consumer

class DownloadImagesProcessor(
    writerFactory: DownloadWriterFactory,
) : URLProcessor {
    private val htmlProcessor = HTMLProcessor()
    private val downloadProcessor = DownloadProcessor(writerFactory)

    override fun process(
        entry: URLQueueEntry,
        connection: URLConnection,
        callback: Consumer<CrawlerEvent>,
    ): Collection<URLQueueEntry> {
        val contentType = connection.contentType
        return when {
            contentType?.contains("image/") == true -> downloadProcessor.process(entry, connection, callback)
            contentType?.contains("text/html") == true -> htmlProcessor.process(entry, connection, callback)
            else -> emptyList()
        }
    }
}
