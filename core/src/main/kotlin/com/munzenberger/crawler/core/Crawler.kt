package com.munzenberger.crawler.core

import com.munzenberger.crawler.core.processor.URLProcessor
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLConnection
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
    private val maxRedirects: Int = DEFAULT_MAX_REDIRECTS,
) {
    companion object {
        private const val DEFAULT_MAX_REDIRECTS = 10
    }

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
        val connection = open(entry)
        return processor
            .process(entry, connection, callback)
            .filter { !queue.contains(it.url) }
            .filter { !registry.contains(it.url) }
            .filter { filter.test(it) }
    }

    @Suppress("MagicNumber")
    private fun open(
        entry: URLQueueEntry,
        locations: Set<String> = emptySet(),
    ): URLConnection {
        val connection =
            URI.create(entry.url).toURL().openConnection().apply {
                userAgent?.run { setRequestProperty("User-Agent", this) }
                setRequestProperty("Referer", entry.referer)
            }

        if (connection is HttpURLConnection) {
            // HttpURLConnection will not follow redirects if the protocol changes (e.g. HTTP -> HTTPS)
            // so we need to manually handle redirects
            connection.instanceFollowRedirects = false

            val code = connection.responseCode

            if (code in 300..399) {
                if (locations.size >= maxRedirects) {
                    error("Too many redirects.")
                }

                val location = connection.getHeaderField("Location")
                when {
                    location == null ->
                        error("HTTP $code without location in header.")
                    locations.contains(location) ->
                        error("Infinite redirect.")
                    else -> {
                        val e = URLQueueEntry(entry.type, location, entry.url)
                        return open(e, locations + location)
                    }
                }
            }

            if (code != HttpURLConnection.HTTP_OK) {
                error("HTTP $code.")
            }
        }

        return connection
    }
}
