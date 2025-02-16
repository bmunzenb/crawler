package com.munzenberger.crawler.core

import com.munzenberger.crawler.core.processor.URLProcessor
import java.util.function.Consumer

class Crawler(
    private val processor: URLProcessor,
    private val filter: URLFilter,
    private val queue: URLQueue = ListURLQueue(),
    private val registry: ProcessedRegistry = SetProcessedRegistry(),
    private val callback: Consumer<CrawlerStatus> = LoggingCrawlerStatusConsumer(),
) {
    fun execute(url: String) {
        queue.add(URLQueueEntry(URLType.Link, url, url))
        executeQueue()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun executeQueue() {
        callback.accept(CrawlerStatus.StartQueue(queue.size))

        while (!queue.isEmpty) {
            val entry = queue.pop()
            callback.accept(CrawlerStatus.StartQueueEntry(entry))
            try {
                val results = processor.process(entry, filter, queue, registry, callback)
                if (results.isNotEmpty()) {
                    queue.addAll(results)
                    callback.accept(CrawlerStatus.AddToQueue(results))
                }
            } catch (e: Exception) {
                callback.accept(CrawlerStatus.Error(e))
            } finally {
                callback.accept(CrawlerStatus.EndQueueEntry(entry))
            }
        }

        callback.accept(CrawlerStatus.EndQueue)
    }
}
