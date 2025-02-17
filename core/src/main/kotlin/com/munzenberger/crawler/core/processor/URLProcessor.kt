package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.ReadOnlyProcessedRegistry
import com.munzenberger.crawler.core.ReadOnlyURLQueue
import com.munzenberger.crawler.core.URLFilter
import com.munzenberger.crawler.core.URLQueueEntry
import java.util.function.Consumer

fun interface URLProcessor {
    fun process(
        entry: URLQueueEntry,
        filter: URLFilter,
        queue: ReadOnlyURLQueue,
        registry: ReadOnlyProcessedRegistry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry>
}
