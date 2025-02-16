package com.munzenberger.crawler.core

sealed class CrawlerStatus {
    data class StartQueue(
        val size: Int,
    ) : CrawlerStatus()

    data class StartQueueEntry(
        val entry: URLQueueEntry,
    ) : CrawlerStatus()

    data class AddToQueue(
        val entries: Collection<URLQueueEntry>,
    ) : CrawlerStatus()

    data class StartDownload(
        val url: String,
        val target: String,
    ) : CrawlerStatus()

    data class EndDownload(
        val bytes: Long,
    ) : CrawlerStatus()

    data class EndQueueEntry(
        val entry: URLQueueEntry,
    ) : CrawlerStatus()

    data object EndQueue : CrawlerStatus()

    data class Error(
        val error: Exception,
    ) : CrawlerStatus()
}
