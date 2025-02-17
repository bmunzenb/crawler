package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerStatus
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import org.jsoup.Jsoup
import java.io.InputStream
import java.net.URI
import java.util.function.Consumer

class LinkProcessor : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        callback: Consumer<CrawlerStatus>,
    ): Collection<URLQueueEntry> {
        val connection = URI.create(entry.url).toURL().openConnection()
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
                .stream()
                .map { it.attr("abs:img") }
                .distinct()
                .map { URLQueueEntry(URLType.Image, it, url) }
                .toList()

        val links =
            doc
                .stream()
                .filter { !it.attr("href").startsWith("#") }
                .map { it.attr("abs:href") }
                .distinct()
                .map { URLQueueEntry(URLType.Link, it, url) }
                .toList()

        return images + links
    }
}
