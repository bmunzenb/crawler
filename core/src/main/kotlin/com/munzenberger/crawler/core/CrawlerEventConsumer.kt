package com.munzenberger.crawler.core

import java.util.function.Consumer

interface CrawlerEventConsumer : Consumer<CrawlerEvent> {
    override fun accept(status: CrawlerEvent) {
        when (status) {
            is CrawlerEvent.StartQueue -> onStartQueue(status.size)
            is CrawlerEvent.StartQueueEntry -> onStartQueueEntry(status.entry)
            is CrawlerEvent.AddToQueue -> onAddToQueue(status.entries)
            is CrawlerEvent.StartDownload -> onStartDownload(status.url, status.target)
            is CrawlerEvent.EndDownload -> onEndDownload(status.bytes)
            is CrawlerEvent.EndQueueEntry -> onEndQueueEntry(status.entry)
            CrawlerEvent.EndQueue -> onEndQueue()
            is CrawlerEvent.Error -> onError(status.error)
        }
    }

    fun onStartQueue(size: Int) {}

    fun onStartQueueEntry(entry: URLQueueEntry) {}

    fun onAddToQueue(entries: Collection<URLQueueEntry>) {}

    fun onStartDownload(
        url: String,
        target: String,
    ) {}

    fun onEndDownload(bytes: Long) {}

    fun onEndQueueEntry(entry: URLQueueEntry) {}

    fun onEndQueue() {}

    fun onError(error: Exception) {}
}
