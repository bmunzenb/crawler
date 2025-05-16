package com.munzenberger.crawler.core

interface Logger {
    fun print(message: String)

    fun println(message: String)

    fun printStackTrace(error: Throwable)
}

object ConsoleLogger : Logger {
    override fun print(message: String) {
        kotlin.io.print(message)
    }

    override fun println(message: String) {
        kotlin.io.println(message)
    }

    override fun printStackTrace(error: Throwable) {
        error.printStackTrace(System.err)
    }
}
