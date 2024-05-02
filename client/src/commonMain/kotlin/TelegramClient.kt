package com.github.omarmiatello.telegram

import com.github.omarmiatello.telegram.TelegramRequest.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class TelegramClient(apiKey: String, private val httpClient: HttpClient = HttpClient()) {
    private val basePath = "https://api.telegram.org/bot$apiKey"
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true; encodeDefaults = false }

    private suspend fun <T> telegramGet(path: String, response: KSerializer<T>): TelegramResponse<T> {
        val responseString: String = httpClient.get(path).body()
        return json.decodeFromString(TelegramResponse.serializer(response), responseString)
    }

    private suspend fun <T> telegramPost(path: String, body: String, response: KSerializer<T>): TelegramResponse<T> {
        val responseString: String = httpClient
            .post(path) { setBody(TextContent(body, ContentType.Application.Json)) }
            .body()
        return json.decodeFromString(TelegramResponse.serializer(response), responseString)
    }

// Getting updates

    /**
     * <p>Use this method to receive incoming updates using long polling (<a href="https://en.wikipedia.org/wiki/Push_technology#Long_polling">wiki</a>). Returns an Array of <a href="#update">Update</a> objects.</p><blockquote>
     *  <p><strong>Notes</strong><br><strong>1.</strong> This method will not work if an outgoing webhook is set up.<br><strong>2.</strong> In order to avoid getting duplicate updates, recalculate <em>offset</em> after each server response.</p>
     * </blockquote>
     *
     * @property offset Identifier of the first update to be returned. Must be greater by one than the highest among the identifiers of previously received updates. By default, updates starting with the earliest unconfirmed update are returned. An update is considered confirmed as soon as <a href="#getupdates">getUpdates</a> is called with an <em>offset</em> higher than its <em>update_id</em>. The negative offset can be specified to retrieve updates starting from <em>-offset</em> update from the end of the updates queue. All previous updates will be forgotten.
     * @property limit Limits the number of updates to be retrieved. Values between 1-100 are accepted. Defaults to 100.
     * @property timeout Timeout in seconds for long polling. Defaults to 0, i.e. usual short polling. Should be positive, short polling should be used for testing purposes only.
     * @property allowed_updates A JSON-serialized list of the update types you want your bot to receive. For example, specify <code>["message", "edited_channel_post", "callback_query"]</code> to only receive updates of these types. See <a href="#update">Update</a> for a complete list of available update types. Specify an empty list to receive all update types except <em>chat_member</em>, <em>message_reaction</em>, and <em>message_reaction_count</em> (default). If not specified, the previous setting will be used.<br><br>Please note that this parameter doesn't affect updates created before the call to the getUpdates, so unwanted updates may be received for a short period of time.
     *
     * @return [List<Update>]
     * */
    suspend fun getUpdates(
        offset: Long? = null,
        limit: Long? = null,
        timeout: Long? = null,
        allowed_updates: List<String>? = null,
    ) = telegramPost(
        "$basePath/getUpdates",
        GetUpdatesRequest(
            offset,
            limit,
            timeout,
            allowed_updates,
        ).toJsonForRequest(),
        ListSerializer(Update.serializer())
    )

    /**
     * <p>Use this method to specify a URL and receive incoming updates via an outgoing webhook. Whenever there is an update for the bot, we will send an HTTPS POST request to the specified URL, containing a JSON-serialized <a href="#update">Update</a>. In case of an unsuccessful request, we will give up after a reasonable amount of attempts. Returns <em>True</em> on success.</p><p>If you'd like to make sure that the webhook was set by you, you can specify secret data in the parameter <em>secret_token</em>. If specified, the request will contain a header “X-Telegram-Bot-Api-Secret-Token” with the secret token as content.</p><blockquote>
     *  <p><strong>Notes</strong><br><strong>1.</strong> You will not be able to receive updates using <a href="#getupdates">getUpdates</a> for as long as an outgoing webhook is set up.<br><strong>2.</strong> To use a self-signed certificate, you need to upload your <a href="/bots/self-signed">public key certificate</a> using <em>certificate</em> parameter. Please upload as InputFile, sending a String will not work.<br><strong>3.</strong> Ports currently supported <em>for webhooks</em>: <strong>443, 80, 88, 8443</strong>.</p>
     *  <p>If you're having any trouble setting up webhooks, please check out this <a href="/bots/webhooks">amazing guide to webhooks</a>.</p>
     * </blockquote>
     *
     * @property url HTTPS URL to send updates to. Use an empty string to remove webhook integration
     * @property certificate Upload your public key certificate so that the root certificate in use can be checked. See our <a href="/bots/self-signed">self-signed guide</a> for details.
     * @property ip_address The fixed IP address which will be used to send webhook requests instead of the IP address resolved through DNS
     * @property max_connections The maximum allowed number of simultaneous HTTPS connections to the webhook for update delivery, 1-100. Defaults to <em>40</em>. Use lower values to limit the load on your bot's server, and higher values to increase your bot's throughput.
     * @property allowed_updates A JSON-serialized list of the update types you want your bot to receive. For example, specify <code>["message", "edited_channel_post", "callback_query"]</code> to only receive updates of these types. See <a href="#update">Update</a> for a complete list of available update types. Specify an empty list to receive all update types except <em>chat_member</em>, <em>message_reaction</em>, and <em>message_reaction_count</em> (default). If not specified, the previous setting will be used.<br>Please note that this parameter doesn't affect updates created before the call to the setWebhook, so unwanted updates may be received for a short period of time.
     * @property drop_pending_updates Pass <em>True</em> to drop all pending updates
     * @property secret_token A secret token to be sent in a header “X-Telegram-Bot-Api-Secret-Token” in every webhook request, 1-256 characters. Only characters <code>A-Z</code>, <code>a-z</code>, <code>0-9</code>, <code>_</code> and <code>-</code> are allowed. The header is useful to ensure that the request comes from a webhook set by you.
     *
     * @return [Boolean]
     * */
    suspend fun setWebhook(
        url: String,
        certificate: Any? = null,
        ip_address: String? = null,
        max_connections: Long? = null,
        allowed_updates: List<String>? = null,
        drop_pending_updates: Boolean? = null,
        secret_token: String? = null,
    ) = telegramPost(
        "$basePath/setWebhook",
        SetWebhookRequest(
            url,
            certificate,
            ip_address,
            max_connections,
            allowed_updates,
            drop_pending_updates,
            secret_token,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to remove webhook integration if you decide to switch back to <a href="#getupdates">getUpdates</a>. Returns <em>True</em> on success.</p>
     *
     * @property drop_pending_updates Pass <em>True</em> to drop all pending updates
     *
     * @return [Boolean]
     * */
    suspend fun deleteWebhook(
        drop_pending_updates: Boolean? = null,
    ) = telegramPost(
        "$basePath/deleteWebhook",
        DeleteWebhookRequest(
            drop_pending_updates,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get current webhook status. Requires no parameters. On success, returns a <a href="#webhookinfo">WebhookInfo</a> object. If the bot is using <a href="#getupdates">getUpdates</a>, will return an object with the <em>url</em> field empty.</p>
     *
     *
     * @return [WebhookInfo]
     * */
    suspend fun getWebhookInfo() = telegramGet("$basePath/getWebhookInfo", WebhookInfo.serializer())

// Available methods

    /**
     * <p>Use this method to log out from the cloud Bot API server before launching the bot locally. You <strong>must</strong> log out the bot before running it locally, otherwise there is no guarantee that the bot will receive updates. After a successful call, you can immediately log in on a local server, but will not be able to log in back to the cloud Bot API server for 10 minutes. Returns <em>True</em> on success. Requires no parameters.</p>
     *
     *
     * @return [Boolean]
     * */
    suspend fun logOut() = telegramGet("$basePath/logOut", Boolean.serializer())

    /**
     * <p>Use this method to close the bot instance before moving it from one local server to another. You need to delete the webhook before calling this method to ensure that the bot isn't launched again after server restart. The method will return error 429 in the first 10 minutes after the bot is launched. Returns <em>True</em> on success. Requires no parameters.</p>
     *
     *
     * @return [Boolean]
     * */
    suspend fun close() = telegramGet("$basePath/close", Boolean.serializer())

    /**
     * <p>Use this method to send text messages. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property text Text of the message to be sent, 1-4096 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
     * @property entities A JSON-serialized list of special entities that appear in message text, which can be specified instead of <em>parse_mode</em>
     * @property link_preview_options Link preview generation options for the message
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendMessage(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        text: String,
        parse_mode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        link_preview_options: LinkPreviewOptions? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendMessage",
        SendMessageRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            text,
            parse_mode,
            entities,
            link_preview_options,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to forward messages of any kind. Service messages and messages with protected content can't be forwarded. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property from_chat_id Unique identifier for the chat where the original message was sent (or channel username in the format <code>@channelusername</code>)
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the forwarded message from forwarding and saving
     * @property message_id Message identifier in the chat specified in <em>from_chat_id</em>
     *
     * @return [Message]
     * */
    suspend fun forwardMessage(
        chat_id: String,
        message_thread_id: Long? = null,
        from_chat_id: String,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        message_id: Long,
    ) = telegramPost(
        "$basePath/forwardMessage",
        ForwardMessageRequest(
            chat_id,
            message_thread_id,
            from_chat_id,
            disable_notification,
            protect_content,
            message_id,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to forward multiple messages of any kind. If some of the specified messages can't be found or forwarded, they are skipped. Service messages and messages with protected content can't be forwarded. Album grouping is kept for forwarded messages. On success, an array of <a href="#messageid">MessageId</a> of the sent messages is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property from_chat_id Unique identifier for the chat where the original messages were sent (or channel username in the format <code>@channelusername</code>)
     * @property message_ids A JSON-serialized list of 1-100 identifiers of messages in the chat <em>from_chat_id</em> to forward. The identifiers must be specified in a strictly increasing order.
     * @property disable_notification Sends the messages <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the forwarded messages from forwarding and saving
     *
     * @return [List<MessageId>]
     * */
    suspend fun forwardMessages(
        chat_id: String,
        message_thread_id: Long? = null,
        from_chat_id: String,
        message_ids: List<Long>,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
    ) = telegramPost(
        "$basePath/forwardMessages",
        ForwardMessagesRequest(
            chat_id,
            message_thread_id,
            from_chat_id,
            message_ids,
            disable_notification,
            protect_content,
        ).toJsonForRequest(),
        ListSerializer(MessageId.serializer())
    )

    /**
     * <p>Use this method to copy messages of any kind. Service messages, giveaway messages, giveaway winners messages, and invoice messages can't be copied. A quiz <a href="#poll">poll</a> can be copied only if the value of the field <em>correct_option_id</em> is known to the bot. The method is analogous to the method <a href="#forwardmessage">forwardMessage</a>, but the copied message doesn't have a link to the original message. Returns the <a href="#messageid">MessageId</a> of the sent message on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property from_chat_id Unique identifier for the chat where the original message was sent (or channel username in the format <code>@channelusername</code>)
     * @property message_id Message identifier in the chat specified in <em>from_chat_id</em>
     * @property caption New caption for media, 0-1024 characters after entities parsing. If not specified, the original caption is kept
     * @property parse_mode Mode for parsing entities in the new caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the new caption, which can be specified instead of <em>parse_mode</em>
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove reply keyboard or to force a reply from the user.
     *
     * @return [MessageId]
     * */
    suspend fun copyMessage(
        chat_id: String,
        message_thread_id: Long? = null,
        from_chat_id: String,
        message_id: Long,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/copyMessage",
        CopyMessageRequest(
            chat_id,
            message_thread_id,
            from_chat_id,
            message_id,
            caption,
            parse_mode,
            caption_entities,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        MessageId.serializer()
    )

    /**
     * <p>Use this method to copy messages of any kind. If some of the specified messages can't be found or copied, they are skipped. Service messages, giveaway messages, giveaway winners messages, and invoice messages can't be copied. A quiz <a href="#poll">poll</a> can be copied only if the value of the field <em>correct_option_id</em> is known to the bot. The method is analogous to the method <a href="#forwardmessages">forwardMessages</a>, but the copied messages don't have a link to the original message. Album grouping is kept for copied messages. On success, an array of <a href="#messageid">MessageId</a> of the sent messages is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property from_chat_id Unique identifier for the chat where the original messages were sent (or channel username in the format <code>@channelusername</code>)
     * @property message_ids A JSON-serialized list of 1-100 identifiers of messages in the chat <em>from_chat_id</em> to copy. The identifiers must be specified in a strictly increasing order.
     * @property disable_notification Sends the messages <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent messages from forwarding and saving
     * @property remove_caption Pass <em>True</em> to copy the messages without their captions
     *
     * @return [List<MessageId>]
     * */
    suspend fun copyMessages(
        chat_id: String,
        message_thread_id: Long? = null,
        from_chat_id: String,
        message_ids: List<Long>,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        remove_caption: Boolean? = null,
    ) = telegramPost(
        "$basePath/copyMessages",
        CopyMessagesRequest(
            chat_id,
            message_thread_id,
            from_chat_id,
            message_ids,
            disable_notification,
            protect_content,
            remove_caption,
        ).toJsonForRequest(),
        ListSerializer(MessageId.serializer())
    )

    /**
     * <p>Use this method to send photos. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property photo Photo to send. Pass a file_id as String to send a photo that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a photo from the Internet, or upload a new photo using multipart/form-data. The photo must be at most 10 MB in size. The photo's width and height must not exceed 10000 in total. Width and height ratio must be at most 20. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Photo caption (may also be used when resending photos by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the photo caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property has_spoiler Pass <em>True</em> if the photo needs to be covered with a spoiler animation
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendPhoto(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        photo: String,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        has_spoiler: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendPhoto",
        SendPhotoRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            photo,
            caption,
            parse_mode,
            caption_entities,
            has_spoiler,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send audio files, if you want Telegram clients to display them in the music player. Your audio must be in the .MP3 or .M4A format. On success, the sent <a href="#message">Message</a> is returned. Bots can currently send audio files of up to 50 MB in size, this limit may be changed in the future.</p><p>For sending voice messages, use the <a href="#sendvoice">sendVoice</a> method instead.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property audio Audio file to send. Pass a file_id as String to send an audio file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get an audio file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Audio caption, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the audio caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property duration Duration of the audio in seconds
     * @property performer Performer
     * @property title Track name
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendAudio(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        audio: String,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        duration: Long? = null,
        performer: String? = null,
        title: String? = null,
        thumbnail: String? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendAudio",
        SendAudioRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            audio,
            caption,
            parse_mode,
            caption_entities,
            duration,
            performer,
            title,
            thumbnail,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send general files. On success, the sent <a href="#message">Message</a> is returned. Bots can currently send files of any type of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property document File to send. Pass a file_id as String to send a file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Document caption (may also be used when resending documents by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the document caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property disable_content_type_detection Disables automatic server-side content type detection for files uploaded using multipart/form-data
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendDocument(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        document: String,
        thumbnail: String? = null,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        disable_content_type_detection: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendDocument",
        SendDocumentRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            document,
            thumbnail,
            caption,
            parse_mode,
            caption_entities,
            disable_content_type_detection,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send video files, Telegram clients support MPEG4 videos (other formats may be sent as <a href="#document">Document</a>). On success, the sent <a href="#message">Message</a> is returned. Bots can currently send video files of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property video Video to send. Pass a file_id as String to send a video that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a video from the Internet, or upload a new video using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property duration Duration of sent video in seconds
     * @property width Video width
     * @property height Video height
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Video caption (may also be used when resending videos by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the video caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property has_spoiler Pass <em>True</em> if the video needs to be covered with a spoiler animation
     * @property supports_streaming Pass <em>True</em> if the uploaded video is suitable for streaming
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendVideo(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        video: String,
        duration: Long? = null,
        width: Long? = null,
        height: Long? = null,
        thumbnail: String? = null,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        has_spoiler: Boolean? = null,
        supports_streaming: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendVideo",
        SendVideoRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            video,
            duration,
            width,
            height,
            thumbnail,
            caption,
            parse_mode,
            caption_entities,
            has_spoiler,
            supports_streaming,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send animation files (GIF or H.264/MPEG-4 AVC video without sound). On success, the sent <a href="#message">Message</a> is returned. Bots can currently send animation files of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property animation Animation to send. Pass a file_id as String to send an animation that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get an animation from the Internet, or upload a new animation using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property duration Duration of sent animation in seconds
     * @property width Animation width
     * @property height Animation height
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Animation caption (may also be used when resending animation by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the animation caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property has_spoiler Pass <em>True</em> if the animation needs to be covered with a spoiler animation
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendAnimation(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        animation: String,
        duration: Long? = null,
        width: Long? = null,
        height: Long? = null,
        thumbnail: String? = null,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        has_spoiler: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendAnimation",
        SendAnimationRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            animation,
            duration,
            width,
            height,
            thumbnail,
            caption,
            parse_mode,
            caption_entities,
            has_spoiler,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send audio files, if you want Telegram clients to display the file as a playable voice message. For this to work, your audio must be in an .OGG file encoded with OPUS (other formats may be sent as <a href="#audio">Audio</a> or <a href="#document">Document</a>). On success, the sent <a href="#message">Message</a> is returned. Bots can currently send voice messages of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property voice Audio file to send. Pass a file_id as String to send a file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Voice message caption, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the voice message caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property duration Duration of the voice message in seconds
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendVoice(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        voice: String,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        duration: Long? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendVoice",
        SendVoiceRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            voice,
            caption,
            parse_mode,
            caption_entities,
            duration,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>As of <a href="https://telegram.org/blog/video-messages-and-telescope">v.4.0</a>, Telegram clients support rounded square MPEG4 videos of up to 1 minute long. Use this method to send video messages. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property video_note Video note to send. Pass a file_id as String to send a video note that exists on the Telegram servers (recommended) or upload a new video using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>. Sending video notes by a URL is currently unsupported
     * @property duration Duration of sent video in seconds
     * @property length Video width and height, i.e. diameter of the video message
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendVideoNote(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        video_note: String,
        duration: Long? = null,
        length: Long? = null,
        thumbnail: String? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendVideoNote",
        SendVideoNoteRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            video_note,
            duration,
            length,
            thumbnail,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send a group of photos, videos, documents or audios as an album. Documents and audio files can be only grouped in an album with messages of the same type. On success, an array of <a href="#message">Messages</a> that were sent is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property media A JSON-serialized array describing messages to be sent, must include 2-10 items
     * @property disable_notification Sends messages <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent messages from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     *
     * @return [List<Message>]
     * */
    suspend fun sendMediaGroup(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        media: List<InputMedia>,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
    ) = telegramPost(
        "$basePath/sendMediaGroup",
        SendMediaGroupRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            media,
            disable_notification,
            protect_content,
            reply_parameters,
        ).toJsonForRequest(),
        ListSerializer(Message.serializer())
    )

    /**
     * <p>Use this method to send point on the map. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property latitude Latitude of the location
     * @property longitude Longitude of the location
     * @property horizontal_accuracy The radius of uncertainty for the location, measured in meters; 0-1500
     * @property live_period Period in seconds for which the location will be updated (see <a href="https://telegram.org/blog/live-locations">Live Locations</a>, should be between 60 and 86400.
     * @property heading For live locations, a direction in which the user is moving, in degrees. Must be between 1 and 360 if specified.
     * @property proximity_alert_radius For live locations, a maximum distance for proximity alerts about approaching another chat member, in meters. Must be between 1 and 100000 if specified.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendLocation(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        latitude: Float,
        longitude: Float,
        horizontal_accuracy: Float? = null,
        live_period: Long? = null,
        heading: Long? = null,
        proximity_alert_radius: Long? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendLocation",
        SendLocationRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            latitude,
            longitude,
            horizontal_accuracy,
            live_period,
            heading,
            proximity_alert_radius,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send information about a venue. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property latitude Latitude of the venue
     * @property longitude Longitude of the venue
     * @property title Name of the venue
     * @property address Address of the venue
     * @property foursquare_id Foursquare identifier of the venue
     * @property foursquare_type Foursquare type of the venue, if known. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
     * @property google_place_id Google Places identifier of the venue
     * @property google_place_type Google Places type of the venue. (See <a href="https://developers.google.com/places/web-service/supported_types">supported types</a>.)
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendVenue(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        latitude: Float,
        longitude: Float,
        title: String,
        address: String,
        foursquare_id: String? = null,
        foursquare_type: String? = null,
        google_place_id: String? = null,
        google_place_type: String? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendVenue",
        SendVenueRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            latitude,
            longitude,
            title,
            address,
            foursquare_id,
            foursquare_type,
            google_place_id,
            google_place_type,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send phone contacts. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property phone_number Contact's phone number
     * @property first_name Contact's first name
     * @property last_name Contact's last name
     * @property vcard Additional data about the contact in the form of a <a href="https://en.wikipedia.org/wiki/VCard">vCard</a>, 0-2048 bytes
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendContact(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        phone_number: String,
        first_name: String,
        last_name: String? = null,
        vcard: String? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendContact",
        SendContactRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            phone_number,
            first_name,
            last_name,
            vcard,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send a native poll. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property question Poll question, 1-300 characters
     * @property options A JSON-serialized list of answer options, 2-10 strings 1-100 characters each
     * @property is_anonymous <em>True</em>, if the poll needs to be anonymous, defaults to <em>True</em>
     * @property type Poll type, “quiz” or “regular”, defaults to “regular”
     * @property allows_multiple_answers <em>True</em>, if the poll allows multiple answers, ignored for polls in quiz mode, defaults to <em>False</em>
     * @property correct_option_id 0-based identifier of the correct answer option, required for polls in quiz mode
     * @property explanation Text that is shown when a user chooses an incorrect answer or taps on the lamp icon in a quiz-style poll, 0-200 characters with at most 2 line feeds after entities parsing
     * @property explanation_parse_mode Mode for parsing entities in the explanation. See <a href="#formatting-options">formatting options</a> for more details.
     * @property explanation_entities A JSON-serialized list of special entities that appear in the poll explanation, which can be specified instead of <em>parse_mode</em>
     * @property open_period Amount of time in seconds the poll will be active after creation, 5-600. Can't be used together with <em>close_date</em>.
     * @property close_date Point in time (Unix timestamp) when the poll will be automatically closed. Must be at least 5 and no more than 600 seconds in the future. Can't be used together with <em>open_period</em>.
     * @property is_closed Pass <em>True</em> if the poll needs to be immediately closed. This can be useful for poll preview.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendPoll(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        question: String,
        options: List<String>,
        is_anonymous: Boolean? = null,
        type: String? = null,
        allows_multiple_answers: Boolean? = null,
        correct_option_id: Long? = null,
        explanation: String? = null,
        explanation_parse_mode: String? = null,
        explanation_entities: List<MessageEntity>? = null,
        open_period: Long? = null,
        close_date: Long? = null,
        is_closed: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendPoll",
        SendPollRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            question,
            options,
            is_anonymous,
            type,
            allows_multiple_answers,
            correct_option_id,
            explanation,
            explanation_parse_mode,
            explanation_entities,
            open_period,
            close_date,
            is_closed,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to send an animated emoji that will display a random value. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property emoji Emoji on which the dice throw animation is based. Currently, must be one of “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EAF.png" width="20" height="20" alt="🎯">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8F80.png" width="20" height="20" alt="🏀">”, “<img class="emoji" src="//telegram.org/img/emoji/40/E29ABD.png" width="20" height="20" alt="⚽">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB3.png" width="20" height="20" alt="🎳">”, or “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB0.png" width="20" height="20" alt="🎰">”. Dice can have values 1-6 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EAF.png" width="20" height="20" alt="🎯">” and “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB3.png" width="20" height="20" alt="🎳">”, values 1-5 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8F80.png" width="20" height="20" alt="🏀">” and “<img class="emoji" src="//telegram.org/img/emoji/40/E29ABD.png" width="20" height="20" alt="⚽">”, and values 1-64 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB0.png" width="20" height="20" alt="🎰">”. Defaults to “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendDice(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        emoji: String? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendDice",
        SendDiceRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            emoji,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method when you need to tell the user that something is happening on the bot's side. The status is set for 5 seconds or less (when a message arrives from your bot, Telegram clients clear its typing status). Returns <em>True</em> on success.</p><blockquote>
     *  <p>Example: The <a href="https://t.me/imagebot">ImageBot</a> needs some time to process a request and upload the image. Instead of sending a text message along the lines of “Retrieving image, please wait…”, the bot may use <a href="#sendchataction">sendChatAction</a> with <em>action</em> = <em>upload_photo</em>. The user will see a “sending photo” status for the bot.</p>
     * </blockquote><p>We only recommend using this method when a response from the bot will take a <strong>noticeable</strong> amount of time to arrive.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the action will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread; for supergroups only
     * @property action Type of action to broadcast. Choose one, depending on what the user is about to receive: <em>typing</em> for <a href="#sendmessage">text messages</a>, <em>upload_photo</em> for <a href="#sendphoto">photos</a>, <em>record_video</em> or <em>upload_video</em> for <a href="#sendvideo">videos</a>, <em>record_voice</em> or <em>upload_voice</em> for <a href="#sendvoice">voice notes</a>, <em>upload_document</em> for <a href="#senddocument">general files</a>, <em>choose_sticker</em> for <a href="#sendsticker">stickers</a>, <em>find_location</em> for <a href="#sendlocation">location data</a>, <em>record_video_note</em> or <em>upload_video_note</em> for <a href="#sendvideonote">video notes</a>.
     *
     * @return [Boolean]
     * */
    suspend fun sendChatAction(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        action: String,
    ) = telegramPost(
        "$basePath/sendChatAction",
        SendChatActionRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            action,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to change the chosen reactions on a message. Service messages can't be reacted to. Automatically forwarded messages from a channel to its discussion group have the same available reactions as messages in the channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of the target message. If the message belongs to a media group, the reaction is set to the first non-deleted message in the group instead.
     * @property reaction A JSON-serialized list of reaction types to set on the message. Currently, as non-premium users, bots can set up to one reaction per message. A custom emoji reaction can be used if it is either already present on the message or explicitly allowed by chat administrators.
     * @property is_big Pass <em>True</em> to set the reaction with a big animation
     *
     * @return [Boolean]
     * */
    suspend fun setMessageReaction(
        chat_id: String,
        message_id: Long,
        reaction: List<ReactionType>? = null,
        is_big: Boolean? = null,
    ) = telegramPost(
        "$basePath/setMessageReaction",
        SetMessageReactionRequest(
            chat_id,
            message_id,
            reaction,
            is_big,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get a list of profile pictures for a user. Returns a <a href="#userprofilephotos">UserProfilePhotos</a> object.</p>
     *
     * @property user_id Unique identifier of the target user
     * @property offset Sequential number of the first photo to be returned. By default, all photos are returned.
     * @property limit Limits the number of photos to be retrieved. Values between 1-100 are accepted. Defaults to 100.
     *
     * @return [UserProfilePhotos]
     * */
    suspend fun getUserProfilePhotos(
        user_id: Long,
        offset: Long? = null,
        limit: Long? = null,
    ) = telegramPost(
        "$basePath/getUserProfilePhotos",
        GetUserProfilePhotosRequest(
            user_id,
            offset,
            limit,
        ).toJsonForRequest(),
        UserProfilePhotos.serializer()
    )

    /**
     * <p>Use this method to get basic information about a file and prepare it for downloading. For the moment, bots can download files of up to 20MB in size. On success, a <a href="#file">File</a> object is returned. The file can then be downloaded via the link <code>https://api.telegram.org/file/bot&lt;token&gt;/&lt;file_path&gt;</code>, where <code>&lt;file_path&gt;</code> is taken from the response. It is guaranteed that the link will be valid for at least 1 hour. When the link expires, a new one can be requested by calling <a href="#getfile">getFile</a> again.</p><p><strong>Note:</strong> This function may not preserve the original file name and MIME type. You should save the file's MIME type and name (if available) when the File object is received.</p>
     *
     * @property file_id File identifier to get information about
     *
     * @return [File]
     * */
    suspend fun getFile(
        file_id: String,
    ) = telegramPost(
        "$basePath/getFile",
        GetFileRequest(
            file_id,
        ).toJsonForRequest(),
        File.serializer()
    )

    /**
     * <p>Use this method to ban a user in a group, a supergroup or a channel. In the case of supergroups and channels, the user will not be able to return to the chat on their own using invite links, etc., unless <a href="#unbanchatmember">unbanned</a> first. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target group or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * @property until_date Date when the user will be unbanned; Unix time. If user is banned for more than 366 days or less than 30 seconds from the current time they are considered to be banned forever. Applied for supergroups and channels only.
     * @property revoke_messages Pass <em>True</em> to delete all messages from the chat for the user that is being removed. If <em>False</em>, the user will be able to see messages in the group that were sent before the user was removed. Always <em>True</em> for supergroups and channels.
     *
     * @return [Boolean]
     * */
    suspend fun banChatMember(
        chat_id: String,
        user_id: Long,
        until_date: Long? = null,
        revoke_messages: Boolean? = null,
    ) = telegramPost(
        "$basePath/banChatMember",
        BanChatMemberRequest(
            chat_id,
            user_id,
            until_date,
            revoke_messages,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to unban a previously banned user in a supergroup or channel. The user will <strong>not</strong> return to the group or channel automatically, but will be able to join via link, etc. The bot must be an administrator for this to work. By default, this method guarantees that after the call the user is not a member of the chat, but will be able to join it. So if the user is a member of the chat they will also be <strong>removed</strong> from the chat. If you don't want this, use the parameter <em>only_if_banned</em>. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target group or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * @property only_if_banned Do nothing if the user is not banned
     *
     * @return [Boolean]
     * */
    suspend fun unbanChatMember(
        chat_id: String,
        user_id: Long,
        only_if_banned: Boolean? = null,
    ) = telegramPost(
        "$basePath/unbanChatMember",
        UnbanChatMemberRequest(
            chat_id,
            user_id,
            only_if_banned,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to restrict a user in a supergroup. The bot must be an administrator in the supergroup for this to work and must have the appropriate administrator rights. Pass <em>True</em> for all permissions to lift restrictions from a user. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property user_id Unique identifier of the target user
     * @property permissions A JSON-serialized object for new user permissions
     * @property use_independent_chat_permissions Pass <em>True</em> if chat permissions are set independently. Otherwise, the <em>can_send_other_messages</em> and <em>can_add_web_page_previews</em> permissions will imply the <em>can_send_messages</em>, <em>can_send_audios</em>, <em>can_send_documents</em>, <em>can_send_photos</em>, <em>can_send_videos</em>, <em>can_send_video_notes</em>, and <em>can_send_voice_notes</em> permissions; the <em>can_send_polls</em> permission will imply the <em>can_send_messages</em> permission.
     * @property until_date Date when restrictions will be lifted for the user; Unix time. If user is restricted for more than 366 days or less than 30 seconds from the current time, they are considered to be restricted forever
     *
     * @return [Boolean]
     * */
    suspend fun restrictChatMember(
        chat_id: String,
        user_id: Long,
        permissions: ChatPermissions,
        use_independent_chat_permissions: Boolean? = null,
        until_date: Long? = null,
    ) = telegramPost(
        "$basePath/restrictChatMember",
        RestrictChatMemberRequest(
            chat_id,
            user_id,
            permissions,
            use_independent_chat_permissions,
            until_date,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to promote or demote a user in a supergroup or a channel. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Pass <em>False</em> for all boolean parameters to demote a user. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * @property is_anonymous Pass <em>True</em> if the administrator's presence in the chat is hidden
     * @property can_manage_chat Pass <em>True</em> if the administrator can access the chat event log, get boost list, see hidden supergroup and channel members, report spam messages and ignore slow mode. Implied by any other administrator privilege.
     * @property can_delete_messages Pass <em>True</em> if the administrator can delete messages of other users
     * @property can_manage_video_chats Pass <em>True</em> if the administrator can manage video chats
     * @property can_restrict_members Pass <em>True</em> if the administrator can restrict, ban or unban chat members, or access supergroup statistics
     * @property can_promote_members Pass <em>True</em> if the administrator can add new administrators with a subset of their own privileges or demote administrators that they have promoted, directly or indirectly (promoted by administrators that were appointed by him)
     * @property can_change_info Pass <em>True</em> if the administrator can change chat title, photo and other settings
     * @property can_invite_users Pass <em>True</em> if the administrator can invite new users to the chat
     * @property can_post_stories Pass <em>True</em> if the administrator can post stories to the chat
     * @property can_edit_stories Pass <em>True</em> if the administrator can edit stories posted by other users
     * @property can_delete_stories Pass <em>True</em> if the administrator can delete stories posted by other users
     * @property can_post_messages Pass <em>True</em> if the administrator can post messages in the channel, or access channel statistics; for channels only
     * @property can_edit_messages Pass <em>True</em> if the administrator can edit messages of other users and can pin messages; for channels only
     * @property can_pin_messages Pass <em>True</em> if the administrator can pin messages; for supergroups only
     * @property can_manage_topics Pass <em>True</em> if the user is allowed to create, rename, close, and reopen forum topics; for supergroups only
     *
     * @return [Boolean]
     * */
    suspend fun promoteChatMember(
        chat_id: String,
        user_id: Long,
        is_anonymous: Boolean? = null,
        can_manage_chat: Boolean? = null,
        can_delete_messages: Boolean? = null,
        can_manage_video_chats: Boolean? = null,
        can_restrict_members: Boolean? = null,
        can_promote_members: Boolean? = null,
        can_change_info: Boolean? = null,
        can_invite_users: Boolean? = null,
        can_post_stories: Boolean? = null,
        can_edit_stories: Boolean? = null,
        can_delete_stories: Boolean? = null,
        can_post_messages: Boolean? = null,
        can_edit_messages: Boolean? = null,
        can_pin_messages: Boolean? = null,
        can_manage_topics: Boolean? = null,
    ) = telegramPost(
        "$basePath/promoteChatMember",
        PromoteChatMemberRequest(
            chat_id,
            user_id,
            is_anonymous,
            can_manage_chat,
            can_delete_messages,
            can_manage_video_chats,
            can_restrict_members,
            can_promote_members,
            can_change_info,
            can_invite_users,
            can_post_stories,
            can_edit_stories,
            can_delete_stories,
            can_post_messages,
            can_edit_messages,
            can_pin_messages,
            can_manage_topics,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set a custom title for an administrator in a supergroup promoted by the bot. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property user_id Unique identifier of the target user
     * @property custom_title New custom title for the administrator; 0-16 characters, emoji are not allowed
     *
     * @return [Boolean]
     * */
    suspend fun setChatAdministratorCustomTitle(
        chat_id: String,
        user_id: Long,
        custom_title: String,
    ) = telegramPost(
        "$basePath/setChatAdministratorCustomTitle",
        SetChatAdministratorCustomTitleRequest(
            chat_id,
            user_id,
            custom_title,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to ban a channel chat in a supergroup or a channel. Until the chat is <a href="#unbanchatsenderchat">unbanned</a>, the owner of the banned chat won't be able to send messages on behalf of <strong>any of their channels</strong>. The bot must be an administrator in the supergroup or channel for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property sender_chat_id Unique identifier of the target sender chat
     *
     * @return [Boolean]
     * */
    suspend fun banChatSenderChat(
        chat_id: String,
        sender_chat_id: Long,
    ) = telegramPost(
        "$basePath/banChatSenderChat",
        BanChatSenderChatRequest(
            chat_id,
            sender_chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to unban a previously banned channel chat in a supergroup or channel. The bot must be an administrator for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property sender_chat_id Unique identifier of the target sender chat
     *
     * @return [Boolean]
     * */
    suspend fun unbanChatSenderChat(
        chat_id: String,
        sender_chat_id: Long,
    ) = telegramPost(
        "$basePath/unbanChatSenderChat",
        UnbanChatSenderChatRequest(
            chat_id,
            sender_chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set default chat permissions for all members. The bot must be an administrator in the group or a supergroup for this to work and must have the <em>can_restrict_members</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property permissions A JSON-serialized object for new default chat permissions
     * @property use_independent_chat_permissions Pass <em>True</em> if chat permissions are set independently. Otherwise, the <em>can_send_other_messages</em> and <em>can_add_web_page_previews</em> permissions will imply the <em>can_send_messages</em>, <em>can_send_audios</em>, <em>can_send_documents</em>, <em>can_send_photos</em>, <em>can_send_videos</em>, <em>can_send_video_notes</em>, and <em>can_send_voice_notes</em> permissions; the <em>can_send_polls</em> permission will imply the <em>can_send_messages</em> permission.
     *
     * @return [Boolean]
     * */
    suspend fun setChatPermissions(
        chat_id: String,
        permissions: ChatPermissions,
        use_independent_chat_permissions: Boolean? = null,
    ) = telegramPost(
        "$basePath/setChatPermissions",
        SetChatPermissionsRequest(
            chat_id,
            permissions,
            use_independent_chat_permissions,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to generate a new primary invite link for a chat; any previously generated primary link is revoked. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns the new invite link as <em>String</em> on success.</p><blockquote>
     *  <p>Note: Each administrator in a chat generates their own invite links. Bots can't use invite links generated by other administrators. If you want your bot to work with invite links, it will need to generate its own link using <a href="#exportchatinvitelink">exportChatInviteLink</a> or by calling the <a href="#getchat">getChat</a> method. If your bot needs to generate a new primary invite link replacing its previous one, use <a href="#exportchatinvitelink">exportChatInviteLink</a> again.</p>
     * </blockquote>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     *
     * @return [String]
     * */
    suspend fun exportChatInviteLink(
        chat_id: String,
    ) = telegramPost(
        "$basePath/exportChatInviteLink",
        ExportChatInviteLinkRequest(
            chat_id,
        ).toJsonForRequest(),
        String.serializer()
    )

    /**
     * <p>Use this method to create an additional invite link for a chat. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. The link can be revoked using the method <a href="#revokechatinvitelink">revokeChatInviteLink</a>. Returns the new invite link as <a href="#chatinvitelink">ChatInviteLink</a> object.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property name Invite link name; 0-32 characters
     * @property expire_date Point in time (Unix timestamp) when the link will expire
     * @property member_limit The maximum number of users that can be members of the chat simultaneously after joining the chat via this invite link; 1-99999
     * @property creates_join_request <em>True</em>, if users joining the chat via the link need to be approved by chat administrators. If <em>True</em>, <em>member_limit</em> can't be specified
     *
     * @return [ChatInviteLink]
     * */
    suspend fun createChatInviteLink(
        chat_id: String,
        name: String? = null,
        expire_date: Long? = null,
        member_limit: Long? = null,
        creates_join_request: Boolean? = null,
    ) = telegramPost(
        "$basePath/createChatInviteLink",
        CreateChatInviteLinkRequest(
            chat_id,
            name,
            expire_date,
            member_limit,
            creates_join_request,
        ).toJsonForRequest(),
        ChatInviteLink.serializer()
    )

    /**
     * <p>Use this method to edit a non-primary invite link created by the bot. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns the edited invite link as a <a href="#chatinvitelink">ChatInviteLink</a> object.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property invite_link The invite link to edit
     * @property name Invite link name; 0-32 characters
     * @property expire_date Point in time (Unix timestamp) when the link will expire
     * @property member_limit The maximum number of users that can be members of the chat simultaneously after joining the chat via this invite link; 1-99999
     * @property creates_join_request <em>True</em>, if users joining the chat via the link need to be approved by chat administrators. If <em>True</em>, <em>member_limit</em> can't be specified
     *
     * @return [ChatInviteLink]
     * */
    suspend fun editChatInviteLink(
        chat_id: String,
        invite_link: String,
        name: String? = null,
        expire_date: Long? = null,
        member_limit: Long? = null,
        creates_join_request: Boolean? = null,
    ) = telegramPost(
        "$basePath/editChatInviteLink",
        EditChatInviteLinkRequest(
            chat_id,
            invite_link,
            name,
            expire_date,
            member_limit,
            creates_join_request,
        ).toJsonForRequest(),
        ChatInviteLink.serializer()
    )

    /**
     * <p>Use this method to revoke an invite link created by the bot. If the primary link is revoked, a new link is automatically generated. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns the revoked invite link as <a href="#chatinvitelink">ChatInviteLink</a> object.</p>
     *
     * @property chat_id Unique identifier of the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property invite_link The invite link to revoke
     *
     * @return [ChatInviteLink]
     * */
    suspend fun revokeChatInviteLink(
        chat_id: String,
        invite_link: String,
    ) = telegramPost(
        "$basePath/revokeChatInviteLink",
        RevokeChatInviteLinkRequest(
            chat_id,
            invite_link,
        ).toJsonForRequest(),
        ChatInviteLink.serializer()
    )

    /**
     * <p>Use this method to approve a chat join request. The bot must be an administrator in the chat for this to work and must have the <em>can_invite_users</em> administrator right. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     *
     * @return [Boolean]
     * */
    suspend fun approveChatJoinRequest(
        chat_id: String,
        user_id: Long,
    ) = telegramPost(
        "$basePath/approveChatJoinRequest",
        ApproveChatJoinRequestRequest(
            chat_id,
            user_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to decline a chat join request. The bot must be an administrator in the chat for this to work and must have the <em>can_invite_users</em> administrator right. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     *
     * @return [Boolean]
     * */
    suspend fun declineChatJoinRequest(
        chat_id: String,
        user_id: Long,
    ) = telegramPost(
        "$basePath/declineChatJoinRequest",
        DeclineChatJoinRequestRequest(
            chat_id,
            user_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set a new profile photo for the chat. Photos can't be changed for private chats. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property photo New chat photo, uploaded using multipart/form-data
     *
     * @return [Boolean]
     * */
    suspend fun setChatPhoto(
        chat_id: String,
        photo: Any,
    ) = telegramPost(
        "$basePath/setChatPhoto",
        SetChatPhotoRequest(
            chat_id,
            photo,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete a chat photo. Photos can't be changed for private chats. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun deleteChatPhoto(
        chat_id: String,
    ) = telegramPost(
        "$basePath/deleteChatPhoto",
        DeleteChatPhotoRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to change the title of a chat. Titles can't be changed for private chats. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property title New chat title, 1-128 characters
     *
     * @return [Boolean]
     * */
    suspend fun setChatTitle(
        chat_id: String,
        title: String,
    ) = telegramPost(
        "$basePath/setChatTitle",
        SetChatTitleRequest(
            chat_id,
            title,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to change the description of a group, a supergroup or a channel. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property description New chat description, 0-255 characters
     *
     * @return [Boolean]
     * */
    suspend fun setChatDescription(
        chat_id: String,
        description: String? = null,
    ) = telegramPost(
        "$basePath/setChatDescription",
        SetChatDescriptionRequest(
            chat_id,
            description,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to add a message to the list of pinned messages in a chat. If the chat is not a private chat, the bot must be an administrator in the chat for this to work and must have the 'can_pin_messages' administrator right in a supergroup or 'can_edit_messages' administrator right in a channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of a message to pin
     * @property disable_notification Pass <em>True</em> if it is not necessary to send a notification to all chat members about the new pinned message. Notifications are always disabled in channels and private chats.
     *
     * @return [Boolean]
     * */
    suspend fun pinChatMessage(
        chat_id: String,
        message_id: Long,
        disable_notification: Boolean? = null,
    ) = telegramPost(
        "$basePath/pinChatMessage",
        PinChatMessageRequest(
            chat_id,
            message_id,
            disable_notification,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to remove a message from the list of pinned messages in a chat. If the chat is not a private chat, the bot must be an administrator in the chat for this to work and must have the 'can_pin_messages' administrator right in a supergroup or 'can_edit_messages' administrator right in a channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of a message to unpin. If not specified, the most recent pinned message (by sending date) will be unpinned.
     *
     * @return [Boolean]
     * */
    suspend fun unpinChatMessage(
        chat_id: String,
        message_id: Long? = null,
    ) = telegramPost(
        "$basePath/unpinChatMessage",
        UnpinChatMessageRequest(
            chat_id,
            message_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to clear the list of pinned messages in a chat. If the chat is not a private chat, the bot must be an administrator in the chat for this to work and must have the 'can_pin_messages' administrator right in a supergroup or 'can_edit_messages' administrator right in a channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun unpinAllChatMessages(
        chat_id: String,
    ) = telegramPost(
        "$basePath/unpinAllChatMessages",
        UnpinAllChatMessagesRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method for your bot to leave a group, supergroup or channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun leaveChat(
        chat_id: String,
    ) = telegramPost(
        "$basePath/leaveChat",
        LeaveChatRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get up to date information about the chat. Returns a <a href="#chat">Chat</a> object on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     *
     * @return [Chat]
     * */
    suspend fun getChat(
        chat_id: String,
    ) = telegramPost(
        "$basePath/getChat",
        GetChatRequest(
            chat_id,
        ).toJsonForRequest(),
        Chat.serializer()
    )

    /**
     * <p>Use this method to get a list of administrators in a chat, which aren't bots. Returns an Array of <a href="#chatmember">ChatMember</a> objects.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     *
     * @return [List<ChatMember>]
     * */
    suspend fun getChatAdministrators(
        chat_id: String,
    ) = telegramPost(
        "$basePath/getChatAdministrators",
        GetChatAdministratorsRequest(
            chat_id,
        ).toJsonForRequest(),
        ListSerializer(ChatMember.serializer())
    )

    /**
     * <p>Use this method to get the number of members in a chat. Returns <em>Int</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     *
     * @return [Int]
     * */
    suspend fun getChatMemberCount(
        chat_id: String,
    ) = telegramPost(
        "$basePath/getChatMemberCount",
        GetChatMemberCountRequest(
            chat_id,
        ).toJsonForRequest(),
        Int.serializer()
    )

    /**
     * <p>Use this method to get information about a member of a chat. The method is only guaranteed to work for other users if the bot is an administrator in the chat. Returns a <a href="#chatmember">ChatMember</a> object on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     *
     * @return [ChatMember]
     * */
    suspend fun getChatMember(
        chat_id: String,
        user_id: Long,
    ) = telegramPost(
        "$basePath/getChatMember",
        GetChatMemberRequest(
            chat_id,
            user_id,
        ).toJsonForRequest(),
        ChatMember.serializer()
    )

    /**
     * <p>Use this method to set a new group sticker set for a supergroup. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Use the field <em>can_set_sticker_set</em> optionally returned in <a href="#getchat">getChat</a> requests to check if the bot can use this method. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property sticker_set_name Name of the sticker set to be set as the group sticker set
     *
     * @return [Boolean]
     * */
    suspend fun setChatStickerSet(
        chat_id: String,
        sticker_set_name: String,
    ) = telegramPost(
        "$basePath/setChatStickerSet",
        SetChatStickerSetRequest(
            chat_id,
            sticker_set_name,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete a group sticker set from a supergroup. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Use the field <em>can_set_sticker_set</em> optionally returned in <a href="#getchat">getChat</a> requests to check if the bot can use this method. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun deleteChatStickerSet(
        chat_id: String,
    ) = telegramPost(
        "$basePath/deleteChatStickerSet",
        DeleteChatStickerSetRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get custom emoji stickers, which can be used as a forum topic icon by any user. Requires no parameters. Returns an Array of <a href="#sticker">Sticker</a> objects.</p>
     *
     *
     * @return [List<Sticker>]
     * */
    suspend fun getForumTopicIconStickers() =
        telegramGet("$basePath/getForumTopicIconStickers", ListSerializer(Sticker.serializer()))

    /**
     * <p>Use this method to create a topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. Returns information about the created topic as a <a href="#forumtopic">ForumTopic</a> object.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property name Topic name, 1-128 characters
     * @property icon_color Color of the topic icon in RGB format. Currently, must be one of 7322096 (0x6FB9F0), 16766590 (0xFFD67E), 13338331 (0xCB86DB), 9367192 (0x8EEE98), 16749490 (0xFF93B2), or 16478047 (0xFB6F5F)
     * @property icon_custom_emoji_id Unique identifier of the custom emoji shown as the topic icon. Use <a href="#getforumtopiciconstickers">getForumTopicIconStickers</a> to get all allowed custom emoji identifiers.
     *
     * @return [ForumTopic]
     * */
    suspend fun createForumTopic(
        chat_id: String,
        name: String,
        icon_color: Long? = null,
        icon_custom_emoji_id: String? = null,
    ) = telegramPost(
        "$basePath/createForumTopic",
        CreateForumTopicRequest(
            chat_id,
            name,
            icon_color,
            icon_custom_emoji_id,
        ).toJsonForRequest(),
        ForumTopic.serializer()
    )

    /**
     * <p>Use this method to edit name and icon of a topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have <em>can_manage_topics</em> administrator rights, unless it is the creator of the topic. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     * @property name New topic name, 0-128 characters. If not specified or empty, the current name of the topic will be kept
     * @property icon_custom_emoji_id New unique identifier of the custom emoji shown as the topic icon. Use <a href="#getforumtopiciconstickers">getForumTopicIconStickers</a> to get all allowed custom emoji identifiers. Pass an empty string to remove the icon. If not specified, the current icon will be kept
     *
     * @return [Boolean]
     * */
    suspend fun editForumTopic(
        chat_id: String,
        message_thread_id: Long,
        name: String? = null,
        icon_custom_emoji_id: String? = null,
    ) = telegramPost(
        "$basePath/editForumTopic",
        EditForumTopicRequest(
            chat_id,
            message_thread_id,
            name,
            icon_custom_emoji_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to close an open topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights, unless it is the creator of the topic. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     *
     * @return [Boolean]
     * */
    suspend fun closeForumTopic(
        chat_id: String,
        message_thread_id: Long,
    ) = telegramPost(
        "$basePath/closeForumTopic",
        CloseForumTopicRequest(
            chat_id,
            message_thread_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to reopen a closed topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights, unless it is the creator of the topic. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     *
     * @return [Boolean]
     * */
    suspend fun reopenForumTopic(
        chat_id: String,
        message_thread_id: Long,
    ) = telegramPost(
        "$basePath/reopenForumTopic",
        ReopenForumTopicRequest(
            chat_id,
            message_thread_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete a forum topic along with all its messages in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_delete_messages</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     *
     * @return [Boolean]
     * */
    suspend fun deleteForumTopic(
        chat_id: String,
        message_thread_id: Long,
    ) = telegramPost(
        "$basePath/deleteForumTopic",
        DeleteForumTopicRequest(
            chat_id,
            message_thread_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to clear the list of pinned messages in a forum topic. The bot must be an administrator in the chat for this to work and must have the <em>can_pin_messages</em> administrator right in the supergroup. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     *
     * @return [Boolean]
     * */
    suspend fun unpinAllForumTopicMessages(
        chat_id: String,
        message_thread_id: Long,
    ) = telegramPost(
        "$basePath/unpinAllForumTopicMessages",
        UnpinAllForumTopicMessagesRequest(
            chat_id,
            message_thread_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to edit the name of the 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have <em>can_manage_topics</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property name New topic name, 1-128 characters
     *
     * @return [Boolean]
     * */
    suspend fun editGeneralForumTopic(
        chat_id: String,
        name: String,
    ) = telegramPost(
        "$basePath/editGeneralForumTopic",
        EditGeneralForumTopicRequest(
            chat_id,
            name,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to close an open 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun closeGeneralForumTopic(
        chat_id: String,
    ) = telegramPost(
        "$basePath/closeGeneralForumTopic",
        CloseGeneralForumTopicRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to reopen a closed 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. The topic will be automatically unhidden if it was hidden. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun reopenGeneralForumTopic(
        chat_id: String,
    ) = telegramPost(
        "$basePath/reopenGeneralForumTopic",
        ReopenGeneralForumTopicRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to hide the 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. The topic will be automatically closed if it was open. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun hideGeneralForumTopic(
        chat_id: String,
    ) = telegramPost(
        "$basePath/hideGeneralForumTopic",
        HideGeneralForumTopicRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to unhide the 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun unhideGeneralForumTopic(
        chat_id: String,
    ) = telegramPost(
        "$basePath/unhideGeneralForumTopic",
        UnhideGeneralForumTopicRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to clear the list of pinned messages in a General forum topic. The bot must be an administrator in the chat for this to work and must have the <em>can_pin_messages</em> administrator right in the supergroup. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     *
     * @return [Boolean]
     * */
    suspend fun unpinAllGeneralForumTopicMessages(
        chat_id: String,
    ) = telegramPost(
        "$basePath/unpinAllGeneralForumTopicMessages",
        UnpinAllGeneralForumTopicMessagesRequest(
            chat_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to send answers to callback queries sent from <a href="/bots/features#inline-keyboards">inline keyboards</a>. The answer will be displayed to the user as a notification at the top of the chat screen or as an alert. On success, <em>True</em> is returned.</p><blockquote>
     *  <p>Alternatively, the user can be redirected to the specified Game URL. For this option to work, you must first create a game for your bot via <a href="https://t.me/botfather">@BotFather</a> and accept the terms. Otherwise, you may use links like <code>t.me/your_bot?start=XXXX</code> that open your bot with a parameter.</p>
     * </blockquote>
     *
     * @property callback_query_id Unique identifier for the query to be answered
     * @property text Text of the notification. If not specified, nothing will be shown to the user, 0-200 characters
     * @property show_alert If <em>True</em>, an alert will be shown by the client instead of a notification at the top of the chat screen. Defaults to <em>false</em>.
     * @property url URL that will be opened by the user's client. If you have created a <a href="#game">Game</a> and accepted the conditions via <a href="https://t.me/botfather">@BotFather</a>, specify the URL that opens your game - note that this will only work if the query comes from a <a href="#inlinekeyboardbutton"><em>callback_game</em></a> button.<br><br>Otherwise, you may use links like <code>t.me/your_bot?start=XXXX</code> that open your bot with a parameter.
     * @property cache_time The maximum amount of time in seconds that the result of the callback query may be cached client-side. Telegram apps will support caching starting in version 3.14. Defaults to 0.
     *
     * @return [Boolean]
     * */
    suspend fun answerCallbackQuery(
        callback_query_id: String,
        text: String? = null,
        show_alert: Boolean? = null,
        url: String? = null,
        cache_time: Long? = null,
    ) = telegramPost(
        "$basePath/answerCallbackQuery",
        AnswerCallbackQueryRequest(
            callback_query_id,
            text,
            show_alert,
            url,
            cache_time,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the list of boosts added to a chat by a user. Requires administrator rights in the chat. Returns a <a href="#userchatboosts">UserChatBoosts</a> object.</p>
     *
     * @property chat_id Unique identifier for the chat or username of the channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     *
     * @return [UserChatBoosts]
     * */
    suspend fun getUserChatBoosts(
        chat_id: String,
        user_id: Long,
    ) = telegramPost(
        "$basePath/getUserChatBoosts",
        GetUserChatBoostsRequest(
            chat_id,
            user_id,
        ).toJsonForRequest(),
        UserChatBoosts.serializer()
    )

    /**
     * <p>Use this method to get information about the connection of the bot with a business account. Returns a <a href="#businessconnection">BusinessConnection</a> object on success.</p>
     *
     * @property business_connection_id Unique identifier of the business connection
     *
     * @return [BusinessConnection]
     * */
    suspend fun getBusinessConnection(
        business_connection_id: String,
    ) = telegramPost(
        "$basePath/getBusinessConnection",
        GetBusinessConnectionRequest(
            business_connection_id,
        ).toJsonForRequest(),
        BusinessConnection.serializer()
    )

    /**
     * <p>Use this method to change the list of the bot's commands. See <a href="/bots/features#commands">this manual</a> for more details about bot commands. Returns <em>True</em> on success.</p>
     *
     * @property commands A JSON-serialized list of bot commands to be set as the list of the bot's commands. At most 100 commands can be specified.
     * @property scope A JSON-serialized object, describing scope of users for which the commands are relevant. Defaults to <a href="#botcommandscopedefault">BotCommandScopeDefault</a>.
     * @property language_code A two-letter ISO 639-1 language code. If empty, commands will be applied to all users from the given scope, for whose language there are no dedicated commands
     *
     * @return [Boolean]
     * */
    suspend fun setMyCommands(
        commands: List<BotCommand>,
        scope: BotCommandScope? = null,
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/setMyCommands",
        SetMyCommandsRequest(
            commands,
            scope,
            language_code,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete the list of the bot's commands for the given scope and user language. After deletion, <a href="#determining-list-of-commands">higher level commands</a> will be shown to affected users. Returns <em>True</em> on success.</p>
     *
     * @property scope A JSON-serialized object, describing scope of users for which the commands are relevant. Defaults to <a href="#botcommandscopedefault">BotCommandScopeDefault</a>.
     * @property language_code A two-letter ISO 639-1 language code. If empty, commands will be applied to all users from the given scope, for whose language there are no dedicated commands
     *
     * @return [Boolean]
     * */
    suspend fun deleteMyCommands(
        scope: BotCommandScope? = null,
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/deleteMyCommands",
        DeleteMyCommandsRequest(
            scope,
            language_code,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the current list of the bot's commands for the given scope and user language. Returns an Array of <a href="#botcommand">BotCommand</a> objects. If commands aren't set, an empty list is returned.</p>
     *
     * @property scope A JSON-serialized object, describing scope of users. Defaults to <a href="#botcommandscopedefault">BotCommandScopeDefault</a>.
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     *
     * @return [List<BotCommand>]
     * */
    suspend fun getMyCommands(
        scope: BotCommandScope? = null,
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/getMyCommands",
        GetMyCommandsRequest(
            scope,
            language_code,
        ).toJsonForRequest(),
        ListSerializer(BotCommand.serializer())
    )

    /**
     * <p>Use this method to change the bot's name. Returns <em>True</em> on success.</p>
     *
     * @property name New bot name; 0-64 characters. Pass an empty string to remove the dedicated name for the given language.
     * @property language_code A two-letter ISO 639-1 language code. If empty, the name will be shown to all users for whose language there is no dedicated name.
     *
     * @return [Boolean]
     * */
    suspend fun setMyName(
        name: String? = null,
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/setMyName",
        SetMyNameRequest(
            name,
            language_code,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the current bot name for the given user language. Returns <a href="#botname">BotName</a> on success.</p>
     *
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     *
     * @return [BotName]
     * */
    suspend fun getMyName(
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/getMyName",
        GetMyNameRequest(
            language_code,
        ).toJsonForRequest(),
        BotName.serializer()
    )

    /**
     * <p>Use this method to change the bot's description, which is shown in the chat with the bot if the chat is empty. Returns <em>True</em> on success.</p>
     *
     * @property description New bot description; 0-512 characters. Pass an empty string to remove the dedicated description for the given language.
     * @property language_code A two-letter ISO 639-1 language code. If empty, the description will be applied to all users for whose language there is no dedicated description.
     *
     * @return [Boolean]
     * */
    suspend fun setMyDescription(
        description: String? = null,
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/setMyDescription",
        SetMyDescriptionRequest(
            description,
            language_code,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the current bot description for the given user language. Returns <a href="#botdescription">BotDescription</a> on success.</p>
     *
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     *
     * @return [BotDescription]
     * */
    suspend fun getMyDescription(
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/getMyDescription",
        GetMyDescriptionRequest(
            language_code,
        ).toJsonForRequest(),
        BotDescription.serializer()
    )

    /**
     * <p>Use this method to change the bot's short description, which is shown on the bot's profile page and is sent together with the link when users share the bot. Returns <em>True</em> on success.</p>
     *
     * @property short_description New short description for the bot; 0-120 characters. Pass an empty string to remove the dedicated short description for the given language.
     * @property language_code A two-letter ISO 639-1 language code. If empty, the short description will be applied to all users for whose language there is no dedicated short description.
     *
     * @return [Boolean]
     * */
    suspend fun setMyShortDescription(
        short_description: String? = null,
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/setMyShortDescription",
        SetMyShortDescriptionRequest(
            short_description,
            language_code,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the current bot short description for the given user language. Returns <a href="#botshortdescription">BotShortDescription</a> on success.</p>
     *
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     *
     * @return [BotShortDescription]
     * */
    suspend fun getMyShortDescription(
        language_code: String? = null,
    ) = telegramPost(
        "$basePath/getMyShortDescription",
        GetMyShortDescriptionRequest(
            language_code,
        ).toJsonForRequest(),
        BotShortDescription.serializer()
    )

    /**
     * <p>Use this method to change the bot's menu button in a private chat, or the default menu button. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target private chat. If not specified, default bot's menu button will be changed
     * @property menu_button A JSON-serialized object for the bot's new menu button. Defaults to <a href="#menubuttondefault">MenuButtonDefault</a>
     *
     * @return [Boolean]
     * */
    suspend fun setChatMenuButton(
        chat_id: Long? = null,
        menu_button: MenuButton? = null,
    ) = telegramPost(
        "$basePath/setChatMenuButton",
        SetChatMenuButtonRequest(
            chat_id,
            menu_button,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the current value of the bot's menu button in a private chat, or the default menu button. Returns <a href="#menubutton">MenuButton</a> on success.</p>
     *
     * @property chat_id Unique identifier for the target private chat. If not specified, default bot's menu button will be returned
     *
     * @return [MenuButton]
     * */
    suspend fun getChatMenuButton(
        chat_id: Long? = null,
    ) = telegramPost(
        "$basePath/getChatMenuButton",
        GetChatMenuButtonRequest(
            chat_id,
        ).toJsonForRequest(),
        MenuButton.serializer()
    )

    /**
     * <p>Use this method to change the default administrator rights requested by the bot when it's added as an administrator to groups or channels. These rights will be suggested to users, but they are free to modify the list before adding the bot. Returns <em>True</em> on success.</p>
     *
     * @property rights A JSON-serialized object describing new default administrator rights. If not specified, the default administrator rights will be cleared.
     * @property for_channels Pass <em>True</em> to change the default administrator rights of the bot in channels. Otherwise, the default administrator rights of the bot for groups and supergroups will be changed.
     *
     * @return [Boolean]
     * */
    suspend fun setMyDefaultAdministratorRights(
        rights: ChatAdministratorRights? = null,
        for_channels: Boolean? = null,
    ) = telegramPost(
        "$basePath/setMyDefaultAdministratorRights",
        SetMyDefaultAdministratorRightsRequest(
            rights,
            for_channels,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to get the current default administrator rights of the bot. Returns <a href="#chatadministratorrights">ChatAdministratorRights</a> on success.</p>
     *
     * @property for_channels Pass <em>True</em> to get default administrator rights of the bot in channels. Otherwise, default administrator rights of the bot for groups and supergroups will be returned.
     *
     * @return [ChatAdministratorRights]
     * */
    suspend fun getMyDefaultAdministratorRights(
        for_channels: Boolean? = null,
    ) = telegramPost(
        "$basePath/getMyDefaultAdministratorRights",
        GetMyDefaultAdministratorRightsRequest(
            for_channels,
        ).toJsonForRequest(),
        ChatAdministratorRights.serializer()
    )

// Updating messages

    /**
     * <p>Use this method to edit text and <a href="#games">game</a> messages. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property text New text of the message, 1-4096 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
     * @property entities A JSON-serialized list of special entities that appear in message text, which can be specified instead of <em>parse_mode</em>
     * @property link_preview_options Link preview generation options for the message
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Message]
     * */
    suspend fun editMessageText(
        chat_id: String? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
        text: String,
        parse_mode: ParseMode? = null,
        entities: List<MessageEntity>? = null,
        link_preview_options: LinkPreviewOptions? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/editMessageText",
        EditMessageTextRequest(
            chat_id,
            message_id,
            inline_message_id,
            text,
            parse_mode,
            entities,
            link_preview_options,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to edit captions of messages. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property caption New caption of the message, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the message caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Message]
     * */
    suspend fun editMessageCaption(
        chat_id: String? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
        caption: String? = null,
        parse_mode: ParseMode? = null,
        caption_entities: List<MessageEntity>? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/editMessageCaption",
        EditMessageCaptionRequest(
            chat_id,
            message_id,
            inline_message_id,
            caption,
            parse_mode,
            caption_entities,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to edit animation, audio, document, photo, or video messages. If a message is part of a message album, then it can be edited only to an audio for audio albums, only to a document for document albums and to a photo or a video otherwise. When an inline message is edited, a new file can't be uploaded; use a previously uploaded file via its file_id or specify a URL. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property media A JSON-serialized object for a new media content of the message
     * @property reply_markup A JSON-serialized object for a new <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Message]
     * */
    suspend fun editMessageMedia(
        chat_id: String? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
        media: InputMedia,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/editMessageMedia",
        EditMessageMediaRequest(
            chat_id,
            message_id,
            inline_message_id,
            media,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to edit live location messages. A location can be edited until its <em>live_period</em> expires or editing is explicitly disabled by a call to <a href="#stopmessagelivelocation">stopMessageLiveLocation</a>. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property latitude Latitude of new location
     * @property longitude Longitude of new location
     * @property horizontal_accuracy The radius of uncertainty for the location, measured in meters; 0-1500
     * @property heading Direction in which the user is moving, in degrees. Must be between 1 and 360 if specified.
     * @property proximity_alert_radius The maximum distance for proximity alerts about approaching another chat member, in meters. Must be between 1 and 100000 if specified.
     * @property reply_markup A JSON-serialized object for a new <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Message]
     * */
    suspend fun editMessageLiveLocation(
        chat_id: String? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
        latitude: Float,
        longitude: Float,
        horizontal_accuracy: Float? = null,
        heading: Long? = null,
        proximity_alert_radius: Long? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/editMessageLiveLocation",
        EditMessageLiveLocationRequest(
            chat_id,
            message_id,
            inline_message_id,
            latitude,
            longitude,
            horizontal_accuracy,
            heading,
            proximity_alert_radius,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to stop updating a live location message before <em>live_period</em> expires. On success, if the message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message with live location to stop
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property reply_markup A JSON-serialized object for a new <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Message]
     * */
    suspend fun stopMessageLiveLocation(
        chat_id: String? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/stopMessageLiveLocation",
        StopMessageLiveLocationRequest(
            chat_id,
            message_id,
            inline_message_id,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to edit only the reply markup of messages. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Message]
     * */
    suspend fun editMessageReplyMarkup(
        chat_id: String? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/editMessageReplyMarkup",
        EditMessageReplyMarkupRequest(
            chat_id,
            message_id,
            inline_message_id,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to stop a poll which was sent by the bot. On success, the stopped <a href="#poll">Poll</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of the original message with the poll
     * @property reply_markup A JSON-serialized object for a new message <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     *
     * @return [Poll]
     * */
    suspend fun stopPoll(
        chat_id: String,
        message_id: Long,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/stopPoll",
        StopPollRequest(
            chat_id,
            message_id,
            reply_markup,
        ).toJsonForRequest(),
        Poll.serializer()
    )

    /**
     * <p>Use this method to delete a message, including service messages, with the following limitations:<br>- A message can only be deleted if it was sent less than 48 hours ago.<br>- Service messages about a supergroup, channel, or forum topic creation can't be deleted.<br>- A dice message in a private chat can only be deleted if it was sent more than 24 hours ago.<br>- Bots can delete outgoing messages in private chats, groups, and supergroups.<br>- Bots can delete incoming messages in private chats.<br>- Bots granted <em>can_post_messages</em> permissions can delete outgoing messages in channels.<br>- If the bot is an administrator of a group, it can delete any message there.<br>- If the bot has <em>can_delete_messages</em> permission in a supergroup or a channel, it can delete any message there.<br>Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of the message to delete
     *
     * @return [Boolean]
     * */
    suspend fun deleteMessage(
        chat_id: String,
        message_id: Long,
    ) = telegramPost(
        "$basePath/deleteMessage",
        DeleteMessageRequest(
            chat_id,
            message_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete multiple messages simultaneously. If some of the specified messages can't be found, they are skipped. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_ids A JSON-serialized list of 1-100 identifiers of messages to delete. See <a href="#deletemessage">deleteMessage</a> for limitations on which messages can be deleted
     *
     * @return [Boolean]
     * */
    suspend fun deleteMessages(
        chat_id: String,
        message_ids: List<Long>,
    ) = telegramPost(
        "$basePath/deleteMessages",
        DeleteMessagesRequest(
            chat_id,
            message_ids,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

// Stickers

    /**
     * <p>Use this method to send static .WEBP, <a href="https://telegram.org/blog/animated-stickers">animated</a> .TGS, or <a href="https://telegram.org/blog/video-stickers-better-reactions">video</a> .WEBM stickers. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property sticker Sticker to send. Pass a file_id as String to send a file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a .WEBP sticker from the Internet, or upload a new .WEBP, .TGS, or .WEBM sticker using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>. Video and animated stickers can't be sent via an HTTP URL.
     * @property emoji Emoji associated with the sticker; only for just uploaded stickers
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove reply keyboard or to force a reply from the user. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendSticker(
        business_connection_id: String? = null,
        chat_id: String,
        message_thread_id: Long? = null,
        sticker: String,
        emoji: String? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: KeyboardOption? = null,
    ) = telegramPost(
        "$basePath/sendSticker",
        SendStickerRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            sticker,
            emoji,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to get a sticker set. On success, a <a href="#stickerset">StickerSet</a> object is returned.</p>
     *
     * @property name Name of the sticker set
     *
     * @return [StickerSet]
     * */
    suspend fun getStickerSet(
        name: String,
    ) = telegramPost(
        "$basePath/getStickerSet",
        GetStickerSetRequest(
            name,
        ).toJsonForRequest(),
        StickerSet.serializer()
    )

    /**
     * <p>Use this method to get information about custom emoji stickers by their identifiers. Returns an Array of <a href="#sticker">Sticker</a> objects.</p>
     *
     * @property custom_emoji_ids A JSON-serialized list of custom emoji identifiers. At most 200 custom emoji identifiers can be specified.
     *
     * @return [List<Sticker>]
     * */
    suspend fun getCustomEmojiStickers(
        custom_emoji_ids: List<String>,
    ) = telegramPost(
        "$basePath/getCustomEmojiStickers",
        GetCustomEmojiStickersRequest(
            custom_emoji_ids,
        ).toJsonForRequest(),
        ListSerializer(Sticker.serializer())
    )

    /**
     * <p>Use this method to upload a file with a sticker for later use in the <a href="#createnewstickerset">createNewStickerSet</a>, <a href="#addstickertoset">addStickerToSet</a>, or <a href="#replacestickerinset">replaceStickerInSet</a> methods (the file can be used multiple times). Returns the uploaded <a href="#file">File</a> on success.</p>
     *
     * @property user_id User identifier of sticker file owner
     * @property sticker A file with the sticker in .WEBP, .PNG, .TGS, or .WEBM format. See <a href="/stickers"></a><a href="https://core.telegram.org/stickers">https://core.telegram.org/stickers</a> for technical requirements. <a href="#sending-files">More information on Sending Files »</a>
     * @property sticker_format Format of the sticker, must be one of “static”, “animated”, “video”
     *
     * @return [File]
     * */
    suspend fun uploadStickerFile(
        user_id: Long,
        sticker: Any,
        sticker_format: String,
    ) = telegramPost(
        "$basePath/uploadStickerFile",
        UploadStickerFileRequest(
            user_id,
            sticker,
            sticker_format,
        ).toJsonForRequest(),
        File.serializer()
    )

    /**
     * <p>Use this method to create a new sticker set owned by a user. The bot will be able to edit the sticker set thus created. Returns <em>True</em> on success.</p>
     *
     * @property user_id User identifier of created sticker set owner
     * @property name Short name of sticker set, to be used in <code>t.me/addstickers/</code> URLs (e.g., <em>animals</em>). Can contain only English letters, digits and underscores. Must begin with a letter, can't contain consecutive underscores and must end in <code>"_by_&lt;bot_username&gt;"</code>. <code>&lt;bot_username&gt;</code> is case insensitive. 1-64 characters.
     * @property title Sticker set title, 1-64 characters
     * @property stickers A JSON-serialized list of 1-50 initial stickers to be added to the sticker set
     * @property sticker_type Type of stickers in the set, pass “regular”, “mask”, or “custom_emoji”. By default, a regular sticker set is created.
     * @property needs_repainting Pass <em>True</em> if stickers in the sticker set must be repainted to the color of text when used in messages, the accent color if used as emoji status, white on chat photos, or another appropriate color based on context; for custom emoji sticker sets only
     *
     * @return [Boolean]
     * */
    suspend fun createNewStickerSet(
        user_id: Long,
        name: String,
        title: String,
        stickers: List<InputSticker>,
        sticker_type: String? = null,
        needs_repainting: Boolean? = null,
    ) = telegramPost(
        "$basePath/createNewStickerSet",
        CreateNewStickerSetRequest(
            user_id,
            name,
            title,
            stickers,
            sticker_type,
            needs_repainting,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to add a new sticker to a set created by the bot. Emoji sticker sets can have up to 200 stickers. Other sticker sets can have up to 120 stickers. Returns <em>True</em> on success.</p>
     *
     * @property user_id User identifier of sticker set owner
     * @property name Sticker set name
     * @property sticker A JSON-serialized object with information about the added sticker. If exactly the same sticker had already been added to the set, then the set isn't changed.
     *
     * @return [Boolean]
     * */
    suspend fun addStickerToSet(
        user_id: Long,
        name: String,
        sticker: InputSticker,
    ) = telegramPost(
        "$basePath/addStickerToSet",
        AddStickerToSetRequest(
            user_id,
            name,
            sticker,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to move a sticker in a set created by the bot to a specific position. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property position New sticker position in the set, zero-based
     *
     * @return [Boolean]
     * */
    suspend fun setStickerPositionInSet(
        sticker: String,
        position: Long,
    ) = telegramPost(
        "$basePath/setStickerPositionInSet",
        SetStickerPositionInSetRequest(
            sticker,
            position,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete a sticker from a set created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     *
     * @return [Boolean]
     * */
    suspend fun deleteStickerFromSet(
        sticker: String,
    ) = telegramPost(
        "$basePath/deleteStickerFromSet",
        DeleteStickerFromSetRequest(
            sticker,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to replace an existing sticker in a sticker set with a new one. The method is equivalent to calling <a href="#deletestickerfromset">deleteStickerFromSet</a>, then <a href="#addstickertoset">addStickerToSet</a>, then <a href="#setstickerpositioninset">setStickerPositionInSet</a>. Returns <em>True</em> on success.</p>
     *
     * @property user_id User identifier of the sticker set owner
     * @property name Sticker set name
     * @property old_sticker File identifier of the replaced sticker
     * @property sticker A JSON-serialized object with information about the added sticker. If exactly the same sticker had already been added to the set, then the set remains unchanged.
     *
     * @return [Boolean]
     * */
    suspend fun replaceStickerInSet(
        user_id: Long,
        name: String,
        old_sticker: String,
        sticker: InputSticker,
    ) = telegramPost(
        "$basePath/replaceStickerInSet",
        ReplaceStickerInSetRequest(
            user_id,
            name,
            old_sticker,
            sticker,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to change the list of emoji assigned to a regular or custom emoji sticker. The sticker must belong to a sticker set created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property emoji_list A JSON-serialized list of 1-20 emoji associated with the sticker
     *
     * @return [Boolean]
     * */
    suspend fun setStickerEmojiList(
        sticker: String,
        emoji_list: List<String>,
    ) = telegramPost(
        "$basePath/setStickerEmojiList",
        SetStickerEmojiListRequest(
            sticker,
            emoji_list,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to change search keywords assigned to a regular or custom emoji sticker. The sticker must belong to a sticker set created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property keywords A JSON-serialized list of 0-20 search keywords for the sticker with total length of up to 64 characters
     *
     * @return [Boolean]
     * */
    suspend fun setStickerKeywords(
        sticker: String,
        keywords: List<String>? = null,
    ) = telegramPost(
        "$basePath/setStickerKeywords",
        SetStickerKeywordsRequest(
            sticker,
            keywords,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to change the <a href="#maskposition">mask position</a> of a mask sticker. The sticker must belong to a sticker set that was created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property mask_position A JSON-serialized object with the position where the mask should be placed on faces. Omit the parameter to remove the mask position.
     *
     * @return [Boolean]
     * */
    suspend fun setStickerMaskPosition(
        sticker: String,
        mask_position: MaskPosition? = null,
    ) = telegramPost(
        "$basePath/setStickerMaskPosition",
        SetStickerMaskPositionRequest(
            sticker,
            mask_position,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set the title of a created sticker set. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * @property title Sticker set title, 1-64 characters
     *
     * @return [Boolean]
     * */
    suspend fun setStickerSetTitle(
        name: String,
        title: String,
    ) = telegramPost(
        "$basePath/setStickerSetTitle",
        SetStickerSetTitleRequest(
            name,
            title,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set the thumbnail of a regular or mask sticker set. The format of the thumbnail file must match the format of the stickers in the set. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * @property user_id User identifier of the sticker set owner
     * @property thumbnail A <strong>.WEBP</strong> or <strong>.PNG</strong> image with the thumbnail, must be up to 128 kilobytes in size and have a width and height of exactly 100px, or a <strong>.TGS</strong> animation with a thumbnail up to 32 kilobytes in size (see <a href="/stickers#animated-sticker-requirements"></a><a href="https://core.telegram.org/stickers#animated-sticker-requirements">https://core.telegram.org/stickers#animated-sticker-requirements</a> for animated sticker technical requirements), or a <strong>WEBM</strong> video with the thumbnail up to 32 kilobytes in size; see <a href="/stickers#video-sticker-requirements"></a><a href="https://core.telegram.org/stickers#video-sticker-requirements">https://core.telegram.org/stickers#video-sticker-requirements</a> for video sticker technical requirements. Pass a <em>file_id</em> as a String to send a file that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>. Animated and video sticker set thumbnails can't be uploaded via HTTP URL. If omitted, then the thumbnail is dropped and the first sticker is used as the thumbnail.
     * @property format Format of the thumbnail, must be one of “static” for a <strong>.WEBP</strong> or <strong>.PNG</strong> image, “animated” for a <strong>.TGS</strong> animation, or “video” for a <strong>WEBM</strong> video
     *
     * @return [Boolean]
     * */
    suspend fun setStickerSetThumbnail(
        name: String,
        user_id: Long,
        thumbnail: String? = null,
        format: String,
    ) = telegramPost(
        "$basePath/setStickerSetThumbnail",
        SetStickerSetThumbnailRequest(
            name,
            user_id,
            thumbnail,
            format,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set the thumbnail of a custom emoji sticker set. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * @property custom_emoji_id Custom emoji identifier of a sticker from the sticker set; pass an empty string to drop the thumbnail and use the first sticker as the thumbnail.
     *
     * @return [Boolean]
     * */
    suspend fun setCustomEmojiStickerSetThumbnail(
        name: String,
        custom_emoji_id: String? = null,
    ) = telegramPost(
        "$basePath/setCustomEmojiStickerSetThumbnail",
        SetCustomEmojiStickerSetThumbnailRequest(
            name,
            custom_emoji_id,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to delete a sticker set that was created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     *
     * @return [Boolean]
     * */
    suspend fun deleteStickerSet(
        name: String,
    ) = telegramPost(
        "$basePath/deleteStickerSet",
        DeleteStickerSetRequest(
            name,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

// Inline mode

    /**
     * <p>Use this method to send answers to an inline query. On success, <em>True</em> is returned.<br>No more than <strong>50</strong> results per query are allowed.</p>
     *
     * @property inline_query_id Unique identifier for the answered query
     * @property results A JSON-serialized array of results for the inline query
     * @property cache_time The maximum amount of time in seconds that the result of the inline query may be cached on the server. Defaults to 300.
     * @property is_personal Pass <em>True</em> if results may be cached on the server side only for the user that sent the query. By default, results may be returned to any user who sends the same query.
     * @property next_offset Pass the offset that a client should send in the next query with the same text to receive more results. Pass an empty string if there are no more results or if you don't support pagination. Offset length can't exceed 64 bytes.
     * @property button A JSON-serialized object describing a button to be shown above inline query results
     *
     * @return [Boolean]
     * */
    suspend fun answerInlineQuery(
        inline_query_id: String,
        results: List<InlineQueryResult>,
        cache_time: Long? = null,
        is_personal: Boolean? = null,
        next_offset: String? = null,
        button: InlineQueryResultsButton? = null,
    ) = telegramPost(
        "$basePath/answerInlineQuery",
        AnswerInlineQueryRequest(
            inline_query_id,
            results,
            cache_time,
            is_personal,
            next_offset,
            button,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Use this method to set the result of an interaction with a <a href="/bots/webapps">Web App</a> and send a corresponding message on behalf of the user to the chat from which the query originated. On success, a <a href="#sentwebappmessage">SentWebAppMessage</a> object is returned.</p>
     *
     * @property web_app_query_id Unique identifier for the query to be answered
     * @property result A JSON-serialized object describing the message to be sent
     *
     * @return [SentWebAppMessage]
     * */
    suspend fun answerWebAppQuery(
        web_app_query_id: String,
        result: InlineQueryResult,
    ) = telegramPost(
        "$basePath/answerWebAppQuery",
        AnswerWebAppQueryRequest(
            web_app_query_id,
            result,
        ).toJsonForRequest(),
        SentWebAppMessage.serializer()
    )

// Payments

    /**
     * <p>Use this method to send invoices. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property title Product name, 1-32 characters
     * @property description Product description, 1-255 characters
     * @property payload Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
     * @property provider_token Payment provider token, obtained via <a href="https://t.me/botfather">@BotFather</a>
     * @property currency Three-letter ISO 4217 currency code, see <a href="/bots/payments#supported-currencies">more on currencies</a>
     * @property prices Price breakdown, a JSON-serialized list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.)
     * @property max_tip_amount The maximum accepted amount for tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a maximum tip of <code>US$ 1.45</code> pass <code>max_tip_amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies). Defaults to 0
     * @property suggested_tip_amounts A JSON-serialized array of suggested amounts of tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). At most 4 suggested tip amounts can be specified. The suggested tip amounts must be positive, passed in a strictly increased order and must not exceed <em>max_tip_amount</em>.
     * @property start_parameter Unique deep-linking parameter. If left empty, <strong>forwarded copies</strong> of the sent message will have a <em>Pay</em> button, allowing multiple users to pay directly from the forwarded message, using the same invoice. If non-empty, forwarded copies of the sent message will have a <em>URL</em> button with a deep link to the bot (instead of a <em>Pay</em> button), with the value used as the start parameter
     * @property provider_data JSON-serialized data about the invoice, which will be shared with the payment provider. A detailed description of required fields should be provided by the payment provider.
     * @property photo_url URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service. People like it better when they see what they are paying for.
     * @property photo_size Photo size in bytes
     * @property photo_width Photo width
     * @property photo_height Photo height
     * @property need_name Pass <em>True</em> if you require the user's full name to complete the order
     * @property need_phone_number Pass <em>True</em> if you require the user's phone number to complete the order
     * @property need_email Pass <em>True</em> if you require the user's email address to complete the order
     * @property need_shipping_address Pass <em>True</em> if you require the user's shipping address to complete the order
     * @property send_phone_number_to_provider Pass <em>True</em> if the user's phone number should be sent to provider
     * @property send_email_to_provider Pass <em>True</em> if the user's email address should be sent to provider
     * @property is_flexible Pass <em>True</em> if the final price depends on the shipping method
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>. If empty, one 'Pay <code>total price</code>' button will be shown. If not empty, the first button must be a Pay button.
     *
     * @return [Message]
     * */
    suspend fun sendInvoice(
        chat_id: String,
        message_thread_id: Long? = null,
        title: String,
        description: String,
        payload: String,
        provider_token: String,
        currency: String,
        prices: List<LabeledPrice>,
        max_tip_amount: Long? = null,
        suggested_tip_amounts: List<Long>? = null,
        start_parameter: String? = null,
        provider_data: String? = null,
        photo_url: String? = null,
        photo_size: Long? = null,
        photo_width: Long? = null,
        photo_height: Long? = null,
        need_name: Boolean? = null,
        need_phone_number: Boolean? = null,
        need_email: Boolean? = null,
        need_shipping_address: Boolean? = null,
        send_phone_number_to_provider: Boolean? = null,
        send_email_to_provider: Boolean? = null,
        is_flexible: Boolean? = null,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/sendInvoice",
        SendInvoiceRequest(
            chat_id,
            message_thread_id,
            title,
            description,
            payload,
            provider_token,
            currency,
            prices,
            max_tip_amount,
            suggested_tip_amounts,
            start_parameter,
            provider_data,
            photo_url,
            photo_size,
            photo_width,
            photo_height,
            need_name,
            need_phone_number,
            need_email,
            need_shipping_address,
            send_phone_number_to_provider,
            send_email_to_provider,
            is_flexible,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to create a link for an invoice. Returns the created invoice link as <em>String</em> on success.</p>
     *
     * @property title Product name, 1-32 characters
     * @property description Product description, 1-255 characters
     * @property payload Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
     * @property provider_token Payment provider token, obtained via <a href="https://t.me/botfather">BotFather</a>
     * @property currency Three-letter ISO 4217 currency code, see <a href="/bots/payments#supported-currencies">more on currencies</a>
     * @property prices Price breakdown, a JSON-serialized list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.)
     * @property max_tip_amount The maximum accepted amount for tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a maximum tip of <code>US$ 1.45</code> pass <code>max_tip_amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies). Defaults to 0
     * @property suggested_tip_amounts A JSON-serialized array of suggested amounts of tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). At most 4 suggested tip amounts can be specified. The suggested tip amounts must be positive, passed in a strictly increased order and must not exceed <em>max_tip_amount</em>.
     * @property provider_data JSON-serialized data about the invoice, which will be shared with the payment provider. A detailed description of required fields should be provided by the payment provider.
     * @property photo_url URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service.
     * @property photo_size Photo size in bytes
     * @property photo_width Photo width
     * @property photo_height Photo height
     * @property need_name Pass <em>True</em> if you require the user's full name to complete the order
     * @property need_phone_number Pass <em>True</em> if you require the user's phone number to complete the order
     * @property need_email Pass <em>True</em> if you require the user's email address to complete the order
     * @property need_shipping_address Pass <em>True</em> if you require the user's shipping address to complete the order
     * @property send_phone_number_to_provider Pass <em>True</em> if the user's phone number should be sent to the provider
     * @property send_email_to_provider Pass <em>True</em> if the user's email address should be sent to the provider
     * @property is_flexible Pass <em>True</em> if the final price depends on the shipping method
     *
     * @return [String]
     * */
    suspend fun createInvoiceLink(
        title: String,
        description: String,
        payload: String,
        provider_token: String,
        currency: String,
        prices: List<LabeledPrice>,
        max_tip_amount: Long? = null,
        suggested_tip_amounts: List<Long>? = null,
        provider_data: String? = null,
        photo_url: String? = null,
        photo_size: Long? = null,
        photo_width: Long? = null,
        photo_height: Long? = null,
        need_name: Boolean? = null,
        need_phone_number: Boolean? = null,
        need_email: Boolean? = null,
        need_shipping_address: Boolean? = null,
        send_phone_number_to_provider: Boolean? = null,
        send_email_to_provider: Boolean? = null,
        is_flexible: Boolean? = null,
    ) = telegramPost(
        "$basePath/createInvoiceLink",
        CreateInvoiceLinkRequest(
            title,
            description,
            payload,
            provider_token,
            currency,
            prices,
            max_tip_amount,
            suggested_tip_amounts,
            provider_data,
            photo_url,
            photo_size,
            photo_width,
            photo_height,
            need_name,
            need_phone_number,
            need_email,
            need_shipping_address,
            send_phone_number_to_provider,
            send_email_to_provider,
            is_flexible,
        ).toJsonForRequest(),
        String.serializer()
    )

    /**
     * <p>If you sent an invoice requesting a shipping address and the parameter <em>is_flexible</em> was specified, the Bot API will send an <a href="#update">Update</a> with a <em>shipping_query</em> field to the bot. Use this method to reply to shipping queries. On success, <em>True</em> is returned.</p>
     *
     * @property shipping_query_id Unique identifier for the query to be answered
     * @property ok Pass <em>True</em> if delivery to the specified address is possible and <em>False</em> if there are any problems (for example, if delivery to the specified address is not possible)
     * @property shipping_options Required if <em>ok</em> is <em>True</em>. A JSON-serialized array of available shipping options.
     * @property error_message Required if <em>ok</em> is <em>False</em>. Error message in human readable form that explains why it is impossible to complete the order (e.g. "Sorry, delivery to your desired address is unavailable'). Telegram will display this message to the user.
     *
     * @return [Boolean]
     * */
    suspend fun answerShippingQuery(
        shipping_query_id: String,
        ok: Boolean,
        shipping_options: List<ShippingOption>? = null,
        error_message: String? = null,
    ) = telegramPost(
        "$basePath/answerShippingQuery",
        AnswerShippingQueryRequest(
            shipping_query_id,
            ok,
            shipping_options,
            error_message,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

    /**
     * <p>Once the user has confirmed their payment and shipping details, the Bot API sends the final confirmation in the form of an <a href="#update">Update</a> with the field <em>pre_checkout_query</em>. Use this method to respond to such pre-checkout queries. On success, <em>True</em> is returned. <strong>Note:</strong> The Bot API must receive an answer within 10 seconds after the pre-checkout query was sent.</p>
     *
     * @property pre_checkout_query_id Unique identifier for the query to be answered
     * @property ok Specify <em>True</em> if everything is alright (goods are available, etc.) and the bot is ready to proceed with the order. Use <em>False</em> if there are any problems.
     * @property error_message Required if <em>ok</em> is <em>False</em>. Error message in human readable form that explains the reason for failure to proceed with the checkout (e.g. "Sorry, somebody just bought the last of our amazing black T-shirts while you were busy filling out your payment details. Please choose a different color or garment!"). Telegram will display this message to the user.
     *
     * @return [Boolean]
     * */
    suspend fun answerPreCheckoutQuery(
        pre_checkout_query_id: String,
        ok: Boolean,
        error_message: String? = null,
    ) = telegramPost(
        "$basePath/answerPreCheckoutQuery",
        AnswerPreCheckoutQueryRequest(
            pre_checkout_query_id,
            ok,
            error_message,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

// Telegram Passport

    /**
     * <p>Informs a user that some of the Telegram Passport elements they provided contains errors. The user will not be able to re-submit their Passport to you until the errors are fixed (the contents of the field for which you returned the error must change). Returns <em>True</em> on success.</p><p>Use this if the data submitted by the user doesn't satisfy the standards your service requires for any reason. For example, if a birthday date seems invalid, a submitted document is blurry, a scan shows evidence of tampering, etc. Supply some details in the error message to make sure the user knows how to correct the issues.</p>
     *
     * @property user_id User identifier
     * @property errors A JSON-serialized array describing the errors
     *
     * @return [Boolean]
     * */
    suspend fun setPassportDataErrors(
        user_id: Long,
        errors: List<PassportElementError>,
    ) = telegramPost(
        "$basePath/setPassportDataErrors",
        SetPassportDataErrorsRequest(
            user_id,
            errors,
        ).toJsonForRequest(),
        Boolean.serializer()
    )

// Games

    /**
     * <p>Use this method to send a game. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property chat_id Unique identifier for the target chat
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property game_short_name Short name of the game, serves as the unique identifier for the game. Set up your games via <a href="https://t.me/botfather">@BotFather</a>.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>. If empty, one 'Play game_title' button will be shown. If not empty, the first button must launch the game. Not supported for messages sent on behalf of a business account.
     *
     * @return [Message]
     * */
    suspend fun sendGame(
        business_connection_id: String? = null,
        chat_id: Long,
        message_thread_id: Long? = null,
        game_short_name: String,
        disable_notification: Boolean? = null,
        protect_content: Boolean? = null,
        reply_parameters: ReplyParameters? = null,
        reply_markup: InlineKeyboardMarkup? = null,
    ) = telegramPost(
        "$basePath/sendGame",
        SendGameRequest(
            business_connection_id,
            chat_id,
            message_thread_id,
            game_short_name,
            disable_notification,
            protect_content,
            reply_parameters,
            reply_markup,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to set the score of the specified user in a game message. On success, if the message is not an inline message, the <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned. Returns an error, if the new score is not greater than the user's current score in the chat and <em>force</em> is <em>False</em>.</p>
     *
     * @property user_id User identifier
     * @property score New score, must be non-negative
     * @property force Pass <em>True</em> if the high score is allowed to decrease. This can be useful when fixing mistakes or banning cheaters
     * @property disable_edit_message Pass <em>True</em> if the game message should not be automatically edited to include the current scoreboard
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the sent message
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     *
     * @return [Message]
     * */
    suspend fun setGameScore(
        user_id: Long,
        score: Long,
        force: Boolean? = null,
        disable_edit_message: Boolean? = null,
        chat_id: Long? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
    ) = telegramPost(
        "$basePath/setGameScore",
        SetGameScoreRequest(
            user_id,
            score,
            force,
            disable_edit_message,
            chat_id,
            message_id,
            inline_message_id,
        ).toJsonForRequest(),
        Message.serializer()
    )

    /**
     * <p>Use this method to get data for high score tables. Will return the score of the specified user and several of their neighbors in a game. Returns an Array of <a href="#gamehighscore">GameHighScore</a> objects.</p><blockquote>
     *  <p>This method will currently return scores for the target user, plus two of their closest neighbors on each side. Will also return the top three users if the user and their neighbors are not among them. Please note that this behavior is subject to change.</p>
     * </blockquote>
     *
     * @property user_id Target user id
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the sent message
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     *
     * @return [List<GameHighScore>]
     * */
    suspend fun getGameHighScores(
        user_id: Long,
        chat_id: Long? = null,
        message_id: Long? = null,
        inline_message_id: String? = null,
    ) = telegramPost(
        "$basePath/getGameHighScores",
        GetGameHighScoresRequest(
            user_id,
            chat_id,
            message_id,
            inline_message_id,
        ).toJsonForRequest(),
        ListSerializer(GameHighScore.serializer())
    )
}
