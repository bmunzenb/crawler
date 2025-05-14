package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import java.net.URLConnection
import java.util.function.Consumer

class DownloadProcessor(
    private val writerFactory: DownloadWriterFactory,
) : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        connection: URLConnection,
        callback: Consumer<CrawlerEvent>,
    ): Collection<URLQueueEntry> {
        val writer = writerFactory.newWriter(entry)

        callback.accept(CrawlerEvent.StartDownload(entry.url, writer.name))
        val bytes = writer.write(connection.getInputStream())
        callback.accept(CrawlerEvent.EndDownload(bytes))

        return emptyList()
    }
}
