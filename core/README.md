# Crawler Core

This is the core library for the crawler.  At its most basic, the crawler executes arbitrary logic on a queue of URLs.
The library includes basic processors for parsing HTML pages for anchor and image tags (using
[Jsoup](https://jsoup.org/)), and downloading content.

## Quick Start

To use the crawler, follow these steps:

1. Create a `URLProcessor` that defines what should happen for each URL crawled
2. Create a `Predicate<URLQueueEntry>` that filters for which URLs should be crawled
3. Create an instance of `Crawler` and call `execute(url)` on it

### Step 1: Create a `URLProcessor`

The `URLProcessor` defines the logic that should execute for every URL crawled. It accepts a URL connection as input and
returns a collection of URLs that could be added to the crawler queue.

You can implement your own `URLProcessor`, or use one of the included ones:

#### `DownloadImagesProcessor`

Use this processor to download all images.

To create an instance of this processor, you will need to specify a `DownloadWriterFactory` in its constructor. This
class is responsible for creating `DownloadWriter`s for each image to download. Included in the library is a
`FileDownloadWriterFactory` that can be used to write image content to the filesystem.

In this example, we create a `DownloadImagesProcessor` that will write downloaded images to the `/user/images`
directory:

```kotlin
val processor = DownloadImagesProcessor(
    writerFactory = FileDownloadWriterFactory(
        targetDir = Path.of("/user/images"),
        withUrlPath = true // when true, creates a directory structure that matches the URL path
    )
)
```

### Step 2: Create a `Predicate<URLQueueEntry>`

The `Predicate<URLQueueEntry>` is called on every URL the crawler encounters to determine whether it should be
processed. An instance should inspect the incoming URL, and optionally its `URLType`, and return `true` if the URL is
eligible for processing.

The `URLType` denotes the source of URL that was added to the queue:

| `URLType` | Description                    |
|-----------|--------------------------------|
| `Link`    | URL is from an HTML anchor tag |
| `Image`   | URL is from an HTML image tag  |

In this example, we create a filter that follows all links whose URLs start with `http://www.example.com/` and ignores
all images:

```kotlin
val filter = Predicate<URLQueueEntry> { (type, url, referer) ->
    when (type) {
        URLType.Link -> url.startsWith("http://www.example.com/")
        URLType.Image -> false
    }
}
```

*Note: An overly permissive filter can result in crawling unintended URLs and/or very long crawling time.*

### Step 3: Create the `Crawler` and call `execute(url)`

Finally, create an instance of `Crawler` passing the `URLProcessor` and `Predicate<URLQueueEntry>` into the constructor,
and call the `execute(url)` function. The URL passed into `execute` should be the entry point for the crawler.

In this example, we start crawling at `http://www.example.com/index.html`, following every link and downloading
every image encountered as long as the link or image URL starts with `http://www.example.com/`. The images are saved
to `/user/images`.

```kotlin
val processor = DownloadImagesProcessor(
    writerFactory = FileDownloadWriterFactory(
        targetDir = Path.of("/user/images")
    )
)

val filter = Predicate<URLQueueEntry> {
    it.url.startsWith("http://www.example.com/")
}

Crawler(
    processor = processor,
    filter = filter
).execute("http://www.example.com/index.html")
```

#### Custom User-Agent Header

To manually specify the value for the `User-Agent` header, pass it into the `Crawler`'s `userAgent` parameter in
the constructor:

```kotlin
Crawler(
    processor = processor,
    filter = filter,
    userAgent = "Custom/1.0 (Custom user agent)"
).execute("http://www.example.com/index.html")
```

## Advanced Use

### Using a custom `URLQueue`

This section will describe the function and customization of `URLQueue`.

### Using a custom `ProcessedRegistry`

This section will describe the function and customization of `ProcessedRegistry`.

### Custom event handling

This section will describe the `CrawlerEvent` interface and the `Consumer<CrawlerEvent>` passed into the `Crawler`.
