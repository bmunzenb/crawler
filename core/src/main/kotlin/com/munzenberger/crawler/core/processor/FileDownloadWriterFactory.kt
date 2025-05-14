package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.URLQueueEntry
import java.nio.file.Path

class FileDownloadWriterFactory(
    private val targetDir: Path,
    private val pathSpec: PathSpec = FilenamePathSpec,
    private val bufferSize: Int = FileDownloadWriter.DEFAULT_BUFFER_SIZE,
) : DownloadWriterFactory {
    override fun newWriter(entry: URLQueueEntry): DownloadWriter {
        val path = pathSpec.pathFor(entry, targetDir)
        return FileDownloadWriter(path, bufferSize)
    }
}
