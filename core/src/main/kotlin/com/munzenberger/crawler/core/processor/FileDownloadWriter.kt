package com.munzenberger.crawler.core.processor

import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FileDownloadWriter(
    private val path: Path,
    private val bufferSize: Int = DEFAULT_BUFFER_SIZE,
) : DownloadWriter {
    companion object {
        const val DEFAULT_BUFFER_SIZE = 8192
    }

    override val name = path.toString()

    override fun write(inStream: InputStream): Long {
        path.parent?.run(Files::createDirectories)

        val outStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

        val source = inStream.source().buffer()
        val sink = outStream.sink().buffer()

        return source.use { inSource ->
            sink.use { outSink ->

                var total: Long = 0
                val byteArray = ByteArray(bufferSize)
                var b = inSource.read(byteArray, 0, byteArray.size)

                while (b > 0) {
                    outSink.write(byteArray, 0, b)
                    total += b.toLong()
                    b = inSource.read(byteArray, 0, byteArray.size)
                }

                total
            }
        }
    }
}
