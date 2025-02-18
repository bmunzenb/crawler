package com.munzenberger.crawler.core

import java.util.Locale

class LoggingCrawlerEventConsumer(
    private val locale: Locale = Locale.getDefault(),
) : CrawlerEventConsumer {
    // statistics
    private var queueStart: Long = 0
    private var entryCount: Int = 0
    private var entryStart: Long = 0
    private var downloadStart: Long = 0

    override fun onStartQueue(size: Int) {
        queueStart = System.currentTimeMillis()
        val msg =
            String.format(
                locale,
                "Starting crawler with %,d %s in queue...",
                size,
                "url".plural(size),
            )
        println(msg)
    }

    override fun onEndQueue() {
        val elapsed = System.currentTimeMillis() - queueStart
        val msg =
            String.format(
                locale,
                "Processed %,d %s in %s.",
                entryCount,
                "URL".plural(entryCount),
                elapsed.formatElapsed,
            )
        println(msg)
    }

    override fun onStartQueueEntry(entry: URLQueueEntry) {
        entryCount++
        entryStart = System.currentTimeMillis()
        val msg =
            String.format(
                locale,
                "[%,d] Processing %s %s...",
                entryCount,
                entry.type,
                entry.url,
            )
        println(msg)
    }

    override fun onAddToQueue(entries: Collection<URLQueueEntry>) {
        val groups = entries.groupingBy { it.type }.eachCount()
        val msg =
            String.format(
                locale,
                "Added to queue: %s",
                groups,
            )
        println(msg)
    }

    override fun onStartDownload(
        url: String,
        target: String,
    ) {
        downloadStart = System.currentTimeMillis()
        val msg =
            String.format(
                locale,
                "Download: %s -> %s ... ",
                url,
                target,
            )
        print(msg)
    }

    override fun onEndDownload(bytes: Long) {
        val elapsed = System.currentTimeMillis() - downloadStart
        val msg =
            String.format(
                locale,
                "%s in %s.",
                bytes.formatBytes(locale),
                elapsed.formatElapsed,
            )
        println(msg)
    }

    override fun onEndQueueEntry(entry: URLQueueEntry) {
        val elapsed = System.currentTimeMillis() - entryStart
        val msg =
            String.format(
                locale,
                "Finished processing %s in %s.",
                entry.type,
                elapsed.formatElapsed,
            )
        println(msg)
    }

    override fun onError(error: Exception) {
        error.printStackTrace(System.err)
    }
}

private fun String.plural(count: Int): String =
    when {
        count == 1 -> this
        else -> "${this}s"
    }

private const val MILLIS_PER_SECOND = 1000
private const val SECONDS_PER_MINUTE = 60
private const val MINUTES_PER_HOUR = 60

val Long.formatElapsed: String
    get() {
        val seconds = this / MILLIS_PER_SECOND
        val minutes = seconds / SECONDS_PER_MINUTE
        val hours = minutes / MINUTES_PER_HOUR

        if (hours > 0) {
            return "$hours h ${minutes % MINUTES_PER_HOUR} m ${seconds % SECONDS_PER_MINUTE} s"
        }

        if (minutes > 0) {
            return "$minutes m ${seconds % SECONDS_PER_MINUTE} s"
        }

        if (seconds > 0) {
            return "$seconds s"
        }

        return "$this ms"
    }

private const val BYTE_BOUNDARY = 1024f

@Suppress("ReturnCount")
fun Long.formatBytes(locale: Locale): String {
    if (this < BYTE_BOUNDARY) {
        return String.format(locale, "%,d bytes", this)
    }

    val kb = this / BYTE_BOUNDARY

    if (kb < BYTE_BOUNDARY) {
        return String.format(locale, "%,.1f KB", kb)
    }

    val mb = kb / BYTE_BOUNDARY

    if (mb < BYTE_BOUNDARY) {
        return String.format(locale, "%,.1f MB", mb)
    }

    val gb = mb / BYTE_BOUNDARY

    return String.format(locale, "%,.1f GB", gb)
}
