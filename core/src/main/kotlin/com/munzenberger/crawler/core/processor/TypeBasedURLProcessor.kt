package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import java.util.function.Consumer

class TypeBasedURLProcessor : URLProcessor {
    private val processorRegistry = mutableMapOf<URLType, URLProcessor>()

    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerEvent>,
        userAgent: String?,
    ): Collection<URLQueueEntry> =
        when (val processor = processorRegistry[entry.type]) {
            null -> error("No processor found for URL type ${entry.type}.")
            else -> processor.process(entry, callback, userAgent)
        }

    fun register(
        type: URLType,
        processor: URLProcessor,
    ) {
        processorRegistry[type] = processor
    }
}
