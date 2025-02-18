package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import java.util.function.Consumer

fun interface URLProcessor {
    fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerEvent>,
        userAgent: String?,
    ): Collection<URLQueueEntry>
}
