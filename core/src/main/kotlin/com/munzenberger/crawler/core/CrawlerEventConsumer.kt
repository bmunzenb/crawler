package com.munzenberger.crawler.core

import java.util.function.Consumer

interface CrawlerEventConsumer : Consumer<CrawlerEvent> {
    override fun accept(event: CrawlerEvent) {
        when (event) {
            is CrawlerEvent.StartQueue -> onStartQueue(event.size)
            is CrawlerEvent.StartQueueEntry -> onStartQueueEntry(event.entry, event.queueSize)
            is CrawlerEvent.AddToQueue -> onAddToQueue(event.entries)
            is CrawlerEvent.StartDownload -> onStartDownload(event.url, event.target)
            is CrawlerEvent.EndDownload -> onEndDownload(event.bytes)
            is CrawlerEvent.EndQueueEntry -> onEndQueueEntry(event.entry)
            CrawlerEvent.EndQueue -> onEndQueue()
            is CrawlerEvent.Error -> onError(event.error)
            else -> onCrawlerEvent(event)
        }
    }

    fun onStartQueue(size: Int) {}

    fun onStartQueueEntry(
        entry: URLQueueEntry,
        queueSize: Int,
    ) {}

    fun onAddToQueue(entries: Collection<URLQueueEntry>) {}

    fun onStartDownload(
        url: String,
        target: String,
    ) {}

    fun onEndDownload(bytes: Long) {}

    fun onEndQueueEntry(entry: URLQueueEntry) {}

    fun onEndQueue() {}

    fun onError(error: Exception) {}

    fun onCrawlerEvent(event: CrawlerEvent) {}
}
