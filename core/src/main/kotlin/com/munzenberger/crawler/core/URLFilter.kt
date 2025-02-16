package com.munzenberger.crawler.core

fun interface URLFilter {
    fun test(
        type: URLType,
        url: String,
    ): Boolean
}
