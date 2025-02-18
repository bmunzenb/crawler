package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import java.util.function.Consumer

class DownloadImagesProcessor(
    writerFactory: DownloadWriterFactory,
) : URLProcessor {
    private val processor =
        TypeBasedURLProcessor().apply {
            register(URLType.Link, LinkProcessor())
            register(URLType.Image, DownloadProcessor(writerFactory))
        }

    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerEvent>,
        userAgent: String?,
    ): Collection<URLQueueEntry> = processor.process(entry, callback, userAgent)
}
