package com.example

import com.github.omarmiatello.telegram.*
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // Starting point for a Ktor app:
        routing {
            get("/") {

                // Endpoint for Telegram webhook. Parse the Telegram request
                val request: Update = call.receiveText().parseTelegramRequest()

                val inputText = request.message?.text
                if (inputText != null) {
                    call.respondText(
                        // Send a message back to the user
                        TelegramRequest.SendMessageRequest(
                            chat_id = request.message!!.chat.id.toString(),
                            text = "Message: ${request.message}",
                            parse_mode = ParseMode.HTML,
                        ).toJsonForResponse(),
                        ContentType.Application.Json
                    )
                }

                if (call.response.status() == null) call.respond(HttpStatusCode.NoContent)
            }
        }
    }.start(wait = true)
}
