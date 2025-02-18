package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import org.jsoup.Jsoup
import java.io.InputStream
import java.net.URI
import java.util.function.Consumer

class LinkProcessor : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerEvent>,
        userAgent: String?,
    ): Collection<URLQueueEntry> {
        val connection =
            URI.create(entry.url).toURL().openConnection().apply {
                userAgent?.run { setRequestProperty("User-Agent", this) }
            }

        val contentType = connection.contentType
        val results =
            when {
                contentType?.contains("image", ignoreCase = true) == true ->
                    listOf(entry.copy(type = URLType.Image))
                else ->
                    processAsLink(
                        entry.url,
                        connection.getInputStream(),
                    )
            }

        return results
    }

    private fun processAsLink(
        url: String,
        inStream: InputStream,
    ): Collection<URLQueueEntry> {
        val doc = Jsoup.parse(inStream, "UTF-8", url)

        val images =
            doc
                .getElementsByTag("img")
                .stream()
                .map { it.attr("abs:src") }
                .distinct()
                .map { URLQueueEntry(URLType.Image, it, url) }
                .toList()

        val links =
            doc
                .getElementsByTag("a")
                .stream()
                .filter { !it.attr("href").startsWith("#") }
                .map { it.attr("abs:href") }
                .distinct()
                .map { URLQueueEntry(URLType.Link, it, url) }
                .toList()

        return images + links
    }
}
