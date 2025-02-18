# Crawler Core

This is the core library for the crawler.  At its most basic, the crawler executes arbitrary logic on a queue of URLs.
The library includes basic processors for parsing HTML pages for anchor and image tags (using
[Jsoup](https://jsoup.org/)), and downloading content.

## Quick Start

To use the crawler, follow these steps:

1. Create a `URLProcessor` that defines what should happen for each URL crawled.
2. Create a `URLFilter` that defines which URLs should be crawled.
3. Create an instance of `Crawler` and call `execute(url)` on it.

### Step 1: Create a `URLProcessor`

The `URLProcessor` defines the logic that should execute for every URL crawled. It accepts a URL as input and returns
a collection of URLs that could be added to the crawler queue.

There are two top-level `URLProcessor` implementations included:

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

#### `TypeBasedURLProcessor`

This is a generic processor that allows you to specify which processor should execute based on the URL type. Supported
types are:

| `URLType` | Description                                      |
|-----------|--------------------------------------------------|
| `Link`    | Target URL of an anchor tag in an HTML document. |
| `Image`   | Target URL of an image tag in an HTML document.  |

After creating the instance, call the `register(type, processor)` function to specify the processor that should execute
for each URL type. Note that only the last registered processor for a URL type will be executed.

In this example, we create a `TypeBasedURLProcessor` that follows links and downloads images. This is functionally
equivalent to the `DownloadImagesProcessor` as described above:

```kotlin
val processor = TypeBasedURLProcessor().apply {
    register(URLType.Link, LinkProcessor()) // follow URLs specified in anchor and image tags
    register(
        URLType.Image, DownloadProcessor(
            writerFactory = FileDownloadWriterFactory(
                targetDir = Path.of("/user/images"),
                withUrlPath = true
            )
        )
    )
}
```

### Step 2: Create a `URLFilter`

The `URLFilter` is called on every URL the crawler encounters to determine whether it should be processed. An
instance should inspect the incoming URL, and optionally its `URLType`, and return `true` if the URL is eligible for
processing.

In this example, we create a filter that follows all links whose URLs start with `http://www.example.com/` and downloads
all images whose URLs start with `http://images.example.com/`:

```kotlin
val filter = URLFilter { type, url ->
    when (type) {
        URLType.Link -> url.startsWith("http://www.example.com/")
        URLType.Image -> url.startsWith("http://images.example.com/")
    }
}
```

*Note: An overly permissive filter can result in crawling unintended URLs and/or very long crawling time.*

### Step 3: Create the `Crawler` and call `execute(url)`

Finally, create an instance of `Crawler` passing the `URLProcessor` and `URLFilter` into the constructor, and
call the `execute(url)` function. The URL passed into `execute` should be the entry point for the crawler.

In this example, we start crawling at `http://www.example.com/index.html`, following every link and downloading
every image encountered as long as the link or image URL starts with `http://www.example.com/`. The images are saved
to `/user/images`.

```kotlin
val processor = DownloadImagesProcessor(
    writerFactory = FileDownloadWriterFactory(targetDir = Path.of("/user/images"))
)

val filter = URLFilter { _, url ->
    url.startsWith("http://www.example.com/")
}

Crawler(
    processor = processor,
    filter = filter
).execute("http://www.example.com/index.html")
```

#### Custom User-Agent Header

To manually specify the value to use in the `User-Agent` header, pass it into the `Crawler`'s `userAgent` parameter in
the constructor:

```kotlin
Crawler(
    processor = processor,
    filter = filter,
    userAgent = "Custom/1.0 (Custom user agent)"
).execute("http://www.example.com/index.html")
```

## Advanced Use

This section will include information about additional included processors, and the `Crawler` optional constructor
parameters for `URLQueue`, `ProcessedRegistry`, and `Consumer<CrawlerEvent>`.