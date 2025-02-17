package com.munzenberger.crawler.core

interface ReadOnlyProcessedRegistry {
    fun contains(url: String): Boolean
}

interface ProcessedRegistry : ReadOnlyProcessedRegistry {
    fun add(url: String)

    companion object {
        fun default() = SetProcessedRegistry()
    }
}

class SetProcessedRegistry(
    private val set: MutableSet<String> = mutableSetOf(),
) : ProcessedRegistry {
    override fun add(url: String) {
        set.add(url)
    }

    override fun contains(url: String): Boolean = set.contains(url)
}
