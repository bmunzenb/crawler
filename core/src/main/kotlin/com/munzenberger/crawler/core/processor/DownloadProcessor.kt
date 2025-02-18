package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import java.net.HttpURLConnection
import java.net.URI
import java.util.function.Consumer

class DownloadProcessor(
    private val writerFactory: DownloadWriterFactory,
) : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerEvent>,
        userAgent: String?,
    ): Collection<URLQueueEntry> {
        val writer = writerFactory.create(entry.url, entry.referer)

        callback.accept(CrawlerEvent.StartDownload(entry.url, writer.name))
        val bytes = transfer(entry.url, writer, userAgent)
        callback.accept(CrawlerEvent.EndDownload(bytes))

        return emptyList()
    }

    private fun transfer(
        url: String,
        writer: DownloadWriter,
        userAgent: String?,
    ): Long {
        val connection =
            URI.create(url).toURL().openConnection().apply {
                userAgent?.run { setRequestProperty("User-Agent", this) }
            }

        if (connection is HttpURLConnection) {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                error("Received HTTP $code.")
            }
        }

        return writer.write(connection.getInputStream())
    }
}
