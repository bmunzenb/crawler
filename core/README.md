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

| `URLType` | Description                                                 |
|-----------|-------------------------------------------------------------|
| `Link`    | Represents the target of an anchor tag in an HTML document. |
| `Image`   | Represents the target of an image tag in an HTML document.  |

You can create your own implementation of `URLProcessor` from the interface, or you can use one of the
provided top-level implementations:

| `URLProcessor`          | Description                                                                                                                                                                                                                                                                     |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DefaultURLProcessor`   | As its name implies, this is the default processor that parses links as HTML and downloads images. You will need to supply an `OutputStreamFactory` to the constructor that defines where to write downloaded images to.                                                        |
| `TypeBasedURLProcessor` | This is a more generic processor that delegates to type-specific processors.  Once created, call the `register(type, processor)` function to register a processor for each URL type.  Note that only the most recently registered processor will be executed for each URL type. |

### Step 2: Create a `URLFilter`

The `URLFilter` is called on every URL the crawler encounters to determine whether it should be processed. An
instance should inspect the incoming URL, and optionally its `URLType`, and return `true` if the URL is eligible for
processing.

*Note: Avoid returning `true` for all URLs of type `Link` as doing so may result in a crawl that never ends.*

### Step 3: Create the `Crawler` and call `execute(url)`

Finally, create an instance of `Crawler` passing the `URLProcessor` and `URLFilter` into the constructor, and
call the `execute(url)` function. The URL passed into `execute` should be the entry point for the crawler.

#### Example

In this example, we start crawling at `http://www.example.com/index.html`, following every link and downloading
every image encountered as long as the link or image URL starts with `http://www.example.com/`. The images are saved
to `/home/images` in a directory structure matching the URL path.

```kotlin
val processor = DefaultURLProcessor(
    FileOutputStreamFactory(Path.of("/home/images"))
)

val filter =
    URLFilter { type, url ->
        url.startsWith("https://www.example.com/")
    }

Crawler(
    processor = processor,
    filter = filter,
).execute("http://www.example.com/index.html")
```