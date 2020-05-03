# Telegram Bot API in Kotlin

[![](https://jitpack.io/v/omarmiatello/telegram.svg)](https://jitpack.io/#omarmiatello/telegram)

### API version 4.8

This library has 3 modules:
- **TelegramModelsOnly**: `data class` only
- **TelegramModels**: `data class` with Kotlinx/Serialization
- **TelegramClient**: TelegramModels with Ktor client

## Setup

Add this in your root `build.gradle` file:
```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}
```

Grab via Gradle (v4 or later):
```groovy
implementation 'com.github.omarmiatello.telegram:client:4.8'

// alternative, contains only: `data class` with Kotlinx/Serialization
// implementation 'com.github.omarmiatello.telegram:dataclass:4.8'

// alternative, contains only: `data class`
// implementation 'com.github.omarmiatello.telegram:dataclass-only:4.8'
```

## How to use `dataclass` module

This module could be used for parse the Telegram requests, and for send back a response.

Example with Ktor server:

```kotlin
post("/") {
    val request = call.receiveText().parseTelegramRequest()

    when {
        request.message != null -> {
            val inputText = request.message.text
            if (inputText != null) {
                call.respondText(
                    SendMessageRequest(
                        chat_id = request.message.chat.id.toString(),
                        text = "Message: ${request.message}",
                        parse_mode = ParseMode.HTML
                    ).toJsonForResponse(),
                    ContentType.Application.Json
                )
            }
        }
    }

    if (call.response.status() == null) call.respond(HttpStatusCode.NoContent)
}
```

## How to use `client` module

> Note: the `client` module contains also `dataclass` module

Example:

```kotlin
val apiKey = "1000000:aaaaaaaaaa"   // Retrieve a key from @BotFather
val chatId = "10000_example"        // Choose a destination for your message

val telegramApi = TelegramClient(apiKey)

telegramApi.sendMessage(chatId, msg)
```

or you could use a custom `HttpClient`
```kotlin
val httpClient = HttpClient(OkHttp) {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}
val telegramApi = TelegramClient(apiKey, httpClient)
```

## License

    MIT License
    
    Copyright (c) 2020 Omar Miatello
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.