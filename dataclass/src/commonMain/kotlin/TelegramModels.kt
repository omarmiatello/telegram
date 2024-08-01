@file:UseSerializers(
    InputMediaSerializer::class,
    InputMessageContentSerializer::class,
    InlineQueryResultSerializer::class,
    PassportElementErrorSerializer::class,
    ChatMemberSerializer::class,
    BotCommandScopeSerializer::class,
    ReactionTypeSerializer::class,
    MessageOriginSerializer::class,
    ChatBoostSourceSerializer::class,
    MenuButtonSerializer::class,
    BackgroundFillSerializer::class,
    BackgroundTypeSerializer::class,
    RevenueWithdrawalStateSerializer::class,
    TransactionPartnerSerializer::class,
    PaidMediaSerializer::class,
    InputPaidMediaSerializer::class,
    KeyboardOptionSerializer::class,
    MaybeInaccessibleMessageSerializer::class,
    InputFileOrStringSerializer::class,
    IntegerOrStringSerializer::class,
)

package com.github.omarmiatello.telegram

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

private val json = Json { ignoreUnknownKeys = true; prettyPrint = true; encodeDefaults = false; isLenient = true; }

sealed class TelegramModel {
    abstract fun toJson(): String
}

private fun <T> Decoder.tryDeserializers(vararg serializers: KSerializer<out T>): T {
    error(buildString {
        appendLine("Tried ${serializers.size} deserializers, but all failed!")
        val jsonEl = decodeSerializableValue(JsonElement.serializer())
        serializers.firstNotNullOfOrNull {
            try {
                json.decodeFromJsonElement(it, jsonEl)
            } catch (e: Exception) {
                appendLine("$it: $e")
                null
            }
        }?.also { return it }
    })
}

@Serializable
@JvmInline
value class UserId(val longValue: Long) {
    fun toChatId(): ChatId = ChatId(longValue.toString())
}

@Serializable
@JvmInline
value class ChatId(val stringValue: String) {
    val longValue: Long get() = stringValue.toLong()
}

@Serializable
@JvmInline
value class MessageId(val longValue: Long)
@Serializable
@JvmInline
value class BusinessConnectionId(val stringValue: String)
@Serializable
@JvmInline
value class MessageThreadId(val longValue: Long)
@Serializable
@JvmInline
value class MessageEffectId(val stringValue: String)

@Serializable
sealed class InputMedia : TelegramModel()
object InputMediaSerializer : KSerializer<InputMedia> {
    override val descriptor: SerialDescriptor = InputMedia.serializer().descriptor
    override fun serialize(encoder: Encoder, value: InputMedia) = when (value) {
        is InputMediaPhoto -> encoder.encodeSerializableValue(serializer(), value)
        is InputMediaVideo -> encoder.encodeSerializableValue(serializer(), value)
        is InputMediaAnimation -> encoder.encodeSerializableValue(serializer(), value)
        is InputMediaAudio -> encoder.encodeSerializableValue(serializer(), value)
        is InputMediaDocument -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): InputMedia =
        decoder.decodeSerializableValue(JsonElement.serializer()).let { jsonElement ->
            json.decodeFromJsonElement(
                deserializer =
                when (val type = jsonElement.jsonObject.getValue("type").jsonPrimitive.content) {
                    "photo" -> InputMediaPhoto.serializer()
                    "video" -> InputMediaVideo.serializer()
                    "animation" -> InputMediaAnimation.serializer()
                    "audio" -> InputMediaAudio.serializer()
                    "document" -> InputMediaDocument.serializer()
                    else -> error("unknown type: " + type)
                },
                element = jsonElement,
            )
        }
}

@Serializable
sealed class InputMessageContent : TelegramModel()
object InputMessageContentSerializer : KSerializer<InputMessageContent> {
    override val descriptor: SerialDescriptor = InputMessageContent.serializer().descriptor
    override fun serialize(encoder: Encoder, value: InputMessageContent) = when (value) {
        is InputTextMessageContent -> encoder.encodeSerializableValue(serializer(), value)
        is InputLocationMessageContent -> encoder.encodeSerializableValue(serializer(), value)
        is InputVenueMessageContent -> encoder.encodeSerializableValue(serializer(), value)
        is InputContactMessageContent -> encoder.encodeSerializableValue(serializer(), value)
        is InputInvoiceMessageContent -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): InputMessageContent =
        decoder.tryDeserializers(
            InputTextMessageContent.serializer(),
            InputLocationMessageContent.serializer(),
            InputVenueMessageContent.serializer(),
            InputContactMessageContent.serializer(),
            InputInvoiceMessageContent.serializer()
        )
}

@Serializable
sealed class InlineQueryResult : TelegramModel()
object InlineQueryResultSerializer : KSerializer<InlineQueryResult> {
    override val descriptor: SerialDescriptor = InlineQueryResult.serializer().descriptor
    override fun serialize(encoder: Encoder, value: InlineQueryResult) = when (value) {
        is InlineQueryResultArticle -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultPhoto -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultGif -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultMpeg4Gif -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultVideo -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultAudio -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultVoice -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultDocument -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultLocation -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultVenue -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultContact -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultGame -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedPhoto -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedGif -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedMpeg4Gif -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedSticker -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedDocument -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedVideo -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedVoice -> encoder.encodeSerializableValue(serializer(), value)
        is InlineQueryResultCachedAudio -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): InlineQueryResult =
        decoder.tryDeserializers(
            InlineQueryResultArticle.serializer(),
            InlineQueryResultPhoto.serializer(),
            InlineQueryResultGif.serializer(),
            InlineQueryResultMpeg4Gif.serializer(),
            InlineQueryResultVideo.serializer(),
            InlineQueryResultAudio.serializer(),
            InlineQueryResultVoice.serializer(),
            InlineQueryResultDocument.serializer(),
            InlineQueryResultLocation.serializer(),
            InlineQueryResultVenue.serializer(),
            InlineQueryResultContact.serializer(),
            InlineQueryResultGame.serializer(),
            InlineQueryResultCachedPhoto.serializer(),
            InlineQueryResultCachedGif.serializer(),
            InlineQueryResultCachedMpeg4Gif.serializer(),
            InlineQueryResultCachedSticker.serializer(),
            InlineQueryResultCachedDocument.serializer(),
            InlineQueryResultCachedVideo.serializer(),
            InlineQueryResultCachedVoice.serializer(),
            InlineQueryResultCachedAudio.serializer()
        )
}

@Serializable
sealed class PassportElementError : TelegramModel()
object PassportElementErrorSerializer : KSerializer<PassportElementError> {
    override val descriptor: SerialDescriptor = PassportElementError.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PassportElementError) = when (value) {
        is PassportElementErrorDataField -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorFrontSide -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorReverseSide -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorSelfie -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorFile -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorFiles -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorTranslationFile -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorTranslationFiles -> encoder.encodeSerializableValue(serializer(), value)
        is PassportElementErrorUnspecified -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): PassportElementError =
        decoder.tryDeserializers(
            PassportElementErrorDataField.serializer(),
            PassportElementErrorFrontSide.serializer(),
            PassportElementErrorReverseSide.serializer(),
            PassportElementErrorSelfie.serializer(),
            PassportElementErrorFile.serializer(),
            PassportElementErrorFiles.serializer(),
            PassportElementErrorTranslationFile.serializer(),
            PassportElementErrorTranslationFiles.serializer(),
            PassportElementErrorUnspecified.serializer()
        )
}

@Serializable
sealed class ChatMember : TelegramModel()
object ChatMemberSerializer : KSerializer<ChatMember> {
    override val descriptor: SerialDescriptor = ChatMember.serializer().descriptor
    override fun serialize(encoder: Encoder, value: ChatMember) = when (value) {
        is ChatMemberUpdated -> encoder.encodeSerializableValue(serializer(), value)
        is ChatMemberOwner -> encoder.encodeSerializableValue(serializer(), value)
        is ChatMemberAdministrator -> encoder.encodeSerializableValue(serializer(), value)
        is ChatMemberMember -> encoder.encodeSerializableValue(serializer(), value)
        is ChatMemberRestricted -> encoder.encodeSerializableValue(serializer(), value)
        is ChatMemberLeft -> encoder.encodeSerializableValue(serializer(), value)
        is ChatMemberBanned -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): ChatMember =
        decoder.tryDeserializers(
            ChatMemberUpdated.serializer(),
            ChatMemberOwner.serializer(),
            ChatMemberAdministrator.serializer(),
            ChatMemberMember.serializer(),
            ChatMemberRestricted.serializer(),
            ChatMemberLeft.serializer(),
            ChatMemberBanned.serializer()
        )
}

@Serializable
sealed class BotCommandScope : TelegramModel()
object BotCommandScopeSerializer : KSerializer<BotCommandScope> {
    override val descriptor: SerialDescriptor = BotCommandScope.serializer().descriptor
    override fun serialize(encoder: Encoder, value: BotCommandScope) = when (value) {
        is BotCommandScopeDefault -> encoder.encodeSerializableValue(serializer(), value)
        is BotCommandScopeAllPrivateChats -> encoder.encodeSerializableValue(serializer(), value)
        is BotCommandScopeAllGroupChats -> encoder.encodeSerializableValue(serializer(), value)
        is BotCommandScopeAllChatAdministrators -> encoder.encodeSerializableValue(serializer(), value)
        is BotCommandScopeChat -> encoder.encodeSerializableValue(serializer(), value)
        is BotCommandScopeChatAdministrators -> encoder.encodeSerializableValue(serializer(), value)
        is BotCommandScopeChatMember -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): BotCommandScope =
        decoder.tryDeserializers(
            BotCommandScopeDefault.serializer(),
            BotCommandScopeAllPrivateChats.serializer(),
            BotCommandScopeAllGroupChats.serializer(),
            BotCommandScopeAllChatAdministrators.serializer(),
            BotCommandScopeChat.serializer(),
            BotCommandScopeChatAdministrators.serializer(),
            BotCommandScopeChatMember.serializer()
        )
}

@Serializable
sealed class ReactionType : TelegramModel()
object ReactionTypeSerializer : KSerializer<ReactionType> {
    override val descriptor: SerialDescriptor = ReactionType.serializer().descriptor
    override fun serialize(encoder: Encoder, value: ReactionType) = when (value) {
        is ReactionTypeEmoji -> encoder.encodeSerializableValue(serializer(), value)
        is ReactionTypeCustomEmoji -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): ReactionType =
        decoder.tryDeserializers(ReactionTypeEmoji.serializer(), ReactionTypeCustomEmoji.serializer())
}

@Serializable
sealed class MessageOrigin : TelegramModel()
object MessageOriginSerializer : KSerializer<MessageOrigin> {
    override val descriptor: SerialDescriptor = MessageOrigin.serializer().descriptor
    override fun serialize(encoder: Encoder, value: MessageOrigin) = when (value) {
        is MessageOriginUser -> encoder.encodeSerializableValue(serializer(), value)
        is MessageOriginHiddenUser -> encoder.encodeSerializableValue(serializer(), value)
        is MessageOriginChat -> encoder.encodeSerializableValue(serializer(), value)
        is MessageOriginChannel -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): MessageOrigin =
        decoder.tryDeserializers(
            MessageOriginUser.serializer(),
            MessageOriginHiddenUser.serializer(),
            MessageOriginChat.serializer(),
            MessageOriginChannel.serializer()
        )
}

@Serializable
sealed class ChatBoostSource : TelegramModel()
object ChatBoostSourceSerializer : KSerializer<ChatBoostSource> {
    override val descriptor: SerialDescriptor = ChatBoostSource.serializer().descriptor
    override fun serialize(encoder: Encoder, value: ChatBoostSource) = when (value) {
        is ChatBoostSourcePremium -> encoder.encodeSerializableValue(serializer(), value)
        is ChatBoostSourceGiftCode -> encoder.encodeSerializableValue(serializer(), value)
        is ChatBoostSourceGiveaway -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): ChatBoostSource =
        decoder.tryDeserializers(
            ChatBoostSourcePremium.serializer(),
            ChatBoostSourceGiftCode.serializer(),
            ChatBoostSourceGiveaway.serializer()
        )
}

@Serializable
sealed class MenuButton : TelegramModel()
object MenuButtonSerializer : KSerializer<MenuButton> {
    override val descriptor: SerialDescriptor = MenuButton.serializer().descriptor
    override fun serialize(encoder: Encoder, value: MenuButton) = when (value) {
        is MenuButtonCommands -> encoder.encodeSerializableValue(serializer(), value)
        is MenuButtonWebApp -> encoder.encodeSerializableValue(serializer(), value)
        is MenuButtonDefault -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): MenuButton =
        decoder.tryDeserializers(
            MenuButtonCommands.serializer(),
            MenuButtonWebApp.serializer(),
            MenuButtonDefault.serializer()
        )
}

@Serializable
sealed class BackgroundFill : TelegramModel()
object BackgroundFillSerializer : KSerializer<BackgroundFill> {
    override val descriptor: SerialDescriptor = BackgroundFill.serializer().descriptor
    override fun serialize(encoder: Encoder, value: BackgroundFill) = when (value) {
        is BackgroundFillSolid -> encoder.encodeSerializableValue(serializer(), value)
        is BackgroundFillGradient -> encoder.encodeSerializableValue(serializer(), value)
        is BackgroundFillFreeformGradient -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): BackgroundFill =
        decoder.tryDeserializers(
            BackgroundFillSolid.serializer(),
            BackgroundFillGradient.serializer(),
            BackgroundFillFreeformGradient.serializer()
        )
}

@Serializable
sealed class BackgroundType : TelegramModel()
object BackgroundTypeSerializer : KSerializer<BackgroundType> {
    override val descriptor: SerialDescriptor = BackgroundType.serializer().descriptor
    override fun serialize(encoder: Encoder, value: BackgroundType) = when (value) {
        is BackgroundTypeFill -> encoder.encodeSerializableValue(serializer(), value)
        is BackgroundTypeWallpaper -> encoder.encodeSerializableValue(serializer(), value)
        is BackgroundTypePattern -> encoder.encodeSerializableValue(serializer(), value)
        is BackgroundTypeChatTheme -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): BackgroundType =
        decoder.tryDeserializers(
            BackgroundTypeFill.serializer(),
            BackgroundTypeWallpaper.serializer(),
            BackgroundTypePattern.serializer(),
            BackgroundTypeChatTheme.serializer()
        )
}

@Serializable
sealed class RevenueWithdrawalState : TelegramModel()
object RevenueWithdrawalStateSerializer : KSerializer<RevenueWithdrawalState> {
    override val descriptor: SerialDescriptor = RevenueWithdrawalState.serializer().descriptor
    override fun serialize(encoder: Encoder, value: RevenueWithdrawalState) = when (value) {
        is RevenueWithdrawalStatePending -> encoder.encodeSerializableValue(serializer(), value)
        is RevenueWithdrawalStateSucceeded -> encoder.encodeSerializableValue(serializer(), value)
        is RevenueWithdrawalStateFailed -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): RevenueWithdrawalState =
        decoder.tryDeserializers(
            RevenueWithdrawalStatePending.serializer(),
            RevenueWithdrawalStateSucceeded.serializer(),
            RevenueWithdrawalStateFailed.serializer()
        )
}

@Serializable
sealed class TransactionPartner : TelegramModel()
object TransactionPartnerSerializer : KSerializer<TransactionPartner> {
    override val descriptor: SerialDescriptor = TransactionPartner.serializer().descriptor
    override fun serialize(encoder: Encoder, value: TransactionPartner) = when (value) {
        is TransactionPartnerUser -> encoder.encodeSerializableValue(serializer(), value)
        is TransactionPartnerFragment -> encoder.encodeSerializableValue(serializer(), value)
        is TransactionPartnerTelegramAds -> encoder.encodeSerializableValue(serializer(), value)
        is TransactionPartnerOther -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): TransactionPartner =
        decoder.tryDeserializers(
            TransactionPartnerUser.serializer(),
            TransactionPartnerFragment.serializer(),
            TransactionPartnerTelegramAds.serializer(),
            TransactionPartnerOther.serializer()
        )
}

@Serializable
sealed class PaidMedia : TelegramModel()
object PaidMediaSerializer : KSerializer<PaidMedia> {
    override val descriptor: SerialDescriptor = PaidMedia.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PaidMedia) = when (value) {
        is PaidMediaInfo -> encoder.encodeSerializableValue(serializer(), value)
        is PaidMediaPreview -> encoder.encodeSerializableValue(serializer(), value)
        is PaidMediaPhoto -> encoder.encodeSerializableValue(serializer(), value)
        is PaidMediaVideo -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): PaidMedia =
        decoder.tryDeserializers(
            PaidMediaInfo.serializer(),
            PaidMediaPreview.serializer(),
            PaidMediaPhoto.serializer(),
            PaidMediaVideo.serializer()
        )
}

@Serializable
sealed class InputPaidMedia : TelegramModel()
object InputPaidMediaSerializer : KSerializer<InputPaidMedia> {
    override val descriptor: SerialDescriptor = InputPaidMedia.serializer().descriptor
    override fun serialize(encoder: Encoder, value: InputPaidMedia) = when (value) {
        is InputPaidMediaPhoto -> encoder.encodeSerializableValue(serializer(), value)
        is InputPaidMediaVideo -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): InputPaidMedia =
        decoder.tryDeserializers(InputPaidMediaPhoto.serializer(), InputPaidMediaVideo.serializer())
}

@Serializable
sealed class KeyboardOption : TelegramModel()
object KeyboardOptionSerializer : KSerializer<KeyboardOption> {
    override val descriptor: SerialDescriptor = KeyboardOption.serializer().descriptor
    override fun serialize(encoder: Encoder, value: KeyboardOption) = when (value) {
        is ReplyKeyboardMarkup -> encoder.encodeSerializableValue(serializer(), value)
        is ReplyKeyboardRemove -> encoder.encodeSerializableValue(serializer(), value)
        is InlineKeyboardMarkup -> encoder.encodeSerializableValue(serializer(), value)
        is ForceReply -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): KeyboardOption =
        decoder.tryDeserializers(
            ReplyKeyboardMarkup.serializer(),
            ReplyKeyboardRemove.serializer(),
            InlineKeyboardMarkup.serializer(),
            ForceReply.serializer()
        )
}

@Serializable
sealed class MaybeInaccessibleMessage : TelegramModel()
object MaybeInaccessibleMessageSerializer : KSerializer<MaybeInaccessibleMessage> {
    override val descriptor: SerialDescriptor = MaybeInaccessibleMessage.serializer().descriptor
    override fun serialize(encoder: Encoder, value: MaybeInaccessibleMessage) = when (value) {
        is Message -> encoder.encodeSerializableValue(serializer(), value)
        is InaccessibleMessage -> encoder.encodeSerializableValue(serializer(), value)
    }

    override fun deserialize(decoder: Decoder): MaybeInaccessibleMessage =
        decoder.decodeSerializableValue(JsonElement.serializer()).let { jsonElement ->
            json.decodeFromJsonElement(
                deserializer = if (jsonElement.jsonObject.getValue("date").jsonPrimitive.long == 0L) {
                    InaccessibleMessage.serializer()
                } else {
                    Message.serializer()
                },
                element = jsonElement,
            )
        }
}

@Serializable
sealed class InputFileOrString : TelegramModel()
object InputFileOrStringSerializer : KSerializer<InputFileOrString> {
    override val descriptor: SerialDescriptor = InputFileOrString.serializer().descriptor
    override fun serialize(encoder: Encoder, value: InputFileOrString) = TODO()
    override fun deserialize(decoder: Decoder): InputFileOrString = TODO()
}

@Serializable
sealed class IntegerOrString : TelegramModel()
object IntegerOrStringSerializer : KSerializer<IntegerOrString> {
    override val descriptor: SerialDescriptor = IntegerOrString.serializer().descriptor
    override fun serialize(encoder: Encoder, value: IntegerOrString) = TODO()
    override fun deserialize(decoder: Decoder): IntegerOrString = TODO()
}

@Serializable
data class TelegramResponse<T>(val ok: Boolean, val result: T? = null)

// --- Utility ---

enum class ParseMode { MarkdownV2, Markdown, HTML }

fun String.parseTelegramRequest() = Update.fromJson(this)

// --- Parameters & Responses ---


// Getting updates

/**
 * <p>This <a href="#available-types">object</a> represents an incoming update.<br>At most <strong>one</strong> of the optional parameters can be present in any given update.</p>
 *
 * @property update_id The update's unique identifier. Update identifiers start from a certain positive number and increase sequentially. This identifier becomes especially handy if you're using <a href="#setwebhook">webhooks</a>, since it allows you to ignore repeated updates or to restore the correct update sequence, should they get out of order. If there are no new updates for at least a week, then identifier of the next update will be chosen randomly instead of sequentially.
 * @property message <em>Optional</em>. New incoming message of any kind - text, photo, sticker, etc.
 * @property edited_message <em>Optional</em>. New version of a message that is known to the bot and was edited. This update may at times be triggered by changes to message fields that are either unavailable or not actively used by your bot.
 * @property channel_post <em>Optional</em>. New incoming channel post of any kind - text, photo, sticker, etc.
 * @property edited_channel_post <em>Optional</em>. New version of a channel post that is known to the bot and was edited. This update may at times be triggered by changes to message fields that are either unavailable or not actively used by your bot.
 * @property business_connection <em>Optional</em>. The bot was connected to or disconnected from a business account, or a user edited an existing connection with the bot
 * @property business_message <em>Optional</em>. New message from a connected business account
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

/**
 * <p>Describes the current status of a webhook.</p>
 *
 * @property url Webhook URL, may be empty if webhook is not set up
 * @property has_custom_certificate <em>True</em>, if a custom certificate was provided for webhook certificate checks
 * @property pending_update_count Number of updates awaiting delivery
 * @property ip_address <em>Optional</em>. Currently used webhook IP address
 * @property last_error_date <em>Optional</em>. Unix time for the most recent error that happened when trying to deliver an update via webhook
 * @property last_error_message <em>Optional</em>. Error message in human-readable format for the most recent error that happened when trying to deliver an update via webhook
 * @property last_synchronization_error_date <em>Optional</em>. Unix time of the most recent error that happened when trying to synchronize available updates with Telegram datacenters
 * @property max_connections <em>Optional</em>. The maximum allowed number of simultaneous HTTPS connections to the webhook for update delivery
 * @property allowed_updates <em>Optional</em>. A list of update types the bot is subscribed to. Defaults to all update types except <em>chat_member</em>
 *
 * @constructor Creates a [WebhookInfo].
 * */
@Serializable
data class WebhookInfo(
    val url: String,
    val has_custom_certificate: Boolean,
    val pending_update_count: Long,
    val ip_address: String? = null,
    val last_error_date: Long? = null,
    val last_error_message: String? = null,
    val last_synchronization_error_date: Long? = null,
    val max_connections: Long? = null,
    val allowed_updates: List<String>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// Available types

/**
 * <p>This object represents a Telegram user or bot.</p>
 *
 * @property id Unique identifier for this user or bot. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property is_bot <em>True</em>, if this user is a bot
 * @property first_name User's or bot's first name
 * @property last_name <em>Optional</em>. User's or bot's last name
 * @property username <em>Optional</em>. User's or bot's username
 * @property language_code <em>Optional</em>. <a href="https://en.wikipedia.org/wiki/IETF_language_tag">IETF language tag</a> of the user's language
 * @property is_premium <em>Optional</em>. <em>True</em>, if this user is a Telegram Premium user
 * @property added_to_attachment_menu <em>Optional</em>. <em>True</em>, if this user added the bot to the attachment menu
 * @property can_join_groups <em>Optional</em>. <em>True</em>, if the bot can be invited to groups. Returned only in <a href="#getme">getMe</a>.
 * @property can_read_all_group_messages <em>Optional</em>. <em>True</em>, if <a href="/bots/features#privacy-mode">privacy mode</a> is disabled for the bot. Returned only in <a href="#getme">getMe</a>.
 * @property supports_inline_queries <em>Optional</em>. <em>True</em>, if the bot supports inline queries. Returned only in <a href="#getme">getMe</a>.
 * @property can_connect_to_business <em>Optional</em>. <em>True</em>, if the bot can be connected to a Telegram Business account to receive its messages. Returned only in <a href="#getme">getMe</a>.
 * @property has_main_web_app <em>Optional</em>. <em>True</em>, if the bot has a main Web App. Returned only in <a href="#getme">getMe</a>.
 *
 * @constructor Creates a [User].
 * */
@Serializable
data class User(
    val id: UserId,
    val is_bot: Boolean,
    val first_name: String,
    val last_name: String? = null,
    val username: String? = null,
    val language_code: String? = null,
    val is_premium: Boolean? = null,
    val added_to_attachment_menu: Boolean? = null,
    val can_join_groups: Boolean? = null,
    val can_read_all_group_messages: Boolean? = null,
    val supports_inline_queries: Boolean? = null,
    val can_connect_to_business: Boolean? = null,
    val has_main_web_app: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a chat.</p>
 *
 * @property id Unique identifier for this chat. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property type Type of the chat, can be either “private”, “group”, “supergroup” or “channel”
 * @property title <em>Optional</em>. Title, for supergroups, channels and group chats
 * @property username <em>Optional</em>. Username, for private chats, supergroups and channels if available
 * @property first_name <em>Optional</em>. First name of the other party in a private chat
 * @property last_name <em>Optional</em>. Last name of the other party in a private chat
 * @property is_forum <em>Optional</em>. <em>True</em>, if the supergroup chat is a forum (has <a href="https://telegram.org/blog/topics-in-groups-collectible-usernames#topics-in-groups">topics</a> enabled)
 *
 * @constructor Creates a [Chat].
 * */
@Serializable
data class Chat(
    val id: ChatId,
    val type: String,
    val title: String? = null,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val is_forum: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains full information about a chat.</p>
 *
 * @property id Unique identifier for this chat. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property type Type of the chat, can be either “private”, “group”, “supergroup” or “channel”
 * @property accent_color_id Identifier of the accent color for the chat name and backgrounds of the chat photo, reply header, and link preview. See <a href="#accent-colors">accent colors</a> for more details.
 * @property max_reaction_count The maximum number of reactions that can be set on a message in the chat
 * @property title <em>Optional</em>. Title, for supergroups, channels and group chats
 * @property username <em>Optional</em>. Username, for private chats, supergroups and channels if available
 * @property first_name <em>Optional</em>. First name of the other party in a private chat
 * @property last_name <em>Optional</em>. Last name of the other party in a private chat
 * @property is_forum <em>Optional</em>. <em>True</em>, if the supergroup chat is a forum (has <a href="https://telegram.org/blog/topics-in-groups-collectible-usernames#topics-in-groups">topics</a> enabled)
 * @property photo <em>Optional</em>. Chat photo
 * @property active_usernames <em>Optional</em>. If non-empty, the list of all <a href="https://telegram.org/blog/topics-in-groups-collectible-usernames#collectible-usernames">active chat usernames</a>; for private chats, supergroups and channels
 * @property birthdate <em>Optional</em>. For private chats, the date of birth of the user
 * @property business_intro <em>Optional</em>. For private chats with business accounts, the intro of the business
 * @property business_location <em>Optional</em>. For private chats with business accounts, the location of the business
 * @property business_opening_hours <em>Optional</em>. For private chats with business accounts, the opening hours of the business
 * @property personal_chat <em>Optional</em>. For private chats, the personal channel of the user
 * @property available_reactions <em>Optional</em>. List of available reactions allowed in the chat. If omitted, then all <a href="#reactiontypeemoji">emoji reactions</a> are allowed.
 * @property background_custom_emoji_id <em>Optional</em>. Custom emoji identifier of the emoji chosen by the chat for the reply header and link preview background
 * @property profile_accent_color_id <em>Optional</em>. Identifier of the accent color for the chat's profile background. See <a href="#profile-accent-colors">profile accent colors</a> for more details.
 * @property profile_background_custom_emoji_id <em>Optional</em>. Custom emoji identifier of the emoji chosen by the chat for its profile background
 * @property emoji_status_custom_emoji_id <em>Optional</em>. Custom emoji identifier of the emoji status of the chat or the other party in a private chat
 * @property emoji_status_expiration_date <em>Optional</em>. Expiration date of the emoji status of the chat or the other party in a private chat, in Unix time, if any
 * @property bio <em>Optional</em>. Bio of the other party in a private chat
 * @property has_private_forwards <em>Optional</em>. <em>True</em>, if privacy settings of the other party in the private chat allows to use <code>tg://user?id=&lt;user_id&gt;</code> links only in chats with the user
 * @property has_restricted_voice_and_video_messages <em>Optional</em>. <em>True</em>, if the privacy settings of the other party restrict sending voice and video note messages in the private chat
 * @property join_to_send_messages <em>Optional</em>. <em>True</em>, if users need to join the supergroup before they can send messages
 * @property join_by_request <em>Optional</em>. <em>True</em>, if all users directly joining the supergroup without using an invite link need to be approved by supergroup administrators
 * @property description <em>Optional</em>. Description, for groups, supergroups and channel chats
 * @property invite_link <em>Optional</em>. Primary invite link, for groups, supergroups and channel chats
 * @property pinned_message <em>Optional</em>. The most recent pinned message (by sending date)
 * @property permissions <em>Optional</em>. Default chat member permissions, for groups and supergroups
 * @property can_send_paid_media <em>Optional</em>. <em>True</em>, if paid media messages can be sent or forwarded to the channel chat. The field is available only for channel chats.
 * @property slow_mode_delay <em>Optional</em>. For supergroups, the minimum allowed delay between consecutive messages sent by each unprivileged user; in seconds
 * @property unrestrict_boost_count <em>Optional</em>. For supergroups, the minimum number of boosts that a non-administrator user needs to add in order to ignore slow mode and chat permissions
 * @property message_auto_delete_time <em>Optional</em>. The time after which all messages sent to the chat will be automatically deleted; in seconds
 * @property has_aggressive_anti_spam_enabled <em>Optional</em>. <em>True</em>, if aggressive anti-spam checks are enabled in the supergroup. The field is only available to chat administrators.
 * @property has_hidden_members <em>Optional</em>. <em>True</em>, if non-administrators can only get the list of bots and administrators in the chat
 * @property has_protected_content <em>Optional</em>. <em>True</em>, if messages from the chat can't be forwarded to other chats
 * @property has_visible_history <em>Optional</em>. <em>True</em>, if new chat members will have access to old messages; available only to chat administrators
 * @property sticker_set_name <em>Optional</em>. For supergroups, name of the group sticker set
 * @property can_set_sticker_set <em>Optional</em>. <em>True</em>, if the bot can change the group sticker set
 * @property custom_emoji_sticker_set_name <em>Optional</em>. For supergroups, the name of the group's custom emoji sticker set. Custom emoji from this set can be used by all users and bots in the group.
 * @property linked_chat_id <em>Optional</em>. Unique identifier for the linked chat, i.e. the discussion group identifier for a channel and vice versa; for supergroups and channel chats. This identifier may be greater than 32 bits and some programming languages may have difficulty/silent defects in interpreting it. But it is smaller than 52 bits, so a signed 64 bit integer or double-precision float type are safe for storing this identifier.
 * @property location <em>Optional</em>. For supergroups, the location to which the supergroup is connected
 *
 * @constructor Creates a [ChatFullInfo].
 * */
@Serializable
data class ChatFullInfo(
    val id: ChatId,
    val type: String,
    val accent_color_id: Long,
    val max_reaction_count: Long,
    val title: String? = null,
    val username: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val is_forum: Boolean? = null,
    val photo: ChatPhoto? = null,
    val active_usernames: List<String>? = null,
    val birthdate: Birthdate? = null,
    val business_intro: BusinessIntro? = null,
    val business_location: BusinessLocation? = null,
    val business_opening_hours: BusinessOpeningHours? = null,
    val personal_chat: Chat? = null,
    val available_reactions: List<@Contextual ReactionType>? = null,
    val background_custom_emoji_id: String? = null,
    val profile_accent_color_id: Long? = null,
    val profile_background_custom_emoji_id: String? = null,
    val emoji_status_custom_emoji_id: String? = null,
    val emoji_status_expiration_date: Long? = null,
    val bio: String? = null,
    val has_private_forwards: Boolean? = null,
    val has_restricted_voice_and_video_messages: Boolean? = null,
    val join_to_send_messages: Boolean? = null,
    val join_by_request: Boolean? = null,
    val description: String? = null,
    val invite_link: String? = null,
    val pinned_message: Message? = null,
    val permissions: ChatPermissions? = null,
    val can_send_paid_media: Boolean? = null,
    val slow_mode_delay: Long? = null,
    val unrestrict_boost_count: Long? = null,
    val message_auto_delete_time: Long? = null,
    val has_aggressive_anti_spam_enabled: Boolean? = null,
    val has_hidden_members: Boolean? = null,
    val has_protected_content: Boolean? = null,
    val has_visible_history: Boolean? = null,
    val sticker_set_name: String? = null,
    val can_set_sticker_set: Boolean? = null,
    val custom_emoji_sticker_set_name: String? = null,
    val linked_chat_id: ChatId? = null,
    val location: ChatLocation? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a message.</p>
 *
 * @property message_id Unique message identifier inside this chat
 * @property date Date the message was sent in Unix time. It is always a positive number, representing a valid date.
 * @property chat Chat the message belongs to
 * @property message_thread_id <em>Optional</em>. Unique identifier of a message thread to which the message belongs; for supergroups only
 * @property from <em>Optional</em>. Sender of the message; empty for messages sent to channels. For backward compatibility, the field contains a fake sender user in non-channel chats, if the message was sent on behalf of a chat.
 * @property sender_chat <em>Optional</em>. Sender of the message, sent on behalf of a chat. For example, the channel itself for channel posts, the supergroup itself for messages from anonymous group administrators, the linked channel for messages automatically forwarded to the discussion group. For backward compatibility, the field <em>from</em> contains a fake sender user in non-channel chats, if the message was sent on behalf of a chat.
 * @property sender_boost_count <em>Optional</em>. If the sender of the message boosted the chat, the number of boosts added by the user
 * @property sender_business_bot <em>Optional</em>. The bot that actually sent the message on behalf of the business account. Available only for outgoing messages sent on behalf of the connected business account.
 * @property business_connection_id <em>Optional</em>. Unique identifier of the business connection from which the message was received. If non-empty, the message belongs to a chat of the corresponding business account that is independent from any potential bot chat which might share the same identifier.
 * @property forward_origin <em>Optional</em>. Information about the original message for forwarded messages
 * @property is_topic_message <em>Optional</em>. <em>True</em>, if the message is sent to a forum topic
 * @property is_automatic_forward <em>Optional</em>. <em>True</em>, if the message is a channel post that was automatically forwarded to the connected discussion group
 * @property reply_to_message <em>Optional</em>. For replies in the same chat and message thread, the original message. Note that the Message object in this field will not contain further <em>reply_to_message</em> fields even if it itself is a reply.
 * @property external_reply <em>Optional</em>. Information about the message that is being replied to, which may come from another chat or forum topic
 * @property quote <em>Optional</em>. For replies that quote part of the original message, the quoted part of the message
 * @property reply_to_story <em>Optional</em>. For replies to a story, the original story
 * @property via_bot <em>Optional</em>. Bot through which the message was sent
 * @property edit_date <em>Optional</em>. Date the message was last edited in Unix time
 * @property has_protected_content <em>Optional</em>. <em>True</em>, if the message can't be forwarded
 * @property is_from_offline <em>Optional</em>. True, if the message was sent by an implicit action, for example, as an away or a greeting business message, or as a scheduled message
 * @property media_group_id <em>Optional</em>. The unique identifier of a media message group this message belongs to
 * @property author_signature <em>Optional</em>. Signature of the post author for messages in channels, or the custom title of an anonymous group administrator
 * @property text <em>Optional</em>. For text messages, the actual UTF-8 text of the message
 * @property entities <em>Optional</em>. For text messages, special entities like usernames, URLs, bot commands, etc. that appear in the text
 * @property link_preview_options <em>Optional</em>. Options used for link preview generation for the message, if it is a text message and link preview options were changed
 * @property effect_id <em>Optional</em>. Unique identifier of the message effect added to the message
 * @property animation <em>Optional</em>. Message is an animation, information about the animation. For backward compatibility, when this field is set, the <em>document</em> field will also be set
 * @property audio <em>Optional</em>. Message is an audio file, information about the file
 * @property document <em>Optional</em>. Message is a general file, information about the file
 * @property paid_media <em>Optional</em>. Message contains paid media; information about the paid media
 * @property photo <em>Optional</em>. Message is a photo, available sizes of the photo
 * @property sticker <em>Optional</em>. Message is a sticker, information about the sticker
 * @property story <em>Optional</em>. Message is a forwarded story
 * @property video <em>Optional</em>. Message is a video, information about the video
 * @property video_note <em>Optional</em>. Message is a <a href="https://telegram.org/blog/video-messages-and-telescope">video note</a>, information about the video message
 * @property voice <em>Optional</em>. Message is a voice message, information about the file
 * @property caption <em>Optional</em>. Caption for the animation, audio, document, paid media, photo, video or voice
 * @property caption_entities <em>Optional</em>. For messages with a caption, special entities like usernames, URLs, bot commands, etc. that appear in the caption
 * @property show_caption_above_media <em>Optional</em>. True, if the caption must be shown above the message media
 * @property has_media_spoiler <em>Optional</em>. <em>True</em>, if the message media is covered by a spoiler animation
 * @property contact <em>Optional</em>. Message is a shared contact, information about the contact
 * @property dice <em>Optional</em>. Message is a dice with random value
 * @property game <em>Optional</em>. Message is a game, information about the game. <a href="#games">More about games »</a>
 * @property poll <em>Optional</em>. Message is a native poll, information about the poll
 * @property venue <em>Optional</em>. Message is a venue, information about the venue. For backward compatibility, when this field is set, the <em>location</em> field will also be set
 * @property location <em>Optional</em>. Message is a shared location, information about the location
 * @property new_chat_members <em>Optional</em>. New members that were added to the group or supergroup and information about them (the bot itself may be one of these members)
 * @property left_chat_member <em>Optional</em>. A member was removed from the group, information about them (this member may be the bot itself)
 * @property new_chat_title <em>Optional</em>. A chat title was changed to this value
 * @property new_chat_photo <em>Optional</em>. A chat photo was change to this value
 * @property delete_chat_photo <em>Optional</em>. Service message: the chat photo was deleted
 * @property group_chat_created <em>Optional</em>. Service message: the group has been created
 * @property supergroup_chat_created <em>Optional</em>. Service message: the supergroup has been created. This field can't be received in a message coming through updates, because bot can't be a member of a supergroup when it is created. It can only be found in reply_to_message if someone replies to a very first message in a directly created supergroup.
 * @property channel_chat_created <em>Optional</em>. Service message: the channel has been created. This field can't be received in a message coming through updates, because bot can't be a member of a channel when it is created. It can only be found in reply_to_message if someone replies to a very first message in a channel.
 * @property message_auto_delete_timer_changed <em>Optional</em>. Service message: auto-delete timer settings changed in the chat
 * @property migrate_to_chat_id <em>Optional</em>. The group has been migrated to a supergroup with the specified identifier. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property migrate_from_chat_id <em>Optional</em>. The supergroup has been migrated from a group with the specified identifier. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property pinned_message <em>Optional</em>. Specified message was pinned. Note that the Message object in this field will not contain further <em>reply_to_message</em> fields even if it itself is a reply.
 * @property invoice <em>Optional</em>. Message is an invoice for a <a href="#payments">payment</a>, information about the invoice. <a href="#payments">More about payments »</a>
 * @property successful_payment <em>Optional</em>. Message is a service message about a successful payment, information about the payment. <a href="#payments">More about payments »</a>
 * @property refunded_payment <em>Optional</em>. Message is a service message about a refunded payment, information about the payment. <a href="#payments">More about payments »</a>
 * @property users_shared <em>Optional</em>. Service message: users were shared with the bot
 * @property chat_shared <em>Optional</em>. Service message: a chat was shared with the bot
 * @property connected_website <em>Optional</em>. The domain name of the website on which the user has logged in. <a href="/widgets/login">More about Telegram Login »</a>
 * @property write_access_allowed <em>Optional</em>. Service message: the user allowed the bot to write messages after adding it to the attachment or side menu, launching a Web App from a link, or accepting an explicit request from a Web App sent by the method <a href="/bots/webapps#initializing-mini-apps">requestWriteAccess</a>
 * @property passport_data <em>Optional</em>. Telegram Passport data
 * @property proximity_alert_triggered <em>Optional</em>. Service message. A user in the chat triggered another user's proximity alert while sharing Live Location.
 * @property boost_added <em>Optional</em>. Service message: user boosted the chat
 * @property chat_background_set <em>Optional</em>. Service message: chat background set
 * @property forum_topic_created <em>Optional</em>. Service message: forum topic created
 * @property forum_topic_edited <em>Optional</em>. Service message: forum topic edited
 * @property forum_topic_closed <em>Optional</em>. Service message: forum topic closed
 * @property forum_topic_reopened <em>Optional</em>. Service message: forum topic reopened
 * @property general_forum_topic_hidden <em>Optional</em>. Service message: the 'General' forum topic hidden
 * @property general_forum_topic_unhidden <em>Optional</em>. Service message: the 'General' forum topic unhidden
 * @property giveaway_created <em>Optional</em>. Service message: a scheduled giveaway was created
 * @property giveaway <em>Optional</em>. The message is a scheduled giveaway message
 * @property giveaway_winners <em>Optional</em>. A giveaway with public winners was completed
 * @property giveaway_completed <em>Optional</em>. Service message: a giveaway without public winners was completed
 * @property video_chat_scheduled <em>Optional</em>. Service message: video chat scheduled
 * @property video_chat_started <em>Optional</em>. Service message: video chat started
 * @property video_chat_ended <em>Optional</em>. Service message: video chat ended
 * @property video_chat_participants_invited <em>Optional</em>. Service message: new participants invited to a video chat
 * @property web_app_data <em>Optional</em>. Service message: data sent by a Web App
 * @property reply_markup <em>Optional</em>. Inline keyboard attached to the message. <code>login_url</code> buttons are represented as ordinary <code>url</code> buttons.
 *
 * @constructor Creates a [Message].
 * */
@Serializable
data class Message(
    val message_id: MessageId,
    val date: Long,
    val chat: Chat,
    val message_thread_id: MessageThreadId? = null,
    val from: User? = null,
    val sender_chat: Chat? = null,
    val sender_boost_count: Long? = null,
    val sender_business_bot: User? = null,
    val business_connection_id: BusinessConnectionId? = null,
    val forward_origin: @Contextual MessageOrigin? = null,
    val is_topic_message: Boolean? = null,
    val is_automatic_forward: Boolean? = null,
    val reply_to_message: Message? = null,
    val external_reply: ExternalReplyInfo? = null,
    val quote: TextQuote? = null,
    val reply_to_story: Story? = null,
    val via_bot: User? = null,
    val edit_date: Long? = null,
    val has_protected_content: Boolean? = null,
    val is_from_offline: Boolean? = null,
    val media_group_id: String? = null,
    val author_signature: String? = null,
    val text: String? = null,
    val entities: List<MessageEntity>? = null,
    val link_preview_options: LinkPreviewOptions? = null,
    val effect_id: String? = null,
    val animation: Animation? = null,
    val audio: Audio? = null,
    val document: Document? = null,
    val paid_media: PaidMediaInfo? = null,
    val photo: List<PhotoSize>? = null,
    val sticker: Sticker? = null,
    val story: Story? = null,
    val video: Video? = null,
    val video_note: VideoNote? = null,
    val voice: Voice? = null,
    val caption: String? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val has_media_spoiler: Boolean? = null,
    val contact: Contact? = null,
    val dice: Dice? = null,
    val game: Game? = null,
    val poll: Poll? = null,
    val venue: Venue? = null,
    val location: Location? = null,
    val new_chat_members: List<User>? = null,
    val left_chat_member: User? = null,
    val new_chat_title: String? = null,
    val new_chat_photo: List<PhotoSize>? = null,
    val delete_chat_photo: Boolean? = null,
    val group_chat_created: Boolean? = null,
    val supergroup_chat_created: Boolean? = null,
    val channel_chat_created: Boolean? = null,
    val message_auto_delete_timer_changed: MessageAutoDeleteTimerChanged? = null,
    val migrate_to_chat_id: ChatId? = null,
    val migrate_from_chat_id: ChatId? = null,
    val pinned_message: @Contextual MaybeInaccessibleMessage? = null,
    val invoice: Invoice? = null,
    val successful_payment: SuccessfulPayment? = null,
    val refunded_payment: RefundedPayment? = null,
    val users_shared: UsersShared? = null,
    val chat_shared: ChatShared? = null,
    val connected_website: String? = null,
    val write_access_allowed: WriteAccessAllowed? = null,
    val passport_data: PassportData? = null,
    val proximity_alert_triggered: ProximityAlertTriggered? = null,
    val boost_added: ChatBoostAdded? = null,
    val chat_background_set: ChatBackground? = null,
    val forum_topic_created: ForumTopicCreated? = null,
    val forum_topic_edited: ForumTopicEdited? = null,
    val forum_topic_closed: @Contextual Any? = null,
    val forum_topic_reopened: @Contextual Any? = null,
    val general_forum_topic_hidden: @Contextual Any? = null,
    val general_forum_topic_unhidden: @Contextual Any? = null,
    val giveaway_created: @Contextual Any? = null,
    val giveaway: Giveaway? = null,
    val giveaway_winners: GiveawayWinners? = null,
    val giveaway_completed: GiveawayCompleted? = null,
    val video_chat_scheduled: VideoChatScheduled? = null,
    val video_chat_started: @Contextual Any? = null,
    val video_chat_ended: VideoChatEnded? = null,
    val video_chat_participants_invited: VideoChatParticipantsInvited? = null,
    val web_app_data: WebAppData? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
) : MaybeInaccessibleMessage() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object describes a message that was deleted or is otherwise inaccessible to the bot.</p>
 *
 * @property chat Chat the message belonged to
 * @property message_id Unique message identifier inside the chat
 * @property date Always 0. The field can be used to differentiate regular and inaccessible messages.
 *
 * @constructor Creates a [InaccessibleMessage].
 * */
@Serializable
data class InaccessibleMessage(
    val chat: Chat,
    val message_id: MessageId,
    val date: Long,
) : MaybeInaccessibleMessage() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents one special entity in a text message. For example, hashtags, usernames, URLs, etc.</p>
 *
 * @property type Type of the entity. Currently, can be “mention” (<code>@username</code>), “hashtag” (<code>#hashtag</code>), “cashtag” (<code>$USD</code>), “bot_command” (<code>/start@jobs_bot</code>), “url” (<code>https://telegram.org</code>), “email” (<code>do-not-reply@telegram.org</code>), “phone_number” (<code>+1-212-555-0123</code>), “bold” (<strong>bold text</strong>), “italic” (<em>italic text</em>), “underline” (underlined text), “strikethrough” (strikethrough text), “spoiler” (spoiler message), “blockquote” (block quotation), “expandable_blockquote” (collapsed-by-default block quotation), “code” (monowidth string), “pre” (monowidth block), “text_link” (for clickable text URLs), “text_mention” (for users <a href="https://telegram.org/blog/edit#new-mentions">without usernames</a>), “custom_emoji” (for inline custom emoji stickers)
 * @property offset Offset in <a href="/api/entities#entity-length">UTF-16 code units</a> to the start of the entity
 * @property length Length of the entity in <a href="/api/entities#entity-length">UTF-16 code units</a>
 * @property url <em>Optional</em>. For “text_link” only, URL that will be opened after user taps on the text
 * @property user <em>Optional</em>. For “text_mention” only, the mentioned user
 * @property language <em>Optional</em>. For “pre” only, the programming language of the entity text
 * @property custom_emoji_id <em>Optional</em>. For “custom_emoji” only, unique identifier of the custom emoji. Use <a href="#getcustomemojistickers">getCustomEmojiStickers</a> to get full information about the sticker
 *
 * @constructor Creates a [MessageEntity].
 * */
@Serializable
data class MessageEntity(
    val type: String,
    val offset: Long,
    val length: Long,
    val url: String? = null,
    val user: User? = null,
    val language: String? = null,
    val custom_emoji_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about the quoted part of a message that is replied to by the given message.</p>
 *
 * @property text Text of the quoted part of a message that is replied to by the given message
 * @property position Approximate quote position in the original message in UTF-16 code units as specified by the sender
 * @property entities <em>Optional</em>. Special entities that appear in the quote. Currently, only <em>bold</em>, <em>italic</em>, <em>underline</em>, <em>strikethrough</em>, <em>spoiler</em>, and <em>custom_emoji</em> entities are kept in quotes.
 * @property is_manual <em>Optional</em>. True, if the quote was chosen manually by the message sender. Otherwise, the quote was added automatically by the server.
 *
 * @constructor Creates a [TextQuote].
 * */
@Serializable
data class TextQuote(
    val text: String,
    val position: Long,
    val entities: List<MessageEntity>? = null,
    val is_manual: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about a message that is being replied to, which may come from another chat or forum topic.</p>
 *
 * @property origin Origin of the message replied to by the given message
 * @property chat <em>Optional</em>. Chat the original message belongs to. Available only if the chat is a supergroup or a channel.
 * @property message_id <em>Optional</em>. Unique message identifier inside the original chat. Available only if the original chat is a supergroup or a channel.
 * @property link_preview_options <em>Optional</em>. Options used for link preview generation for the original message, if it is a text message
 * @property animation <em>Optional</em>. Message is an animation, information about the animation
 * @property audio <em>Optional</em>. Message is an audio file, information about the file
 * @property document <em>Optional</em>. Message is a general file, information about the file
 * @property paid_media <em>Optional</em>. Message contains paid media; information about the paid media
 * @property photo <em>Optional</em>. Message is a photo, available sizes of the photo
 * @property sticker <em>Optional</em>. Message is a sticker, information about the sticker
 * @property story <em>Optional</em>. Message is a forwarded story
 * @property video <em>Optional</em>. Message is a video, information about the video
 * @property video_note <em>Optional</em>. Message is a <a href="https://telegram.org/blog/video-messages-and-telescope">video note</a>, information about the video message
 * @property voice <em>Optional</em>. Message is a voice message, information about the file
 * @property has_media_spoiler <em>Optional</em>. <em>True</em>, if the message media is covered by a spoiler animation
 * @property contact <em>Optional</em>. Message is a shared contact, information about the contact
 * @property dice <em>Optional</em>. Message is a dice with random value
 * @property game <em>Optional</em>. Message is a game, information about the game. <a href="#games">More about games »</a>
 * @property giveaway <em>Optional</em>. Message is a scheduled giveaway, information about the giveaway
 * @property giveaway_winners <em>Optional</em>. A giveaway with public winners was completed
 * @property invoice <em>Optional</em>. Message is an invoice for a <a href="#payments">payment</a>, information about the invoice. <a href="#payments">More about payments »</a>
 * @property location <em>Optional</em>. Message is a shared location, information about the location
 * @property poll <em>Optional</em>. Message is a native poll, information about the poll
 * @property venue <em>Optional</em>. Message is a venue, information about the venue
 *
 * @constructor Creates a [ExternalReplyInfo].
 * */
@Serializable
data class ExternalReplyInfo(
    val origin: @Contextual MessageOrigin,
    val chat: Chat? = null,
    val message_id: MessageId? = null,
    val link_preview_options: LinkPreviewOptions? = null,
    val animation: Animation? = null,
    val audio: Audio? = null,
    val document: Document? = null,
    val paid_media: PaidMediaInfo? = null,
    val photo: List<PhotoSize>? = null,
    val sticker: Sticker? = null,
    val story: Story? = null,
    val video: Video? = null,
    val video_note: VideoNote? = null,
    val voice: Voice? = null,
    val has_media_spoiler: Boolean? = null,
    val contact: Contact? = null,
    val dice: Dice? = null,
    val game: Game? = null,
    val giveaway: Giveaway? = null,
    val giveaway_winners: GiveawayWinners? = null,
    val invoice: Invoice? = null,
    val location: Location? = null,
    val poll: Poll? = null,
    val venue: Venue? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes reply parameters for the message that is being sent.</p>
 *
 * @property message_id Identifier of the message that will be replied to in the current chat, or in the chat <em>chat_id</em> if it is specified
 * @property chat_id <em>Optional</em>. If the message to be replied to is from a different chat, unique identifier for the chat or username of the channel (in the format <code>@channelusername</code>). Not supported for messages sent on behalf of a business account.
 * @property allow_sending_without_reply <em>Optional</em>. Pass <em>True</em> if the message should be sent even if the specified message to be replied to is not found. Always <em>False</em> for replies in another chat or forum topic. Always <em>True</em> for messages sent on behalf of a business account.
 * @property quote <em>Optional</em>. Quoted part of the message to be replied to; 0-1024 characters after entities parsing. The quote must be an exact substring of the message to be replied to, including <em>bold</em>, <em>italic</em>, <em>underline</em>, <em>strikethrough</em>, <em>spoiler</em>, and <em>custom_emoji</em> entities. The message will fail to send if the quote isn't found in the original message.
 * @property quote_parse_mode <em>Optional</em>. Mode for parsing entities in the quote. See <a href="#formatting-options">formatting options</a> for more details.
 * @property quote_entities <em>Optional</em>. A JSON-serialized list of special entities that appear in the quote. It can be specified instead of <em>quote_parse_mode</em>.
 * @property quote_position <em>Optional</em>. Position of the quote in the original message in UTF-16 code units
 *
 * @constructor Creates a [ReplyParameters].
 * */
@Serializable
data class ReplyParameters(
    val message_id: MessageId,
    val chat_id: ChatId? = null,
    val allow_sending_without_reply: Boolean? = null,
    val quote: String? = null,
    val quote_parse_mode: String? = null,
    val quote_entities: List<MessageEntity>? = null,
    val quote_position: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The message was originally sent by a known user.</p>
 *
 * @property type Type of the message origin, always “user”
 * @property date Date the message was sent originally in Unix time
 * @property sender_user User that sent the message originally
 *
 * @constructor Creates a [MessageOriginUser].
 * */
@Serializable
data class MessageOriginUser(
    val type: String,
    val date: Long,
    val sender_user: User,
) : MessageOrigin() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The message was originally sent by an unknown user.</p>
 *
 * @property type Type of the message origin, always “hidden_user”
 * @property date Date the message was sent originally in Unix time
 * @property sender_user_name Name of the user that sent the message originally
 *
 * @constructor Creates a [MessageOriginHiddenUser].
 * */
@Serializable
data class MessageOriginHiddenUser(
    val type: String,
    val date: Long,
    val sender_user_name: String,
) : MessageOrigin() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The message was originally sent on behalf of a chat to a group chat.</p>
 *
 * @property type Type of the message origin, always “chat”
 * @property date Date the message was sent originally in Unix time
 * @property sender_chat Chat that sent the message originally
 * @property author_signature <em>Optional</em>. For messages originally sent by an anonymous chat administrator, original message author signature
 *
 * @constructor Creates a [MessageOriginChat].
 * */
@Serializable
data class MessageOriginChat(
    val type: String,
    val date: Long,
    val sender_chat: Chat,
    val author_signature: String? = null,
) : MessageOrigin() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The message was originally sent to a channel chat.</p>
 *
 * @property type Type of the message origin, always “channel”
 * @property date Date the message was sent originally in Unix time
 * @property chat Channel chat to which the message was originally sent
 * @property message_id Unique message identifier inside the chat
 * @property author_signature <em>Optional</em>. Signature of the original post author
 *
 * @constructor Creates a [MessageOriginChannel].
 * */
@Serializable
data class MessageOriginChannel(
    val type: String,
    val date: Long,
    val chat: Chat,
    val message_id: MessageId,
    val author_signature: String? = null,
) : MessageOrigin() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents one size of a photo or a <a href="#document">file</a> / <a href="#sticker">sticker</a> thumbnail.</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property width Photo width
 * @property height Photo height
 * @property file_size <em>Optional</em>. File size in bytes
 *
 * @constructor Creates a [PhotoSize].
 * */
@Serializable
data class PhotoSize(
    val file_id: String,
    val file_unique_id: String,
    val width: Long,
    val height: Long,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an animation file (GIF or H.264/MPEG-4 AVC video without sound).</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property width Video width as defined by the sender
 * @property height Video height as defined by the sender
 * @property duration Duration of the video in seconds as defined by the sender
 * @property thumbnail <em>Optional</em>. Animation thumbnail as defined by the sender
 * @property file_name <em>Optional</em>. Original animation filename as defined by the sender
 * @property mime_type <em>Optional</em>. MIME type of the file as defined by the sender
 * @property file_size <em>Optional</em>. File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
 *
 * @constructor Creates a [Animation].
 * */
@Serializable
data class Animation(
    val file_id: String,
    val file_unique_id: String,
    val width: Long,
    val height: Long,
    val duration: Long,
    val thumbnail: PhotoSize? = null,
    val file_name: String? = null,
    val mime_type: String? = null,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an audio file to be treated as music by the Telegram clients.</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property duration Duration of the audio in seconds as defined by the sender
 * @property performer <em>Optional</em>. Performer of the audio as defined by the sender or by audio tags
 * @property title <em>Optional</em>. Title of the audio as defined by the sender or by audio tags
 * @property file_name <em>Optional</em>. Original filename as defined by the sender
 * @property mime_type <em>Optional</em>. MIME type of the file as defined by the sender
 * @property file_size <em>Optional</em>. File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
 * @property thumbnail <em>Optional</em>. Thumbnail of the album cover to which the music file belongs
 *
 * @constructor Creates a [Audio].
 * */
@Serializable
data class Audio(
    val file_id: String,
    val file_unique_id: String,
    val duration: Long,
    val performer: String? = null,
    val title: String? = null,
    val file_name: String? = null,
    val mime_type: String? = null,
    val file_size: Long? = null,
    val thumbnail: PhotoSize? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a general file (as opposed to <a href="#photosize">photos</a>, <a href="#voice">voice messages</a> and <a href="#audio">audio files</a>).</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property thumbnail <em>Optional</em>. Document thumbnail as defined by the sender
 * @property file_name <em>Optional</em>. Original filename as defined by the sender
 * @property mime_type <em>Optional</em>. MIME type of the file as defined by the sender
 * @property file_size <em>Optional</em>. File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
 *
 * @constructor Creates a [Document].
 * */
@Serializable
data class Document(
    val file_id: String,
    val file_unique_id: String,
    val thumbnail: PhotoSize? = null,
    val file_name: String? = null,
    val mime_type: String? = null,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a story.</p>
 *
 * @property chat Chat that posted the story
 * @property id Unique identifier for the story in the chat
 *
 * @constructor Creates a [Story].
 * */
@Serializable
data class Story(
    val chat: Chat,
    val id: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a video file.</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property width Video width as defined by the sender
 * @property height Video height as defined by the sender
 * @property duration Duration of the video in seconds as defined by the sender
 * @property thumbnail <em>Optional</em>. Video thumbnail
 * @property file_name <em>Optional</em>. Original filename as defined by the sender
 * @property mime_type <em>Optional</em>. MIME type of the file as defined by the sender
 * @property file_size <em>Optional</em>. File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
 *
 * @constructor Creates a [Video].
 * */
@Serializable
data class Video(
    val file_id: String,
    val file_unique_id: String,
    val width: Long,
    val height: Long,
    val duration: Long,
    val thumbnail: PhotoSize? = null,
    val file_name: String? = null,
    val mime_type: String? = null,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a <a href="https://telegram.org/blog/video-messages-and-telescope">video message</a> (available in Telegram apps as of <a href="https://telegram.org/blog/video-messages-and-telescope">v.4.0</a>).</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property length Video width and height (diameter of the video message) as defined by the sender
 * @property duration Duration of the video in seconds as defined by the sender
 * @property thumbnail <em>Optional</em>. Video thumbnail
 * @property file_size <em>Optional</em>. File size in bytes
 *
 * @constructor Creates a [VideoNote].
 * */
@Serializable
data class VideoNote(
    val file_id: String,
    val file_unique_id: String,
    val length: Long,
    val duration: Long,
    val thumbnail: PhotoSize? = null,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a voice note.</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property duration Duration of the audio in seconds as defined by the sender
 * @property mime_type <em>Optional</em>. MIME type of the file as defined by the sender
 * @property file_size <em>Optional</em>. File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
 *
 * @constructor Creates a [Voice].
 * */
@Serializable
data class Voice(
    val file_id: String,
    val file_unique_id: String,
    val duration: Long,
    val mime_type: String? = null,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes the paid media added to a message.</p>
 *
 * @property star_count The number of Telegram Stars that must be paid to buy access to the media
 * @property paid_media Information about the paid media
 *
 * @constructor Creates a [PaidMediaInfo].
 * */
@Serializable
data class PaidMediaInfo(
    val star_count: Long,
    val paid_media: List<@Contextual PaidMedia>,
) : PaidMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The paid media isn't available before the payment.</p>
 *
 * @property type Type of the paid media, always “preview”
 * @property width <em>Optional</em>. Media width as defined by the sender
 * @property height <em>Optional</em>. Media height as defined by the sender
 * @property duration <em>Optional</em>. Duration of the media in seconds as defined by the sender
 *
 * @constructor Creates a [PaidMediaPreview].
 * */
@Serializable
data class PaidMediaPreview(
    val type: String,
    val width: Long? = null,
    val height: Long? = null,
    val duration: Long? = null,
) : PaidMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The paid media is a photo.</p>
 *
 * @property type Type of the paid media, always “photo”
 * @property photo The photo
 *
 * @constructor Creates a [PaidMediaPhoto].
 * */
@Serializable
data class PaidMediaPhoto(
    val type: String,
    val photo: List<PhotoSize>,
) : PaidMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The paid media is a video.</p>
 *
 * @property type Type of the paid media, always “video”
 * @property video The video
 *
 * @constructor Creates a [PaidMediaVideo].
 * */
@Serializable
data class PaidMediaVideo(
    val type: String,
    val video: Video,
) : PaidMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a phone contact.</p>
 *
 * @property phone_number Contact's phone number
 * @property first_name Contact's first name
 * @property last_name <em>Optional</em>. Contact's last name
 * @property user_id <em>Optional</em>. Contact's user identifier in Telegram. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property vcard <em>Optional</em>. Additional data about the contact in the form of a <a href="https://en.wikipedia.org/wiki/VCard">vCard</a>
 *
 * @constructor Creates a [Contact].
 * */
@Serializable
data class Contact(
    val phone_number: String,
    val first_name: String,
    val last_name: String? = null,
    val user_id: UserId? = null,
    val vcard: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an animated emoji that displays a random value.</p>
 *
 * @property emoji Emoji on which the dice throw animation is based
 * @property value Value of the dice, 1-6 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EAF.png" width="20" height="20" alt="🎯">” and “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB3.png" width="20" height="20" alt="🎳">” base emoji, 1-5 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8F80.png" width="20" height="20" alt="🏀">” and “<img class="emoji" src="//telegram.org/img/emoji/40/E29ABD.png" width="20" height="20" alt="⚽">” base emoji, 1-64 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB0.png" width="20" height="20" alt="🎰">” base emoji
 *
 * @constructor Creates a [Dice].
 * */
@Serializable
data class Dice(
    val emoji: String,
    val value: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about one answer option in a poll.</p>
 *
 * @property text Option text, 1-100 characters
 * @property voter_count Number of users that voted for this option
 * @property text_entities <em>Optional</em>. Special entities that appear in the option <em>text</em>. Currently, only custom emoji entities are allowed in poll option texts
 *
 * @constructor Creates a [PollOption].
 * */
@Serializable
data class PollOption(
    val text: String,
    val voter_count: Long,
    val text_entities: List<MessageEntity>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about one answer option in a poll to be sent.</p>
 *
 * @property text Option text, 1-100 characters
 * @property text_parse_mode <em>Optional</em>. Mode for parsing entities in the text. See <a href="#formatting-options">formatting options</a> for more details. Currently, only custom emoji entities are allowed
 * @property text_entities <em>Optional</em>. A JSON-serialized list of special entities that appear in the poll option text. It can be specified instead of <em>text_parse_mode</em>
 *
 * @constructor Creates a [InputPollOption].
 * */
@Serializable
data class InputPollOption(
    val text: String,
    val text_parse_mode: String? = null,
    val text_entities: List<MessageEntity>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an answer of a user in a non-anonymous poll.</p>
 *
 * @property poll_id Unique poll identifier
 * @property option_ids 0-based identifiers of chosen answer options. May be empty if the vote was retracted.
 * @property voter_chat <em>Optional</em>. The chat that changed the answer to the poll, if the voter is anonymous
 * @property user <em>Optional</em>. The user that changed the answer to the poll, if the voter isn't anonymous
 *
 * @constructor Creates a [PollAnswer].
 * */
@Serializable
data class PollAnswer(
    val poll_id: String,
    val option_ids: List<Long>,
    val voter_chat: Chat? = null,
    val user: User? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about a poll.</p>
 *
 * @property id Unique poll identifier
 * @property question Poll question, 1-300 characters
 * @property options List of poll options
 * @property total_voter_count Total number of users that voted in the poll
 * @property is_closed <em>True</em>, if the poll is closed
 * @property is_anonymous <em>True</em>, if the poll is anonymous
 * @property type Poll type, currently can be “regular” or “quiz”
 * @property allows_multiple_answers <em>True</em>, if the poll allows multiple answers
 * @property question_entities <em>Optional</em>. Special entities that appear in the <em>question</em>. Currently, only custom emoji entities are allowed in poll questions
 * @property correct_option_id <em>Optional</em>. 0-based identifier of the correct answer option. Available only for polls in the quiz mode, which are closed, or was sent (not forwarded) by the bot or to the private chat with the bot.
 * @property explanation <em>Optional</em>. Text that is shown when a user chooses an incorrect answer or taps on the lamp icon in a quiz-style poll, 0-200 characters
 * @property explanation_entities <em>Optional</em>. Special entities like usernames, URLs, bot commands, etc. that appear in the <em>explanation</em>
 * @property open_period <em>Optional</em>. Amount of time in seconds the poll will be active after creation
 * @property close_date <em>Optional</em>. Point in time (Unix timestamp) when the poll will be automatically closed
 *
 * @constructor Creates a [Poll].
 * */
@Serializable
data class Poll(
    val id: String,
    val question: String,
    val options: List<PollOption>,
    val total_voter_count: Long,
    val is_closed: Boolean,
    val is_anonymous: Boolean,
    val type: String,
    val allows_multiple_answers: Boolean,
    val question_entities: List<MessageEntity>? = null,
    val correct_option_id: Long? = null,
    val explanation: String? = null,
    val explanation_entities: List<MessageEntity>? = null,
    val open_period: Long? = null,
    val close_date: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a point on the map.</p>
 *
 * @property latitude Latitude as defined by the sender
 * @property longitude Longitude as defined by the sender
 * @property horizontal_accuracy <em>Optional</em>. The radius of uncertainty for the location, measured in meters; 0-1500
 * @property live_period <em>Optional</em>. Time relative to the message sending date, during which the location can be updated; in seconds. For active live locations only.
 * @property heading <em>Optional</em>. The direction in which user is moving, in degrees; 1-360. For active live locations only.
 * @property proximity_alert_radius <em>Optional</em>. The maximum distance for proximity alerts about approaching another chat member, in meters. For sent live locations only.
 *
 * @constructor Creates a [Location].
 * */
@Serializable
data class Location(
    val latitude: Float,
    val longitude: Float,
    val horizontal_accuracy: Float? = null,
    val live_period: Long? = null,
    val heading: Long? = null,
    val proximity_alert_radius: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a venue.</p>
 *
 * @property location Venue location. Can't be a live location
 * @property title Name of the venue
 * @property address Address of the venue
 * @property foursquare_id <em>Optional</em>. Foursquare identifier of the venue
 * @property foursquare_type <em>Optional</em>. Foursquare type of the venue. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
 * @property google_place_id <em>Optional</em>. Google Places identifier of the venue
 * @property google_place_type <em>Optional</em>. Google Places type of the venue. (See <a href="https://developers.google.com/places/web-service/supported_types">supported types</a>.)
 *
 * @constructor Creates a [Venue].
 * */
@Serializable
data class Venue(
    val location: Location,
    val title: String,
    val address: String,
    val foursquare_id: String? = null,
    val foursquare_type: String? = null,
    val google_place_id: String? = null,
    val google_place_type: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes data sent from a <a href="/bots/webapps">Web App</a> to the bot.</p>
 *
 * @property data The data. Be aware that a bad client can send arbitrary data in this field.
 * @property button_text Text of the <em>web_app</em> keyboard button from which the Web App was opened. Be aware that a bad client can send arbitrary data in this field.
 *
 * @constructor Creates a [WebAppData].
 * */
@Serializable
data class WebAppData(
    val data: String,
    val button_text: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents the content of a service message, sent whenever a user in the chat triggers a proximity alert set by another user.</p>
 *
 * @property traveler User that triggered the alert
 * @property watcher User that set the alert
 * @property distance The distance between the users
 *
 * @constructor Creates a [ProximityAlertTriggered].
 * */
@Serializable
data class ProximityAlertTriggered(
    val traveler: User,
    val watcher: User,
    val distance: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about a change in auto-delete timer settings.</p>
 *
 * @property message_auto_delete_time New auto-delete time for messages in the chat; in seconds
 *
 * @constructor Creates a [MessageAutoDeleteTimerChanged].
 * */
@Serializable
data class MessageAutoDeleteTimerChanged(
    val message_auto_delete_time: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about a user boosting a chat.</p>
 *
 * @property boost_count Number of boosts added by the user
 *
 * @constructor Creates a [ChatBoostAdded].
 * */
@Serializable
data class ChatBoostAdded(
    val boost_count: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is filled using the selected color.</p>
 *
 * @property type Type of the background fill, always “solid”
 * @property color The color of the background fill in the RGB24 format
 *
 * @constructor Creates a [BackgroundFillSolid].
 * */
@Serializable
data class BackgroundFillSolid(
    val type: String,
    val color: Long,
) : BackgroundFill() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is a gradient fill.</p>
 *
 * @property type Type of the background fill, always “gradient”
 * @property top_color Top color of the gradient in the RGB24 format
 * @property bottom_color Bottom color of the gradient in the RGB24 format
 * @property rotation_angle Clockwise rotation angle of the background fill in degrees; 0-359
 *
 * @constructor Creates a [BackgroundFillGradient].
 * */
@Serializable
data class BackgroundFillGradient(
    val type: String,
    val top_color: Long,
    val bottom_color: Long,
    val rotation_angle: Long,
) : BackgroundFill() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is a freeform gradient that rotates after every message in the chat.</p>
 *
 * @property type Type of the background fill, always “freeform_gradient”
 * @property colors A list of the 3 or 4 base colors that are used to generate the freeform gradient in the RGB24 format
 *
 * @constructor Creates a [BackgroundFillFreeformGradient].
 * */
@Serializable
data class BackgroundFillFreeformGradient(
    val type: String,
    val colors: List<Long>,
) : BackgroundFill() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is automatically filled based on the selected colors.</p>
 *
 * @property type Type of the background, always “fill”
 * @property fill The background fill
 * @property dark_theme_dimming Dimming of the background in dark themes, as a percentage; 0-100
 *
 * @constructor Creates a [BackgroundTypeFill].
 * */
@Serializable
data class BackgroundTypeFill(
    val type: String,
    val fill: @Contextual BackgroundFill,
    val dark_theme_dimming: Long,
) : BackgroundType() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is a wallpaper in the JPEG format.</p>
 *
 * @property type Type of the background, always “wallpaper”
 * @property document Document with the wallpaper
 * @property dark_theme_dimming Dimming of the background in dark themes, as a percentage; 0-100
 * @property is_blurred <em>Optional</em>. <em>True</em>, if the wallpaper is downscaled to fit in a 450x450 square and then box-blurred with radius 12
 * @property is_moving <em>Optional</em>. <em>True</em>, if the background moves slightly when the device is tilted
 *
 * @constructor Creates a [BackgroundTypeWallpaper].
 * */
@Serializable
data class BackgroundTypeWallpaper(
    val type: String,
    val document: Document,
    val dark_theme_dimming: Long,
    val is_blurred: Boolean? = null,
    val is_moving: Boolean? = null,
) : BackgroundType() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is a PNG or TGV (gzipped subset of SVG with MIME type “application/x-tgwallpattern”) pattern to be combined with the background fill chosen by the user.</p>
 *
 * @property type Type of the background, always “pattern”
 * @property document Document with the pattern
 * @property fill The background fill that is combined with the pattern
 * @property intensity Intensity of the pattern when it is shown above the filled background; 0-100
 * @property is_inverted <em>Optional</em>. <em>True</em>, if the background fill must be applied only to the pattern itself. All other pixels are black in this case. For dark themes only
 * @property is_moving <em>Optional</em>. <em>True</em>, if the background moves slightly when the device is tilted
 *
 * @constructor Creates a [BackgroundTypePattern].
 * */
@Serializable
data class BackgroundTypePattern(
    val type: String,
    val document: Document,
    val fill: @Contextual BackgroundFill,
    val intensity: Long,
    val is_inverted: Boolean? = null,
    val is_moving: Boolean? = null,
) : BackgroundType() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The background is taken directly from a built-in chat theme.</p>
 *
 * @property type Type of the background, always “chat_theme”
 * @property theme_name Name of the chat theme, which is usually an emoji
 *
 * @constructor Creates a [BackgroundTypeChatTheme].
 * */
@Serializable
data class BackgroundTypeChatTheme(
    val type: String,
    val theme_name: String,
) : BackgroundType() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a chat background.</p>
 *
 * @property type Type of the background
 *
 * @constructor Creates a [ChatBackground].
 * */
@Serializable
data class ChatBackground(
    val type: @Contextual BackgroundType,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about a new forum topic created in the chat.</p>
 *
 * @property name Name of the topic
 * @property icon_color Color of the topic icon in RGB format
 * @property icon_custom_emoji_id <em>Optional</em>. Unique identifier of the custom emoji shown as the topic icon
 *
 * @constructor Creates a [ForumTopicCreated].
 * */
@Serializable
data class ForumTopicCreated(
    val name: String,
    val icon_color: Long,
    val icon_custom_emoji_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about an edited forum topic.</p>
 *
 * @property name <em>Optional</em>. New name of the topic, if it was edited
 * @property icon_custom_emoji_id <em>Optional</em>. New identifier of the custom emoji shown as the topic icon, if it was edited; an empty string if the icon was removed
 *
 * @constructor Creates a [ForumTopicEdited].
 * */
@Serializable
data class ForumTopicEdited(
    val name: String? = null,
    val icon_custom_emoji_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about a user that was shared with the bot using a <a href="#keyboardbuttonrequestusers">KeyboardButtonRequestUsers</a> button.</p>
 *
 * @property user_id Identifier of the shared user. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so 64-bit integers or double-precision float types are safe for storing these identifiers. The bot may not have access to the user and could be unable to use this identifier, unless the user is already known to the bot by some other means.
 * @property first_name <em>Optional</em>. First name of the user, if the name was requested by the bot
 * @property last_name <em>Optional</em>. Last name of the user, if the name was requested by the bot
 * @property username <em>Optional</em>. Username of the user, if the username was requested by the bot
 * @property photo <em>Optional</em>. Available sizes of the chat photo, if the photo was requested by the bot
 *
 * @constructor Creates a [SharedUser].
 * */
@Serializable
data class SharedUser(
    val user_id: UserId,
    val first_name: String? = null,
    val last_name: String? = null,
    val username: String? = null,
    val photo: List<PhotoSize>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about the users whose identifiers were shared with the bot using a <a href="#keyboardbuttonrequestusers">KeyboardButtonRequestUsers</a> button.</p>
 *
 * @property request_id Identifier of the request
 * @property users Information about users shared with the bot.
 *
 * @constructor Creates a [UsersShared].
 * */
@Serializable
data class UsersShared(
    val request_id: Long,
    val users: List<SharedUser>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about a chat that was shared with the bot using a <a href="#keyboardbuttonrequestchat">KeyboardButtonRequestChat</a> button.</p>
 *
 * @property request_id Identifier of the request
 * @property chat_id Identifier of the shared chat. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a 64-bit integer or double-precision float type are safe for storing this identifier. The bot may not have access to the chat and could be unable to use this identifier, unless the chat is already known to the bot by some other means.
 * @property title <em>Optional</em>. Title of the chat, if the title was requested by the bot.
 * @property username <em>Optional</em>. Username of the chat, if the username was requested by the bot and available.
 * @property photo <em>Optional</em>. Available sizes of the chat photo, if the photo was requested by the bot
 *
 * @constructor Creates a [ChatShared].
 * */
@Serializable
data class ChatShared(
    val request_id: Long,
    val chat_id: ChatId,
    val title: String? = null,
    val username: String? = null,
    val photo: List<PhotoSize>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about a user allowing a bot to write messages after adding it to the attachment menu, launching a Web App from a link, or accepting an explicit request from a Web App sent by the method <a href="/bots/webapps#initializing-mini-apps">requestWriteAccess</a>.</p>
 *
 * @property from_request <em>Optional</em>. True, if the access was granted after the user accepted an explicit request from a Web App sent by the method <a href="/bots/webapps#initializing-mini-apps">requestWriteAccess</a>
 * @property web_app_name <em>Optional</em>. Name of the Web App, if the access was granted when the Web App was launched from a link
 * @property from_attachment_menu <em>Optional</em>. True, if the access was granted when the bot was added to the attachment or side menu
 *
 * @constructor Creates a [WriteAccessAllowed].
 * */
@Serializable
data class WriteAccessAllowed(
    val from_request: Boolean? = null,
    val web_app_name: String? = null,
    val from_attachment_menu: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about a video chat scheduled in the chat.</p>
 *
 * @property start_date Point in time (Unix timestamp) when the video chat is supposed to be started by a chat administrator
 *
 * @constructor Creates a [VideoChatScheduled].
 * */
@Serializable
data class VideoChatScheduled(
    val start_date: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about a video chat ended in the chat.</p>
 *
 * @property duration Video chat duration in seconds
 *
 * @constructor Creates a [VideoChatEnded].
 * */
@Serializable
data class VideoChatEnded(
    val duration: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about new members invited to a video chat.</p>
 *
 * @property users New members that were invited to the video chat
 *
 * @constructor Creates a [VideoChatParticipantsInvited].
 * */
@Serializable
data class VideoChatParticipantsInvited(
    val users: List<User>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a message about a scheduled giveaway.</p>
 *
 * @property chats The list of chats which the user must join to participate in the giveaway
 * @property winners_selection_date Point in time (Unix timestamp) when winners of the giveaway will be selected
 * @property winner_count The number of users which are supposed to be selected as winners of the giveaway
 * @property only_new_members <em>Optional</em>. <em>True</em>, if only users who join the chats after the giveaway started should be eligible to win
 * @property has_public_winners <em>Optional</em>. <em>True</em>, if the list of giveaway winners will be visible to everyone
 * @property prize_description <em>Optional</em>. Description of additional giveaway prize
 * @property country_codes <em>Optional</em>. A list of two-letter <a href="https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> country codes indicating the countries from which eligible users for the giveaway must come. If empty, then all users can participate in the giveaway. Users with a phone number that was bought on Fragment can always participate in giveaways.
 * @property premium_subscription_month_count <em>Optional</em>. The number of months the Telegram Premium subscription won from the giveaway will be active for
 *
 * @constructor Creates a [Giveaway].
 * */
@Serializable
data class Giveaway(
    val chats: List<Chat>,
    val winners_selection_date: Long,
    val winner_count: Long,
    val only_new_members: Boolean? = null,
    val has_public_winners: Boolean? = null,
    val prize_description: String? = null,
    val country_codes: List<String>? = null,
    val premium_subscription_month_count: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a message about the completion of a giveaway with public winners.</p>
 *
 * @property chat The chat that created the giveaway
 * @property giveaway_message_id Identifier of the message with the giveaway in the chat
 * @property winners_selection_date Point in time (Unix timestamp) when winners of the giveaway were selected
 * @property winner_count Total number of winners in the giveaway
 * @property winners List of up to 100 winners of the giveaway
 * @property additional_chat_count <em>Optional</em>. The number of other chats the user had to join in order to be eligible for the giveaway
 * @property premium_subscription_month_count <em>Optional</em>. The number of months the Telegram Premium subscription won from the giveaway will be active for
 * @property unclaimed_prize_count <em>Optional</em>. Number of undistributed prizes
 * @property only_new_members <em>Optional</em>. <em>True</em>, if only users who had joined the chats after the giveaway started were eligible to win
 * @property was_refunded <em>Optional</em>. <em>True</em>, if the giveaway was canceled because the payment for it was refunded
 * @property prize_description <em>Optional</em>. Description of additional giveaway prize
 *
 * @constructor Creates a [GiveawayWinners].
 * */
@Serializable
data class GiveawayWinners(
    val chat: Chat,
    val giveaway_message_id: Long,
    val winners_selection_date: Long,
    val winner_count: Long,
    val winners: List<User>,
    val additional_chat_count: Long? = null,
    val premium_subscription_month_count: Long? = null,
    val unclaimed_prize_count: Long? = null,
    val only_new_members: Boolean? = null,
    val was_refunded: Boolean? = null,
    val prize_description: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a service message about the completion of a giveaway without public winners.</p>
 *
 * @property winner_count Number of winners in the giveaway
 * @property unclaimed_prize_count <em>Optional</em>. Number of undistributed prizes
 * @property giveaway_message <em>Optional</em>. Message with the giveaway that was completed, if it wasn't deleted
 *
 * @constructor Creates a [GiveawayCompleted].
 * */
@Serializable
data class GiveawayCompleted(
    val winner_count: Long,
    val unclaimed_prize_count: Long? = null,
    val giveaway_message: Message? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes the options used for link preview generation.</p>
 *
 * @property is_disabled <em>Optional</em>. <em>True</em>, if the link preview is disabled
 * @property url <em>Optional</em>. URL to use for the link preview. If empty, then the first URL found in the message text will be used
 * @property prefer_small_media <em>Optional</em>. <em>True</em>, if the media in the link preview is supposed to be shrunk; ignored if the URL isn't explicitly specified or media size change isn't supported for the preview
 * @property prefer_large_media <em>Optional</em>. <em>True</em>, if the media in the link preview is supposed to be enlarged; ignored if the URL isn't explicitly specified or media size change isn't supported for the preview
 * @property show_above_text <em>Optional</em>. <em>True</em>, if the link preview must be shown above the message text; otherwise, the link preview will be shown below the message text
 *
 * @constructor Creates a [LinkPreviewOptions].
 * */
@Serializable
data class LinkPreviewOptions(
    val is_disabled: Boolean? = null,
    val url: String? = null,
    val prefer_small_media: Boolean? = null,
    val prefer_large_media: Boolean? = null,
    val show_above_text: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represent a user's profile pictures.</p>
 *
 * @property total_count Total number of profile pictures the target user has
 * @property photos Requested profile pictures (in up to 4 sizes each)
 *
 * @constructor Creates a [UserProfilePhotos].
 * */
@Serializable
data class UserProfilePhotos(
    val total_count: Long,
    val photos: List<List<PhotoSize>>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a file ready to be downloaded. The file can be downloaded via the link <code>https://api.telegram.org/file/bot&lt;token&gt;/&lt;file_path&gt;</code>. It is guaranteed that the link will be valid for at least 1 hour. When the link expires, a new one can be requested by calling <a href="#getfile">getFile</a>.</p><blockquote>
 *  <p>The maximum file size to download is 20 MB</p>
 * </blockquote>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property file_size <em>Optional</em>. File size in bytes. It can be bigger than 2^31 and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this value.
 * @property file_path <em>Optional</em>. File path. Use <code>https://api.telegram.org/file/bot&lt;token&gt;/&lt;file_path&gt;</code> to get the file.
 *
 * @constructor Creates a [File].
 * */
@Serializable
data class File(
    val file_id: String,
    val file_unique_id: String,
    val file_size: Long? = null,
    val file_path: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes a <a href="/bots/webapps">Web App</a>.</p>
 *
 * @property url An HTTPS URL of a Web App to be opened with additional data as specified in <a href="/bots/webapps#initializing-mini-apps">Initializing Web Apps</a>
 *
 * @constructor Creates a [WebAppInfo].
 * */
@Serializable
data class WebAppInfo(
    val url: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a <a href="/bots/features#keyboards">custom keyboard</a> with reply options (see <a href="/bots/features#keyboards">Introduction to bots</a> for details and examples). Not supported in channels and for messages sent on behalf of a Telegram Business account.</p>
 *
 * @property keyboard Array of button rows, each represented by an Array of <a href="#keyboardbutton">KeyboardButton</a> objects
 * @property is_persistent <em>Optional</em>. Requests clients to always show the keyboard when the regular keyboard is hidden. Defaults to <em>false</em>, in which case the custom keyboard can be hidden and opened with a keyboard icon.
 * @property resize_keyboard <em>Optional</em>. Requests clients to resize the keyboard vertically for optimal fit (e.g., make the keyboard smaller if there are just two rows of buttons). Defaults to <em>false</em>, in which case the custom keyboard is always of the same height as the app's standard keyboard.
 * @property one_time_keyboard <em>Optional</em>. Requests clients to hide the keyboard as soon as it's been used. The keyboard will still be available, but clients will automatically display the usual letter-keyboard in the chat - the user can press a special button in the input field to see the custom keyboard again. Defaults to <em>false</em>.
 * @property input_field_placeholder <em>Optional</em>. The placeholder to be shown in the input field when the keyboard is active; 1-64 characters
 * @property selective <em>Optional</em>. Use this parameter if you want to show the keyboard to specific users only. Targets: 1) users that are @mentioned in the <em>text</em> of the <a href="#message">Message</a> object; 2) if the bot's message is a reply to a message in the same chat and forum topic, sender of the original message.<br><br><em>Example:</em> A user requests to change the bot's language, bot replies to the request with a keyboard to select the new language. Other users in the group don't see the keyboard.
 *
 * @constructor Creates a [ReplyKeyboardMarkup].
 * */
@Serializable
data class ReplyKeyboardMarkup(
    val keyboard: List<List<KeyboardButton>>,
    val is_persistent: Boolean? = null,
    val resize_keyboard: Boolean? = null,
    val one_time_keyboard: Boolean? = null,
    val input_field_placeholder: String? = null,
    val selective: Boolean? = null,
) : KeyboardOption() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents one button of the reply keyboard. At most one of the optional fields must be used to specify type of the button. For simple text buttons, <em>String</em> can be used instead of this object to specify the button text.</p><p><strong>Note:</strong> <em>request_users</em> and <em>request_chat</em> options will only work in Telegram versions released after 3 February, 2023. Older clients will display <em>unsupported message</em>.</p>
 *
 * @property text Text of the button. If none of the optional fields are used, it will be sent as a message when the button is pressed
 * @property request_users <em>Optional.</em> If specified, pressing the button will open a list of suitable users. Identifiers of selected users will be sent to the bot in a “users_shared” service message. Available in private chats only.
 * @property request_chat <em>Optional.</em> If specified, pressing the button will open a list of suitable chats. Tapping on a chat will send its identifier to the bot in a “chat_shared” service message. Available in private chats only.
 * @property request_contact <em>Optional</em>. If <em>True</em>, the user's phone number will be sent as a contact when the button is pressed. Available in private chats only.
 * @property request_location <em>Optional</em>. If <em>True</em>, the user's current location will be sent when the button is pressed. Available in private chats only.
 * @property request_poll <em>Optional</em>. If specified, the user will be asked to create a poll and send it to the bot when the button is pressed. Available in private chats only.
 * @property web_app <em>Optional</em>. If specified, the described <a href="/bots/webapps">Web App</a> will be launched when the button is pressed. The Web App will be able to send a “web_app_data” service message. Available in private chats only.
 *
 * @constructor Creates a [KeyboardButton].
 * */
@Serializable
data class KeyboardButton(
    val text: String,
    val request_users: KeyboardButtonRequestUsers? = null,
    val request_chat: KeyboardButtonRequestChat? = null,
    val request_contact: Boolean? = null,
    val request_location: Boolean? = null,
    val request_poll: KeyboardButtonPollType? = null,
    val web_app: WebAppInfo? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object defines the criteria used to request suitable users. Information about the selected users will be shared with the bot when the corresponding button is pressed. <a href="/bots/features#chat-and-user-selection">More about requesting users »</a></p>
 *
 * @property request_id Signed 32-bit identifier of the request that will be received back in the <a href="#usersshared">UsersShared</a> object. Must be unique within the message
 * @property user_is_bot <em>Optional</em>. Pass <em>True</em> to request bots, pass <em>False</em> to request regular users. If not specified, no additional restrictions are applied.
 * @property user_is_premium <em>Optional</em>. Pass <em>True</em> to request premium users, pass <em>False</em> to request non-premium users. If not specified, no additional restrictions are applied.
 * @property max_quantity <em>Optional</em>. The maximum number of users to be selected; 1-10. Defaults to 1.
 * @property request_name <em>Optional</em>. Pass <em>True</em> to request the users' first and last names
 * @property request_username <em>Optional</em>. Pass <em>True</em> to request the users' usernames
 * @property request_photo <em>Optional</em>. Pass <em>True</em> to request the users' photos
 *
 * @constructor Creates a [KeyboardButtonRequestUsers].
 * */
@Serializable
data class KeyboardButtonRequestUsers(
    val request_id: Long,
    val user_is_bot: Boolean? = null,
    val user_is_premium: Boolean? = null,
    val max_quantity: Long? = null,
    val request_name: Boolean? = null,
    val request_username: Boolean? = null,
    val request_photo: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object defines the criteria used to request a suitable chat. Information about the selected chat will be shared with the bot when the corresponding button is pressed. The bot will be granted requested rights in the chat if appropriate. <a href="/bots/features#chat-and-user-selection">More about requesting chats »</a>.</p>
 *
 * @property request_id Signed 32-bit identifier of the request, which will be received back in the <a href="#chatshared">ChatShared</a> object. Must be unique within the message
 * @property chat_is_channel Pass <em>True</em> to request a channel chat, pass <em>False</em> to request a group or a supergroup chat.
 * @property chat_is_forum <em>Optional</em>. Pass <em>True</em> to request a forum supergroup, pass <em>False</em> to request a non-forum chat. If not specified, no additional restrictions are applied.
 * @property chat_has_username <em>Optional</em>. Pass <em>True</em> to request a supergroup or a channel with a username, pass <em>False</em> to request a chat without a username. If not specified, no additional restrictions are applied.
 * @property chat_is_created <em>Optional</em>. Pass <em>True</em> to request a chat owned by the user. Otherwise, no additional restrictions are applied.
 * @property user_administrator_rights <em>Optional</em>. A JSON-serialized object listing the required administrator rights of the user in the chat. The rights must be a superset of <em>bot_administrator_rights</em>. If not specified, no additional restrictions are applied.
 * @property bot_administrator_rights <em>Optional</em>. A JSON-serialized object listing the required administrator rights of the bot in the chat. The rights must be a subset of <em>user_administrator_rights</em>. If not specified, no additional restrictions are applied.
 * @property bot_is_member <em>Optional</em>. Pass <em>True</em> to request a chat with the bot as a member. Otherwise, no additional restrictions are applied.
 * @property request_title <em>Optional</em>. Pass <em>True</em> to request the chat's title
 * @property request_username <em>Optional</em>. Pass <em>True</em> to request the chat's username
 * @property request_photo <em>Optional</em>. Pass <em>True</em> to request the chat's photo
 *
 * @constructor Creates a [KeyboardButtonRequestChat].
 * */
@Serializable
data class KeyboardButtonRequestChat(
    val request_id: Long,
    val chat_is_channel: Boolean,
    val chat_is_forum: Boolean? = null,
    val chat_has_username: Boolean? = null,
    val chat_is_created: Boolean? = null,
    val user_administrator_rights: ChatAdministratorRights? = null,
    val bot_administrator_rights: ChatAdministratorRights? = null,
    val bot_is_member: Boolean? = null,
    val request_title: Boolean? = null,
    val request_username: Boolean? = null,
    val request_photo: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents type of a poll, which is allowed to be created and sent when the corresponding button is pressed.</p>
 *
 * @property type <em>Optional</em>. If <em>quiz</em> is passed, the user will be allowed to create only polls in the quiz mode. If <em>regular</em> is passed, only regular polls will be allowed. Otherwise, the user will be allowed to create a poll of any type.
 *
 * @constructor Creates a [KeyboardButtonPollType].
 * */
@Serializable
data class KeyboardButtonPollType(
    val type: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Upon receiving a message with this object, Telegram clients will remove the current custom keyboard and display the default letter-keyboard. By default, custom keyboards are displayed until a new keyboard is sent by a bot. An exception is made for one-time keyboards that are hidden immediately after the user presses a button (see <a href="#replykeyboardmarkup">ReplyKeyboardMarkup</a>). Not supported in channels and for messages sent on behalf of a Telegram Business account.</p>
 *
 * @property remove_keyboard Requests clients to remove the custom keyboard (user will not be able to summon this keyboard; if you want to hide the keyboard from sight but keep it accessible, use <em>one_time_keyboard</em> in <a href="#replykeyboardmarkup">ReplyKeyboardMarkup</a>)
 * @property selective <em>Optional</em>. Use this parameter if you want to remove the keyboard for specific users only. Targets: 1) users that are @mentioned in the <em>text</em> of the <a href="#message">Message</a> object; 2) if the bot's message is a reply to a message in the same chat and forum topic, sender of the original message.<br><br><em>Example:</em> A user votes in a poll, bot returns confirmation message in reply to the vote and removes the keyboard for that user, while still showing the keyboard with poll options to users who haven't voted yet.
 *
 * @constructor Creates a [ReplyKeyboardRemove].
 * */
@Serializable
data class ReplyKeyboardRemove(
    val remove_keyboard: Boolean,
    val selective: Boolean? = null,
) : KeyboardOption() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an <a href="/bots/features#inline-keyboards">inline keyboard</a> that appears right next to the message it belongs to.</p>
 *
 * @property inline_keyboard Array of button rows, each represented by an Array of <a href="#inlinekeyboardbutton">InlineKeyboardButton</a> objects
 *
 * @constructor Creates a [InlineKeyboardMarkup].
 * */
@Serializable
data class InlineKeyboardMarkup(
    val inline_keyboard: List<List<InlineKeyboardButton>>,
) : KeyboardOption() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents one button of an inline keyboard. Exactly one of the optional fields must be used to specify type of the button.</p>
 *
 * @property text Label text on the button
 * @property url <em>Optional</em>. HTTP or tg:// URL to be opened when the button is pressed. Links <code>tg://user?id=&lt;user_id&gt;</code> can be used to mention a user by their identifier without using a username, if this is allowed by their privacy settings.
 * @property callback_data <em>Optional</em>. Data to be sent in a <a href="#callbackquery">callback query</a> to the bot when the button is pressed, 1-64 bytes
 * @property web_app <em>Optional</em>. Description of the <a href="/bots/webapps">Web App</a> that will be launched when the user presses the button. The Web App will be able to send an arbitrary message on behalf of the user using the method <a href="#answerwebappquery">answerWebAppQuery</a>. Available only in private chats between a user and the bot. Not supported for messages sent on behalf of a Telegram Business account.
 * @property login_url <em>Optional</em>. An HTTPS URL used to automatically authorize the user. Can be used as a replacement for the <a href="/widgets/login">Telegram Login Widget</a>.
 * @property switch_inline_query <em>Optional</em>. If set, pressing the button will prompt the user to select one of their chats, open that chat and insert the bot's username and the specified inline query in the input field. May be empty, in which case just the bot's username will be inserted. Not supported for messages sent on behalf of a Telegram Business account.
 * @property switch_inline_query_current_chat <em>Optional</em>. If set, pressing the button will insert the bot's username and the specified inline query in the current chat's input field. May be empty, in which case only the bot's username will be inserted.<br><br>This offers a quick way for the user to open your bot in inline mode in the same chat - good for selecting something from multiple options. Not supported in channels and for messages sent on behalf of a Telegram Business account.
 * @property switch_inline_query_chosen_chat <em>Optional</em>. If set, pressing the button will prompt the user to select one of their chats of the specified type, open that chat and insert the bot's username and the specified inline query in the input field. Not supported for messages sent on behalf of a Telegram Business account.
 * @property callback_game <em>Optional</em>. Description of the game that will be launched when the user presses the button.<br><br><strong>NOTE:</strong> This type of button <strong>must</strong> always be the first button in the first row.
 * @property pay <em>Optional</em>. Specify <em>True</em>, to send a <a href="#payments">Pay button</a>. Substrings “<img class="emoji" src="//telegram.org/img/emoji/40/E2AD90.png" width="20" height="20" alt="⭐">” and “XTR” in the buttons's text will be replaced with a Telegram Star icon.<br><br><strong>NOTE:</strong> This type of button <strong>must</strong> always be the first button in the first row and can only be used in invoice messages.
 *
 * @constructor Creates a [InlineKeyboardButton].
 * */
@Serializable
data class InlineKeyboardButton(
    val text: String,
    val url: String? = null,
    val callback_data: String? = null,
    val web_app: WebAppInfo? = null,
    val login_url: LoginUrl? = null,
    val switch_inline_query: String? = null,
    val switch_inline_query_current_chat: String? = null,
    val switch_inline_query_chosen_chat: SwitchInlineQueryChosenChat? = null,
    val callback_game: @Contextual Any? = null,
    val pay: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a parameter of the inline keyboard button used to automatically authorize a user. Serves as a great replacement for the <a href="/widgets/login">Telegram Login Widget</a> when the user is coming from Telegram. All the user needs to do is tap/click a button and confirm that they want to log in:</p><p>Telegram apps support these buttons as of <a href="https://telegram.org/blog/privacy-discussions-web-bots#meet-seamless-web-bots">version 5.7</a>.</p><blockquote>
 *  <p>Sample bot: <a href="https://t.me/discussbot">@discussbot</a></p>
 * </blockquote>
 *
 * @property url An HTTPS URL to be opened with user authorization data added to the query string when the button is pressed. If the user refuses to provide authorization data, the original URL without information about the user will be opened. The data added is the same as described in <a href="/widgets/login#receiving-authorization-data">Receiving authorization data</a>.<br><br><strong>NOTE:</strong> You <strong>must</strong> always check the hash of the received data to verify the authentication and the integrity of the data as described in <a href="/widgets/login#checking-authorization">Checking authorization</a>.
 * @property forward_text <em>Optional</em>. New text of the button in forwarded messages.
 * @property bot_username <em>Optional</em>. Username of a bot, which will be used for user authorization. See <a href="/widgets/login#setting-up-a-bot">Setting up a bot</a> for more details. If not specified, the current bot's username will be assumed. The <em>url</em>'s domain must be the same as the domain linked with the bot. See <a href="/widgets/login#linking-your-domain-to-the-bot">Linking your domain to the bot</a> for more details.
 * @property request_write_access <em>Optional</em>. Pass <em>True</em> to request the permission for your bot to send messages to the user.
 *
 * @constructor Creates a [LoginUrl].
 * */
@Serializable
data class LoginUrl(
    val url: String,
    val forward_text: String? = null,
    val bot_username: String? = null,
    val request_write_access: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an inline button that switches the current user to inline mode in a chosen chat, with an optional default inline query.</p>
 *
 * @property query <em>Optional</em>. The default inline query to be inserted in the input field. If left empty, only the bot's username will be inserted
 * @property allow_user_chats <em>Optional</em>. True, if private chats with users can be chosen
 * @property allow_bot_chats <em>Optional</em>. True, if private chats with bots can be chosen
 * @property allow_group_chats <em>Optional</em>. True, if group and supergroup chats can be chosen
 * @property allow_channel_chats <em>Optional</em>. True, if channel chats can be chosen
 *
 * @constructor Creates a [SwitchInlineQueryChosenChat].
 * */
@Serializable
data class SwitchInlineQueryChosenChat(
    val query: String? = null,
    val allow_user_chats: Boolean? = null,
    val allow_bot_chats: Boolean? = null,
    val allow_group_chats: Boolean? = null,
    val allow_channel_chats: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents an incoming callback query from a callback button in an <a href="/bots/features#inline-keyboards">inline keyboard</a>. If the button that originated the query was attached to a message sent by the bot, the field <em>message</em> will be present. If the button was attached to a message sent via the bot (in <a href="#inline-mode">inline mode</a>), the field <em>inline_message_id</em> will be present. Exactly one of the fields <em>data</em> or <em>game_short_name</em> will be present.</p><blockquote>
 *  <p><strong>NOTE:</strong> After the user presses a callback button, Telegram clients will display a progress bar until you call <a href="#answercallbackquery">answerCallbackQuery</a>. It is, therefore, necessary to react by calling <a href="#answercallbackquery">answerCallbackQuery</a> even if no notification to the user is needed (e.g., without specifying any of the optional parameters).</p>
 * </blockquote>
 *
 * @property id Unique identifier for this query
 * @property from Sender
 * @property chat_instance Global identifier, uniquely corresponding to the chat to which the message with the callback button was sent. Useful for high scores in <a href="#games">games</a>.
 * @property message <em>Optional</em>. Message sent by the bot with the callback button that originated the query
 * @property inline_message_id <em>Optional</em>. Identifier of the message sent via the bot in inline mode, that originated the query.
 * @property data <em>Optional</em>. Data associated with the callback button. Be aware that the message originated the query can contain no callback buttons with this data.
 * @property game_short_name <em>Optional</em>. Short name of a <a href="#games">Game</a> to be returned, serves as the unique identifier for the game
 *
 * @constructor Creates a [CallbackQuery].
 * */
@Serializable
data class CallbackQuery(
    val id: String,
    val from: User,
    val chat_instance: String,
    val message: @Contextual MaybeInaccessibleMessage? = null,
    val inline_message_id: String? = null,
    val data: String? = null,
    val game_short_name: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Upon receiving a message with this object, Telegram clients will display a reply interface to the user (act as if the user has selected the bot's message and tapped 'Reply'). This can be extremely useful if you want to create user-friendly step-by-step interfaces without having to sacrifice <a href="/bots/features#privacy-mode">privacy mode</a>. Not supported in channels and for messages sent on behalf of a Telegram Business account.</p><blockquote>
 *  <p><strong>Example:</strong> A <a href="https://t.me/PollBot">poll bot</a> for groups runs in privacy mode (only receives commands, replies to its messages and mentions). There could be two ways to create a new poll:</p>
 *  <ul>
 *   <li>Explain the user how to send a command with parameters (e.g. /newpoll question answer1 answer2). May be appealing for hardcore users but lacks modern day polish.</li>
 *   <li>Guide the user through a step-by-step process. 'Please send me your question', 'Cool, now let's add the first answer option', 'Great. Keep adding answer options, then send /done when you're ready'.</li>
 *  </ul>
 *  <p>The last option is definitely more attractive. And if you use <a href="#forcereply">ForceReply</a> in your bot's questions, it will receive the user's answers even if it only receives replies, commands and mentions - without any extra work for the user.</p>
 * </blockquote>
 *
 * @property force_reply Shows reply interface to the user, as if they manually selected the bot's message and tapped 'Reply'
 * @property input_field_placeholder <em>Optional</em>. The placeholder to be shown in the input field when the reply is active; 1-64 characters
 * @property selective <em>Optional</em>. Use this parameter if you want to force reply from specific users only. Targets: 1) users that are @mentioned in the <em>text</em> of the <a href="#message">Message</a> object; 2) if the bot's message is a reply to a message in the same chat and forum topic, sender of the original message.
 *
 * @constructor Creates a [ForceReply].
 * */
@Serializable
data class ForceReply(
    val force_reply: Boolean,
    val input_field_placeholder: String? = null,
    val selective: Boolean? = null,
) : KeyboardOption() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a chat photo.</p>
 *
 * @property small_file_id File identifier of small (160x160) chat photo. This file_id can be used only for photo download and only for as long as the photo is not changed.
 * @property small_file_unique_id Unique file identifier of small (160x160) chat photo, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property big_file_id File identifier of big (640x640) chat photo. This file_id can be used only for photo download and only for as long as the photo is not changed.
 * @property big_file_unique_id Unique file identifier of big (640x640) chat photo, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 *
 * @constructor Creates a [ChatPhoto].
 * */
@Serializable
data class ChatPhoto(
    val small_file_id: String,
    val small_file_unique_id: String,
    val big_file_id: String,
    val big_file_unique_id: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an invite link for a chat.</p>
 *
 * @property invite_link The invite link. If the link was created by another chat administrator, then the second part of the link will be replaced with “…”.
 * @property creator Creator of the link
 * @property creates_join_request <em>True</em>, if users joining the chat via the link need to be approved by chat administrators
 * @property is_primary <em>True</em>, if the link is primary
 * @property is_revoked <em>True</em>, if the link is revoked
 * @property name <em>Optional</em>. Invite link name
 * @property expire_date <em>Optional</em>. Point in time (Unix timestamp) when the link will expire or has been expired
 * @property member_limit <em>Optional</em>. The maximum number of users that can be members of the chat simultaneously after joining the chat via this invite link; 1-99999
 * @property pending_join_request_count <em>Optional</em>. Number of pending join requests created using this link
 *
 * @constructor Creates a [ChatInviteLink].
 * */
@Serializable
data class ChatInviteLink(
    val invite_link: String,
    val creator: User,
    val creates_join_request: Boolean,
    val is_primary: Boolean,
    val is_revoked: Boolean,
    val name: String? = null,
    val expire_date: Long? = null,
    val member_limit: Long? = null,
    val pending_join_request_count: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the rights of an administrator in a chat.</p>
 *
 * @property is_anonymous <em>True</em>, if the user's presence in the chat is hidden
 * @property can_manage_chat <em>True</em>, if the administrator can access the chat event log, get boost list, see hidden supergroup and channel members, report spam messages and ignore slow mode. Implied by any other administrator privilege.
 * @property can_delete_messages <em>True</em>, if the administrator can delete messages of other users
 * @property can_manage_video_chats <em>True</em>, if the administrator can manage video chats
 * @property can_restrict_members <em>True</em>, if the administrator can restrict, ban or unban chat members, or access supergroup statistics
 * @property can_promote_members <em>True</em>, if the administrator can add new administrators with a subset of their own privileges or demote administrators that they have promoted, directly or indirectly (promoted by administrators that were appointed by the user)
 * @property can_change_info <em>True</em>, if the user is allowed to change the chat title, photo and other settings
 * @property can_invite_users <em>True</em>, if the user is allowed to invite new users to the chat
 * @property can_post_stories <em>True</em>, if the administrator can post stories to the chat
 * @property can_edit_stories <em>True</em>, if the administrator can edit stories posted by other users, post stories to the chat page, pin chat stories, and access the chat's story archive
 * @property can_delete_stories <em>True</em>, if the administrator can delete stories posted by other users
 * @property can_post_messages <em>Optional</em>. <em>True</em>, if the administrator can post messages in the channel, or access channel statistics; for channels only
 * @property can_edit_messages <em>Optional</em>. <em>True</em>, if the administrator can edit messages of other users and can pin messages; for channels only
 * @property can_pin_messages <em>Optional</em>. <em>True</em>, if the user is allowed to pin messages; for groups and supergroups only
 * @property can_manage_topics <em>Optional</em>. <em>True</em>, if the user is allowed to create, rename, close, and reopen forum topics; for supergroups only
 *
 * @constructor Creates a [ChatAdministratorRights].
 * */
@Serializable
data class ChatAdministratorRights(
    val is_anonymous: Boolean,
    val can_manage_chat: Boolean,
    val can_delete_messages: Boolean,
    val can_manage_video_chats: Boolean,
    val can_restrict_members: Boolean,
    val can_promote_members: Boolean,
    val can_change_info: Boolean,
    val can_invite_users: Boolean,
    val can_post_stories: Boolean,
    val can_edit_stories: Boolean,
    val can_delete_stories: Boolean,
    val can_post_messages: Boolean? = null,
    val can_edit_messages: Boolean? = null,
    val can_pin_messages: Boolean? = null,
    val can_manage_topics: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents changes in the status of a chat member.</p>
 *
 * @property chat Chat the user belongs to
 * @property from Performer of the action, which resulted in the change
 * @property date Date the change was done in Unix time
 * @property old_chat_member Previous information about the chat member
 * @property new_chat_member New information about the chat member
 * @property invite_link <em>Optional</em>. Chat invite link, which was used by the user to join the chat; for joining by invite link events only.
 * @property via_join_request <em>Optional</em>. True, if the user joined the chat after sending a direct join request without using an invite link and being approved by an administrator
 * @property via_chat_folder_invite_link <em>Optional</em>. True, if the user joined the chat via a chat folder invite link
 *
 * @constructor Creates a [ChatMemberUpdated].
 * */
@Serializable
data class ChatMemberUpdated(
    val chat: Chat,
    val from: User,
    val date: Long,
    val old_chat_member: @Contextual ChatMember,
    val new_chat_member: @Contextual ChatMember,
    val invite_link: ChatInviteLink? = null,
    val via_join_request: Boolean? = null,
    val via_chat_folder_invite_link: Boolean? = null,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#chatmember">chat member</a> that owns the chat and has all administrator privileges.</p>
 *
 * @property status The member's status in the chat, always “creator”
 * @property user Information about the user
 * @property is_anonymous <em>True</em>, if the user's presence in the chat is hidden
 * @property custom_title <em>Optional</em>. Custom title for this user
 *
 * @constructor Creates a [ChatMemberOwner].
 * */
@Serializable
data class ChatMemberOwner(
    val status: String,
    val user: User,
    val is_anonymous: Boolean,
    val custom_title: String? = null,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#chatmember">chat member</a> that has some additional privileges.</p>
 *
 * @property status The member's status in the chat, always “administrator”
 * @property user Information about the user
 * @property can_be_edited <em>True</em>, if the bot is allowed to edit administrator privileges of that user
 * @property is_anonymous <em>True</em>, if the user's presence in the chat is hidden
 * @property can_manage_chat <em>True</em>, if the administrator can access the chat event log, get boost list, see hidden supergroup and channel members, report spam messages and ignore slow mode. Implied by any other administrator privilege.
 * @property can_delete_messages <em>True</em>, if the administrator can delete messages of other users
 * @property can_manage_video_chats <em>True</em>, if the administrator can manage video chats
 * @property can_restrict_members <em>True</em>, if the administrator can restrict, ban or unban chat members, or access supergroup statistics
 * @property can_promote_members <em>True</em>, if the administrator can add new administrators with a subset of their own privileges or demote administrators that they have promoted, directly or indirectly (promoted by administrators that were appointed by the user)
 * @property can_change_info <em>True</em>, if the user is allowed to change the chat title, photo and other settings
 * @property can_invite_users <em>True</em>, if the user is allowed to invite new users to the chat
 * @property can_post_stories <em>True</em>, if the administrator can post stories to the chat
 * @property can_edit_stories <em>True</em>, if the administrator can edit stories posted by other users, post stories to the chat page, pin chat stories, and access the chat's story archive
 * @property can_delete_stories <em>True</em>, if the administrator can delete stories posted by other users
 * @property can_post_messages <em>Optional</em>. <em>True</em>, if the administrator can post messages in the channel, or access channel statistics; for channels only
 * @property can_edit_messages <em>Optional</em>. <em>True</em>, if the administrator can edit messages of other users and can pin messages; for channels only
 * @property can_pin_messages <em>Optional</em>. <em>True</em>, if the user is allowed to pin messages; for groups and supergroups only
 * @property can_manage_topics <em>Optional</em>. <em>True</em>, if the user is allowed to create, rename, close, and reopen forum topics; for supergroups only
 * @property custom_title <em>Optional</em>. Custom title for this user
 *
 * @constructor Creates a [ChatMemberAdministrator].
 * */
@Serializable
data class ChatMemberAdministrator(
    val status: String,
    val user: User,
    val can_be_edited: Boolean,
    val is_anonymous: Boolean,
    val can_manage_chat: Boolean,
    val can_delete_messages: Boolean,
    val can_manage_video_chats: Boolean,
    val can_restrict_members: Boolean,
    val can_promote_members: Boolean,
    val can_change_info: Boolean,
    val can_invite_users: Boolean,
    val can_post_stories: Boolean,
    val can_edit_stories: Boolean,
    val can_delete_stories: Boolean,
    val can_post_messages: Boolean? = null,
    val can_edit_messages: Boolean? = null,
    val can_pin_messages: Boolean? = null,
    val can_manage_topics: Boolean? = null,
    val custom_title: String? = null,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#chatmember">chat member</a> that has no additional privileges or restrictions.</p>
 *
 * @property status The member's status in the chat, always “member”
 * @property user Information about the user
 *
 * @constructor Creates a [ChatMemberMember].
 * */
@Serializable
data class ChatMemberMember(
    val status: String,
    val user: User,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#chatmember">chat member</a> that is under certain restrictions in the chat. Supergroups only.</p>
 *
 * @property status The member's status in the chat, always “restricted”
 * @property user Information about the user
 * @property is_member <em>True</em>, if the user is a member of the chat at the moment of the request
 * @property can_send_messages <em>True</em>, if the user is allowed to send text messages, contacts, giveaways, giveaway winners, invoices, locations and venues
 * @property can_send_audios <em>True</em>, if the user is allowed to send audios
 * @property can_send_documents <em>True</em>, if the user is allowed to send documents
 * @property can_send_photos <em>True</em>, if the user is allowed to send photos
 * @property can_send_videos <em>True</em>, if the user is allowed to send videos
 * @property can_send_video_notes <em>True</em>, if the user is allowed to send video notes
 * @property can_send_voice_notes <em>True</em>, if the user is allowed to send voice notes
 * @property can_send_polls <em>True</em>, if the user is allowed to send polls
 * @property can_send_other_messages <em>True</em>, if the user is allowed to send animations, games, stickers and use inline bots
 * @property can_add_web_page_previews <em>True</em>, if the user is allowed to add web page previews to their messages
 * @property can_change_info <em>True</em>, if the user is allowed to change the chat title, photo and other settings
 * @property can_invite_users <em>True</em>, if the user is allowed to invite new users to the chat
 * @property can_pin_messages <em>True</em>, if the user is allowed to pin messages
 * @property can_manage_topics <em>True</em>, if the user is allowed to create forum topics
 * @property until_date Date when restrictions will be lifted for this user; Unix time. If 0, then the user is restricted forever
 *
 * @constructor Creates a [ChatMemberRestricted].
 * */
@Serializable
data class ChatMemberRestricted(
    val status: String,
    val user: User,
    val is_member: Boolean,
    val can_send_messages: Boolean,
    val can_send_audios: Boolean,
    val can_send_documents: Boolean,
    val can_send_photos: Boolean,
    val can_send_videos: Boolean,
    val can_send_video_notes: Boolean,
    val can_send_voice_notes: Boolean,
    val can_send_polls: Boolean,
    val can_send_other_messages: Boolean,
    val can_add_web_page_previews: Boolean,
    val can_change_info: Boolean,
    val can_invite_users: Boolean,
    val can_pin_messages: Boolean,
    val can_manage_topics: Boolean,
    val until_date: Long,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#chatmember">chat member</a> that isn't currently a member of the chat, but may join it themselves.</p>
 *
 * @property status The member's status in the chat, always “left”
 * @property user Information about the user
 *
 * @constructor Creates a [ChatMemberLeft].
 * */
@Serializable
data class ChatMemberLeft(
    val status: String,
    val user: User,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#chatmember">chat member</a> that was banned in the chat and can't return to the chat or view chat messages.</p>
 *
 * @property status The member's status in the chat, always “kicked”
 * @property user Information about the user
 * @property until_date Date when restrictions will be lifted for this user; Unix time. If 0, then the user is banned forever
 *
 * @constructor Creates a [ChatMemberBanned].
 * */
@Serializable
data class ChatMemberBanned(
    val status: String,
    val user: User,
    val until_date: Long,
) : ChatMember() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a join request sent to a chat.</p>
 *
 * @property chat Chat to which the request was sent
 * @property from User that sent the join request
 * @property user_chat_id Identifier of a private chat with the user who sent the join request. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a 64-bit integer or double-precision float type are safe for storing this identifier. The bot can use this identifier for 5 minutes to send messages until the join request is processed, assuming no other administrator contacted the user.
 * @property date Date the request was sent in Unix time
 * @property bio <em>Optional</em>. Bio of the user.
 * @property invite_link <em>Optional</em>. Chat invite link that was used by the user to send the join request
 *
 * @constructor Creates a [ChatJoinRequest].
 * */
@Serializable
data class ChatJoinRequest(
    val chat: Chat,
    val from: User,
    val user_chat_id: ChatId,
    val date: Long,
    val bio: String? = null,
    val invite_link: ChatInviteLink? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes actions that a non-administrator user is allowed to take in a chat.</p>
 *
 * @property can_send_messages <em>Optional</em>. <em>True</em>, if the user is allowed to send text messages, contacts, giveaways, giveaway winners, invoices, locations and venues
 * @property can_send_audios <em>Optional</em>. <em>True</em>, if the user is allowed to send audios
 * @property can_send_documents <em>Optional</em>. <em>True</em>, if the user is allowed to send documents
 * @property can_send_photos <em>Optional</em>. <em>True</em>, if the user is allowed to send photos
 * @property can_send_videos <em>Optional</em>. <em>True</em>, if the user is allowed to send videos
 * @property can_send_video_notes <em>Optional</em>. <em>True</em>, if the user is allowed to send video notes
 * @property can_send_voice_notes <em>Optional</em>. <em>True</em>, if the user is allowed to send voice notes
 * @property can_send_polls <em>Optional</em>. <em>True</em>, if the user is allowed to send polls
 * @property can_send_other_messages <em>Optional</em>. <em>True</em>, if the user is allowed to send animations, games, stickers and use inline bots
 * @property can_add_web_page_previews <em>Optional</em>. <em>True</em>, if the user is allowed to add web page previews to their messages
 * @property can_change_info <em>Optional</em>. <em>True</em>, if the user is allowed to change the chat title, photo and other settings. Ignored in public supergroups
 * @property can_invite_users <em>Optional</em>. <em>True</em>, if the user is allowed to invite new users to the chat
 * @property can_pin_messages <em>Optional</em>. <em>True</em>, if the user is allowed to pin messages. Ignored in public supergroups
 * @property can_manage_topics <em>Optional</em>. <em>True</em>, if the user is allowed to create forum topics. If omitted defaults to the value of can_pin_messages
 *
 * @constructor Creates a [ChatPermissions].
 * */
@Serializable
data class ChatPermissions(
    val can_send_messages: Boolean? = null,
    val can_send_audios: Boolean? = null,
    val can_send_documents: Boolean? = null,
    val can_send_photos: Boolean? = null,
    val can_send_videos: Boolean? = null,
    val can_send_video_notes: Boolean? = null,
    val can_send_voice_notes: Boolean? = null,
    val can_send_polls: Boolean? = null,
    val can_send_other_messages: Boolean? = null,
    val can_add_web_page_previews: Boolean? = null,
    val can_change_info: Boolean? = null,
    val can_invite_users: Boolean? = null,
    val can_pin_messages: Boolean? = null,
    val can_manage_topics: Boolean? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes the birthdate of a user.</p>
 *
 * @property day Day of the user's birth; 1-31
 * @property month Month of the user's birth; 1-12
 * @property year <em>Optional</em>. Year of the user's birth
 *
 * @constructor Creates a [Birthdate].
 * */
@Serializable
data class Birthdate(
    val day: Long,
    val month: Long,
    val year: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Contains information about the start page settings of a Telegram Business account.</p>
 *
 * @property title <em>Optional</em>. Title text of the business intro
 * @property message <em>Optional</em>. Message text of the business intro
 * @property sticker <em>Optional</em>. Sticker of the business intro
 *
 * @constructor Creates a [BusinessIntro].
 * */
@Serializable
data class BusinessIntro(
    val title: String? = null,
    val message: String? = null,
    val sticker: Sticker? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Contains information about the location of a Telegram Business account.</p>
 *
 * @property address Address of the business
 * @property location <em>Optional</em>. Location of the business
 *
 * @constructor Creates a [BusinessLocation].
 * */
@Serializable
data class BusinessLocation(
    val address: String,
    val location: Location? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes an interval of time during which a business is open.</p>
 *
 * @property opening_minute The minute's sequence number in a week, starting on Monday, marking the start of the time interval during which the business is open; 0 - 7 * 24 * 60
 * @property closing_minute The minute's sequence number in a week, starting on Monday, marking the end of the time interval during which the business is open; 0 - 8 * 24 * 60
 *
 * @constructor Creates a [BusinessOpeningHoursInterval].
 * */
@Serializable
data class BusinessOpeningHoursInterval(
    val opening_minute: Long,
    val closing_minute: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes the opening hours of a business.</p>
 *
 * @property time_zone_name Unique name of the time zone for which the opening hours are defined
 * @property opening_hours List of time intervals describing business opening hours
 *
 * @constructor Creates a [BusinessOpeningHours].
 * */
@Serializable
data class BusinessOpeningHours(
    val time_zone_name: String,
    val opening_hours: List<BusinessOpeningHoursInterval>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a location to which a chat is connected.</p>
 *
 * @property location The location to which the supergroup is connected. Can't be a live location.
 * @property address Location address; 1-64 characters, as defined by the chat owner
 *
 * @constructor Creates a [ChatLocation].
 * */
@Serializable
data class ChatLocation(
    val location: Location,
    val address: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The reaction is based on an emoji.</p>
 *
 * @property type Type of the reaction, always “emoji”
 * @property emoji Reaction emoji. Currently, it can be one of "<img class="emoji" src="//telegram.org/img/emoji/40/F09F918D.png" width="20" height="20" alt="👍">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F918E.png" width="20" height="20" alt="👎">", "<img class="emoji" src="//telegram.org/img/emoji/40/E29DA4.png" width="20" height="20" alt="❤">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F94A5.png" width="20" height="20" alt="🔥">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA5B0.png" width="20" height="20" alt="🥰">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F918F.png" width="20" height="20" alt="👏">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9881.png" width="20" height="20" alt="😁">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA494.png" width="20" height="20" alt="🤔">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4AF.png" width="20" height="20" alt="🤯">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F98B1.png" width="20" height="20" alt="😱">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4AC.png" width="20" height="20" alt="🤬">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F98A2.png" width="20" height="20" alt="😢">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8E89.png" width="20" height="20" alt="🎉">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4A9.png" width="20" height="20" alt="🤩">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4AE.png" width="20" height="20" alt="🤮">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F92A9.png" width="20" height="20" alt="💩">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F998F.png" width="20" height="20" alt="🙏">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F918C.png" width="20" height="20" alt="👌">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F958A.png" width="20" height="20" alt="🕊">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4A1.png" width="20" height="20" alt="🤡">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA5B1.png" width="20" height="20" alt="🥱">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA5B4.png" width="20" height="20" alt="🥴">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F988D.png" width="20" height="20" alt="😍">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F90B3.png" width="20" height="20" alt="🐳">", "<img class="emoji" src="//telegram.org/img/emoji/40/E29DA4E2808DF09F94A5.png" width="20" height="20" alt="❤‍🔥">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8C9A.png" width="20" height="20" alt="🌚">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8CAD.png" width="20" height="20" alt="🌭">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F92AF.png" width="20" height="20" alt="💯">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4A3.png" width="20" height="20" alt="🤣">", "<img class="emoji" src="//telegram.org/img/emoji/40/E29AA1.png" width="20" height="20" alt="⚡">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8D8C.png" width="20" height="20" alt="🍌">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8F86.png" width="20" height="20" alt="🏆">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9294.png" width="20" height="20" alt="💔">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4A8.png" width="20" height="20" alt="🤨">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9890.png" width="20" height="20" alt="😐">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8D93.png" width="20" height="20" alt="🍓">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8DBE.png" width="20" height="20" alt="🍾">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F928B.png" width="20" height="20" alt="💋">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9695.png" width="20" height="20" alt="🖕">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9888.png" width="20" height="20" alt="😈">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F98B4.png" width="20" height="20" alt="😴">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F98AD.png" width="20" height="20" alt="😭">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA493.png" width="20" height="20" alt="🤓">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F91BB.png" width="20" height="20" alt="👻">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F91A8E2808DF09F92BB.png" width="20" height="20" alt="👨‍💻">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9180.png" width="20" height="20" alt="👀">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8E83.png" width="20" height="20" alt="🎃">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9988.png" width="20" height="20" alt="🙈">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9887.png" width="20" height="20" alt="😇">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F98A8.png" width="20" height="20" alt="😨">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA49D.png" width="20" height="20" alt="🤝">", "<img class="emoji" src="//telegram.org/img/emoji/40/E29C8D.png" width="20" height="20" alt="✍">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA497.png" width="20" height="20" alt="🤗">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FABA1.png" width="20" height="20" alt="🫡">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8E85.png" width="20" height="20" alt="🎅">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8E84.png" width="20" height="20" alt="🎄">", "<img class="emoji" src="//telegram.org/img/emoji/40/E29883.png" width="20" height="20" alt="☃">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9285.png" width="20" height="20" alt="💅">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4AA.png" width="20" height="20" alt="🤪">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F97BF.png" width="20" height="20" alt="🗿">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F8692.png" width="20" height="20" alt="🆒">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9298.png" width="20" height="20" alt="💘">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9989.png" width="20" height="20" alt="🙉">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA684.png" width="20" height="20" alt="🦄">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F9898.png" width="20" height="20" alt="😘">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F928A.png" width="20" height="20" alt="💊">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F998A.png" width="20" height="20" alt="🙊">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F988E.png" width="20" height="20" alt="😎">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F91BE.png" width="20" height="20" alt="👾">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4B7E2808DE29982.png" width="20" height="20" alt="🤷‍♂">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4B7.png" width="20" height="20" alt="🤷">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09FA4B7E2808DE29980.png" width="20" height="20" alt="🤷‍♀">", "<img class="emoji" src="//telegram.org/img/emoji/40/F09F98A1.png" width="20" height="20" alt="😡">"
 *
 * @constructor Creates a [ReactionTypeEmoji].
 * */
@Serializable
data class ReactionTypeEmoji(
    val type: String,
    val emoji: String,
) : ReactionType() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The reaction is based on a custom emoji.</p>
 *
 * @property type Type of the reaction, always “custom_emoji”
 * @property custom_emoji_id Custom emoji identifier
 *
 * @constructor Creates a [ReactionTypeCustomEmoji].
 * */
@Serializable
data class ReactionTypeCustomEmoji(
    val type: String,
    val custom_emoji_id: String,
) : ReactionType() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a reaction added to a message along with the number of times it was added.</p>
 *
 * @property type Type of the reaction
 * @property total_count Number of times the reaction was added
 *
 * @constructor Creates a [ReactionCount].
 * */
@Serializable
data class ReactionCount(
    val type: @Contextual ReactionType,
    val total_count: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a change of a reaction on a message performed by a user.</p>
 *
 * @property chat The chat containing the message the user reacted to
 * @property message_id Unique identifier of the message inside the chat
 * @property date Date of the change in Unix time
 * @property old_reaction Previous list of reaction types that were set by the user
 * @property new_reaction New list of reaction types that have been set by the user
 * @property user <em>Optional</em>. The user that changed the reaction, if the user isn't anonymous
 * @property actor_chat <em>Optional</em>. The chat on behalf of which the reaction was changed, if the user is anonymous
 *
 * @constructor Creates a [MessageReactionUpdated].
 * */
@Serializable
data class MessageReactionUpdated(
    val chat: Chat,
    val message_id: MessageId,
    val date: Long,
    val old_reaction: List<@Contextual ReactionType>,
    val new_reaction: List<@Contextual ReactionType>,
    val user: User? = null,
    val actor_chat: Chat? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents reaction changes on a message with anonymous reactions.</p>
 *
 * @property chat The chat containing the message
 * @property message_id Unique message identifier inside the chat
 * @property date Date of the change in Unix time
 * @property reactions List of reactions that are present on the message
 *
 * @constructor Creates a [MessageReactionCountUpdated].
 * */
@Serializable
data class MessageReactionCountUpdated(
    val chat: Chat,
    val message_id: MessageId,
    val date: Long,
    val reactions: List<ReactionCount>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a forum topic.</p>
 *
 * @property message_thread_id Unique identifier of the forum topic
 * @property name Name of the topic
 * @property icon_color Color of the topic icon in RGB format
 * @property icon_custom_emoji_id <em>Optional</em>. Unique identifier of the custom emoji shown as the topic icon
 *
 * @constructor Creates a [ForumTopic].
 * */
@Serializable
data class ForumTopic(
    val message_thread_id: MessageThreadId,
    val name: String,
    val icon_color: Long,
    val icon_custom_emoji_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a bot command.</p>
 *
 * @property command Text of the command; 1-32 characters. Can contain only lowercase English letters, digits and underscores.
 * @property description Description of the command; 1-256 characters.
 *
 * @constructor Creates a [BotCommand].
 * */
@Serializable
data class BotCommand(
    val command: String,
    val description: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the default <a href="#botcommandscope">scope</a> of bot commands. Default commands are used if no commands with a <a href="#determining-list-of-commands">narrower scope</a> are specified for the user.</p>
 *
 * @property type Scope type, must be <em>default</em>
 *
 * @constructor Creates a [BotCommandScopeDefault].
 * */
@Serializable
data class BotCommandScopeDefault(
    val type: String,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#botcommandscope">scope</a> of bot commands, covering all private chats.</p>
 *
 * @property type Scope type, must be <em>all_private_chats</em>
 *
 * @constructor Creates a [BotCommandScopeAllPrivateChats].
 * */
@Serializable
data class BotCommandScopeAllPrivateChats(
    val type: String,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#botcommandscope">scope</a> of bot commands, covering all group and supergroup chats.</p>
 *
 * @property type Scope type, must be <em>all_group_chats</em>
 *
 * @constructor Creates a [BotCommandScopeAllGroupChats].
 * */
@Serializable
data class BotCommandScopeAllGroupChats(
    val type: String,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#botcommandscope">scope</a> of bot commands, covering all group and supergroup chat administrators.</p>
 *
 * @property type Scope type, must be <em>all_chat_administrators</em>
 *
 * @constructor Creates a [BotCommandScopeAllChatAdministrators].
 * */
@Serializable
data class BotCommandScopeAllChatAdministrators(
    val type: String,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#botcommandscope">scope</a> of bot commands, covering a specific chat.</p>
 *
 * @property type Scope type, must be <em>chat</em>
 * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
 *
 * @constructor Creates a [BotCommandScopeChat].
 * */
@Serializable
data class BotCommandScopeChat(
    val type: String,
    val chat_id: ChatId,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#botcommandscope">scope</a> of bot commands, covering all administrators of a specific group or supergroup chat.</p>
 *
 * @property type Scope type, must be <em>chat_administrators</em>
 * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
 *
 * @constructor Creates a [BotCommandScopeChatAdministrators].
 * */
@Serializable
data class BotCommandScopeChatAdministrators(
    val type: String,
    val chat_id: ChatId,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#botcommandscope">scope</a> of bot commands, covering a specific member of a group or supergroup chat.</p>
 *
 * @property type Scope type, must be <em>chat_member</em>
 * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
 * @property user_id Unique identifier of the target user
 *
 * @constructor Creates a [BotCommandScopeChatMember].
 * */
@Serializable
data class BotCommandScopeChatMember(
    val type: String,
    val chat_id: ChatId,
    val user_id: UserId,
) : BotCommandScope() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents the bot's name.</p>
 *
 * @property name The bot's name
 *
 * @constructor Creates a [BotName].
 * */
@Serializable
data class BotName(
    val name: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents the bot's description.</p>
 *
 * @property description The bot's description
 *
 * @constructor Creates a [BotDescription].
 * */
@Serializable
data class BotDescription(
    val description: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents the bot's short description.</p>
 *
 * @property short_description The bot's short description
 *
 * @constructor Creates a [BotShortDescription].
 * */
@Serializable
data class BotShortDescription(
    val short_description: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a menu button, which opens the bot's list of commands.</p>
 *
 * @property type Type of the button, must be <em>commands</em>
 *
 * @constructor Creates a [MenuButtonCommands].
 * */
@Serializable
data class MenuButtonCommands(
    val type: String,
) : MenuButton() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a menu button, which launches a <a href="/bots/webapps">Web App</a>.</p>
 *
 * @property type Type of the button, must be <em>web_app</em>
 * @property text Text on the button
 * @property web_app Description of the Web App that will be launched when the user presses the button. The Web App will be able to send an arbitrary message on behalf of the user using the method <a href="#answerwebappquery">answerWebAppQuery</a>. Alternatively, a <code>t.me</code> link to a Web App of the bot can be specified in the object instead of the Web App's URL, in which case the Web App will be opened as if the user pressed the link.
 *
 * @constructor Creates a [MenuButtonWebApp].
 * */
@Serializable
data class MenuButtonWebApp(
    val type: String,
    val text: String,
    val web_app: WebAppInfo,
) : MenuButton() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes that no specific value for the menu button was set.</p>
 *
 * @property type Type of the button, must be <em>default</em>
 *
 * @constructor Creates a [MenuButtonDefault].
 * */
@Serializable
data class MenuButtonDefault(
    val type: String,
) : MenuButton() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The boost was obtained by subscribing to Telegram Premium or by gifting a Telegram Premium subscription to another user.</p>
 *
 * @property source Source of the boost, always “premium”
 * @property user User that boosted the chat
 *
 * @constructor Creates a [ChatBoostSourcePremium].
 * */
@Serializable
data class ChatBoostSourcePremium(
    val source: String,
    val user: User,
) : ChatBoostSource() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The boost was obtained by the creation of Telegram Premium gift codes to boost a chat. Each such code boosts the chat 4 times for the duration of the corresponding Telegram Premium subscription.</p>
 *
 * @property source Source of the boost, always “gift_code”
 * @property user User for which the gift code was created
 *
 * @constructor Creates a [ChatBoostSourceGiftCode].
 * */
@Serializable
data class ChatBoostSourceGiftCode(
    val source: String,
    val user: User,
) : ChatBoostSource() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The boost was obtained by the creation of a Telegram Premium giveaway. This boosts the chat 4 times for the duration of the corresponding Telegram Premium subscription.</p>
 *
 * @property source Source of the boost, always “giveaway”
 * @property giveaway_message_id Identifier of a message in the chat with the giveaway; the message could have been deleted already. May be 0 if the message isn't sent yet.
 * @property user <em>Optional</em>. User that won the prize in the giveaway if any
 * @property is_unclaimed <em>Optional</em>. True, if the giveaway was completed, but there was no user to win the prize
 *
 * @constructor Creates a [ChatBoostSourceGiveaway].
 * */
@Serializable
data class ChatBoostSourceGiveaway(
    val source: String,
    val giveaway_message_id: Long,
    val user: User? = null,
    val is_unclaimed: Boolean? = null,
) : ChatBoostSource() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about a chat boost.</p>
 *
 * @property boost_id Unique identifier of the boost
 * @property add_date Point in time (Unix timestamp) when the chat was boosted
 * @property expiration_date Point in time (Unix timestamp) when the boost will automatically expire, unless the booster's Telegram Premium subscription is prolonged
 * @property source Source of the added boost
 *
 * @constructor Creates a [ChatBoost].
 * */
@Serializable
data class ChatBoost(
    val boost_id: String,
    val add_date: Long,
    val expiration_date: Long,
    val source: @Contextual ChatBoostSource,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a boost added to a chat or changed.</p>
 *
 * @property chat Chat which was boosted
 * @property boost Information about the chat boost
 *
 * @constructor Creates a [ChatBoostUpdated].
 * */
@Serializable
data class ChatBoostUpdated(
    val chat: Chat,
    val boost: ChatBoost,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a boost removed from a chat.</p>
 *
 * @property chat Chat which was boosted
 * @property boost_id Unique identifier of the boost
 * @property remove_date Point in time (Unix timestamp) when the boost was removed
 * @property source Source of the removed boost
 *
 * @constructor Creates a [ChatBoostRemoved].
 * */
@Serializable
data class ChatBoostRemoved(
    val chat: Chat,
    val boost_id: String,
    val remove_date: Long,
    val source: @Contextual ChatBoostSource,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a list of boosts added to a chat by a user.</p>
 *
 * @property boosts The list of boosts added to the chat by the user
 *
 * @constructor Creates a [UserChatBoosts].
 * */
@Serializable
data class UserChatBoosts(
    val boosts: List<ChatBoost>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes the connection of the bot with a business account.</p>
 *
 * @property id Unique identifier of the business connection
 * @property user Business account user that created the business connection
 * @property user_chat_id Identifier of a private chat with the user who created the business connection. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property date Date the connection was established in Unix time
 * @property can_reply True, if the bot can act on behalf of the business account in chats that were active in the last 24 hours
 * @property is_enabled True, if the connection is active
 *
 * @constructor Creates a [BusinessConnection].
 * */
@Serializable
data class BusinessConnection(
    val id: BusinessConnectionId,
    val user: User,
    val user_chat_id: ChatId,
    val date: Long,
    val can_reply: Boolean,
    val is_enabled: Boolean,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object is received when messages are deleted from a connected business account.</p>
 *
 * @property business_connection_id Unique identifier of the business connection
 * @property chat Information about a chat in the business account. The bot may not have access to the chat or the corresponding user.
 * @property message_ids The list of identifiers of deleted messages in the chat of the business account
 *
 * @constructor Creates a [BusinessMessagesDeleted].
 * */
@Serializable
data class BusinessMessagesDeleted(
    val business_connection_id: BusinessConnectionId,
    val chat: Chat,
    val message_ids: List<MessageId>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes why a request was unsuccessful.</p>
 *
 * @property migrate_to_chat_id <em>Optional</em>. The group has been migrated to a supergroup with the specified identifier. This number may have more than 32 significant bits and some programming languages may have difficulty/silent defects in interpreting it. But it has at most 52 significant bits, so a signed 64-bit integer or double-precision float type are safe for storing this identifier.
 * @property retry_after <em>Optional</em>. In case of exceeding flood control, the number of seconds left to wait before the request can be repeated
 *
 * @constructor Creates a [ResponseParameters].
 * */
@Serializable
data class ResponseParameters(
    val migrate_to_chat_id: ChatId? = null,
    val retry_after: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a photo to be sent.</p>
 *
 * @property type Type of the result, must be <em>photo</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 * @property caption <em>Optional</em>. Caption of the photo to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the photo caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property has_spoiler <em>Optional</em>. Pass <em>True</em> if the photo needs to be covered with a spoiler animation
 *
 * @constructor Creates a [InputMediaPhoto].
 * */
@Serializable
data class InputMediaPhoto(
    val type: String,
    val media: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val has_spoiler: Boolean? = null,
) : InputMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a video to be sent.</p>
 *
 * @property type Type of the result, must be <em>video</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 * @property thumbnail <em>Optional</em>. Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
 * @property caption <em>Optional</em>. Caption of the video to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the video caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property width <em>Optional</em>. Video width
 * @property height <em>Optional</em>. Video height
 * @property duration <em>Optional</em>. Video duration in seconds
 * @property supports_streaming <em>Optional</em>. Pass <em>True</em> if the uploaded video is suitable for streaming
 * @property has_spoiler <em>Optional</em>. Pass <em>True</em> if the video needs to be covered with a spoiler animation
 *
 * @constructor Creates a [InputMediaVideo].
 * */
@Serializable
data class InputMediaVideo(
    val type: String,
    val media: String,
    val thumbnail: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val width: Long? = null,
    val height: Long? = null,
    val duration: Long? = null,
    val supports_streaming: Boolean? = null,
    val has_spoiler: Boolean? = null,
) : InputMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an animation file (GIF or H.264/MPEG-4 AVC video without sound) to be sent.</p>
 *
 * @property type Type of the result, must be <em>animation</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 * @property thumbnail <em>Optional</em>. Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
 * @property caption <em>Optional</em>. Caption of the animation to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the animation caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property width <em>Optional</em>. Animation width
 * @property height <em>Optional</em>. Animation height
 * @property duration <em>Optional</em>. Animation duration in seconds
 * @property has_spoiler <em>Optional</em>. Pass <em>True</em> if the animation needs to be covered with a spoiler animation
 *
 * @constructor Creates a [InputMediaAnimation].
 * */
@Serializable
data class InputMediaAnimation(
    val type: String,
    val media: String,
    val thumbnail: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val width: Long? = null,
    val height: Long? = null,
    val duration: Long? = null,
    val has_spoiler: Boolean? = null,
) : InputMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an audio file to be treated as music to be sent.</p>
 *
 * @property type Type of the result, must be <em>audio</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 * @property thumbnail <em>Optional</em>. Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
 * @property caption <em>Optional</em>. Caption of the audio to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the audio caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property duration <em>Optional</em>. Duration of the audio in seconds
 * @property performer <em>Optional</em>. Performer of the audio
 * @property title <em>Optional</em>. Title of the audio
 *
 * @constructor Creates a [InputMediaAudio].
 * */
@Serializable
data class InputMediaAudio(
    val type: String,
    val media: String,
    val thumbnail: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val duration: Long? = null,
    val performer: String? = null,
    val title: String? = null,
) : InputMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a general file to be sent.</p>
 *
 * @property type Type of the result, must be <em>document</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 * @property thumbnail <em>Optional</em>. Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
 * @property caption <em>Optional</em>. Caption of the document to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the document caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property disable_content_type_detection <em>Optional</em>. Disables automatic server-side content type detection for files uploaded using multipart/form-data. Always <em>True</em>, if the document is sent as part of an album.
 *
 * @constructor Creates a [InputMediaDocument].
 * */
@Serializable
data class InputMediaDocument(
    val type: String,
    val media: String,
    val thumbnail: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val disable_content_type_detection: Boolean? = null,
) : InputMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The paid media to send is a photo.</p>
 *
 * @property type Type of the media, must be <em>photo</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 *
 * @constructor Creates a [InputPaidMediaPhoto].
 * */
@Serializable
data class InputPaidMediaPhoto(
    val type: String,
    val media: String,
) : InputPaidMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The paid media to send is a video.</p>
 *
 * @property type Type of the media, must be <em>video</em>
 * @property media File to send. Pass a file_id to send a file that exists on the Telegram servers (recommended), pass an HTTP URL for Telegram to get a file from the Internet, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. <a href="#sending-files">More information on Sending Files »</a>
 * @property thumbnail <em>Optional</em>. Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
 * @property width <em>Optional</em>. Video width
 * @property height <em>Optional</em>. Video height
 * @property duration <em>Optional</em>. Video duration in seconds
 * @property supports_streaming <em>Optional</em>. Pass <em>True</em> if the uploaded video is suitable for streaming
 *
 * @constructor Creates a [InputPaidMediaVideo].
 * */
@Serializable
data class InputPaidMediaVideo(
    val type: String,
    val media: String,
    val thumbnail: String? = null,
    val width: Long? = null,
    val height: Long? = null,
    val duration: Long? = null,
    val supports_streaming: Boolean? = null,
) : InputPaidMedia() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// Stickers

/**
 * <p>This object represents a sticker.</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property type Type of the sticker, currently one of “regular”, “mask”, “custom_emoji”. The type of the sticker is independent from its format, which is determined by the fields <em>is_animated</em> and <em>is_video</em>.
 * @property width Sticker width
 * @property height Sticker height
 * @property is_animated <em>True</em>, if the sticker is <a href="https://telegram.org/blog/animated-stickers">animated</a>
 * @property is_video <em>True</em>, if the sticker is a <a href="https://telegram.org/blog/video-stickers-better-reactions">video sticker</a>
 * @property thumbnail <em>Optional</em>. Sticker thumbnail in the .WEBP or .JPG format
 * @property emoji <em>Optional</em>. Emoji associated with the sticker
 * @property set_name <em>Optional</em>. Name of the sticker set to which the sticker belongs
 * @property premium_animation <em>Optional</em>. For premium regular stickers, premium animation for the sticker
 * @property mask_position <em>Optional</em>. For mask stickers, the position where the mask should be placed
 * @property custom_emoji_id <em>Optional</em>. For custom emoji stickers, unique identifier of the custom emoji
 * @property needs_repainting <em>Optional</em>. <em>True</em>, if the sticker must be repainted to a text color in messages, the color of the Telegram Premium badge in emoji status, white color on chat photos, or another appropriate color in other places
 * @property file_size <em>Optional</em>. File size in bytes
 *
 * @constructor Creates a [Sticker].
 * */
@Serializable
data class Sticker(
    val file_id: String,
    val file_unique_id: String,
    val type: String,
    val width: Long,
    val height: Long,
    val is_animated: Boolean,
    val is_video: Boolean,
    val thumbnail: PhotoSize? = null,
    val emoji: String? = null,
    val set_name: String? = null,
    val premium_animation: File? = null,
    val mask_position: MaskPosition? = null,
    val custom_emoji_id: String? = null,
    val needs_repainting: Boolean? = null,
    val file_size: Long? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a sticker set.</p>
 *
 * @property name Sticker set name
 * @property title Sticker set title
 * @property sticker_type Type of stickers in the set, currently one of “regular”, “mask”, “custom_emoji”
 * @property stickers List of all set stickers
 * @property thumbnail <em>Optional</em>. Sticker set thumbnail in the .WEBP, .TGS, or .WEBM format
 *
 * @constructor Creates a [StickerSet].
 * */
@Serializable
data class StickerSet(
    val name: String,
    val title: String,
    val sticker_type: String,
    val stickers: List<Sticker>,
    val thumbnail: PhotoSize? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object describes the position on faces where a mask should be placed by default.</p>
 *
 * @property point The part of the face relative to which the mask should be placed. One of “forehead”, “eyes”, “mouth”, or “chin”.
 * @property x_shift Shift by X-axis measured in widths of the mask scaled to the face size, from left to right. For example, choosing -1.0 will place mask just to the left of the default mask position.
 * @property y_shift Shift by Y-axis measured in heights of the mask scaled to the face size, from top to bottom. For example, 1.0 will place the mask just below the default mask position.
 * @property scale Mask scaling coefficient. For example, 2.0 means double size.
 *
 * @constructor Creates a [MaskPosition].
 * */
@Serializable
data class MaskPosition(
    val point: String,
    val x_shift: Float,
    val y_shift: Float,
    val scale: Float,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object describes a sticker to be added to a sticker set.</p>
 *
 * @property sticker The added sticker. Pass a <em>file_id</em> as a String to send a file that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram to get a file from the Internet, upload a new one using multipart/form-data, or pass “attach://&lt;file_attach_name&gt;” to upload a new one using multipart/form-data under &lt;file_attach_name&gt; name. Animated and video stickers can't be uploaded via HTTP URL. <a href="#sending-files">More information on Sending Files »</a>
 * @property format Format of the added sticker, must be one of “static” for a <strong>.WEBP</strong> or <strong>.PNG</strong> image, “animated” for a <strong>.TGS</strong> animation, “video” for a <strong>WEBM</strong> video
 * @property emoji_list List of 1-20 emoji associated with the sticker
 * @property mask_position <em>Optional</em>. Position where the mask should be placed on faces. For “mask” stickers only.
 * @property keywords <em>Optional</em>. List of 0-20 search keywords for the sticker with total length of up to 64 characters. For “regular” and “custom_emoji” stickers only.
 *
 * @constructor Creates a [InputSticker].
 * */
@Serializable
data class InputSticker(
    val sticker: String,
    val format: String,
    val emoji_list: List<String>,
    val mask_position: MaskPosition? = null,
    val keywords: List<String>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// Inline mode

/**
 * <p>This object represents an incoming inline query. When the user sends an empty query, your bot could return some default or trending results.</p>
 *
 * @property id Unique identifier for this query
 * @property from Sender
 * @property query Text of the query (up to 256 characters)
 * @property offset Offset of the results to be returned, can be controlled by the bot
 * @property chat_type <em>Optional</em>. Type of the chat from which the inline query was sent. Can be either “sender” for a private chat with the inline query sender, “private”, “group”, “supergroup”, or “channel”. The chat type should be always known for requests sent from official clients and most third-party clients, unless the request was sent from a secret chat
 * @property location <em>Optional</em>. Sender location, only for bots that request user location
 *
 * @constructor Creates a [InlineQuery].
 * */
@Serializable
data class InlineQuery(
    val id: String,
    val from: User,
    val query: String,
    val offset: String,
    val chat_type: String? = null,
    val location: Location? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a button to be shown above inline query results. You <strong>must</strong> use exactly one of the optional fields.</p>
 *
 * @property text Label text on the button
 * @property web_app <em>Optional</em>. Description of the <a href="/bots/webapps">Web App</a> that will be launched when the user presses the button. The Web App will be able to switch back to the inline mode using the method <a href="/bots/webapps#initializing-mini-apps">switchInlineQuery</a> inside the Web App.
 * @property start_parameter <em>Optional</em>. <a href="/bots/features#deep-linking">Deep-linking</a> parameter for the /start message sent to the bot when a user presses the button. 1-64 characters, only <code>A-Z</code>, <code>a-z</code>, <code>0-9</code>, <code>_</code> and <code>-</code> are allowed.<br><br><em>Example:</em> An inline bot that sends YouTube videos can ask the user to connect the bot to their YouTube account to adapt search results accordingly. To do this, it displays a 'Connect your YouTube account' button above the results, or even before showing any. The user presses the button, switches to a private chat with the bot and, in doing so, passes a start parameter that instructs the bot to return an OAuth link. Once done, the bot can offer a <a href="#inlinekeyboardmarkup"><em>switch_inline</em></a> button so that the user can easily return to the chat where they wanted to use the bot's inline capabilities.
 *
 * @constructor Creates a [InlineQueryResultsButton].
 * */
@Serializable
data class InlineQueryResultsButton(
    val text: String,
    val web_app: WebAppInfo? = null,
    val start_parameter: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to an article or web page.</p>
 *
 * @property type Type of the result, must be <em>article</em>
 * @property id Unique identifier for this result, 1-64 Bytes
 * @property title Title of the result
 * @property input_message_content Content of the message to be sent
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property url <em>Optional</em>. URL of the result
 * @property hide_url <em>Optional</em>. Pass <em>True</em> if you don't want the URL to be shown in the message
 * @property description <em>Optional</em>. Short description of the result
 * @property thumbnail_url <em>Optional</em>. Url of the thumbnail for the result
 * @property thumbnail_width <em>Optional</em>. Thumbnail width
 * @property thumbnail_height <em>Optional</em>. Thumbnail height
 *
 * @constructor Creates a [InlineQueryResultArticle].
 * */
@Serializable
data class InlineQueryResultArticle(
    val type: String,
    val id: String,
    val title: String,
    val input_message_content: @Contextual InputMessageContent,
    val reply_markup: InlineKeyboardMarkup? = null,
    val url: String? = null,
    val hide_url: Boolean? = null,
    val description: String? = null,
    val thumbnail_url: String? = null,
    val thumbnail_width: Long? = null,
    val thumbnail_height: Long? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a photo. By default, this photo will be sent by the user with optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the photo.</p>
 *
 * @property type Type of the result, must be <em>photo</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property photo_url A valid URL of the photo. Photo must be in <strong>JPEG</strong> format. Photo size must not exceed 5MB
 * @property thumbnail_url URL of the thumbnail for the photo
 * @property photo_width <em>Optional</em>. Width of the photo
 * @property photo_height <em>Optional</em>. Height of the photo
 * @property title <em>Optional</em>. Title for the result
 * @property description <em>Optional</em>. Short description of the result
 * @property caption <em>Optional</em>. Caption of the photo to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the photo caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the photo
 *
 * @constructor Creates a [InlineQueryResultPhoto].
 * */
@Serializable
data class InlineQueryResultPhoto(
    val type: String,
    val id: String,
    val photo_url: String,
    val thumbnail_url: String,
    val photo_width: Long? = null,
    val photo_height: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to an animated GIF file. By default, this animated GIF file will be sent by the user with optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the animation.</p>
 *
 * @property type Type of the result, must be <em>gif</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property gif_url A valid URL for the GIF file. File size must not exceed 1MB
 * @property thumbnail_url URL of the static (JPEG or GIF) or animated (MPEG4) thumbnail for the result
 * @property gif_width <em>Optional</em>. Width of the GIF
 * @property gif_height <em>Optional</em>. Height of the GIF
 * @property gif_duration <em>Optional</em>. Duration of the GIF in seconds
 * @property thumbnail_mime_type <em>Optional</em>. MIME type of the thumbnail, must be one of “image/jpeg”, “image/gif”, or “video/mp4”. Defaults to “image/jpeg”
 * @property title <em>Optional</em>. Title for the result
 * @property caption <em>Optional</em>. Caption of the GIF file to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the GIF animation
 *
 * @constructor Creates a [InlineQueryResultGif].
 * */
@Serializable
data class InlineQueryResultGif(
    val type: String,
    val id: String,
    val gif_url: String,
    val thumbnail_url: String,
    val gif_width: Long? = null,
    val gif_height: Long? = null,
    val gif_duration: Long? = null,
    val thumbnail_mime_type: String? = null,
    val title: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a video animation (H.264/MPEG-4 AVC video without sound). By default, this animated MPEG-4 file will be sent by the user with optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the animation.</p>
 *
 * @property type Type of the result, must be <em>mpeg4_gif</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property mpeg4_url A valid URL for the MPEG4 file. File size must not exceed 1MB
 * @property thumbnail_url URL of the static (JPEG or GIF) or animated (MPEG4) thumbnail for the result
 * @property mpeg4_width <em>Optional</em>. Video width
 * @property mpeg4_height <em>Optional</em>. Video height
 * @property mpeg4_duration <em>Optional</em>. Video duration in seconds
 * @property thumbnail_mime_type <em>Optional</em>. MIME type of the thumbnail, must be one of “image/jpeg”, “image/gif”, or “video/mp4”. Defaults to “image/jpeg”
 * @property title <em>Optional</em>. Title for the result
 * @property caption <em>Optional</em>. Caption of the MPEG-4 file to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the video animation
 *
 * @constructor Creates a [InlineQueryResultMpeg4Gif].
 * */
@Serializable
data class InlineQueryResultMpeg4Gif(
    val type: String,
    val id: String,
    val mpeg4_url: String,
    val thumbnail_url: String,
    val mpeg4_width: Long? = null,
    val mpeg4_height: Long? = null,
    val mpeg4_duration: Long? = null,
    val thumbnail_mime_type: String? = null,
    val title: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a page containing an embedded video player or a video file. By default, this video file will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the video.</p><blockquote>
 *  <p>If an InlineQueryResultVideo message contains an embedded video (e.g., YouTube), you <strong>must</strong> replace its content using <em>input_message_content</em>.</p>
 * </blockquote>
 *
 * @property type Type of the result, must be <em>video</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property video_url A valid URL for the embedded video player or video file
 * @property mime_type MIME type of the content of the video URL, “text/html” or “video/mp4”
 * @property thumbnail_url URL of the thumbnail (JPEG only) for the video
 * @property title Title for the result
 * @property caption <em>Optional</em>. Caption of the video to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the video caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property video_width <em>Optional</em>. Video width
 * @property video_height <em>Optional</em>. Video height
 * @property video_duration <em>Optional</em>. Video duration in seconds
 * @property description <em>Optional</em>. Short description of the result
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the video. This field is <strong>required</strong> if InlineQueryResultVideo is used to send an HTML-page as a result (e.g., a YouTube video).
 *
 * @constructor Creates a [InlineQueryResultVideo].
 * */
@Serializable
data class InlineQueryResultVideo(
    val type: String,
    val id: String,
    val video_url: String,
    val mime_type: String,
    val thumbnail_url: String,
    val title: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val video_width: Long? = null,
    val video_height: Long? = null,
    val video_duration: Long? = null,
    val description: String? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to an MP3 audio file. By default, this audio file will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the audio.</p>
 *
 * @property type Type of the result, must be <em>audio</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property audio_url A valid URL for the audio file
 * @property title Title
 * @property caption <em>Optional</em>. Caption, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the audio caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property performer <em>Optional</em>. Performer
 * @property audio_duration <em>Optional</em>. Audio duration in seconds
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the audio
 *
 * @constructor Creates a [InlineQueryResultAudio].
 * */
@Serializable
data class InlineQueryResultAudio(
    val type: String,
    val id: String,
    val audio_url: String,
    val title: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val performer: String? = null,
    val audio_duration: Long? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a voice recording in an .OGG container encoded with OPUS. By default, this voice recording will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the the voice message.</p>
 *
 * @property type Type of the result, must be <em>voice</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property voice_url A valid URL for the voice recording
 * @property title Recording title
 * @property caption <em>Optional</em>. Caption, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the voice message caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property voice_duration <em>Optional</em>. Recording duration in seconds
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the voice recording
 *
 * @constructor Creates a [InlineQueryResultVoice].
 * */
@Serializable
data class InlineQueryResultVoice(
    val type: String,
    val id: String,
    val voice_url: String,
    val title: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val voice_duration: Long? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a file. By default, this file will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the file. Currently, only <strong>.PDF</strong> and <strong>.ZIP</strong> files can be sent using this method.</p>
 *
 * @property type Type of the result, must be <em>document</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property title Title for the result
 * @property document_url A valid URL for the file
 * @property mime_type MIME type of the content of the file, either “application/pdf” or “application/zip”
 * @property caption <em>Optional</em>. Caption of the document to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the document caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property description <em>Optional</em>. Short description of the result
 * @property reply_markup <em>Optional</em>. Inline keyboard attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the file
 * @property thumbnail_url <em>Optional</em>. URL of the thumbnail (JPEG only) for the file
 * @property thumbnail_width <em>Optional</em>. Thumbnail width
 * @property thumbnail_height <em>Optional</em>. Thumbnail height
 *
 * @constructor Creates a [InlineQueryResultDocument].
 * */
@Serializable
data class InlineQueryResultDocument(
    val type: String,
    val id: String,
    val title: String,
    val document_url: String,
    val mime_type: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val description: String? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
    val thumbnail_url: String? = null,
    val thumbnail_width: Long? = null,
    val thumbnail_height: Long? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a location on a map. By default, the location will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the location.</p>
 *
 * @property type Type of the result, must be <em>location</em>
 * @property id Unique identifier for this result, 1-64 Bytes
 * @property latitude Location latitude in degrees
 * @property longitude Location longitude in degrees
 * @property title Location title
 * @property horizontal_accuracy <em>Optional</em>. The radius of uncertainty for the location, measured in meters; 0-1500
 * @property live_period <em>Optional</em>. Period in seconds during which the location can be updated, should be between 60 and 86400, or 0x7FFFFFFF for live locations that can be edited indefinitely.
 * @property heading <em>Optional</em>. For live locations, a direction in which the user is moving, in degrees. Must be between 1 and 360 if specified.
 * @property proximity_alert_radius <em>Optional</em>. For live locations, a maximum distance for proximity alerts about approaching another chat member, in meters. Must be between 1 and 100000 if specified.
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the location
 * @property thumbnail_url <em>Optional</em>. Url of the thumbnail for the result
 * @property thumbnail_width <em>Optional</em>. Thumbnail width
 * @property thumbnail_height <em>Optional</em>. Thumbnail height
 *
 * @constructor Creates a [InlineQueryResultLocation].
 * */
@Serializable
data class InlineQueryResultLocation(
    val type: String,
    val id: String,
    val latitude: Float,
    val longitude: Float,
    val title: String,
    val horizontal_accuracy: Float? = null,
    val live_period: Long? = null,
    val heading: Long? = null,
    val proximity_alert_radius: Long? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
    val thumbnail_url: String? = null,
    val thumbnail_width: Long? = null,
    val thumbnail_height: Long? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a venue. By default, the venue will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the venue.</p>
 *
 * @property type Type of the result, must be <em>venue</em>
 * @property id Unique identifier for this result, 1-64 Bytes
 * @property latitude Latitude of the venue location in degrees
 * @property longitude Longitude of the venue location in degrees
 * @property title Title of the venue
 * @property address Address of the venue
 * @property foursquare_id <em>Optional</em>. Foursquare identifier of the venue if known
 * @property foursquare_type <em>Optional</em>. Foursquare type of the venue, if known. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
 * @property google_place_id <em>Optional</em>. Google Places identifier of the venue
 * @property google_place_type <em>Optional</em>. Google Places type of the venue. (See <a href="https://developers.google.com/places/web-service/supported_types">supported types</a>.)
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the venue
 * @property thumbnail_url <em>Optional</em>. Url of the thumbnail for the result
 * @property thumbnail_width <em>Optional</em>. Thumbnail width
 * @property thumbnail_height <em>Optional</em>. Thumbnail height
 *
 * @constructor Creates a [InlineQueryResultVenue].
 * */
@Serializable
data class InlineQueryResultVenue(
    val type: String,
    val id: String,
    val latitude: Float,
    val longitude: Float,
    val title: String,
    val address: String,
    val foursquare_id: String? = null,
    val foursquare_type: String? = null,
    val google_place_id: String? = null,
    val google_place_type: String? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
    val thumbnail_url: String? = null,
    val thumbnail_width: Long? = null,
    val thumbnail_height: Long? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a contact with a phone number. By default, this contact will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the contact.</p>
 *
 * @property type Type of the result, must be <em>contact</em>
 * @property id Unique identifier for this result, 1-64 Bytes
 * @property phone_number Contact's phone number
 * @property first_name Contact's first name
 * @property last_name <em>Optional</em>. Contact's last name
 * @property vcard <em>Optional</em>. Additional data about the contact in the form of a <a href="https://en.wikipedia.org/wiki/VCard">vCard</a>, 0-2048 bytes
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the contact
 * @property thumbnail_url <em>Optional</em>. Url of the thumbnail for the result
 * @property thumbnail_width <em>Optional</em>. Thumbnail width
 * @property thumbnail_height <em>Optional</em>. Thumbnail height
 *
 * @constructor Creates a [InlineQueryResultContact].
 * */
@Serializable
data class InlineQueryResultContact(
    val type: String,
    val id: String,
    val phone_number: String,
    val first_name: String,
    val last_name: String? = null,
    val vcard: String? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
    val thumbnail_url: String? = null,
    val thumbnail_width: Long? = null,
    val thumbnail_height: Long? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#games">Game</a>.</p>
 *
 * @property type Type of the result, must be <em>game</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property game_short_name Short name of the game
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 *
 * @constructor Creates a [InlineQueryResultGame].
 * */
@Serializable
data class InlineQueryResultGame(
    val type: String,
    val id: String,
    val game_short_name: String,
    val reply_markup: InlineKeyboardMarkup? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a photo stored on the Telegram servers. By default, this photo will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the photo.</p>
 *
 * @property type Type of the result, must be <em>photo</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property photo_file_id A valid file identifier of the photo
 * @property title <em>Optional</em>. Title for the result
 * @property description <em>Optional</em>. Short description of the result
 * @property caption <em>Optional</em>. Caption of the photo to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the photo caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the photo
 *
 * @constructor Creates a [InlineQueryResultCachedPhoto].
 * */
@Serializable
data class InlineQueryResultCachedPhoto(
    val type: String,
    val id: String,
    val photo_file_id: String,
    val title: String? = null,
    val description: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to an animated GIF file stored on the Telegram servers. By default, this animated GIF file will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with specified content instead of the animation.</p>
 *
 * @property type Type of the result, must be <em>gif</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property gif_file_id A valid file identifier for the GIF file
 * @property title <em>Optional</em>. Title for the result
 * @property caption <em>Optional</em>. Caption of the GIF file to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the GIF animation
 *
 * @constructor Creates a [InlineQueryResultCachedGif].
 * */
@Serializable
data class InlineQueryResultCachedGif(
    val type: String,
    val id: String,
    val gif_file_id: String,
    val title: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a video animation (H.264/MPEG-4 AVC video without sound) stored on the Telegram servers. By default, this animated MPEG-4 file will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the animation.</p>
 *
 * @property type Type of the result, must be <em>mpeg4_gif</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property mpeg4_file_id A valid file identifier for the MPEG4 file
 * @property title <em>Optional</em>. Title for the result
 * @property caption <em>Optional</em>. Caption of the MPEG-4 file to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the video animation
 *
 * @constructor Creates a [InlineQueryResultCachedMpeg4Gif].
 * */
@Serializable
data class InlineQueryResultCachedMpeg4Gif(
    val type: String,
    val id: String,
    val mpeg4_file_id: String,
    val title: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a sticker stored on the Telegram servers. By default, this sticker will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the sticker.</p>
 *
 * @property type Type of the result, must be <em>sticker</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property sticker_file_id A valid file identifier of the sticker
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the sticker
 *
 * @constructor Creates a [InlineQueryResultCachedSticker].
 * */
@Serializable
data class InlineQueryResultCachedSticker(
    val type: String,
    val id: String,
    val sticker_file_id: String,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a file stored on the Telegram servers. By default, this file will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the file.</p>
 *
 * @property type Type of the result, must be <em>document</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property title Title for the result
 * @property document_file_id A valid file identifier for the file
 * @property description <em>Optional</em>. Short description of the result
 * @property caption <em>Optional</em>. Caption of the document to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the document caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the file
 *
 * @constructor Creates a [InlineQueryResultCachedDocument].
 * */
@Serializable
data class InlineQueryResultCachedDocument(
    val type: String,
    val id: String,
    val title: String,
    val document_file_id: String,
    val description: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a video file stored on the Telegram servers. By default, this video file will be sent by the user with an optional caption. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the video.</p>
 *
 * @property type Type of the result, must be <em>video</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property video_file_id A valid file identifier for the video file
 * @property title Title for the result
 * @property description <em>Optional</em>. Short description of the result
 * @property caption <em>Optional</em>. Caption of the video to be sent, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the video caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property show_caption_above_media <em>Optional</em>. Pass <em>True</em>, if the caption must be shown above the message media
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the video
 *
 * @constructor Creates a [InlineQueryResultCachedVideo].
 * */
@Serializable
data class InlineQueryResultCachedVideo(
    val type: String,
    val id: String,
    val video_file_id: String,
    val title: String,
    val description: String? = null,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val show_caption_above_media: Boolean? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to a voice message stored on the Telegram servers. By default, this voice message will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the voice message.</p>
 *
 * @property type Type of the result, must be <em>voice</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property voice_file_id A valid file identifier for the voice message
 * @property title Voice message title
 * @property caption <em>Optional</em>. Caption, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the voice message caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the voice message
 *
 * @constructor Creates a [InlineQueryResultCachedVoice].
 * */
@Serializable
data class InlineQueryResultCachedVoice(
    val type: String,
    val id: String,
    val voice_file_id: String,
    val title: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a link to an MP3 audio file stored on the Telegram servers. By default, this audio file will be sent by the user. Alternatively, you can use <em>input_message_content</em> to send a message with the specified content instead of the audio.</p>
 *
 * @property type Type of the result, must be <em>audio</em>
 * @property id Unique identifier for this result, 1-64 bytes
 * @property audio_file_id A valid file identifier for the audio file
 * @property caption <em>Optional</em>. Caption, 0-1024 characters after entities parsing
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the audio caption. See <a href="#formatting-options">formatting options</a> for more details.
 * @property caption_entities <em>Optional</em>. List of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
 * @property reply_markup <em>Optional</em>. <a href="/bots/features#inline-keyboards">Inline keyboard</a> attached to the message
 * @property input_message_content <em>Optional</em>. Content of the message to be sent instead of the audio
 *
 * @constructor Creates a [InlineQueryResultCachedAudio].
 * */
@Serializable
data class InlineQueryResultCachedAudio(
    val type: String,
    val id: String,
    val audio_file_id: String,
    val caption: String? = null,
    val parse_mode: ParseMode? = null,
    val caption_entities: List<MessageEntity>? = null,
    val reply_markup: InlineKeyboardMarkup? = null,
    val input_message_content: @Contextual InputMessageContent? = null,
) : InlineQueryResult() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#inputmessagecontent">content</a> of a text message to be sent as the result of an inline query.</p>
 *
 * @property message_text Text of the message to be sent, 1-4096 characters
 * @property parse_mode <em>Optional</em>. Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
 * @property entities <em>Optional</em>. List of special entities that appear in message text, which can be specified instead of <em>parse_mode</em>
 * @property link_preview_options <em>Optional</em>. Link preview generation options for the message
 *
 * @constructor Creates a [InputTextMessageContent].
 * */
@Serializable
data class InputTextMessageContent(
    val message_text: String,
    val parse_mode: ParseMode? = null,
    val entities: List<MessageEntity>? = null,
    val link_preview_options: LinkPreviewOptions? = null,
) : InputMessageContent() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#inputmessagecontent">content</a> of a location message to be sent as the result of an inline query.</p>
 *
 * @property latitude Latitude of the location in degrees
 * @property longitude Longitude of the location in degrees
 * @property horizontal_accuracy <em>Optional</em>. The radius of uncertainty for the location, measured in meters; 0-1500
 * @property live_period <em>Optional</em>. Period in seconds during which the location can be updated, should be between 60 and 86400, or 0x7FFFFFFF for live locations that can be edited indefinitely.
 * @property heading <em>Optional</em>. For live locations, a direction in which the user is moving, in degrees. Must be between 1 and 360 if specified.
 * @property proximity_alert_radius <em>Optional</em>. For live locations, a maximum distance for proximity alerts about approaching another chat member, in meters. Must be between 1 and 100000 if specified.
 *
 * @constructor Creates a [InputLocationMessageContent].
 * */
@Serializable
data class InputLocationMessageContent(
    val latitude: Float,
    val longitude: Float,
    val horizontal_accuracy: Float? = null,
    val live_period: Long? = null,
    val heading: Long? = null,
    val proximity_alert_radius: Long? = null,
) : InputMessageContent() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#inputmessagecontent">content</a> of a venue message to be sent as the result of an inline query.</p>
 *
 * @property latitude Latitude of the venue in degrees
 * @property longitude Longitude of the venue in degrees
 * @property title Name of the venue
 * @property address Address of the venue
 * @property foursquare_id <em>Optional</em>. Foursquare identifier of the venue, if known
 * @property foursquare_type <em>Optional</em>. Foursquare type of the venue, if known. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
 * @property google_place_id <em>Optional</em>. Google Places identifier of the venue
 * @property google_place_type <em>Optional</em>. Google Places type of the venue. (See <a href="https://developers.google.com/places/web-service/supported_types">supported types</a>.)
 *
 * @constructor Creates a [InputVenueMessageContent].
 * */
@Serializable
data class InputVenueMessageContent(
    val latitude: Float,
    val longitude: Float,
    val title: String,
    val address: String,
    val foursquare_id: String? = null,
    val foursquare_type: String? = null,
    val google_place_id: String? = null,
    val google_place_type: String? = null,
) : InputMessageContent() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#inputmessagecontent">content</a> of a contact message to be sent as the result of an inline query.</p>
 *
 * @property phone_number Contact's phone number
 * @property first_name Contact's first name
 * @property last_name <em>Optional</em>. Contact's last name
 * @property vcard <em>Optional</em>. Additional data about the contact in the form of a <a href="https://en.wikipedia.org/wiki/VCard">vCard</a>, 0-2048 bytes
 *
 * @constructor Creates a [InputContactMessageContent].
 * */
@Serializable
data class InputContactMessageContent(
    val phone_number: String,
    val first_name: String,
    val last_name: String? = null,
    val vcard: String? = null,
) : InputMessageContent() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents the <a href="#inputmessagecontent">content</a> of an invoice message to be sent as the result of an inline query.</p>
 *
 * @property title Product name, 1-32 characters
 * @property description Product description, 1-255 characters
 * @property payload Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
 * @property currency Three-letter ISO 4217 currency code, see <a href="/bots/payments#supported-currencies">more on currencies</a>. Pass “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property prices Price breakdown, a JSON-serialized list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.). Must contain exactly one item for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property provider_token <em>Optional</em>. Payment provider token, obtained via <a href="https://t.me/botfather">@BotFather</a>. Pass an empty string for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property max_tip_amount <em>Optional</em>. The maximum accepted amount for tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a maximum tip of <code>US$ 1.45</code> pass <code>max_tip_amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies). Defaults to 0. Not supported for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property suggested_tip_amounts <em>Optional</em>. A JSON-serialized array of suggested amounts of tip in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). At most 4 suggested tip amounts can be specified. The suggested tip amounts must be positive, passed in a strictly increased order and must not exceed <em>max_tip_amount</em>.
 * @property provider_data <em>Optional</em>. A JSON-serialized object for data about the invoice, which will be shared with the payment provider. A detailed description of the required fields should be provided by the payment provider.
 * @property photo_url <em>Optional</em>. URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service.
 * @property photo_size <em>Optional</em>. Photo size in bytes
 * @property photo_width <em>Optional</em>. Photo width
 * @property photo_height <em>Optional</em>. Photo height
 * @property need_name <em>Optional</em>. Pass <em>True</em> if you require the user's full name to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property need_phone_number <em>Optional</em>. Pass <em>True</em> if you require the user's phone number to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property need_email <em>Optional</em>. Pass <em>True</em> if you require the user's email address to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property need_shipping_address <em>Optional</em>. Pass <em>True</em> if you require the user's shipping address to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property send_phone_number_to_provider <em>Optional</em>. Pass <em>True</em> if the user's phone number should be sent to the provider. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property send_email_to_provider <em>Optional</em>. Pass <em>True</em> if the user's email address should be sent to the provider. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 * @property is_flexible <em>Optional</em>. Pass <em>True</em> if the final price depends on the shipping method. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
 *
 * @constructor Creates a [InputInvoiceMessageContent].
 * */
@Serializable
data class InputInvoiceMessageContent(
    val title: String,
    val description: String,
    val payload: String,
    val currency: String,
    val prices: List<LabeledPrice>,
    val provider_token: String? = null,
    val max_tip_amount: Long? = null,
    val suggested_tip_amounts: List<Long>? = null,
    val provider_data: String? = null,
    val photo_url: String? = null,
    val photo_size: Long? = null,
    val photo_width: Long? = null,
    val photo_height: Long? = null,
    val need_name: Boolean? = null,
    val need_phone_number: Boolean? = null,
    val need_email: Boolean? = null,
    val need_shipping_address: Boolean? = null,
    val send_phone_number_to_provider: Boolean? = null,
    val send_email_to_provider: Boolean? = null,
    val is_flexible: Boolean? = null,
) : InputMessageContent() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents a <a href="#inlinequeryresult">result</a> of an inline query that was chosen by the user and sent to their chat partner.</p><p><strong>Note:</strong> It is necessary to enable <a href="/bots/inline#collecting-feedback">inline feedback</a> via <a href="https://t.me/botfather">@BotFather</a> in order to receive these objects in updates.</p>
 *
 * @property result_id The unique identifier for the result that was chosen
 * @property from The user that chose the result
 * @property query The query that was used to obtain the result
 * @property location <em>Optional</em>. Sender location, only for bots that require user location
 * @property inline_message_id <em>Optional</em>. Identifier of the sent inline message. Available only if there is an <a href="#inlinekeyboardmarkup">inline keyboard</a> attached to the message. Will be also received in <a href="#callbackquery">callback queries</a> and can be used to <a href="#updating-messages">edit</a> the message.
 *
 * @constructor Creates a [ChosenInlineResult].
 * */
@Serializable
data class ChosenInlineResult(
    val result_id: String,
    val from: User,
    val query: String,
    val location: Location? = null,
    val inline_message_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes an inline message sent by a <a href="/bots/webapps">Web App</a> on behalf of a user.</p>
 *
 * @property inline_message_id <em>Optional</em>. Identifier of the sent inline message. Available only if there is an <a href="#inlinekeyboardmarkup">inline keyboard</a> attached to the message.
 *
 * @constructor Creates a [SentWebAppMessage].
 * */
@Serializable
data class SentWebAppMessage(
    val inline_message_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// Payments

/**
 * <p>This object represents a portion of the price for goods or services.</p>
 *
 * @property label Portion label
 * @property amount Price of the product in the <em>smallest units</em> of the <a href="/bots/payments#supported-currencies">currency</a> (integer, <strong>not</strong> float/double). For example, for a price of <code>US$ 1.45</code> pass <code>amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies).
 *
 * @constructor Creates a [LabeledPrice].
 * */
@Serializable
data class LabeledPrice(
    val label: String,
    val amount: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains basic information about an invoice.</p>
 *
 * @property title Product name
 * @property description Product description
 * @property start_parameter Unique bot deep-linking parameter that can be used to generate this invoice
 * @property currency Three-letter ISO 4217 <a href="/bots/payments#supported-currencies">currency</a> code, or “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>
 * @property total_amount Total price in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a price of <code>US$ 1.45</code> pass <code>amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies).
 *
 * @constructor Creates a [Invoice].
 * */
@Serializable
data class Invoice(
    val title: String,
    val description: String,
    val start_parameter: String,
    val currency: String,
    val total_amount: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a shipping address.</p>
 *
 * @property country_code Two-letter <a href="https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">ISO 3166-1 alpha-2</a> country code
 * @property state State, if applicable
 * @property city City
 * @property street_line1 First line for the address
 * @property street_line2 Second line for the address
 * @property post_code Address post code
 *
 * @constructor Creates a [ShippingAddress].
 * */
@Serializable
data class ShippingAddress(
    val country_code: String,
    val state: String,
    val city: String,
    val street_line1: String,
    val street_line2: String,
    val post_code: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents information about an order.</p>
 *
 * @property name <em>Optional</em>. User name
 * @property phone_number <em>Optional</em>. User's phone number
 * @property email <em>Optional</em>. User email
 * @property shipping_address <em>Optional</em>. User shipping address
 *
 * @constructor Creates a [OrderInfo].
 * */
@Serializable
data class OrderInfo(
    val name: String? = null,
    val phone_number: String? = null,
    val email: String? = null,
    val shipping_address: ShippingAddress? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents one shipping option.</p>
 *
 * @property id Shipping option identifier
 * @property title Option title
 * @property prices List of price portions
 *
 * @constructor Creates a [ShippingOption].
 * */
@Serializable
data class ShippingOption(
    val id: String,
    val title: String,
    val prices: List<LabeledPrice>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains basic information about a successful payment.</p>
 *
 * @property currency Three-letter ISO 4217 <a href="/bots/payments#supported-currencies">currency</a> code, or “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>
 * @property total_amount Total price in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a price of <code>US$ 1.45</code> pass <code>amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies).
 * @property invoice_payload Bot-specified invoice payload
 * @property telegram_payment_charge_id Telegram payment identifier
 * @property provider_payment_charge_id Provider payment identifier
 * @property shipping_option_id <em>Optional</em>. Identifier of the shipping option chosen by the user
 * @property order_info <em>Optional</em>. Order information provided by the user
 *
 * @constructor Creates a [SuccessfulPayment].
 * */
@Serializable
data class SuccessfulPayment(
    val currency: String,
    val total_amount: Long,
    val invoice_payload: String,
    val telegram_payment_charge_id: String,
    val provider_payment_charge_id: String,
    val shipping_option_id: String? = null,
    val order_info: OrderInfo? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains basic information about a refunded payment.</p>
 *
 * @property currency Three-letter ISO 4217 <a href="/bots/payments#supported-currencies">currency</a> code, or “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>. Currently, always “XTR”
 * @property total_amount Total refunded price in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a price of <code>US$ 1.45</code>, <code>total_amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies).
 * @property invoice_payload Bot-specified invoice payload
 * @property telegram_payment_charge_id Telegram payment identifier
 * @property provider_payment_charge_id <em>Optional</em>. Provider payment identifier
 *
 * @constructor Creates a [RefundedPayment].
 * */
@Serializable
data class RefundedPayment(
    val currency: String,
    val total_amount: Long,
    val invoice_payload: String,
    val telegram_payment_charge_id: String,
    val provider_payment_charge_id: String? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about an incoming shipping query.</p>
 *
 * @property id Unique query identifier
 * @property from User who sent the query
 * @property invoice_payload Bot-specified invoice payload
 * @property shipping_address User specified shipping address
 *
 * @constructor Creates a [ShippingQuery].
 * */
@Serializable
data class ShippingQuery(
    val id: String,
    val from: User,
    val invoice_payload: String,
    val shipping_address: ShippingAddress,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object contains information about an incoming pre-checkout query.</p>
 *
 * @property id Unique query identifier
 * @property from User who sent the query
 * @property currency Three-letter ISO 4217 <a href="/bots/payments#supported-currencies">currency</a> code, or “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>
 * @property total_amount Total price in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a price of <code>US$ 1.45</code> pass <code>amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies).
 * @property invoice_payload Bot-specified invoice payload
 * @property shipping_option_id <em>Optional</em>. Identifier of the shipping option chosen by the user
 * @property order_info <em>Optional</em>. Order information provided by the user
 *
 * @constructor Creates a [PreCheckoutQuery].
 * */
@Serializable
data class PreCheckoutQuery(
    val id: String,
    val from: User,
    val currency: String,
    val total_amount: Long,
    val invoice_payload: String,
    val shipping_option_id: String? = null,
    val order_info: OrderInfo? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The withdrawal is in progress.</p>
 *
 * @property type Type of the state, always “pending”
 *
 * @constructor Creates a [RevenueWithdrawalStatePending].
 * */
@Serializable
data class RevenueWithdrawalStatePending(
    val type: String,
) : RevenueWithdrawalState() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The withdrawal succeeded.</p>
 *
 * @property type Type of the state, always “succeeded”
 * @property date Date the withdrawal was completed in Unix time
 * @property url An HTTPS URL that can be used to see transaction details
 *
 * @constructor Creates a [RevenueWithdrawalStateSucceeded].
 * */
@Serializable
data class RevenueWithdrawalStateSucceeded(
    val type: String,
    val date: Long,
    val url: String,
) : RevenueWithdrawalState() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>The withdrawal failed and the transaction was refunded.</p>
 *
 * @property type Type of the state, always “failed”
 *
 * @constructor Creates a [RevenueWithdrawalStateFailed].
 * */
@Serializable
data class RevenueWithdrawalStateFailed(
    val type: String,
) : RevenueWithdrawalState() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes a transaction with a user.</p>
 *
 * @property type Type of the transaction partner, always “user”
 * @property user Information about the user
 * @property invoice_payload <em>Optional</em>. Bot-specified invoice payload
 *
 * @constructor Creates a [TransactionPartnerUser].
 * */
@Serializable
data class TransactionPartnerUser(
    val type: String,
    val user: User,
    val invoice_payload: String? = null,
) : TransactionPartner() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes a withdrawal transaction with Fragment.</p>
 *
 * @property type Type of the transaction partner, always “fragment”
 * @property withdrawal_state <em>Optional</em>. State of the transaction if the transaction is outgoing
 *
 * @constructor Creates a [TransactionPartnerFragment].
 * */
@Serializable
data class TransactionPartnerFragment(
    val type: String,
    val withdrawal_state: @Contextual RevenueWithdrawalState? = null,
) : TransactionPartner() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes a withdrawal transaction to the Telegram Ads platform.</p>
 *
 * @property type Type of the transaction partner, always “telegram_ads”
 *
 * @constructor Creates a [TransactionPartnerTelegramAds].
 * */
@Serializable
data class TransactionPartnerTelegramAds(
    val type: String,
) : TransactionPartner() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes a transaction with an unknown source or recipient.</p>
 *
 * @property type Type of the transaction partner, always “other”
 *
 * @constructor Creates a [TransactionPartnerOther].
 * */
@Serializable
data class TransactionPartnerOther(
    val type: String,
) : TransactionPartner() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes a Telegram Star transaction.</p>
 *
 * @property id Unique identifier of the transaction. Coincides with the identifer of the original transaction for refund transactions. Coincides with <em>SuccessfulPayment.telegram_payment_charge_id</em> for successful incoming payments from users.
 * @property amount Number of Telegram Stars transferred by the transaction
 * @property date Date the transaction was created in Unix time
 * @property source <em>Optional</em>. Source of an incoming transaction (e.g., a user purchasing goods or services, Fragment refunding a failed withdrawal). Only for incoming transactions
 * @property receiver <em>Optional</em>. Receiver of an outgoing transaction (e.g., a user for a purchase refund, Fragment for a withdrawal). Only for outgoing transactions
 *
 * @constructor Creates a [StarTransaction].
 * */
@Serializable
data class StarTransaction(
    val id: String,
    val amount: Long,
    val date: Long,
    val source: @Contextual TransactionPartner? = null,
    val receiver: @Contextual TransactionPartner? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Contains a list of Telegram Star transactions.</p>
 *
 * @property transactions The list of transactions
 *
 * @constructor Creates a [StarTransactions].
 * */
@Serializable
data class StarTransactions(
    val transactions: List<StarTransaction>,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// Telegram Passport

/**
 * <p>Describes Telegram Passport data shared with the bot by the user.</p>
 *
 * @property data Array with information about documents and other Telegram Passport elements that was shared with the bot
 * @property credentials Encrypted credentials required to decrypt the data
 *
 * @constructor Creates a [PassportData].
 * */
@Serializable
data class PassportData(
    val data: List<EncryptedPassportElement>,
    val credentials: EncryptedCredentials,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents a file uploaded to Telegram Passport. Currently all Telegram Passport files are in JPEG format when decrypted and don't exceed 10MB.</p>
 *
 * @property file_id Identifier for this file, which can be used to download or reuse the file
 * @property file_unique_id Unique identifier for this file, which is supposed to be the same over time and for different bots. Can't be used to download or reuse the file.
 * @property file_size File size in bytes
 * @property file_date Unix time when the file was uploaded
 *
 * @constructor Creates a [PassportFile].
 * */
@Serializable
data class PassportFile(
    val file_id: String,
    val file_unique_id: String,
    val file_size: Long,
    val file_date: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes documents or other Telegram Passport elements shared with the bot by the user.</p>
 *
 * @property type Element type. One of “personal_details”, “passport”, “driver_license”, “identity_card”, “internal_passport”, “address”, “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”, “phone_number”, “email”.
 * @property hash Base64-encoded element hash for using in <a href="#passportelementerrorunspecified">PassportElementErrorUnspecified</a>
 * @property data <em>Optional</em>. Base64-encoded encrypted Telegram Passport element data provided by the user; available only for “personal_details”, “passport”, “driver_license”, “identity_card”, “internal_passport” and “address” types. Can be decrypted and verified using the accompanying <a href="#encryptedcredentials">EncryptedCredentials</a>.
 * @property phone_number <em>Optional</em>. User's verified phone number; available only for “phone_number” type
 * @property email <em>Optional</em>. User's verified email address; available only for “email” type
 * @property files <em>Optional</em>. Array of encrypted files with documents provided by the user; available only for “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration” and “temporary_registration” types. Files can be decrypted and verified using the accompanying <a href="#encryptedcredentials">EncryptedCredentials</a>.
 * @property front_side <em>Optional</em>. Encrypted file with the front side of the document, provided by the user; available only for “passport”, “driver_license”, “identity_card” and “internal_passport”. The file can be decrypted and verified using the accompanying <a href="#encryptedcredentials">EncryptedCredentials</a>.
 * @property reverse_side <em>Optional</em>. Encrypted file with the reverse side of the document, provided by the user; available only for “driver_license” and “identity_card”. The file can be decrypted and verified using the accompanying <a href="#encryptedcredentials">EncryptedCredentials</a>.
 * @property selfie <em>Optional</em>. Encrypted file with the selfie of the user holding a document, provided by the user; available if requested for “passport”, “driver_license”, “identity_card” and “internal_passport”. The file can be decrypted and verified using the accompanying <a href="#encryptedcredentials">EncryptedCredentials</a>.
 * @property translation <em>Optional</em>. Array of encrypted files with translated versions of documents provided by the user; available if requested for “passport”, “driver_license”, “identity_card”, “internal_passport”, “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration” and “temporary_registration” types. Files can be decrypted and verified using the accompanying <a href="#encryptedcredentials">EncryptedCredentials</a>.
 *
 * @constructor Creates a [EncryptedPassportElement].
 * */
@Serializable
data class EncryptedPassportElement(
    val type: String,
    val hash: String,
    val data: String? = null,
    val phone_number: String? = null,
    val email: String? = null,
    val files: List<PassportFile>? = null,
    val front_side: PassportFile? = null,
    val reverse_side: PassportFile? = null,
    val selfie: PassportFile? = null,
    val translation: List<PassportFile>? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Describes data required for decrypting and authenticating <a href="#encryptedpassportelement">EncryptedPassportElement</a>. See the <a href="/passport#receiving-information">Telegram Passport Documentation</a> for a complete description of the data decryption and authentication processes.</p>
 *
 * @property data Base64-encoded encrypted JSON-serialized data with unique user's payload, data hashes and secrets required for <a href="#encryptedpassportelement">EncryptedPassportElement</a> decryption and authentication
 * @property hash Base64-encoded data hash for data authentication
 * @property secret Base64-encoded secret, encrypted with the bot's public RSA key, required for data decryption
 *
 * @constructor Creates a [EncryptedCredentials].
 * */
@Serializable
data class EncryptedCredentials(
    val data: String,
    val hash: String,
    val secret: String,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue in one of the data fields that was provided by the user. The error is considered resolved when the field's value changes.</p>
 *
 * @property source Error source, must be <em>data</em>
 * @property type The section of the user's Telegram Passport which has the error, one of “personal_details”, “passport”, “driver_license”, “identity_card”, “internal_passport”, “address”
 * @property field_name Name of the data field which has the error
 * @property data_hash Base64-encoded data hash
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorDataField].
 * */
@Serializable
data class PassportElementErrorDataField(
    val source: String,
    val type: String,
    val field_name: String,
    val data_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with the front side of a document. The error is considered resolved when the file with the front side of the document changes.</p>
 *
 * @property source Error source, must be <em>front_side</em>
 * @property type The section of the user's Telegram Passport which has the issue, one of “passport”, “driver_license”, “identity_card”, “internal_passport”
 * @property file_hash Base64-encoded hash of the file with the front side of the document
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorFrontSide].
 * */
@Serializable
data class PassportElementErrorFrontSide(
    val source: String,
    val type: String,
    val file_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with the reverse side of a document. The error is considered resolved when the file with reverse side of the document changes.</p>
 *
 * @property source Error source, must be <em>reverse_side</em>
 * @property type The section of the user's Telegram Passport which has the issue, one of “driver_license”, “identity_card”
 * @property file_hash Base64-encoded hash of the file with the reverse side of the document
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorReverseSide].
 * */
@Serializable
data class PassportElementErrorReverseSide(
    val source: String,
    val type: String,
    val file_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with the selfie with a document. The error is considered resolved when the file with the selfie changes.</p>
 *
 * @property source Error source, must be <em>selfie</em>
 * @property type The section of the user's Telegram Passport which has the issue, one of “passport”, “driver_license”, “identity_card”, “internal_passport”
 * @property file_hash Base64-encoded hash of the file with the selfie
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorSelfie].
 * */
@Serializable
data class PassportElementErrorSelfie(
    val source: String,
    val type: String,
    val file_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with a document scan. The error is considered resolved when the file with the document scan changes.</p>
 *
 * @property source Error source, must be <em>file</em>
 * @property type The section of the user's Telegram Passport which has the issue, one of “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
 * @property file_hash Base64-encoded file hash
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorFile].
 * */
@Serializable
data class PassportElementErrorFile(
    val source: String,
    val type: String,
    val file_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with a list of scans. The error is considered resolved when the list of files containing the scans changes.</p>
 *
 * @property source Error source, must be <em>files</em>
 * @property type The section of the user's Telegram Passport which has the issue, one of “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
 * @property file_hashes List of base64-encoded file hashes
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorFiles].
 * */
@Serializable
data class PassportElementErrorFiles(
    val source: String,
    val type: String,
    val file_hashes: List<String>,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with one of the files that constitute the translation of a document. The error is considered resolved when the file changes.</p>
 *
 * @property source Error source, must be <em>translation_file</em>
 * @property type Type of element of the user's Telegram Passport which has the issue, one of “passport”, “driver_license”, “identity_card”, “internal_passport”, “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
 * @property file_hash Base64-encoded file hash
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorTranslationFile].
 * */
@Serializable
data class PassportElementErrorTranslationFile(
    val source: String,
    val type: String,
    val file_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue with the translated version of a document. The error is considered resolved when a file with the document translation change.</p>
 *
 * @property source Error source, must be <em>translation_files</em>
 * @property type Type of element of the user's Telegram Passport which has the issue, one of “passport”, “driver_license”, “identity_card”, “internal_passport”, “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
 * @property file_hashes List of base64-encoded file hashes
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorTranslationFiles].
 * */
@Serializable
data class PassportElementErrorTranslationFiles(
    val source: String,
    val type: String,
    val file_hashes: List<String>,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>Represents an issue in an unspecified place. The error is considered resolved when new data is added.</p>
 *
 * @property source Error source, must be <em>unspecified</em>
 * @property type Type of element of the user's Telegram Passport which has the issue
 * @property element_hash Base64-encoded element hash
 * @property message Error message
 *
 * @constructor Creates a [PassportElementErrorUnspecified].
 * */
@Serializable
data class PassportElementErrorUnspecified(
    val source: String,
    val type: String,
    val element_hash: String,
    val message: String,
) : PassportElementError() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// Games

/**
 * <p>This object represents a game. Use BotFather to create and edit games, their short names will act as unique identifiers.</p>
 *
 * @property title Title of the game
 * @property description Description of the game
 * @property photo Photo that will be displayed in the game message in chats.
 * @property text <em>Optional</em>. Brief description of the game or high scores included in the game message. Can be automatically edited to include current high scores for the game when the bot calls <a href="#setgamescore">setGameScore</a>, or manually edited using <a href="#editmessagetext">editMessageText</a>. 0-4096 characters.
 * @property text_entities <em>Optional</em>. Special entities that appear in <em>text</em>, such as usernames, URLs, bot commands, etc.
 * @property animation <em>Optional</em>. Animation that will be displayed in the game message in chats. Upload via <a href="https://t.me/botfather">BotFather</a>
 *
 * @constructor Creates a [Game].
 * */
@Serializable
data class Game(
    val title: String,
    val description: String,
    val photo: List<PhotoSize>,
    val text: String? = null,
    val text_entities: List<MessageEntity>? = null,
    val animation: Animation? = null,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}

/**
 * <p>This object represents one row of the high scores table for a game.</p><p>And that's about all we've got for now.<br>If you've got any questions, please check out our <a href="/bots/faq"><strong>Bot FAQ »</strong></a></p>
 *
 * @property position Position in high score table for the game
 * @property user User
 * @property score Score
 *
 * @constructor Creates a [GameHighScore].
 * */
@Serializable
data class GameHighScore(
    val position: Long,
    val user: User,
    val score: Long,
) : TelegramModel() {
    override fun toJson() = json.encodeToString(serializer(), this)

    companion object {
        fun fromJson(string: String) = json.decodeFromString(serializer(), string)
    }
}


// --- Requests ---

sealed class TelegramRequest {
    abstract fun toJsonForRequest(): String
    abstract fun toJsonForResponse(): String

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
     * */
    @Serializable
    data class GetUpdatesRequest(
        val offset: Long? = null,
        val limit: Long? = null,
        val timeout: Long? = null,
        val allowed_updates: List<String>? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getUpdates"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

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
     * */
    @Serializable
    data class SetWebhookRequest(
        val url: String,
        val certificate: @Contextual Any? = null,
        val ip_address: String? = null,
        val max_connections: Long? = null,
        val allowed_updates: List<String>? = null,
        val drop_pending_updates: Boolean? = null,
        val secret_token: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setWebhook"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to remove webhook integration if you decide to switch back to <a href="#getupdates">getUpdates</a>. Returns <em>True</em> on success.</p>
     *
     * @property drop_pending_updates Pass <em>True</em> to drop all pending updates
     * */
    @Serializable
    data class DeleteWebhookRequest(
        val drop_pending_updates: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteWebhook"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


// Available methods

    /**
     * <p>Use this method to send text messages. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property text Text of the message to be sent, 1-4096 characters after entities parsing
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property parse_mode Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
     * @property entities A JSON-serialized list of special entities that appear in message text, which can be specified instead of <em>parse_mode</em>
     * @property link_preview_options Link preview generation options for the message
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendMessageRequest(
        val chat_id: ChatId,
        val text: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val parse_mode: ParseMode? = null,
        val entities: List<MessageEntity>? = null,
        val link_preview_options: LinkPreviewOptions? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendMessage"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to forward messages of any kind. Service messages and messages with protected content can't be forwarded. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property from_chat_id Unique identifier for the chat where the original message was sent (or channel username in the format <code>@channelusername</code>)
     * @property message_id Message identifier in the chat specified in <em>from_chat_id</em>
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the forwarded message from forwarding and saving
     * */
    @Serializable
    data class ForwardMessageRequest(
        val chat_id: ChatId,
        val from_chat_id: ChatId,
        val message_id: MessageId,
        val message_thread_id: MessageThreadId? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("forwardMessage"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to forward multiple messages of any kind. If some of the specified messages can't be found or forwarded, they are skipped. Service messages and messages with protected content can't be forwarded. Album grouping is kept for forwarded messages. On success, an array of <a href="#messageid">MessageId</a> of the sent messages is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property from_chat_id Unique identifier for the chat where the original messages were sent (or channel username in the format <code>@channelusername</code>)
     * @property message_ids A JSON-serialized list of 1-100 identifiers of messages in the chat <em>from_chat_id</em> to forward. The identifiers must be specified in a strictly increasing order.
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property disable_notification Sends the messages <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the forwarded messages from forwarding and saving
     * */
    @Serializable
    data class ForwardMessagesRequest(
        val chat_id: ChatId,
        val from_chat_id: ChatId,
        val message_ids: List<MessageId>,
        val message_thread_id: MessageThreadId? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("forwardMessages"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to copy messages of any kind. Service messages, paid media messages, giveaway messages, giveaway winners messages, and invoice messages can't be copied. A quiz <a href="#poll">poll</a> can be copied only if the value of the field <em>correct_option_id</em> is known to the bot. The method is analogous to the method <a href="#forwardmessage">forwardMessage</a>, but the copied message doesn't have a link to the original message. Returns the <a href="#messageid">MessageId</a> of the sent message on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property from_chat_id Unique identifier for the chat where the original message was sent (or channel username in the format <code>@channelusername</code>)
     * @property message_id Message identifier in the chat specified in <em>from_chat_id</em>
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property caption New caption for media, 0-1024 characters after entities parsing. If not specified, the original caption is kept
     * @property parse_mode Mode for parsing entities in the new caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the new caption, which can be specified instead of <em>parse_mode</em>
     * @property show_caption_above_media Pass <em>True</em>, if the caption must be shown above the message media. Ignored if a new caption isn't specified.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class CopyMessageRequest(
        val chat_id: ChatId,
        val from_chat_id: ChatId,
        val message_id: MessageId,
        val message_thread_id: MessageThreadId? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val show_caption_above_media: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("copyMessage"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to copy messages of any kind. If some of the specified messages can't be found or copied, they are skipped. Service messages, paid media messages, giveaway messages, giveaway winners messages, and invoice messages can't be copied. A quiz <a href="#poll">poll</a> can be copied only if the value of the field <em>correct_option_id</em> is known to the bot. The method is analogous to the method <a href="#forwardmessages">forwardMessages</a>, but the copied messages don't have a link to the original message. Album grouping is kept for copied messages. On success, an array of <a href="#messageid">MessageId</a> of the sent messages is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property from_chat_id Unique identifier for the chat where the original messages were sent (or channel username in the format <code>@channelusername</code>)
     * @property message_ids A JSON-serialized list of 1-100 identifiers of messages in the chat <em>from_chat_id</em> to copy. The identifiers must be specified in a strictly increasing order.
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property disable_notification Sends the messages <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent messages from forwarding and saving
     * @property remove_caption Pass <em>True</em> to copy the messages without their captions
     * */
    @Serializable
    data class CopyMessagesRequest(
        val chat_id: ChatId,
        val from_chat_id: ChatId,
        val message_ids: List<MessageId>,
        val message_thread_id: MessageThreadId? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val remove_caption: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("copyMessages"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send photos. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property photo Photo to send. Pass a file_id as String to send a photo that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a photo from the Internet, or upload a new photo using multipart/form-data. The photo must be at most 10 MB in size. The photo's width and height must not exceed 10000 in total. Width and height ratio must be at most 20. <a href="#sending-files">More information on Sending Files »</a>
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property caption Photo caption (may also be used when resending photos by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the photo caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property show_caption_above_media Pass <em>True</em>, if the caption must be shown above the message media
     * @property has_spoiler Pass <em>True</em> if the photo needs to be covered with a spoiler animation
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendPhotoRequest(
        val chat_id: ChatId,
        val photo: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val show_caption_above_media: Boolean? = null,
        val has_spoiler: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendPhoto"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send audio files, if you want Telegram clients to display them in the music player. Your audio must be in the .MP3 or .M4A format. On success, the sent <a href="#message">Message</a> is returned. Bots can currently send audio files of up to 50 MB in size, this limit may be changed in the future.</p><p>For sending voice messages, use the <a href="#sendvoice">sendVoice</a> method instead.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property audio Audio file to send. Pass a file_id as String to send an audio file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get an audio file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property caption Audio caption, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the audio caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property duration Duration of the audio in seconds
     * @property performer Performer
     * @property title Track name
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendAudioRequest(
        val chat_id: ChatId,
        val audio: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val duration: Long? = null,
        val performer: String? = null,
        val title: String? = null,
        val thumbnail: String? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendAudio"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send general files. On success, the sent <a href="#message">Message</a> is returned. Bots can currently send files of any type of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property document File to send. Pass a file_id as String to send a file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Document caption (may also be used when resending documents by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the document caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property disable_content_type_detection Disables automatic server-side content type detection for files uploaded using multipart/form-data
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendDocumentRequest(
        val chat_id: ChatId,
        val document: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val thumbnail: String? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val disable_content_type_detection: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendDocument"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send video files, Telegram clients support MPEG4 videos (other formats may be sent as <a href="#document">Document</a>). On success, the sent <a href="#message">Message</a> is returned. Bots can currently send video files of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property video Video to send. Pass a file_id as String to send a video that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a video from the Internet, or upload a new video using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property duration Duration of sent video in seconds
     * @property width Video width
     * @property height Video height
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Video caption (may also be used when resending videos by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the video caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property show_caption_above_media Pass <em>True</em>, if the caption must be shown above the message media
     * @property has_spoiler Pass <em>True</em> if the video needs to be covered with a spoiler animation
     * @property supports_streaming Pass <em>True</em> if the uploaded video is suitable for streaming
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendVideoRequest(
        val chat_id: ChatId,
        val video: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val duration: Long? = null,
        val width: Long? = null,
        val height: Long? = null,
        val thumbnail: String? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val show_caption_above_media: Boolean? = null,
        val has_spoiler: Boolean? = null,
        val supports_streaming: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendVideo"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send animation files (GIF or H.264/MPEG-4 AVC video without sound). On success, the sent <a href="#message">Message</a> is returned. Bots can currently send animation files of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property animation Animation to send. Pass a file_id as String to send an animation that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get an animation from the Internet, or upload a new animation using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property duration Duration of sent animation in seconds
     * @property width Animation width
     * @property height Animation height
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property caption Animation caption (may also be used when resending animation by <em>file_id</em>), 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the animation caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property show_caption_above_media Pass <em>True</em>, if the caption must be shown above the message media
     * @property has_spoiler Pass <em>True</em> if the animation needs to be covered with a spoiler animation
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendAnimationRequest(
        val chat_id: ChatId,
        val animation: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val duration: Long? = null,
        val width: Long? = null,
        val height: Long? = null,
        val thumbnail: String? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val show_caption_above_media: Boolean? = null,
        val has_spoiler: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendAnimation"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send audio files, if you want Telegram clients to display the file as a playable voice message. For this to work, your audio must be in an .OGG file encoded with OPUS, or in .MP3 format, or in .M4A format (other formats may be sent as <a href="#audio">Audio</a> or <a href="#document">Document</a>). On success, the sent <a href="#message">Message</a> is returned. Bots can currently send voice messages of up to 50 MB in size, this limit may be changed in the future.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property voice Audio file to send. Pass a file_id as String to send a file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property caption Voice message caption, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the voice message caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property duration Duration of the voice message in seconds
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendVoiceRequest(
        val chat_id: ChatId,
        val voice: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val duration: Long? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendVoice"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>As of <a href="https://telegram.org/blog/video-messages-and-telescope">v.4.0</a>, Telegram clients support rounded square MPEG4 videos of up to 1 minute long. Use this method to send video messages. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property video_note Video note to send. Pass a file_id as String to send a video note that exists on the Telegram servers (recommended) or upload a new video using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>. Sending video notes by a URL is currently unsupported
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property duration Duration of sent video in seconds
     * @property length Video width and height, i.e. diameter of the video message
     * @property thumbnail Thumbnail of the file sent; can be ignored if thumbnail generation for the file is supported server-side. The thumbnail should be in JPEG format and less than 200 kB in size. A thumbnail's width and height should not exceed 320. Ignored if the file is not uploaded using multipart/form-data. Thumbnails can't be reused and can be only uploaded as a new file, so you can pass “attach://&lt;file_attach_name&gt;” if the thumbnail was uploaded using multipart/form-data under &lt;file_attach_name&gt;. <a href="#sending-files">More information on Sending Files »</a>
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendVideoNoteRequest(
        val chat_id: ChatId,
        val video_note: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val duration: Long? = null,
        val length: Long? = null,
        val thumbnail: String? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendVideoNote"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send paid media to channel chats. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property star_count The number of Telegram Stars that must be paid to buy access to the media
     * @property media A JSON-serialized array describing the media to be sent; up to 10 items
     * @property caption Media caption, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the media caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property show_caption_above_media Pass <em>True</em>, if the caption must be shown above the message media
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendPaidMediaRequest(
        val chat_id: ChatId,
        val star_count: Long,
        val media: List<@Contextual InputPaidMedia>,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val show_caption_above_media: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendPaidMedia"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send a group of photos, videos, documents or audios as an album. Documents and audio files can be only grouped in an album with messages of the same type. On success, an array of <a href="#message">Messages</a> that were sent is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property media A JSON-serialized array describing messages to be sent, must include 2-10 items
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property disable_notification Sends messages <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent messages from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * */
    @Serializable
    data class SendMediaGroupRequest(
        val chat_id: ChatId,
        val media: List<@Contextual InputMedia>,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendMediaGroup"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send point on the map. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property latitude Latitude of the location
     * @property longitude Longitude of the location
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property horizontal_accuracy The radius of uncertainty for the location, measured in meters; 0-1500
     * @property live_period Period in seconds during which the location will be updated (see <a href="https://telegram.org/blog/live-locations">Live Locations</a>, should be between 60 and 86400, or 0x7FFFFFFF for live locations that can be edited indefinitely.
     * @property heading For live locations, a direction in which the user is moving, in degrees. Must be between 1 and 360 if specified.
     * @property proximity_alert_radius For live locations, a maximum distance for proximity alerts about approaching another chat member, in meters. Must be between 1 and 100000 if specified.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendLocationRequest(
        val chat_id: ChatId,
        val latitude: Float,
        val longitude: Float,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val horizontal_accuracy: Float? = null,
        val live_period: Long? = null,
        val heading: Long? = null,
        val proximity_alert_radius: Long? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendLocation"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send information about a venue. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property latitude Latitude of the venue
     * @property longitude Longitude of the venue
     * @property title Name of the venue
     * @property address Address of the venue
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property foursquare_id Foursquare identifier of the venue
     * @property foursquare_type Foursquare type of the venue, if known. (For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.)
     * @property google_place_id Google Places identifier of the venue
     * @property google_place_type Google Places type of the venue. (See <a href="https://developers.google.com/places/web-service/supported_types">supported types</a>.)
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendVenueRequest(
        val chat_id: ChatId,
        val latitude: Float,
        val longitude: Float,
        val title: String,
        val address: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val foursquare_id: String? = null,
        val foursquare_type: String? = null,
        val google_place_id: String? = null,
        val google_place_type: String? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendVenue"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send phone contacts. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property phone_number Contact's phone number
     * @property first_name Contact's first name
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property last_name Contact's last name
     * @property vcard Additional data about the contact in the form of a <a href="https://en.wikipedia.org/wiki/VCard">vCard</a>, 0-2048 bytes
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendContactRequest(
        val chat_id: ChatId,
        val phone_number: String,
        val first_name: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val last_name: String? = null,
        val vcard: String? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendContact"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send a native poll. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property question Poll question, 1-300 characters
     * @property options A JSON-serialized list of 2-10 answer options
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property question_parse_mode Mode for parsing entities in the question. See <a href="#formatting-options">formatting options</a> for more details. Currently, only custom emoji entities are allowed
     * @property question_entities A JSON-serialized list of special entities that appear in the poll question. It can be specified instead of <em>question_parse_mode</em>
     * @property is_anonymous <em>True</em>, if the poll needs to be anonymous, defaults to <em>True</em>
     * @property type Poll type, “quiz” or “regular”, defaults to “regular”
     * @property allows_multiple_answers <em>True</em>, if the poll allows multiple answers, ignored for polls in quiz mode, defaults to <em>False</em>
     * @property correct_option_id 0-based identifier of the correct answer option, required for polls in quiz mode
     * @property explanation Text that is shown when a user chooses an incorrect answer or taps on the lamp icon in a quiz-style poll, 0-200 characters with at most 2 line feeds after entities parsing
     * @property explanation_parse_mode Mode for parsing entities in the explanation. See <a href="#formatting-options">formatting options</a> for more details.
     * @property explanation_entities A JSON-serialized list of special entities that appear in the poll explanation. It can be specified instead of <em>explanation_parse_mode</em>
     * @property open_period Amount of time in seconds the poll will be active after creation, 5-600. Can't be used together with <em>close_date</em>.
     * @property close_date Point in time (Unix timestamp) when the poll will be automatically closed. Must be at least 5 and no more than 600 seconds in the future. Can't be used together with <em>open_period</em>.
     * @property is_closed Pass <em>True</em> if the poll needs to be immediately closed. This can be useful for poll preview.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendPollRequest(
        val chat_id: ChatId,
        val question: String,
        val options: List<InputPollOption>,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val question_parse_mode: String? = null,
        val question_entities: List<MessageEntity>? = null,
        val is_anonymous: Boolean? = null,
        val type: String? = null,
        val allows_multiple_answers: Boolean? = null,
        val correct_option_id: Long? = null,
        val explanation: String? = null,
        val explanation_parse_mode: String? = null,
        val explanation_entities: List<MessageEntity>? = null,
        val open_period: Long? = null,
        val close_date: Long? = null,
        val is_closed: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendPoll"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to send an animated emoji that will display a random value. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property emoji Emoji on which the dice throw animation is based. Currently, must be one of “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EAF.png" width="20" height="20" alt="🎯">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8F80.png" width="20" height="20" alt="🏀">”, “<img class="emoji" src="//telegram.org/img/emoji/40/E29ABD.png" width="20" height="20" alt="⚽">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB3.png" width="20" height="20" alt="🎳">”, or “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB0.png" width="20" height="20" alt="🎰">”. Dice can have values 1-6 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”, “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EAF.png" width="20" height="20" alt="🎯">” and “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB3.png" width="20" height="20" alt="🎳">”, values 1-5 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8F80.png" width="20" height="20" alt="🏀">” and “<img class="emoji" src="//telegram.org/img/emoji/40/E29ABD.png" width="20" height="20" alt="⚽">”, and values 1-64 for “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB0.png" width="20" height="20" alt="🎰">”. Defaults to “<img class="emoji" src="//telegram.org/img/emoji/40/F09F8EB2.png" width="20" height="20" alt="🎲">”
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendDiceRequest(
        val chat_id: ChatId,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val emoji: String? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendDice"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method when you need to tell the user that something is happening on the bot's side. The status is set for 5 seconds or less (when a message arrives from your bot, Telegram clients clear its typing status). Returns <em>True</em> on success.</p><blockquote>
     *  <p>Example: The <a href="https://t.me/imagebot">ImageBot</a> needs some time to process a request and upload the image. Instead of sending a text message along the lines of “Retrieving image, please wait…”, the bot may use <a href="#sendchataction">sendChatAction</a> with <em>action</em> = <em>upload_photo</em>. The user will see a “sending photo” status for the bot.</p>
     * </blockquote><p>We only recommend using this method when a response from the bot will take a <strong>noticeable</strong> amount of time to arrive.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property action Type of action to broadcast. Choose one, depending on what the user is about to receive: <em>typing</em> for <a href="#sendmessage">text messages</a>, <em>upload_photo</em> for <a href="#sendphoto">photos</a>, <em>record_video</em> or <em>upload_video</em> for <a href="#sendvideo">videos</a>, <em>record_voice</em> or <em>upload_voice</em> for <a href="#sendvoice">voice notes</a>, <em>upload_document</em> for <a href="#senddocument">general files</a>, <em>choose_sticker</em> for <a href="#sendsticker">stickers</a>, <em>find_location</em> for <a href="#sendlocation">location data</a>, <em>record_video_note</em> or <em>upload_video_note</em> for <a href="#sendvideonote">video notes</a>.
     * @property business_connection_id Unique identifier of the business connection on behalf of which the action will be sent
     * @property message_thread_id Unique identifier for the target message thread; for supergroups only
     * */
    @Serializable
    data class SendChatActionRequest(
        val chat_id: ChatId,
        val action: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendChatAction"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the chosen reactions on a message. Service messages can't be reacted to. Automatically forwarded messages from a channel to its discussion group have the same available reactions as messages in the channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of the target message. If the message belongs to a media group, the reaction is set to the first non-deleted message in the group instead.
     * @property reaction A JSON-serialized list of reaction types to set on the message. Currently, as non-premium users, bots can set up to one reaction per message. A custom emoji reaction can be used if it is either already present on the message or explicitly allowed by chat administrators.
     * @property is_big Pass <em>True</em> to set the reaction with a big animation
     * */
    @Serializable
    data class SetMessageReactionRequest(
        val chat_id: ChatId,
        val message_id: MessageId,
        val reaction: List<@Contextual ReactionType>? = null,
        val is_big: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setMessageReaction"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get a list of profile pictures for a user. Returns a <a href="#userprofilephotos">UserProfilePhotos</a> object.</p>
     *
     * @property user_id Unique identifier of the target user
     * @property offset Sequential number of the first photo to be returned. By default, all photos are returned.
     * @property limit Limits the number of photos to be retrieved. Values between 1-100 are accepted. Defaults to 100.
     * */
    @Serializable
    data class GetUserProfilePhotosRequest(
        val user_id: UserId,
        val offset: Long? = null,
        val limit: Long? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("getUserProfilePhotos"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get basic information about a file and prepare it for downloading. For the moment, bots can download files of up to 20MB in size. On success, a <a href="#file">File</a> object is returned. The file can then be downloaded via the link <code>https://api.telegram.org/file/bot&lt;token&gt;/&lt;file_path&gt;</code>, where <code>&lt;file_path&gt;</code> is taken from the response. It is guaranteed that the link will be valid for at least 1 hour. When the link expires, a new one can be requested by calling <a href="#getfile">getFile</a> again.</p><p><strong>Note:</strong> This function may not preserve the original file name and MIME type. You should save the file's MIME type and name (if available) when the File object is received.</p>
     *
     * @property file_id File identifier to get information about
     * */
    @Serializable
    data class GetFileRequest(
        val file_id: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getFile"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to ban a user in a group, a supergroup or a channel. In the case of supergroups and channels, the user will not be able to return to the chat on their own using invite links, etc., unless <a href="#unbanchatmember">unbanned</a> first. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target group or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * @property until_date Date when the user will be unbanned; Unix time. If user is banned for more than 366 days or less than 30 seconds from the current time they are considered to be banned forever. Applied for supergroups and channels only.
     * @property revoke_messages Pass <em>True</em> to delete all messages from the chat for the user that is being removed. If <em>False</em>, the user will be able to see messages in the group that were sent before the user was removed. Always <em>True</em> for supergroups and channels.
     * */
    @Serializable
    data class BanChatMemberRequest(
        val chat_id: ChatId,
        val user_id: UserId,
        val until_date: Long? = null,
        val revoke_messages: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("banChatMember"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to unban a previously banned user in a supergroup or channel. The user will <strong>not</strong> return to the group or channel automatically, but will be able to join via link, etc. The bot must be an administrator for this to work. By default, this method guarantees that after the call the user is not a member of the chat, but will be able to join it. So if the user is a member of the chat they will also be <strong>removed</strong> from the chat. If you don't want this, use the parameter <em>only_if_banned</em>. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target group or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * @property only_if_banned Do nothing if the user is not banned
     * */
    @Serializable
    data class UnbanChatMemberRequest(
        val chat_id: ChatId,
        val user_id: UserId,
        val only_if_banned: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("unbanChatMember"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to restrict a user in a supergroup. The bot must be an administrator in the supergroup for this to work and must have the appropriate administrator rights. Pass <em>True</em> for all permissions to lift restrictions from a user. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property user_id Unique identifier of the target user
     * @property permissions A JSON-serialized object for new user permissions
     * @property use_independent_chat_permissions Pass <em>True</em> if chat permissions are set independently. Otherwise, the <em>can_send_other_messages</em> and <em>can_add_web_page_previews</em> permissions will imply the <em>can_send_messages</em>, <em>can_send_audios</em>, <em>can_send_documents</em>, <em>can_send_photos</em>, <em>can_send_videos</em>, <em>can_send_video_notes</em>, and <em>can_send_voice_notes</em> permissions; the <em>can_send_polls</em> permission will imply the <em>can_send_messages</em> permission.
     * @property until_date Date when restrictions will be lifted for the user; Unix time. If user is restricted for more than 366 days or less than 30 seconds from the current time, they are considered to be restricted forever
     * */
    @Serializable
    data class RestrictChatMemberRequest(
        val chat_id: ChatId,
        val user_id: UserId,
        val permissions: ChatPermissions,
        val use_independent_chat_permissions: Boolean? = null,
        val until_date: Long? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("restrictChatMember"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

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
     * @property can_edit_stories Pass <em>True</em> if the administrator can edit stories posted by other users, post stories to the chat page, pin chat stories, and access the chat's story archive
     * @property can_delete_stories Pass <em>True</em> if the administrator can delete stories posted by other users
     * @property can_post_messages Pass <em>True</em> if the administrator can post messages in the channel, or access channel statistics; for channels only
     * @property can_edit_messages Pass <em>True</em> if the administrator can edit messages of other users and can pin messages; for channels only
     * @property can_pin_messages Pass <em>True</em> if the administrator can pin messages; for supergroups only
     * @property can_manage_topics Pass <em>True</em> if the user is allowed to create, rename, close, and reopen forum topics; for supergroups only
     * */
    @Serializable
    data class PromoteChatMemberRequest(
        val chat_id: ChatId,
        val user_id: UserId,
        val is_anonymous: Boolean? = null,
        val can_manage_chat: Boolean? = null,
        val can_delete_messages: Boolean? = null,
        val can_manage_video_chats: Boolean? = null,
        val can_restrict_members: Boolean? = null,
        val can_promote_members: Boolean? = null,
        val can_change_info: Boolean? = null,
        val can_invite_users: Boolean? = null,
        val can_post_stories: Boolean? = null,
        val can_edit_stories: Boolean? = null,
        val can_delete_stories: Boolean? = null,
        val can_post_messages: Boolean? = null,
        val can_edit_messages: Boolean? = null,
        val can_pin_messages: Boolean? = null,
        val can_manage_topics: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("promoteChatMember"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set a custom title for an administrator in a supergroup promoted by the bot. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property user_id Unique identifier of the target user
     * @property custom_title New custom title for the administrator; 0-16 characters, emoji are not allowed
     * */
    @Serializable
    data class SetChatAdministratorCustomTitleRequest(
        val chat_id: ChatId,
        val user_id: UserId,
        val custom_title: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setChatAdministratorCustomTitle"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to ban a channel chat in a supergroup or a channel. Until the chat is <a href="#unbanchatsenderchat">unbanned</a>, the owner of the banned chat won't be able to send messages on behalf of <strong>any of their channels</strong>. The bot must be an administrator in the supergroup or channel for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property sender_chat_id Unique identifier of the target sender chat
     * */
    @Serializable
    data class BanChatSenderChatRequest(
        val chat_id: ChatId,
        val sender_chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("banChatSenderChat"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to unban a previously banned channel chat in a supergroup or channel. The bot must be an administrator for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property sender_chat_id Unique identifier of the target sender chat
     * */
    @Serializable
    data class UnbanChatSenderChatRequest(
        val chat_id: ChatId,
        val sender_chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("unbanChatSenderChat"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set default chat permissions for all members. The bot must be an administrator in the group or a supergroup for this to work and must have the <em>can_restrict_members</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property permissions A JSON-serialized object for new default chat permissions
     * @property use_independent_chat_permissions Pass <em>True</em> if chat permissions are set independently. Otherwise, the <em>can_send_other_messages</em> and <em>can_add_web_page_previews</em> permissions will imply the <em>can_send_messages</em>, <em>can_send_audios</em>, <em>can_send_documents</em>, <em>can_send_photos</em>, <em>can_send_videos</em>, <em>can_send_video_notes</em>, and <em>can_send_voice_notes</em> permissions; the <em>can_send_polls</em> permission will imply the <em>can_send_messages</em> permission.
     * */
    @Serializable
    data class SetChatPermissionsRequest(
        val chat_id: ChatId,
        val permissions: ChatPermissions,
        val use_independent_chat_permissions: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setChatPermissions"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to generate a new primary invite link for a chat; any previously generated primary link is revoked. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns the new invite link as <em>String</em> on success.</p><blockquote>
     *  <p>Note: Each administrator in a chat generates their own invite links. Bots can't use invite links generated by other administrators. If you want your bot to work with invite links, it will need to generate its own link using <a href="#exportchatinvitelink">exportChatInviteLink</a> or by calling the <a href="#getchat">getChat</a> method. If your bot needs to generate a new primary invite link replacing its previous one, use <a href="#exportchatinvitelink">exportChatInviteLink</a> again.</p>
     * </blockquote>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class ExportChatInviteLinkRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("exportChatInviteLink"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to create an additional invite link for a chat. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. The link can be revoked using the method <a href="#revokechatinvitelink">revokeChatInviteLink</a>. Returns the new invite link as <a href="#chatinvitelink">ChatInviteLink</a> object.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property name Invite link name; 0-32 characters
     * @property expire_date Point in time (Unix timestamp) when the link will expire
     * @property member_limit The maximum number of users that can be members of the chat simultaneously after joining the chat via this invite link; 1-99999
     * @property creates_join_request <em>True</em>, if users joining the chat via the link need to be approved by chat administrators. If <em>True</em>, <em>member_limit</em> can't be specified
     * */
    @Serializable
    data class CreateChatInviteLinkRequest(
        val chat_id: ChatId,
        val name: String? = null,
        val expire_date: Long? = null,
        val member_limit: Long? = null,
        val creates_join_request: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("createChatInviteLink"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit a non-primary invite link created by the bot. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns the edited invite link as a <a href="#chatinvitelink">ChatInviteLink</a> object.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property invite_link The invite link to edit
     * @property name Invite link name; 0-32 characters
     * @property expire_date Point in time (Unix timestamp) when the link will expire
     * @property member_limit The maximum number of users that can be members of the chat simultaneously after joining the chat via this invite link; 1-99999
     * @property creates_join_request <em>True</em>, if users joining the chat via the link need to be approved by chat administrators. If <em>True</em>, <em>member_limit</em> can't be specified
     * */
    @Serializable
    data class EditChatInviteLinkRequest(
        val chat_id: ChatId,
        val invite_link: String,
        val name: String? = null,
        val expire_date: Long? = null,
        val member_limit: Long? = null,
        val creates_join_request: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("editChatInviteLink"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to revoke an invite link created by the bot. If the primary link is revoked, a new link is automatically generated. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns the revoked invite link as <a href="#chatinvitelink">ChatInviteLink</a> object.</p>
     *
     * @property chat_id Unique identifier of the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property invite_link The invite link to revoke
     * */
    @Serializable
    data class RevokeChatInviteLinkRequest(
        val chat_id: ChatId,
        val invite_link: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("revokeChatInviteLink"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to approve a chat join request. The bot must be an administrator in the chat for this to work and must have the <em>can_invite_users</em> administrator right. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * */
    @Serializable
    data class ApproveChatJoinRequestRequest(
        val chat_id: ChatId,
        val user_id: UserId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("approveChatJoinRequest"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to decline a chat join request. The bot must be an administrator in the chat for this to work and must have the <em>can_invite_users</em> administrator right. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * */
    @Serializable
    data class DeclineChatJoinRequestRequest(
        val chat_id: ChatId,
        val user_id: UserId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("declineChatJoinRequest"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set a new profile photo for the chat. Photos can't be changed for private chats. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property photo New chat photo, uploaded using multipart/form-data
     * */
    @Serializable
    data class SetChatPhotoRequest(
        val chat_id: ChatId,
        val photo: @Contextual Any,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setChatPhoto"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete a chat photo. Photos can't be changed for private chats. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class DeleteChatPhotoRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteChatPhoto"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the title of a chat. Titles can't be changed for private chats. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property title New chat title, 1-128 characters
     * */
    @Serializable
    data class SetChatTitleRequest(
        val chat_id: ChatId,
        val title: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setChatTitle"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the description of a group, a supergroup or a channel. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property description New chat description, 0-255 characters
     * */
    @Serializable
    data class SetChatDescriptionRequest(
        val chat_id: ChatId,
        val description: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setChatDescription"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to add a message to the list of pinned messages in a chat. If the chat is not a private chat, the bot must be an administrator in the chat for this to work and must have the 'can_pin_messages' administrator right in a supergroup or 'can_edit_messages' administrator right in a channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of a message to pin
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be pinned
     * @property disable_notification Pass <em>True</em> if it is not necessary to send a notification to all chat members about the new pinned message. Notifications are always disabled in channels and private chats.
     * */
    @Serializable
    data class PinChatMessageRequest(
        val chat_id: ChatId,
        val message_id: MessageId,
        val business_connection_id: BusinessConnectionId? = null,
        val disable_notification: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("pinChatMessage"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to remove a message from the list of pinned messages in a chat. If the chat is not a private chat, the bot must be an administrator in the chat for this to work and must have the 'can_pin_messages' administrator right in a supergroup or 'can_edit_messages' administrator right in a channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be unpinned
     * @property message_id Identifier of the message to unpin. Required if <em>business_connection_id</em> is specified. If not specified, the most recent pinned message (by sending date) will be unpinned.
     * */
    @Serializable
    data class UnpinChatMessageRequest(
        val chat_id: ChatId,
        val business_connection_id: BusinessConnectionId? = null,
        val message_id: MessageId? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("unpinChatMessage"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to clear the list of pinned messages in a chat. If the chat is not a private chat, the bot must be an administrator in the chat for this to work and must have the 'can_pin_messages' administrator right in a supergroup or 'can_edit_messages' administrator right in a channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class UnpinAllChatMessagesRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("unpinAllChatMessages"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method for your bot to leave a group, supergroup or channel. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class LeaveChatRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("leaveChat"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get up-to-date information about the chat. Returns a <a href="#chatfullinfo">ChatFullInfo</a> object on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class GetChatRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getChat"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get a list of administrators in a chat, which aren't bots. Returns an Array of <a href="#chatmember">ChatMember</a> objects.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class GetChatAdministratorsRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("getChatAdministrators"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the number of members in a chat. Returns <em>Int</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * */
    @Serializable
    data class GetChatMemberCountRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getChatMemberCount"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get information about a member of a chat. The method is only guaranteed to work for other users if the bot is an administrator in the chat. Returns a <a href="#chatmember">ChatMember</a> object on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup or channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * */
    @Serializable
    data class GetChatMemberRequest(
        val chat_id: ChatId,
        val user_id: UserId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getChatMember"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set a new group sticker set for a supergroup. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Use the field <em>can_set_sticker_set</em> optionally returned in <a href="#getchat">getChat</a> requests to check if the bot can use this method. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property sticker_set_name Name of the sticker set to be set as the group sticker set
     * */
    @Serializable
    data class SetChatStickerSetRequest(
        val chat_id: ChatId,
        val sticker_set_name: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setChatStickerSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete a group sticker set from a supergroup. The bot must be an administrator in the chat for this to work and must have the appropriate administrator rights. Use the field <em>can_set_sticker_set</em> optionally returned in <a href="#getchat">getChat</a> requests to check if the bot can use this method. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * */
    @Serializable
    data class DeleteChatStickerSetRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("deleteChatStickerSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to create a topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. Returns information about the created topic as a <a href="#forumtopic">ForumTopic</a> object.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property name Topic name, 1-128 characters
     * @property icon_color Color of the topic icon in RGB format. Currently, must be one of 7322096 (0x6FB9F0), 16766590 (0xFFD67E), 13338331 (0xCB86DB), 9367192 (0x8EEE98), 16749490 (0xFF93B2), or 16478047 (0xFB6F5F)
     * @property icon_custom_emoji_id Unique identifier of the custom emoji shown as the topic icon. Use <a href="#getforumtopiciconstickers">getForumTopicIconStickers</a> to get all allowed custom emoji identifiers.
     * */
    @Serializable
    data class CreateForumTopicRequest(
        val chat_id: ChatId,
        val name: String,
        val icon_color: Long? = null,
        val icon_custom_emoji_id: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("createForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit name and icon of a topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have <em>can_manage_topics</em> administrator rights, unless it is the creator of the topic. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     * @property name New topic name, 0-128 characters. If not specified or empty, the current name of the topic will be kept
     * @property icon_custom_emoji_id New unique identifier of the custom emoji shown as the topic icon. Use <a href="#getforumtopiciconstickers">getForumTopicIconStickers</a> to get all allowed custom emoji identifiers. Pass an empty string to remove the icon. If not specified, the current icon will be kept
     * */
    @Serializable
    data class EditForumTopicRequest(
        val chat_id: ChatId,
        val message_thread_id: MessageThreadId,
        val name: String? = null,
        val icon_custom_emoji_id: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("editForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to close an open topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights, unless it is the creator of the topic. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     * */
    @Serializable
    data class CloseForumTopicRequest(
        val chat_id: ChatId,
        val message_thread_id: MessageThreadId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("closeForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to reopen a closed topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights, unless it is the creator of the topic. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     * */
    @Serializable
    data class ReopenForumTopicRequest(
        val chat_id: ChatId,
        val message_thread_id: MessageThreadId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("reopenForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete a forum topic along with all its messages in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_delete_messages</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     * */
    @Serializable
    data class DeleteForumTopicRequest(
        val chat_id: ChatId,
        val message_thread_id: MessageThreadId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to clear the list of pinned messages in a forum topic. The bot must be an administrator in the chat for this to work and must have the <em>can_pin_messages</em> administrator right in the supergroup. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property message_thread_id Unique identifier for the target message thread of the forum topic
     * */
    @Serializable
    data class UnpinAllForumTopicMessagesRequest(
        val chat_id: ChatId,
        val message_thread_id: MessageThreadId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("unpinAllForumTopicMessages"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit the name of the 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have <em>can_manage_topics</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * @property name New topic name, 1-128 characters
     * */
    @Serializable
    data class EditGeneralForumTopicRequest(
        val chat_id: ChatId,
        val name: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("editGeneralForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to close an open 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * */
    @Serializable
    data class CloseGeneralForumTopicRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("closeGeneralForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to reopen a closed 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. The topic will be automatically unhidden if it was hidden. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * */
    @Serializable
    data class ReopenGeneralForumTopicRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("reopenGeneralForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to hide the 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. The topic will be automatically closed if it was open. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * */
    @Serializable
    data class HideGeneralForumTopicRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("hideGeneralForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to unhide the 'General' topic in a forum supergroup chat. The bot must be an administrator in the chat for this to work and must have the <em>can_manage_topics</em> administrator rights. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * */
    @Serializable
    data class UnhideGeneralForumTopicRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("unhideGeneralForumTopic"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to clear the list of pinned messages in a General forum topic. The bot must be an administrator in the chat for this to work and must have the <em>can_pin_messages</em> administrator right in the supergroup. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target supergroup (in the format <code>@supergroupusername</code>)
     * */
    @Serializable
    data class UnpinAllGeneralForumTopicMessagesRequest(
        val chat_id: ChatId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("unpinAllGeneralForumTopicMessages"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

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
     * */
    @Serializable
    data class AnswerCallbackQueryRequest(
        val callback_query_id: String,
        val text: String? = null,
        val show_alert: Boolean? = null,
        val url: String? = null,
        val cache_time: Long? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("answerCallbackQuery"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the list of boosts added to a chat by a user. Requires administrator rights in the chat. Returns a <a href="#userchatboosts">UserChatBoosts</a> object.</p>
     *
     * @property chat_id Unique identifier for the chat or username of the channel (in the format <code>@channelusername</code>)
     * @property user_id Unique identifier of the target user
     * */
    @Serializable
    data class GetUserChatBoostsRequest(
        val chat_id: ChatId,
        val user_id: UserId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getUserChatBoosts"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get information about the connection of the bot with a business account. Returns a <a href="#businessconnection">BusinessConnection</a> object on success.</p>
     *
     * @property business_connection_id Unique identifier of the business connection
     * */
    @Serializable
    data class GetBusinessConnectionRequest(
        val business_connection_id: BusinessConnectionId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("getBusinessConnection"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the list of the bot's commands. See <a href="/bots/features#commands">this manual</a> for more details about bot commands. Returns <em>True</em> on success.</p>
     *
     * @property commands A JSON-serialized list of bot commands to be set as the list of the bot's commands. At most 100 commands can be specified.
     * @property scope A JSON-serialized object, describing scope of users for which the commands are relevant. Defaults to <a href="#botcommandscopedefault">BotCommandScopeDefault</a>.
     * @property language_code A two-letter ISO 639-1 language code. If empty, commands will be applied to all users from the given scope, for whose language there are no dedicated commands
     * */
    @Serializable
    data class SetMyCommandsRequest(
        val commands: List<BotCommand>,
        val scope: @Contextual BotCommandScope? = null,
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setMyCommands"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete the list of the bot's commands for the given scope and user language. After deletion, <a href="#determining-list-of-commands">higher level commands</a> will be shown to affected users. Returns <em>True</em> on success.</p>
     *
     * @property scope A JSON-serialized object, describing scope of users for which the commands are relevant. Defaults to <a href="#botcommandscopedefault">BotCommandScopeDefault</a>.
     * @property language_code A two-letter ISO 639-1 language code. If empty, commands will be applied to all users from the given scope, for whose language there are no dedicated commands
     * */
    @Serializable
    data class DeleteMyCommandsRequest(
        val scope: @Contextual BotCommandScope? = null,
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteMyCommands"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the current list of the bot's commands for the given scope and user language. Returns an Array of <a href="#botcommand">BotCommand</a> objects. If commands aren't set, an empty list is returned.</p>
     *
     * @property scope A JSON-serialized object, describing scope of users. Defaults to <a href="#botcommandscopedefault">BotCommandScopeDefault</a>.
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     * */
    @Serializable
    data class GetMyCommandsRequest(
        val scope: @Contextual BotCommandScope? = null,
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getMyCommands"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the bot's name. Returns <em>True</em> on success.</p>
     *
     * @property name New bot name; 0-64 characters. Pass an empty string to remove the dedicated name for the given language.
     * @property language_code A two-letter ISO 639-1 language code. If empty, the name will be shown to all users for whose language there is no dedicated name.
     * */
    @Serializable
    data class SetMyNameRequest(
        val name: String? = null,
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setMyName"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the current bot name for the given user language. Returns <a href="#botname">BotName</a> on success.</p>
     *
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     * */
    @Serializable
    data class GetMyNameRequest(
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getMyName"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the bot's description, which is shown in the chat with the bot if the chat is empty. Returns <em>True</em> on success.</p>
     *
     * @property description New bot description; 0-512 characters. Pass an empty string to remove the dedicated description for the given language.
     * @property language_code A two-letter ISO 639-1 language code. If empty, the description will be applied to all users for whose language there is no dedicated description.
     * */
    @Serializable
    data class SetMyDescriptionRequest(
        val description: String? = null,
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setMyDescription"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the current bot description for the given user language. Returns <a href="#botdescription">BotDescription</a> on success.</p>
     *
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     * */
    @Serializable
    data class GetMyDescriptionRequest(
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getMyDescription"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the bot's short description, which is shown on the bot's profile page and is sent together with the link when users share the bot. Returns <em>True</em> on success.</p>
     *
     * @property short_description New short description for the bot; 0-120 characters. Pass an empty string to remove the dedicated short description for the given language.
     * @property language_code A two-letter ISO 639-1 language code. If empty, the short description will be applied to all users for whose language there is no dedicated short description.
     * */
    @Serializable
    data class SetMyShortDescriptionRequest(
        val short_description: String? = null,
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setMyShortDescription"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the current bot short description for the given user language. Returns <a href="#botshortdescription">BotShortDescription</a> on success.</p>
     *
     * @property language_code A two-letter ISO 639-1 language code or an empty string
     * */
    @Serializable
    data class GetMyShortDescriptionRequest(
        val language_code: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("getMyShortDescription"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the bot's menu button in a private chat, or the default menu button. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target private chat. If not specified, default bot's menu button will be changed
     * @property menu_button A JSON-serialized object for the bot's new menu button. Defaults to <a href="#menubuttondefault">MenuButtonDefault</a>
     * */
    @Serializable
    data class SetChatMenuButtonRequest(
        val chat_id: ChatId? = null,
        val menu_button: @Contextual MenuButton? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setChatMenuButton"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the current value of the bot's menu button in a private chat, or the default menu button. Returns <a href="#menubutton">MenuButton</a> on success.</p>
     *
     * @property chat_id Unique identifier for the target private chat. If not specified, default bot's menu button will be returned
     * */
    @Serializable
    data class GetChatMenuButtonRequest(
        val chat_id: ChatId? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getChatMenuButton"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the default administrator rights requested by the bot when it's added as an administrator to groups or channels. These rights will be suggested to users, but they are free to modify the list before adding the bot. Returns <em>True</em> on success.</p>
     *
     * @property rights A JSON-serialized object describing new default administrator rights. If not specified, the default administrator rights will be cleared.
     * @property for_channels Pass <em>True</em> to change the default administrator rights of the bot in channels. Otherwise, the default administrator rights of the bot for groups and supergroups will be changed.
     * */
    @Serializable
    data class SetMyDefaultAdministratorRightsRequest(
        val rights: ChatAdministratorRights? = null,
        val for_channels: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setMyDefaultAdministratorRights"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get the current default administrator rights of the bot. Returns <a href="#chatadministratorrights">ChatAdministratorRights</a> on success.</p>
     *
     * @property for_channels Pass <em>True</em> to get default administrator rights of the bot in channels. Otherwise, default administrator rights of the bot for groups and supergroups will be returned.
     * */
    @Serializable
    data class GetMyDefaultAdministratorRightsRequest(
        val for_channels: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("getMyDefaultAdministratorRights"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


// Updating messages

    /**
     * <p>Use this method to edit text and <a href="#games">game</a> messages. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned. Note that business messages that were not sent by the bot and do not contain an inline keyboard can only be edited within <strong>48 hours</strong> from the time they were sent.</p>
     *
     * @property text New text of the message, 1-4096 characters after entities parsing
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property parse_mode Mode for parsing entities in the message text. See <a href="#formatting-options">formatting options</a> for more details.
     * @property entities A JSON-serialized list of special entities that appear in message text, which can be specified instead of <em>parse_mode</em>
     * @property link_preview_options Link preview generation options for the message
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class EditMessageTextRequest(
        val text: String,
        val business_connection_id: BusinessConnectionId? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
        val parse_mode: ParseMode? = null,
        val entities: List<MessageEntity>? = null,
        val link_preview_options: LinkPreviewOptions? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("editMessageText"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit captions of messages. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned. Note that business messages that were not sent by the bot and do not contain an inline keyboard can only be edited within <strong>48 hours</strong> from the time they were sent.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property caption New caption of the message, 0-1024 characters after entities parsing
     * @property parse_mode Mode for parsing entities in the message caption. See <a href="#formatting-options">formatting options</a> for more details.
     * @property caption_entities A JSON-serialized list of special entities that appear in the caption, which can be specified instead of <em>parse_mode</em>
     * @property show_caption_above_media Pass <em>True</em>, if the caption must be shown above the message media. Supported only for animation, photo and video messages.
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class EditMessageCaptionRequest(
        val business_connection_id: BusinessConnectionId? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
        val caption: String? = null,
        val parse_mode: ParseMode? = null,
        val caption_entities: List<MessageEntity>? = null,
        val show_caption_above_media: Boolean? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("editMessageCaption"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit animation, audio, document, photo, or video messages. If a message is part of a message album, then it can be edited only to an audio for audio albums, only to a document for document albums and to a photo or a video otherwise. When an inline message is edited, a new file can't be uploaded; use a previously uploaded file via its file_id or specify a URL. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned. Note that business messages that were not sent by the bot and do not contain an inline keyboard can only be edited within <strong>48 hours</strong> from the time they were sent.</p>
     *
     * @property media A JSON-serialized object for a new media content of the message
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property reply_markup A JSON-serialized object for a new <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class EditMessageMediaRequest(
        val media: @Contextual InputMedia,
        val business_connection_id: BusinessConnectionId? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("editMessageMedia"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit live location messages. A location can be edited until its <em>live_period</em> expires or editing is explicitly disabled by a call to <a href="#stopmessagelivelocation">stopMessageLiveLocation</a>. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property latitude Latitude of new location
     * @property longitude Longitude of new location
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property live_period New period in seconds during which the location can be updated, starting from the message send date. If 0x7FFFFFFF is specified, then the location can be updated forever. Otherwise, the new value must not exceed the current <em>live_period</em> by more than a day, and the live location expiration date must remain within the next 90 days. If not specified, then <em>live_period</em> remains unchanged
     * @property horizontal_accuracy The radius of uncertainty for the location, measured in meters; 0-1500
     * @property heading Direction in which the user is moving, in degrees. Must be between 1 and 360 if specified.
     * @property proximity_alert_radius The maximum distance for proximity alerts about approaching another chat member, in meters. Must be between 1 and 100000 if specified.
     * @property reply_markup A JSON-serialized object for a new <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class EditMessageLiveLocationRequest(
        val latitude: Float,
        val longitude: Float,
        val business_connection_id: BusinessConnectionId? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
        val live_period: Long? = null,
        val horizontal_accuracy: Float? = null,
        val heading: Long? = null,
        val proximity_alert_radius: Long? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("editMessageLiveLocation"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to stop updating a live location message before <em>live_period</em> expires. On success, if the message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message with live location to stop
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property reply_markup A JSON-serialized object for a new <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class StopMessageLiveLocationRequest(
        val business_connection_id: BusinessConnectionId? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("stopMessageLiveLocation"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to edit only the reply markup of messages. On success, if the edited message is not an inline message, the edited <a href="#message">Message</a> is returned, otherwise <em>True</em> is returned. Note that business messages that were not sent by the bot and do not contain an inline keyboard can only be edited within <strong>48 hours</strong> from the time they were sent.</p>
     *
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the message to edit
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class EditMessageReplyMarkupRequest(
        val business_connection_id: BusinessConnectionId? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("editMessageReplyMarkup"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to stop a poll which was sent by the bot. On success, the stopped <a href="#poll">Poll</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of the original message with the poll
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message to be edited was sent
     * @property reply_markup A JSON-serialized object for a new message <a href="/bots/features#inline-keyboards">inline keyboard</a>.
     * */
    @Serializable
    data class StopPollRequest(
        val chat_id: ChatId,
        val message_id: MessageId,
        val business_connection_id: BusinessConnectionId? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("stopPoll"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete a message, including service messages, with the following limitations:<br>- A message can only be deleted if it was sent less than 48 hours ago.<br>- Service messages about a supergroup, channel, or forum topic creation can't be deleted.<br>- A dice message in a private chat can only be deleted if it was sent more than 24 hours ago.<br>- Bots can delete outgoing messages in private chats, groups, and supergroups.<br>- Bots can delete incoming messages in private chats.<br>- Bots granted <em>can_post_messages</em> permissions can delete outgoing messages in channels.<br>- If the bot is an administrator of a group, it can delete any message there.<br>- If the bot has <em>can_delete_messages</em> permission in a supergroup or a channel, it can delete any message there.<br>Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_id Identifier of the message to delete
     * */
    @Serializable
    data class DeleteMessageRequest(
        val chat_id: ChatId,
        val message_id: MessageId,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteMessage"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete multiple messages simultaneously. If some of the specified messages can't be found, they are skipped. Returns <em>True</em> on success.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property message_ids A JSON-serialized list of 1-100 identifiers of messages to delete. See <a href="#deletemessage">deleteMessage</a> for limitations on which messages can be deleted
     * */
    @Serializable
    data class DeleteMessagesRequest(
        val chat_id: ChatId,
        val message_ids: List<MessageId>,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteMessages"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


// Stickers

    /**
     * <p>Use this method to send static .WEBP, <a href="https://telegram.org/blog/animated-stickers">animated</a> .TGS, or <a href="https://telegram.org/blog/video-stickers-better-reactions">video</a> .WEBM stickers. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property sticker Sticker to send. Pass a file_id as String to send a file that exists on the Telegram servers (recommended), pass an HTTP URL as a String for Telegram to get a .WEBP sticker from the Internet, or upload a new .WEBP, .TGS, or .WEBM sticker using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>. Video and animated stickers can't be sent via an HTTP URL.
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property emoji Emoji associated with the sticker; only for just uploaded stickers
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup Additional interface options. A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>, <a href="/bots/features#keyboards">custom reply keyboard</a>, instructions to remove a reply keyboard or to force a reply from the user
     * */
    @Serializable
    data class SendStickerRequest(
        val chat_id: ChatId,
        val sticker: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val emoji: String? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: @Contextual KeyboardOption? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendSticker"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get a sticker set. On success, a <a href="#stickerset">StickerSet</a> object is returned.</p>
     *
     * @property name Name of the sticker set
     * */
    @Serializable
    data class GetStickerSetRequest(
        val name: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getStickerSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get information about custom emoji stickers by their identifiers. Returns an Array of <a href="#sticker">Sticker</a> objects.</p>
     *
     * @property custom_emoji_ids A JSON-serialized list of custom emoji identifiers. At most 200 custom emoji identifiers can be specified.
     * */
    @Serializable
    data class GetCustomEmojiStickersRequest(
        val custom_emoji_ids: List<String>,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("getCustomEmojiStickers"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to upload a file with a sticker for later use in the <a href="#createnewstickerset">createNewStickerSet</a>, <a href="#addstickertoset">addStickerToSet</a>, or <a href="#replacestickerinset">replaceStickerInSet</a> methods (the file can be used multiple times). Returns the uploaded <a href="#file">File</a> on success.</p>
     *
     * @property user_id User identifier of sticker file owner
     * @property sticker A file with the sticker in .WEBP, .PNG, .TGS, or .WEBM format. See <a href="/stickers"></a><a href="https://core.telegram.org/stickers">https://core.telegram.org/stickers</a> for technical requirements. <a href="#sending-files">More information on Sending Files »</a>
     * @property sticker_format Format of the sticker, must be one of “static”, “animated”, “video”
     * */
    @Serializable
    data class UploadStickerFileRequest(
        val user_id: UserId,
        val sticker: @Contextual Any,
        val sticker_format: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("uploadStickerFile"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to create a new sticker set owned by a user. The bot will be able to edit the sticker set thus created. Returns <em>True</em> on success.</p>
     *
     * @property user_id User identifier of created sticker set owner
     * @property name Short name of sticker set, to be used in <code>t.me/addstickers/</code> URLs (e.g., <em>animals</em>). Can contain only English letters, digits and underscores. Must begin with a letter, can't contain consecutive underscores and must end in <code>"_by_&lt;bot_username&gt;"</code>. <code>&lt;bot_username&gt;</code> is case insensitive. 1-64 characters.
     * @property title Sticker set title, 1-64 characters
     * @property stickers A JSON-serialized list of 1-50 initial stickers to be added to the sticker set
     * @property sticker_type Type of stickers in the set, pass “regular”, “mask”, or “custom_emoji”. By default, a regular sticker set is created.
     * @property needs_repainting Pass <em>True</em> if stickers in the sticker set must be repainted to the color of text when used in messages, the accent color if used as emoji status, white on chat photos, or another appropriate color based on context; for custom emoji sticker sets only
     * */
    @Serializable
    data class CreateNewStickerSetRequest(
        val user_id: UserId,
        val name: String,
        val title: String,
        val stickers: List<InputSticker>,
        val sticker_type: String? = null,
        val needs_repainting: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("createNewStickerSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to add a new sticker to a set created by the bot. Emoji sticker sets can have up to 200 stickers. Other sticker sets can have up to 120 stickers. Returns <em>True</em> on success.</p>
     *
     * @property user_id User identifier of sticker set owner
     * @property name Sticker set name
     * @property sticker A JSON-serialized object with information about the added sticker. If exactly the same sticker had already been added to the set, then the set isn't changed.
     * */
    @Serializable
    data class AddStickerToSetRequest(
        val user_id: UserId,
        val name: String,
        val sticker: InputSticker,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("addStickerToSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to move a sticker in a set created by the bot to a specific position. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property position New sticker position in the set, zero-based
     * */
    @Serializable
    data class SetStickerPositionInSetRequest(
        val sticker: String,
        val position: Long,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setStickerPositionInSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete a sticker from a set created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * */
    @Serializable
    data class DeleteStickerFromSetRequest(
        val sticker: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("deleteStickerFromSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to replace an existing sticker in a sticker set with a new one. The method is equivalent to calling <a href="#deletestickerfromset">deleteStickerFromSet</a>, then <a href="#addstickertoset">addStickerToSet</a>, then <a href="#setstickerpositioninset">setStickerPositionInSet</a>. Returns <em>True</em> on success.</p>
     *
     * @property user_id User identifier of the sticker set owner
     * @property name Sticker set name
     * @property old_sticker File identifier of the replaced sticker
     * @property sticker A JSON-serialized object with information about the added sticker. If exactly the same sticker had already been added to the set, then the set remains unchanged.
     * */
    @Serializable
    data class ReplaceStickerInSetRequest(
        val user_id: UserId,
        val name: String,
        val old_sticker: String,
        val sticker: InputSticker,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("replaceStickerInSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the list of emoji assigned to a regular or custom emoji sticker. The sticker must belong to a sticker set created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property emoji_list A JSON-serialized list of 1-20 emoji associated with the sticker
     * */
    @Serializable
    data class SetStickerEmojiListRequest(
        val sticker: String,
        val emoji_list: List<String>,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setStickerEmojiList"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change search keywords assigned to a regular or custom emoji sticker. The sticker must belong to a sticker set created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property keywords A JSON-serialized list of 0-20 search keywords for the sticker with total length of up to 64 characters
     * */
    @Serializable
    data class SetStickerKeywordsRequest(
        val sticker: String,
        val keywords: List<String>? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setStickerKeywords"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to change the <a href="#maskposition">mask position</a> of a mask sticker. The sticker must belong to a sticker set that was created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property sticker File identifier of the sticker
     * @property mask_position A JSON-serialized object with the position where the mask should be placed on faces. Omit the parameter to remove the mask position.
     * */
    @Serializable
    data class SetStickerMaskPositionRequest(
        val sticker: String,
        val mask_position: MaskPosition? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setStickerMaskPosition"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set the title of a created sticker set. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * @property title Sticker set title, 1-64 characters
     * */
    @Serializable
    data class SetStickerSetTitleRequest(
        val name: String,
        val title: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setStickerSetTitle"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set the thumbnail of a regular or mask sticker set. The format of the thumbnail file must match the format of the stickers in the set. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * @property user_id User identifier of the sticker set owner
     * @property format Format of the thumbnail, must be one of “static” for a <strong>.WEBP</strong> or <strong>.PNG</strong> image, “animated” for a <strong>.TGS</strong> animation, or “video” for a <strong>WEBM</strong> video
     * @property thumbnail A <strong>.WEBP</strong> or <strong>.PNG</strong> image with the thumbnail, must be up to 128 kilobytes in size and have a width and height of exactly 100px, or a <strong>.TGS</strong> animation with a thumbnail up to 32 kilobytes in size (see <a href="/stickers#animation-requirements"></a><a href="https://core.telegram.org/stickers#animation-requirements">https://core.telegram.org/stickers#animation-requirements</a> for animated sticker technical requirements), or a <strong>WEBM</strong> video with the thumbnail up to 32 kilobytes in size; see <a href="/stickers#video-requirements"></a><a href="https://core.telegram.org/stickers#video-requirements">https://core.telegram.org/stickers#video-requirements</a> for video sticker technical requirements. Pass a <em>file_id</em> as a String to send a file that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. <a href="#sending-files">More information on Sending Files »</a>. Animated and video sticker set thumbnails can't be uploaded via HTTP URL. If omitted, then the thumbnail is dropped and the first sticker is used as the thumbnail.
     * */
    @Serializable
    data class SetStickerSetThumbnailRequest(
        val name: String,
        val user_id: UserId,
        val format: String,
        val thumbnail: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setStickerSetThumbnail"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set the thumbnail of a custom emoji sticker set. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * @property custom_emoji_id Custom emoji identifier of a sticker from the sticker set; pass an empty string to drop the thumbnail and use the first sticker as the thumbnail.
     * */
    @Serializable
    data class SetCustomEmojiStickerSetThumbnailRequest(
        val name: String,
        val custom_emoji_id: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setCustomEmojiStickerSetThumbnail"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to delete a sticker set that was created by the bot. Returns <em>True</em> on success.</p>
     *
     * @property name Sticker set name
     * */
    @Serializable
    data class DeleteStickerSetRequest(
        val name: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("deleteStickerSet"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


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
     * */
    @Serializable
    data class AnswerInlineQueryRequest(
        val inline_query_id: String,
        val results: List<@Contextual InlineQueryResult>,
        val cache_time: Long? = null,
        val is_personal: Boolean? = null,
        val next_offset: String? = null,
        val button: InlineQueryResultsButton? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("answerInlineQuery"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to set the result of an interaction with a <a href="/bots/webapps">Web App</a> and send a corresponding message on behalf of the user to the chat from which the query originated. On success, a <a href="#sentwebappmessage">SentWebAppMessage</a> object is returned.</p>
     *
     * @property web_app_query_id Unique identifier for the query to be answered
     * @property result A JSON-serialized object describing the message to be sent
     * */
    @Serializable
    data class AnswerWebAppQueryRequest(
        val web_app_query_id: String,
        val result: @Contextual InlineQueryResult,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("answerWebAppQuery"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


// Payments

    /**
     * <p>Use this method to send invoices. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat or username of the target channel (in the format <code>@channelusername</code>)
     * @property title Product name, 1-32 characters
     * @property description Product description, 1-255 characters
     * @property payload Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
     * @property currency Three-letter ISO 4217 currency code, see <a href="/bots/payments#supported-currencies">more on currencies</a>. Pass “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property prices Price breakdown, a JSON-serialized list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.). Must contain exactly one item for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property provider_token Payment provider token, obtained via <a href="https://t.me/botfather">@BotFather</a>. Pass an empty string for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property max_tip_amount The maximum accepted amount for tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a maximum tip of <code>US$ 1.45</code> pass <code>max_tip_amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies). Defaults to 0. Not supported for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property suggested_tip_amounts A JSON-serialized array of suggested amounts of tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). At most 4 suggested tip amounts can be specified. The suggested tip amounts must be positive, passed in a strictly increased order and must not exceed <em>max_tip_amount</em>.
     * @property start_parameter Unique deep-linking parameter. If left empty, <strong>forwarded copies</strong> of the sent message will have a <em>Pay</em> button, allowing multiple users to pay directly from the forwarded message, using the same invoice. If non-empty, forwarded copies of the sent message will have a <em>URL</em> button with a deep link to the bot (instead of a <em>Pay</em> button), with the value used as the start parameter
     * @property provider_data JSON-serialized data about the invoice, which will be shared with the payment provider. A detailed description of required fields should be provided by the payment provider.
     * @property photo_url URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service. People like it better when they see what they are paying for.
     * @property photo_size Photo size in bytes
     * @property photo_width Photo width
     * @property photo_height Photo height
     * @property need_name Pass <em>True</em> if you require the user's full name to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property need_phone_number Pass <em>True</em> if you require the user's phone number to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property need_email Pass <em>True</em> if you require the user's email address to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property need_shipping_address Pass <em>True</em> if you require the user's shipping address to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property send_phone_number_to_provider Pass <em>True</em> if the user's phone number should be sent to the provider. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property send_email_to_provider Pass <em>True</em> if the user's email address should be sent to the provider. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property is_flexible Pass <em>True</em> if the final price depends on the shipping method. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>. If empty, one 'Pay <code>total price</code>' button will be shown. If not empty, the first button must be a Pay button.
     * */
    @Serializable
    data class SendInvoiceRequest(
        val chat_id: ChatId,
        val title: String,
        val description: String,
        val payload: String,
        val currency: String,
        val prices: List<LabeledPrice>,
        val message_thread_id: MessageThreadId? = null,
        val provider_token: String? = null,
        val max_tip_amount: Long? = null,
        val suggested_tip_amounts: List<Long>? = null,
        val start_parameter: String? = null,
        val provider_data: String? = null,
        val photo_url: String? = null,
        val photo_size: Long? = null,
        val photo_width: Long? = null,
        val photo_height: Long? = null,
        val need_name: Boolean? = null,
        val need_phone_number: Boolean? = null,
        val need_email: Boolean? = null,
        val need_shipping_address: Boolean? = null,
        val send_phone_number_to_provider: Boolean? = null,
        val send_email_to_provider: Boolean? = null,
        val is_flexible: Boolean? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendInvoice"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to create a link for an invoice. Returns the created invoice link as <em>String</em> on success.</p>
     *
     * @property title Product name, 1-32 characters
     * @property description Product description, 1-255 characters
     * @property payload Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
     * @property currency Three-letter ISO 4217 currency code, see <a href="/bots/payments#supported-currencies">more on currencies</a>. Pass “XTR” for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property prices Price breakdown, a JSON-serialized list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.). Must contain exactly one item for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property provider_token Payment provider token, obtained via <a href="https://t.me/botfather">@BotFather</a>. Pass an empty string for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property max_tip_amount The maximum accepted amount for tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). For example, for a maximum tip of <code>US$ 1.45</code> pass <code>max_tip_amount = 145</code>. See the <em>exp</em> parameter in <a href="/bots/payments/currencies.json">currencies.json</a>, it shows the number of digits past the decimal point for each currency (2 for the majority of currencies). Defaults to 0. Not supported for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property suggested_tip_amounts A JSON-serialized array of suggested amounts of tips in the <em>smallest units</em> of the currency (integer, <strong>not</strong> float/double). At most 4 suggested tip amounts can be specified. The suggested tip amounts must be positive, passed in a strictly increased order and must not exceed <em>max_tip_amount</em>.
     * @property provider_data JSON-serialized data about the invoice, which will be shared with the payment provider. A detailed description of required fields should be provided by the payment provider.
     * @property photo_url URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service.
     * @property photo_size Photo size in bytes
     * @property photo_width Photo width
     * @property photo_height Photo height
     * @property need_name Pass <em>True</em> if you require the user's full name to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property need_phone_number Pass <em>True</em> if you require the user's phone number to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property need_email Pass <em>True</em> if you require the user's email address to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property need_shipping_address Pass <em>True</em> if you require the user's shipping address to complete the order. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property send_phone_number_to_provider Pass <em>True</em> if the user's phone number should be sent to the provider. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property send_email_to_provider Pass <em>True</em> if the user's email address should be sent to the provider. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * @property is_flexible Pass <em>True</em> if the final price depends on the shipping method. Ignored for payments in <a href="https://t.me/BotNews/90">Telegram Stars</a>.
     * */
    @Serializable
    data class CreateInvoiceLinkRequest(
        val title: String,
        val description: String,
        val payload: String,
        val currency: String,
        val prices: List<LabeledPrice>,
        val provider_token: String? = null,
        val max_tip_amount: Long? = null,
        val suggested_tip_amounts: List<Long>? = null,
        val provider_data: String? = null,
        val photo_url: String? = null,
        val photo_size: Long? = null,
        val photo_width: Long? = null,
        val photo_height: Long? = null,
        val need_name: Boolean? = null,
        val need_phone_number: Boolean? = null,
        val need_email: Boolean? = null,
        val need_shipping_address: Boolean? = null,
        val send_phone_number_to_provider: Boolean? = null,
        val send_email_to_provider: Boolean? = null,
        val is_flexible: Boolean? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("createInvoiceLink"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>If you sent an invoice requesting a shipping address and the parameter <em>is_flexible</em> was specified, the Bot API will send an <a href="#update">Update</a> with a <em>shipping_query</em> field to the bot. Use this method to reply to shipping queries. On success, <em>True</em> is returned.</p>
     *
     * @property shipping_query_id Unique identifier for the query to be answered
     * @property ok Pass <em>True</em> if delivery to the specified address is possible and <em>False</em> if there are any problems (for example, if delivery to the specified address is not possible)
     * @property shipping_options Required if <em>ok</em> is <em>True</em>. A JSON-serialized array of available shipping options.
     * @property error_message Required if <em>ok</em> is <em>False</em>. Error message in human readable form that explains why it is impossible to complete the order (e.g. "Sorry, delivery to your desired address is unavailable'). Telegram will display this message to the user.
     * */
    @Serializable
    data class AnswerShippingQueryRequest(
        val shipping_query_id: String,
        val ok: Boolean,
        val shipping_options: List<ShippingOption>? = null,
        val error_message: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("answerShippingQuery"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Once the user has confirmed their payment and shipping details, the Bot API sends the final confirmation in the form of an <a href="#update">Update</a> with the field <em>pre_checkout_query</em>. Use this method to respond to such pre-checkout queries. On success, <em>True</em> is returned. <strong>Note:</strong> The Bot API must receive an answer within 10 seconds after the pre-checkout query was sent.</p>
     *
     * @property pre_checkout_query_id Unique identifier for the query to be answered
     * @property ok Specify <em>True</em> if everything is alright (goods are available, etc.) and the bot is ready to proceed with the order. Use <em>False</em> if there are any problems.
     * @property error_message Required if <em>ok</em> is <em>False</em>. Error message in human readable form that explains the reason for failure to proceed with the checkout (e.g. "Sorry, somebody just bought the last of our amazing black T-shirts while you were busy filling out your payment details. Please choose a different color or garment!"). Telegram will display this message to the user.
     * */
    @Serializable
    data class AnswerPreCheckoutQueryRequest(
        val pre_checkout_query_id: String,
        val ok: Boolean,
        val error_message: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("answerPreCheckoutQuery"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Returns the bot's Telegram Star transactions in chronological order. On success, returns a <a href="#startransactions">StarTransactions</a> object.</p>
     *
     * @property offset Number of transactions to skip in the response
     * @property limit The maximum number of transactions to be retrieved. Values between 1-100 are accepted. Defaults to 100.
     * */
    @Serializable
    data class GetStarTransactionsRequest(
        val offset: Long? = null,
        val limit: Long? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getStarTransactions"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Refunds a successful payment in <a href="https://t.me/BotNews/90">Telegram Stars</a>. Returns <em>True</em> on success.</p>
     *
     * @property user_id Identifier of the user whose payment will be refunded
     * @property telegram_payment_charge_id Telegram payment identifier
     * */
    @Serializable
    data class RefundStarPaymentRequest(
        val user_id: UserId,
        val telegram_payment_charge_id: String,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("refundStarPayment"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


// Telegram Passport

    /**
     * <p>Informs a user that some of the Telegram Passport elements they provided contains errors. The user will not be able to re-submit their Passport to you until the errors are fixed (the contents of the field for which you returned the error must change). Returns <em>True</em> on success.</p><p>Use this if the data submitted by the user doesn't satisfy the standards your service requires for any reason. For example, if a birthday date seems invalid, a submitted document is blurry, a scan shows evidence of tampering, etc. Supply some details in the error message to make sure the user knows how to correct the issues.</p>
     *
     * @property user_id User identifier
     * @property errors A JSON-serialized array describing the errors
     * */
    @Serializable
    data class SetPassportDataErrorsRequest(
        val user_id: UserId,
        val errors: List<@Contextual PassportElementError>,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(
                serializer(),
                this
            ).jsonObject + ("method" to JsonPrimitive("setPassportDataErrors"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }


// Games

    /**
     * <p>Use this method to send a game. On success, the sent <a href="#message">Message</a> is returned.</p>
     *
     * @property chat_id Unique identifier for the target chat
     * @property game_short_name Short name of the game, serves as the unique identifier for the game. Set up your games via <a href="https://t.me/botfather">@BotFather</a>.
     * @property business_connection_id Unique identifier of the business connection on behalf of which the message will be sent
     * @property message_thread_id Unique identifier for the target message thread (topic) of the forum; for forum supergroups only
     * @property disable_notification Sends the message <a href="https://telegram.org/blog/channels-2-0#silent-messages">silently</a>. Users will receive a notification with no sound.
     * @property protect_content Protects the contents of the sent message from forwarding and saving
     * @property message_effect_id Unique identifier of the message effect to be added to the message; for private chats only
     * @property reply_parameters Description of the message to reply to
     * @property reply_markup A JSON-serialized object for an <a href="/bots/features#inline-keyboards">inline keyboard</a>. If empty, one 'Play game_title' button will be shown. If not empty, the first button must launch the game.
     * */
    @Serializable
    data class SendGameRequest(
        val chat_id: ChatId,
        val game_short_name: String,
        val business_connection_id: BusinessConnectionId? = null,
        val message_thread_id: MessageThreadId? = null,
        val disable_notification: Boolean? = null,
        val protect_content: Boolean? = null,
        val message_effect_id: MessageEffectId? = null,
        val reply_parameters: ReplyParameters? = null,
        val reply_markup: InlineKeyboardMarkup? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("sendGame"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

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
     * */
    @Serializable
    data class SetGameScoreRequest(
        val user_id: UserId,
        val score: Long,
        val force: Boolean? = null,
        val disable_edit_message: Boolean? = null,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("setGameScore"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

    /**
     * <p>Use this method to get data for high score tables. Will return the score of the specified user and several of their neighbors in a game. Returns an Array of <a href="#gamehighscore">GameHighScore</a> objects.</p><blockquote>
     *  <p>This method will currently return scores for the target user, plus two of their closest neighbors on each side. Will also return the top three users if the user and their neighbors are not among them. Please note that this behavior is subject to change.</p>
     * </blockquote>
     *
     * @property user_id Target user id
     * @property chat_id Required if <em>inline_message_id</em> is not specified. Unique identifier for the target chat
     * @property message_id Required if <em>inline_message_id</em> is not specified. Identifier of the sent message
     * @property inline_message_id Required if <em>chat_id</em> and <em>message_id</em> are not specified. Identifier of the inline message
     * */
    @Serializable
    data class GetGameHighScoresRequest(
        val user_id: UserId,
        val chat_id: ChatId? = null,
        val message_id: MessageId? = null,
        val inline_message_id: String? = null,
    ) : TelegramRequest() {
        override fun toJsonForRequest() = json.encodeToString(serializer(), this)
        override fun toJsonForResponse() = JsonObject(
            json.encodeToJsonElement(serializer(), this).jsonObject + ("method" to JsonPrimitive("getGameHighScores"))
        ).toString()

        companion object {
            fun fromJson(string: String) = json.decodeFromString(serializer(), string)
        }
    }

}
