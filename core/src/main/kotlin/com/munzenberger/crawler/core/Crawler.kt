package com.munzenberger.crawler.core

import com.munzenberger.crawler.core.processor.URLProcessor
import java.net.HttpURLConnection
import java.net.URI
import java.util.function.Consumer

class Crawler(
    private val processor: URLProcessor,
    private val filter: URLFilter,
    private val queue: URLQueue = URLQueue.default(),
    private val registry: ProcessedRegistry = ProcessedRegistry.default(),
    private val callback: Consumer<CrawlerEvent> = LoggingCrawlerEventConsumer(),
    private val userAgent: String? = null,
) {
    fun execute(url: String) {
        queue.add(URLQueueEntry(URLType.Link, url, url))
        executeQueue()
    }

    @Suppress("TooGenericExceptionCaught")
    fun executeQueue() {
        callback.accept(CrawlerEvent.StartQueue(queue.size))

        while (!queue.isEmpty) {
            val entry = queue.pop()
            callback.accept(CrawlerEvent.StartQueueEntry(entry))
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
        val connection =
            URI.create(entry.url).toURL().openConnection().apply {
                userAgent?.run { setRequestProperty("User-Agent", this) }
            }

        if (connection is HttpURLConnection) {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                error("HTTP $code.")
            }
        }

        return processor
            .process(entry, connection, callback)
            .filter { !queue.contains(it.url) }
            .filter { !registry.contains(it.url) }
            .filter { filter.test(it.type, it.url) }
    }
}
