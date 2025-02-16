package com.munzenberger.crawler.core

import java.util.function.Consumer

interface CrawlerStatusConsumer : Consumer<CrawlerStatus> {
    override fun accept(status: CrawlerStatus) {
        when (status) {
            is CrawlerStatus.StartQueue -> onStartQueue(status.size)
            is CrawlerStatus.StartQueueEntry -> onStartQueueEntry(status.entry)
            is CrawlerStatus.AddToQueue -> onAddToQueue(status.entries)
            is CrawlerStatus.StartDownload -> onStartDownload(status.url, status.target)
            is CrawlerStatus.EndDownload -> onEndDownload(status.bytes)
            is CrawlerStatus.EndQueueEntry -> onEndQueueEntry(status.entry)
            CrawlerStatus.EndQueue -> onEndQueue()
            is CrawlerStatus.Error -> onError(status.error)
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
