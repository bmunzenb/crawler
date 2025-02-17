package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.ReadOnlyProcessedRegistry
import com.munzenberger.crawler.core.ReadOnlyURLQueue
import com.munzenberger.crawler.core.URLFilter
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import java.util.function.Consumer

class TypeBasedURLProcessor : URLProcessor {
    private val processorRegistry = mutableMapOf<URLType, URLProcessor>()

    override fun process(
        entry: URLQueueEntry,
        filter: URLFilter,
        queue: ReadOnlyURLQueue,
        registry: ReadOnlyProcessedRegistry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry> =
        when (val processor = processorRegistry[entry.type]) {
            null -> error("No processor found for URL type ${entry.type}.")
            else -> processor.process(entry, filter, queue, registry, callback)
        }

    fun register(
        type: URLType,
        processor: URLProcessor,
    ) {
        processorRegistry[type] = processor
    }
}
