import com.github.omarmiatello.telegram.TelegramRequest

fun main() {
    val sendMessageRequest = TelegramRequest.SendMessageRequest(
        chat_id = "chat123",
        text = "test message",
    )
    println("Request: ${sendMessageRequest.toJsonForRequest()}")
    println("Response: ${sendMessageRequest.toJsonForResponse()}")
}