package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.URLQueueEntry
import java.util.function.Consumer

fun interface URLProcessor {
    fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry>
}
