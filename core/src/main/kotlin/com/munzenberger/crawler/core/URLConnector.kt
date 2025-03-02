package com.munzenberger.crawler.core

import java.net.HttpURLConnection
import java.net.URI
import java.net.URLConnection

interface URLConnector {
    fun connect(
        entry: URLQueueEntry,
        requestProperties: Map<String, String> = emptyMap(),
    ): URLConnection

    companion object {
        fun default() = HttpAwareURLConnector()
    }
}

class HttpAwareURLConnector(
    private val maxRedirects: Int = DEFAULT_MAX_REDIRECTS,
) : URLConnector {
    companion object {
        private const val DEFAULT_MAX_REDIRECTS = 10
    }

    override fun connect(
        entry: URLQueueEntry,
        requestProperties: Map<String, String>,
    ): URLConnection = connect(entry, requestProperties, emptySet())

    @Suppress("MagicNumber")
    private fun connect(
        entry: URLQueueEntry,
        requestProperties: Map<String, String>,
        redirects: Set<String>,
    ): URLConnection {
        val connection =
            URI.create(entry.url).toURL().openConnection().apply {
                requestProperties.forEach { (key, value) -> setRequestProperty(key, value) }
            }

        if (connection is HttpURLConnection) {
            // HttpURLConnection will not follow redirects if the protocol changes (e.g. HTTP -> HTTPS)
            // so we need to manually handle redirects
            connection.instanceFollowRedirects = false

            val code = connection.responseCode

            if (code in 300..399) {
                if (redirects.size >= maxRedirects) {
                    error("Too many redirects.")
                }

                val location = connection.getHeaderField("Location")
                when {
                    location == null ->
                        error("HTTP $code without location in header.")
                    redirects.contains(location) ->
                        error("Infinite redirect.")
                    else -> {
                        val e = URLQueueEntry(entry.type, location, entry.url)
                        return connect(e, requestProperties, redirects + location)
                    }
                }
            }

            if (code != HttpURLConnection.HTTP_OK) {
                error("HTTP $code.")
            }
        }

        return connection
    }
}
