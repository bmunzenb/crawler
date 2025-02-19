package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.CrawlerEvent
import com.munzenberger.crawler.core.URLQueueEntry
import com.munzenberger.crawler.core.URLType
import org.jsoup.Jsoup
import java.net.URLConnection
import java.util.function.Consumer

class HTMLProcessor : URLProcessor {
    override fun process(
        entry: URLQueueEntry,
        connection: URLConnection,
        callback: Consumer<CrawlerEvent>,
    ): Collection<URLQueueEntry> {
        val doc = Jsoup.parse(connection.getInputStream(), "UTF-8", entry.url)

        val images =
            doc
                .getElementsByTag("img")
                .stream()
                .map { it.attr("abs:src") }
                .distinct()
                .map { URLQueueEntry(URLType.Image, it, entry.url) }
                .toList()

        val links =
            doc
                .getElementsByTag("a")
                .stream()
                .filter { !it.attr("href").startsWith("#") }
                .map { it.attr("abs:href") }
                .distinct()
                .map { URLQueueEntry(URLType.Link, it, entry.url) }
                .toList()

        return images + links
    }
}
