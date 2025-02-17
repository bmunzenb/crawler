package com.munzenberger.crawler.core

interface ProcessedRegistry {
    fun add(url: String)

    fun contains(url: String): Boolean

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
