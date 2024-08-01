# Telegram Bot API in Kotlin Multiplatform

[![](https://img.shields.io/maven-central/v/com.github.omarmiatello.telegram/dataclass)](https://search.maven.org/search?q=g:com.github.omarmiatello.telegram)

Full API documentation of Telegram Bot API
https://core.telegram.org/bots/api

**Library API version 7.8**

This library has 3 modules:
- Module [:dataclass:](#how-to-use-dataclass-module)
    - 198 `data class` with [Kotlinx/Serialization](https://github.com/Kotlin/kotlinx.serialization)
    - Contains only 1 file: [TelegramModels.kt](dataclass/src/commonMain/kotlin/TelegramModels.kt)
    - [See example](#example-with-ktor-server): Example with Ktor server
    - [See example](#telegram-webhook-parse-the-telegram-request): Telegram Webhook: Parse the Telegram request
    - [See example](#reply-to-the-user-with-a-message): Reply to the user with a Message
- Module [:client:](#how-to-use-client-module) ([TelegramModels.kt](dataclass/src/commonMain/kotlin/TelegramModels.kt) + [TelegramClient.kt](client/src/commonMain/kotlin/TelegramClient.kt))
    - 198 `data class` with [Kotlinx/Serialization](https://github.com/Kotlin/kotlinx.serialization) + [Ktor client](https://ktor.io/clients/) with 122 methods for Telegram bot API
    - [See example](#send-a-message-to-a-usergroupchannel): Send a message to a user/group/channel
- Module [:dataclass-only:](#how-to-use-dataclass-only-module) **(not recommended)**
    - 198 `data class` only (serializer not included)
    - Contains only 1 file: [TelegramModelsOnly.kt](dataclass-only/src/commonMain/kotlin/TelegramModelsOnly.kt)
    - Could be used with [Gson](https://github.com/google/gson) or plain Java / Kotlin project.


### This project use [Kotlin Multiplatform](https://kotlinlang.org/docs/mpp-intro.html)

Published on Maven Central:
- `Kotlin/JVM` for Java/Android
- `Kotlin/JS` configuration used: IR, browser
- `Kotlin/Native` configuration used: linuxX64

![](https://kotlinlang.org/docs/images/kotlin-multiplatform.png)

Learn more about [Kotlin Multiplatform benefits](https://kotlinlang.org/docs/multiplatform.html).

## How to use `dataclass` module

This module could be used for parse the Telegram requests, and for send back a response.

This module contains only 1 file: [TelegramModels.kt](dataclass/src/commonMain/kotlin/TelegramModels.kt)

#### Setup `dataclass` module

Add this in your `build.gradle.ktx` file:
```kotlin
// `data class` with Kotlinx/Serialization
implementation("com.github.omarmiatello.telegram:dataclass:7.8")
```

### Example 1 - Ktor 3.0 server

Please start from https://start.ktor.io/

See [Application.kt](ktor-sample/src/main/kotlin/com/example/Application.kt)

```kotlin
fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  routing {
    post("/") {
      var request: Update? = null
      try {
        // Endpoint for Telegram webhook. Parse the Telegram request
        request = call.receiveText().parseTelegramRequest()

        telegramRequest(request)
      } catch (e: Exception) {
        val text = e.stackTraceToString()
        val message = request?.message
        val chatId = message?.chat?.id?.toString()
        if (chatId != null) {
          call.respond(
            SendMessageRequest(
              chat_id = chatId,
              text = "```\n$text\n```",
              parse_mode = ParseMode.Markdown,
              reply_parameters = ReplyParameters(
                message_id = message.message_id
              )
            )
          )
        }
      }

      if (call.response.status() == null) call.respond(HttpStatusCode.NoContent)
    }
  }
}

suspend fun RoutingContext.telegramRequest(request: Update) {
  call.respond(
    // Send a message back to the user
    TelegramRequest.SendMessageRequest(
      chat_id = request.message!!.chat.id.toString(),
      text = "Message: ${request.message}\n\n```\n$request\n```",
      parse_mode = ParseMode.Markdown,
    )
  )
}


suspend inline fun RoutingCall.respond(response: TelegramRequest) {
  respondText(text = response.toJsonForResponse(), contentType = ContentType.Application.Json)
}
```

### Example 2 - GitHub Actions using Kotlin Script

See [https://github.com/omarmiatello/github-actions-kotlin-script-template](https://github.com/omarmiatello/github-actions-kotlin-script-template)

```kotlin
#!/usr/bin/env kotlin
@file:Repository("https://repo.maven.apache.org/maven2")
@file:DependsOn("com.github.omarmiatello.kotlin-script-toolbox:zero-setup:0.0.3")
@file:DependsOn("com.github.omarmiatello.telegram:client-jvm:7.8")
@file:DependsOn("io.ktor:ktor-client-okhttp-jvm:3.0.0")  // required for com.github.omarmiatello.telegram:client

import com.github.omarmiatello.kotlinscripttoolbox.core.launchKotlinScriptToolbox
import com.github.omarmiatello.telegram.TelegramClient
import kotlin.system.exitProcess

launchKotlinScriptToolbox(scriptName = "Telegram example") {
    // Set up: Telegram notification
    val telegramClient = TelegramClient(apiKey = readSystemPropertyOrNull("TELEGRAM_BOT_APIKEY")!!)
    val defaultChatId = readSystemPropertyOrNull("TELEGRAM_CHAT_ID")!!
    suspend fun sendTelegramMessage(text: String, chatId: String = defaultChatId) {
        println("💬 $text")
        telegramClient.sendMessage(chat_id = chatId, text = text)
    }

    // Send notification
    sendTelegramMessage("Hello world!")
}
```

### Telegram Webhook: Parse the Telegram request
In the previous example we use this below, to parse the Telegram request.
```kotlin
// Endpoint for Telegram webhook. Parse the Telegram request
val request: Update = call.receiveText().parseTelegramRequest()
```

The `Update` object has some properties:
```kotlin
/**
 * <p>This <a href="#available-types">object</a> represents an incoming update.<br>At most <strong>one</strong> of the optional parameters can be present in any given update.</p>
 *
 * @property update_id The update's unique identifier. Update identifiers start from a certain positive number and increase sequentially. This identifier becomes especially handy if you're using <a href="#setwebhook">webhooks</a>, since it allows you to ignore repeated updates or to restore the correct update sequence, should they get out of order. If there are no new updates for at least a week, then identifier of the next update will be chosen randomly instead of sequentially.
 * @property message <em>Optional</em>. New incoming message of any kind - text, photo, sticker, etc.
 * @property edited_message <em>Optional</em>. New version of a message that is known to the bot and was edited. This update may at times be triggered by changes to message fields that are either unavailable or not actively used by your bot.
 * @property channel_post <em>Optional</em>. New incoming channel post of any kind - text, photo, sticker, etc.
 * @property edited_channel_post <em>Optional</em>. New version of a channel post that is known to the bot and was edited. This update may at times be triggered by changes to message fields that are either unavailable or not actively used by your bot.
 * @property business_connection <em>Optional</em>. The bot was connected to or disconnected from a business account, or a user edited an existing connection with the bot
 * @property business_message <em>Optional</em>. New non-service message from a connected business account
 * @property edited_business_message <em>Optional</em>. New version of a message from a connected business account
 * @property deleted_business_messages <em>Optional</em>. Messages were deleted from a connected business account
 * @property message_reaction <em>Optional</em>. A reaction to a message was changed by a user. The bot must be an administrator in the chat and must explicitly specify <code>"message_reaction"</code> in the list of <em>allowed_updates</em> to receive these updates. The update isn't received for reactions set by bots.
 * @property message_reaction_count <em>Optional</em>. Reactions to a message with anonymous reactions were changed. The bot must be an administrator in the chat and must explicitly specify <code>"message_reaction_count"</code> in the list of <em>allowed_updates</em> to receive these updates. The updates are grouped and can be sent with delay up to a few minutes.
 * @property inline_query <em>Optional</em>. New incoming <a href="#inline-mode">inline</a> query
 * @property chosen_inline_result <em>Optional</em>. The result of an <a href="#inline-mode">inline</a> query that was chosen by a user and sent to their chat partner. Please see our documentation on the <a href="/bots/inline#collecting-feedback">feedback collecting</a> for details on how to enable these updates for your bot.
 * @property callback_query <em>Optional</em>. New incoming callback query
 * @property shipping_query <em>Optional</em>. New incoming shipping query. Only for invoices with flexible price
 * @property pre_checkout_query <em>Optional</em>. New incoming pre-checkout query. Contains full information about checkout
 * @property poll <em>Optional</em>. New poll state. Bots receive only updates about manually stopped polls and polls, which are sent by the bot
 * @property poll_answer <em>Optional</em>. A user changed their answer in a non-anonymous poll. Bots receive new votes only in polls that were sent by the bot itself.
 * @property my_chat_member <em>Optional</em>. The bot's chat member status was updated in a chat. For private chats, this update is received only when the bot is blocked or unblocked by the user.
 * @property chat_member <em>Optional</em>. A chat member's status was updated in a chat. The bot must be an administrator in the chat and must explicitly specify <code>"chat_member"</code> in the list of <em>allowed_updates</em> to receive these updates.
 * @property chat_join_request <em>Optional</em>. A request to join the chat has been sent. The bot must have the <em>can_invite_users</em> administrator right in the chat to receive these updates.
 * @property chat_boost <em>Optional</em>. A chat boost was added or changed. The bot must be an administrator in the chat to receive these updates.
 * @property removed_chat_boost <em>Optional</em>. A boost was removed from a chat. The bot must be an administrator in the chat to receive these updates.
 *
 * @constructor Creates a [Update].
 * */
@Serializable
data class Update(
  val update_id: Long,
  val message: Message? = null,
  val edited_message: Message? = null,
  val channel_post: Message? = null,
  val edited_channel_post: Message? = null,
  val business_connection: BusinessConnection? = null,
  val business_message: Message? = null,
  val edited_business_message: Message? = null,
  val deleted_business_messages: BusinessMessagesDeleted? = null,
  val message_reaction: MessageReactionUpdated? = null,
  val message_reaction_count: MessageReactionCountUpdated? = null,
  val inline_query: InlineQuery? = null,
  val chosen_inline_result: ChosenInlineResult? = null,
  val callback_query: CallbackQuery? = null,
  val shipping_query: ShippingQuery? = null,
  val pre_checkout_query: PreCheckoutQuery? = null,
  val poll: Poll? = null,
  val poll_answer: PollAnswer? = null,
  val my_chat_member: ChatMemberUpdated? = null,
  val chat_member: ChatMemberUpdated? = null,
  val chat_join_request: ChatJoinRequest? = null,
  val chat_boost: ChatBoostUpdated? = null,
  val removed_chat_boost: ChatBoostRemoved? = null,
) : TelegramModel() {
  override fun toJson() = json.encodeToString(serializer(), this)

  companion object {
    fun fromJson(string: String) = json.decodeFromString(serializer(), string)
  }
}
```

This object handle all possible Telegram update, not just `message`.

NOTE: This library contains a useful Kotlin extension for parse Telegram request from a `String`.
```kotlin
fun String.parseTelegramRequest() = Update.fromJson(this)
```

### Reply to the user with a Message

To reply we could use the `SendMessageRequest` class. This class has 2 mandatory fields: `chat_id` and `text`.

```kotlin
/**
 * <p>Use this method to send text messages. On success, the sent <a href="#message">Message</a> is returned.</p>
 *
 * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
 * @property text Text of the message to be sent, 1-4096 characters after entities parsing
 * @property parse_mode Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
 * @property disable_web_page_preview Disables link previews for links in this message
 * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
 * @property reply_to_message_id If the message is a reply, ID of the original message
 * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="https://core.telegram.org/bots#inline-keyboards-and-on-the-fly-updating">inline keyboard</a>, <a href="https://core.telegram.org/bots#keyboards">custom reply keyboard</a>, instructions to remove reply keyboard or to force a reply from the user.
 * */
@Serializable
data class SendMessageRequest(
    val chat_id: String,
    val text: String,
    val parse_mode: ParseMode? = null,
    val disable_web_page_preview: Boolean? = null,
    val disable_notification: Boolean? = null,
    val reply_to_message_id: Int? = null,
    val reply_markup: @ContextualSerialization KeyboardOption? = null
) : TelegramRequest() {
    override fun toJsonForRequest() = json.stringify(serializer(), this)
    override fun toJsonForResponse() = JsonObject(
        json.toJson(serializer(), this).jsonObject.content + ("method" to JsonLiteral("sendMessage"))
    ).toString()

    companion object {
        fun fromJson(string: String) = json.parse(serializer(), string)
    }
}
```

## How to use `client` module

This module contains only 2 file: [TelegramModels.kt](dataclass/src/commonMain/kotlin/TelegramModels.kt) and [TelegramClient.kt](client/src/commonMain/kotlin/TelegramClient.kt)

> Note: the `client` module include `dataclass` module

#### Setup `client` module

Add this in your `build.gradle.ktx` file:
```kotlin
// `data class` with Kotlinx/Serialization + Ktor client
implementation("com.github.omarmiatello.telegram:client:7.8")
```

### Send a message to a user/group/channel

You could use a custom `HttpClient` (example with logger)
```kotlin
val httpClient = HttpClient(OkHttp) {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }
}
```

Retrive a key from [@BotFather](https://t.me/BotFather)
```kotlin
val apiKey = "1000000:aaaaaaaaaa"   // Retrieve a key from @BotFather
val chatId = "10000_example"        // Choose a destination for your message

val telegramApi = TelegramClient(apiKey, httpClient) // NOTE httpClient is an optional parameter

telegramApi.sendMessage(chatId, msg)
```

NOTE: `sendMessage()` is a suspend function and return a `Message`.
```kotlin
/**
 * <p>Use this method to send text messages. On success, the sent <a href="#message">Message</a> is returned.</p>
 *
 * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
 * @property text Text of the message to be sent, 1-4096 characters after entities parsing
 * @property parse_mode Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
 * @property disable_web_page_preview Disables link previews for links in this message
 * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
 * @property reply_to_message_id If the message is a reply, ID of the original message
 * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="https://core.telegram.org/bots#inline-keyboards-and-on-the-fly-updating">inline keyboard</a>, <a href="https://core.telegram.org/bots#keyboards">custom reply keyboard</a>, instructions to remove reply keyboard or to force a reply from the user.
 *
 * @return [Message]
 * */
suspend fun sendMessage(
    chat_id: String,
    text: String,
    parse_mode: ParseMode? = null,
    disable_web_page_preview: Boolean? = null,
    disable_notification: Boolean? = null,
    reply_to_message_id: Int? = null,
    reply_markup: KeyboardOption? = null
) = telegramPost(
    "$basePath/sendMessage",
    SendMessageRequest(chat_id, text, parse_mode, disable_web_page_preview, disable_notification, reply_to_message_id, reply_markup).toJsonForRequest(),
    Message.serializer()
)
```

## How to use `dataclass-only` module

This module contains only 1 file: [TelegramModelsOnly.kt](dataclass-only/src/commonMain/kotlin/TelegramModelsOnly.kt)

NOTE: Not for beginner. Guide [here](docs/dataclass-only.md).

## Recap: Setup

Add this in your `build.gradle.ktx` file:
```kotlin
// alternative, contains: `data class` with Kotlinx/Serialization + Ktor client
implementation("com.github.omarmiatello.telegram:client:7.8")

// alternative, contains only: `data class` with Kotlinx/Serialization
implementation("com.github.omarmiatello.telegram:dataclass:7.8")

// alternative, contains only: `data class` (for plain Java/Kotlin project)
implementation("com.github.omarmiatello.telegram:dataclass-only:7.8")
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
