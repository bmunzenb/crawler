package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.ReadOnlyProcessedRegistry
import com.munzenberger.crawler.core.ReadOnlyURLQueue
import com.munzenberger.crawler.core.URLFilter
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import java.util.function.Consumer

class DefaultURLProcessor(
    outFactory: OutputStreamFactory,
) : URLProcessor {
    private val processor =
        TypeBasedURLProcessor().apply {
            register(URLType.Link, LinkProcessor())
            register(URLType.Image, DownloadProcessor(outFactory))
        }

    override fun process(
        entry: URLQueueEntry,
        filter: URLFilter,
        queue: ReadOnlyURLQueue,
        registry: ReadOnlyProcessedRegistry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry> = processor.process(entry, filter, queue, registry, callback)
}
