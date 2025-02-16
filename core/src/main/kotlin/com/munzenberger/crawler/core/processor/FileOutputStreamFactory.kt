package com.munzenberger.crawler.core.processor

import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FileOutputStreamFactory(
    private val destination: Path,
    private val useUrlPath: Boolean = false,
) : OutputStreamFactory {
    override fun open(
        url: String,
        referer: String,
    ): Output {
        val source = URI.create(url).toURL()
        val parts = source.pathParts

        val path =
            if (useUrlPath) {
                val dir =
                    parts.dropLast(1).fold(destination.resolve(source.host)) { acc, part ->
                        acc.resolve(part)
                    }

                if (!Files.exists(dir)) {
                    Files.createDirectories(dir)
                }

                dir.resolve(parts.last())
            } else {
                destination.resolve(parts.last())
            }

        return Output(
            stream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING),
            name = path.toString(),
        )
    }
}

private val URL.pathParts: List<String>
    get() = this.path.split("/").filter { it.isNotEmpty() }
