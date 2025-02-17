package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import java.util.function.Consumer

class TypeBasedURLProcessor : URLProcessor {
    private val processorRegistry = mutableMapOf<URLType, URLProcessor>()

    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry> =
        when (val processor = processorRegistry[entry.type]) {
            null -> error("No processor found for URL type ${entry.type}.")
            else -> processor.process(entry, callback)
        }

    fun register(
        type: URLType,
        processor: URLProcessor,
    ) {
        processorRegistry[type] = processor
    }
}
