package com.munzenberger.crawler.core.processor

import com.munzenberger.crawler.core.URLQueueEntry
import java.net.URI
import java.nio.file.Path

fun interface PathSpec {
    fun pathFor(
        entry: URLQueueEntry,
        targetDir: Path,
    ): Path
}

fun interface SourcePathSpec : PathSpec {
    fun pathFor(
        targetDir: Path,
        host: String,
        path: List<String>,
    ): Path

    override fun pathFor(
        entry: URLQueueEntry,
        targetDir: Path,
    ): Path {
        val source = URI.create(entry.url).toURL()

        val path =
            source.path
                .split("/")
                .filter { it.isNotEmpty() }

        return pathFor(targetDir, source.host, path)
    }
}

object FilenamePathSpec : SourcePathSpec {
    override fun pathFor(
        targetDir: Path,
        host: String,
        path: List<String>,
    ): Path = targetDir.resolve(path.last())
}

object URLPathSpec : SourcePathSpec {
    override fun pathFor(
        targetDir: Path,
        host: String,
        path: List<String>,
    ): Path {
        val root = targetDir.resolve(host)
        return path.fold(root) { acc, part -> acc.resolve(part) }
    }
}

object URLFilenamePathSpec : SourcePathSpec {
    override fun pathFor(
        targetDir: Path,
        host: String,
        path: List<String>,
    ): Path {
        val filename = (listOf(host) + path).joinToString("-")
        return targetDir.resolve(filename)
    }
}
