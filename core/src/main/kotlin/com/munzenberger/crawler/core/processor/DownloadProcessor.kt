package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.URLQueueEntry
import java.net.HttpURLConnection
import java.net.URI
import java.util.function.Consumer

class DownloadProcessor(
    private val writerFactory: DownloadWriterFactory,
) : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry> {
        val writer = writerFactory.create(entry.url, entry.referer)

        callback.accept(CrawlerStatus.StartDownload(entry.url, writer.name))
        val bytes = transfer(entry.url, writer)
        callback.accept(CrawlerStatus.EndDownload(bytes))

        return emptyList()
    }

    private fun transfer(
        url: String,
        writer: DownloadWriter,
    ): Long {
        val connection = URI.create(url).toURL().openConnection()

        if (connection is HttpURLConnection) {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                error("Received HTTP $code.")
            }
        }

        return writer.write(connection.getInputStream())
    }
}
