package com.munzenberger.crawler.core.processor

import java.net.URI
import java.nio.file.Path

class FileDownloadWriterFactory(
    private val targetDir: Path,
    private val withUrlPath: Boolean = false,
    private val bufferSize: Int = FileDownloadWriter.DEFAULT_BUFFER_SIZE,
) : DownloadWriterFactory {
    override fun create(
        url: String,
        referer: String,
    ): DownloadWriter {
        val source = URI.create(url).toURL()

        val parts =
            source.path
                .split("/")
                .filter { it.isNotEmpty() }

        val path =
            if (withUrlPath) {
                val root = targetDir.resolve(source.host)
                parts.fold(root) { acc, part -> acc.resolve(part) }
            } else {
                targetDir.resolve(parts.last())
            }

        return FileDownloadWriter(path, bufferSize)
    }
}
