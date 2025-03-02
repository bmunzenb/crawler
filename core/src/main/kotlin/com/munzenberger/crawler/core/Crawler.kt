package com.munzenberger.crawler.core

import com.munzenberger.crawler.core.processor.URLProcessor
import java.util.function.Consumer
import java.util.function.Predicate

@Suppress("LongParameterList")
class Crawler(
    private val processor: URLProcessor,
    private val filter: Predicate<URLQueueEntry>,
    private val queue: URLQueue = URLQueue.default(),
    private val registry: ProcessedRegistry = ProcessedRegistry.default(),
    private val callback: Consumer<CrawlerEvent> = LoggingCrawlerEventConsumer(),
    private val userAgent: String? = null,
    private val urlConnector: URLConnector = URLConnector.default(),
) {
    fun execute(
        url: String,
        type: URLType = URLType.Link,
    ) {
        queue.add(URLQueueEntry(type, url, url))
        executeQueue()
    }

    @Suppress("TooGenericExceptionCaught")
    fun executeQueue() {
        callback.accept(CrawlerEvent.StartQueue(queue.size))

        while (!queue.isEmpty) {
            val entry = queue.pop()
            callback.accept(CrawlerEvent.StartQueueEntry(entry, queue.size))
            try {
                val results = executeForEntry(entry)
                if (results.isNotEmpty()) {
                    queue.addAll(results)
                    callback.accept(CrawlerEvent.AddToQueue(results))
                }
            } catch (e: Exception) {
                callback.accept(CrawlerEvent.Error(e))
            } finally {
                registry.add(entry.url)
                callback.accept(CrawlerEvent.EndQueueEntry(entry))
            }
        }

        callback.accept(CrawlerEvent.EndQueue)
    }

    private fun executeForEntry(entry: URLQueueEntry): Collection<URLQueueEntry> {
        val headers =
            buildMap {
                userAgent?.let { put("User-Agent", it) }
                put("Referer", entry.referer)
            }

        val connection = urlConnector.connect(entry, headers)

        return processor
            .process(entry, connection, callback)
            .filter { !queue.contains(it.url) }
            .filter { !registry.contains(it.url) }
            .filter { filter.test(it) }
    }
}
