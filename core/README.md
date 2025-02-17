# Crawler Core

This is the core library for the crawler.

## User Guide

To use the crawler, follow these simple steps:

1. Create a `URLProcessor` that defines what should happen for each URL crawled.
2. Create a `URLFilter` that defines which URLs should be crawled.
3. Create an instance of `Crawler` and call `execute(url)` on it.

### Step 1: Create a `URLProcessor`

The `URLProcessor` is called for every URL that is crawled.  Generally, you will want to handle each
`URLType` uniquely, whether that be parsing it as HTML, downloading it as a binary file, or some other
action.

The following `URLType`s are supported:

| `URLType` | Description                                      |
|-----------|--------------------------------------------------|
| `Link`    | Target URL of an anchor tag in an HTML document. |
| `Image`   | Target URL of an image tag in an HTML document.  |

You can create your own implementation of `URLProcessor` from the interface, or you can use one of the
provided top-level implementations:

| `URLProcessor`            | Description                                                                                                                                                                                                                                                                     |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DownloadImagesProcessor` | Download any images from HTML image tags or links encountered during the crawl. To use this processor, you must specify a `DownloadWriterFactory` that is responsible for writing the downloaded image content.                                                                 |
| `TypeBasedURLProcessor`   | This is a more generic processor that delegates to type-specific processors.  Once created, call the `register(type, processor)` function to register a processor for each URL type.  Note that only the most recently registered processor will be executed for each URL type. |

### Step 2: Create a `URLFilter`

The `URLFilter` is called on every URL the crawler encounters to determine whether it should be processed. An
instance should inspect the incoming URL, and optionally its `URLType`, and return `true` if the URL is eligible for
processing.

*Note: Avoid overly permissive filters as this might result in crawling unintended URLs and/or very long execution
time.*

### Step 3: Create the `Crawler` and call `execute(url)`

Finally, create an instance of `Crawler` passing the `URLProcessor` and `URLFilter` into the constructor, and
call the `execute(url)` function. The URL passed into `execute` should be the entry point for the crawler.

#### Example

In this example, we start crawling at `http://www.example.com/index.html`, following every link and downloading
every image encountered as long as the link or image URL starts with `http://www.example.com/`. The images are saved
to `/home/images`.

```kotlin
val processor = DownloadImagesProcessor(
    writerFactory = FileDownloadWriterFactory(targetDir = Path.of("/home/images"))
)

val filter = URLFilter { type, url ->
    url.startsWith("http://www.example.com/")
}

Crawler(
    processor = processor,
    filter = filter
).execute("http://www.example.com/index.html")
```
