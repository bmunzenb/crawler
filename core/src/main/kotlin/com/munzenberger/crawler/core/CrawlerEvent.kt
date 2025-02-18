package com.munzenberger.crawler.core

sealed class CrawlerEvent {
    data class StartQueue(
        val size: Int,
    ) : CrawlerEvent()

    data class StartQueueEntry(
        val entry: URLQueueEntry,
    ) : CrawlerEvent()

    data class AddToQueue(
        val entries: Collection<URLQueueEntry>,
    ) : CrawlerEvent()

    data class StartDownload(
        val url: String,
        val target: String,
    ) : CrawlerEvent()

    data class EndDownload(
        val bytes: Long,
    ) : CrawlerEvent()

    data class EndQueueEntry(
        val entry: URLQueueEntry,
    ) : CrawlerEvent()

    data object EndQueue : CrawlerEvent()

    data class Error(
        val error: Exception,
    ) : CrawlerEvent()
}
