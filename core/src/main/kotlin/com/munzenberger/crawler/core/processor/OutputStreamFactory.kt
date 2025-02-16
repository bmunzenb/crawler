package com.munzenberger.crawler.core.processor

import java.io.OutputStream

data class Output(
    val stream: OutputStream,
    val name: String,
)

fun interface OutputStreamFactory {
    fun open(
        url: String,
        referer: String,
    ): Output
}
