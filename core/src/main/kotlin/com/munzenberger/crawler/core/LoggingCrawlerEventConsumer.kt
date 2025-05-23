package com.munzenberger.crawler.core

import java.util.Locale

class LoggingCrawlerEventConsumer(
    private val locale: Locale = Locale.getDefault(),
    private val logger: Logger = ConsoleLogger,
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
                "Starting crawler with %,d %s in queue",
                size,
                "URL".plural(size),
            )
        logger.println(msg)
    }

    override fun onEndQueue() {
        val elapsed = System.currentTimeMillis() - queueStart
        val msg =
            String.format(
                locale,
                "Queue complete, processed %,d %s in %s.",
                entryCount,
                "URL".plural(entryCount),
                elapsed.formatElapsed,
            )
        logger.println(msg)
    }

    override fun onStartQueueEntry(
        entry: URLQueueEntry,
        queueSize: Int,
    ) {
        entryCount++
        entryStart = System.currentTimeMillis()
        val msg =
            String.format(
                locale,
                "[%,d/%,d] Processing %s %s ...",
                entryCount,
                queueSize,
                entry.type,
                entry.url,
            )
        logger.println(msg)
    }

    override fun onAddToQueue(entries: Collection<URLQueueEntry>) {
        val groups = entries.groupingBy { it.type }.eachCount()
        val msg =
            String.format(
                locale,
                "Added %,d %s to queue: %s",
                entries.size,
                "URL".plural(entries.size),
                groups.entries.joinToString {
                    String.format(
                        locale,
                        "%,d %s",
                        it.value,
                        it.key.name.plural(it.value),
                    )
                },
            )
        logger.println(msg)
    }

    override fun onStartDownload(
        url: String,
        target: String,
    ) {
        downloadStart = System.currentTimeMillis()
        val msg =
            String.format(
                locale,
                "Download to %s ... ",
                target,
            )
        logger.print(msg)
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
        logger.println(msg)
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
        logger.println(msg)
    }

    override fun onError(error: Exception) {
        println("Error: ${error.message}")
        logger.printStackTrace(error)
    }

    override fun onCrawlerEvent(event: CrawlerEvent) {
        logger.println(event.toString())
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
