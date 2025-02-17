package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.ReadOnlyProcessedRegistry
import com.munzenberger.crawler.core.ReadOnlyURLQueue
import com.munzenberger.crawler.core.URLFilter
import com.munzenberger.crawler.core.URLQueueEntry
import okio.buffer
import okio.sink
import okio.source
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.util.function.Consumer

class DownloadProcessor(
    private val outFactory: OutputStreamFactory,
    private val bufferSize: Int = 8192,
) : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        filter: URLFilter,
        queue: ReadOnlyURLQueue,
        registry: ReadOnlyProcessedRegistry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry> {
        val output = outFactory.open(entry.url, entry.referer)

        callback.accept(CrawlerStatus.StartDownload(entry.url, output.name))
        val bytes = transfer(entry.url, output.stream)
        callback.accept(CrawlerStatus.EndDownload(bytes))

        return emptyList()
    }

    private fun transfer(
        url: String,
        outStream: OutputStream,
    ): Long {
        val connection = URI.create(url).toURL().openConnection()

        if (connection is HttpURLConnection) {
            val code = connection.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                error("Received HTTP $code.")
            }
        }

        val source = connection.getInputStream().source().buffer()
        val sink = outStream.sink().buffer()

        return source.use { inSource ->
            sink.use { outSink ->

                var total: Long = 0
                val byteArray = ByteArray(bufferSize)
                var b = inSource.read(byteArray, 0, byteArray.size)

                while (b > 0) {
                    outSink.write(byteArray, 0, b)
                    total += b.toLong()
                    b = inSource.read(byteArray, 0, byteArray.size)
                }

                total
            }
        }
    }
}
