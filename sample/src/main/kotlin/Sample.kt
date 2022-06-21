import com.github.omarmiatello.telegram.InlineKeyboardButton
import com.github.omarmiatello.telegram.InlineKeyboardMarkup
import com.github.omarmiatello.telegram.ParseMode
import com.github.omarmiatello.telegram.TelegramRequest

fun main() {
    val sendMessageRequest = TelegramRequest.SendMessageRequest(
        chat_id = "chat123",
        text = "test message\\.",
        parse_mode = ParseMode.MarkdownV2,
        reply_markup = InlineKeyboardMarkup(listOf(
            listOf(
                InlineKeyboardButton("Button1", url = "https://example.com")
            )
        ))
    )
    println("Request: ${sendMessageRequest.toJsonForRequest()}")
    println("Response: ${sendMessageRequest.toJsonForResponse()}")
}