package com.munzenberger.crawler.core

data class URLQueueEntry(
    val type: URLType,
    val url: String,
    val referer: String,
)

interface ReadOnlyURLQueue {
    val size: Int

    val isEmpty: Boolean
        get() = size == 0

    fun contains(url: String): Boolean
}

interface URLQueue : ReadOnlyURLQueue {
    fun add(entry: URLQueueEntry)

    fun addAll(entries: Collection<URLQueueEntry>) {
        entries.forEach(::add)
    }

    fun pop(): URLQueueEntry

    companion object {
        fun default() = ListURLQueue()
    }
}

class ListURLQueue(
    private val list: MutableList<URLQueueEntry> = mutableListOf(),
) : URLQueue {
    override val size: Int
        get() = list.size

    override val isEmpty: Boolean
        get() = list.isEmpty()

    override fun add(entry: URLQueueEntry) {
        list.add(entry)
    }

    override fun addAll(entries: Collection<URLQueueEntry>) {
        list.addAll(entries)
    }

    override fun pop(): URLQueueEntry = list.removeFirst()

    override fun contains(url: String): Boolean = list.find { it.url == url } != null
}
