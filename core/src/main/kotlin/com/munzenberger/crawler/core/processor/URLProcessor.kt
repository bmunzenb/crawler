package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import java.net.URLConnection
import java.util.function.Consumer

fun interface URLProcessor {
    fun process(
        entry: URLQueueEntry,
        connection: URLConnection,
        callback: Consumer<CrawlerEvent>,
    ): Collection<URLQueueEntry>
}
