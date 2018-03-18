package com.sitexa.ktor.chat

import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.handler.Login
import com.sitexa.ktor.redirect
import kotlinx.coroutines.experimental.channels.consumeEach
import io.ktor.application.*
import io.ktor.content.*
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*

/**
 * Created by open on 23/04/2017.
 *
 */

@Location("/chat")
class Chat

@Location("/js/chat.js")
class ChatJs

fun Route.chatHandler() {

    get<Chat> {
        val session = call.sessions.get<SweetSession>()
        if (session == null) call.redirect(Login())
        call.respond(FreeMarkerContent("chat/chat.ftl", mapOf("user" to "xnpeng"), "1001"))
    }

    get<ChatJs> {
        call.respond(call.resolveResource("chat/main.js", "templates")!!)
    }

    webSocket("/ws") {
        val session = call.sessions.get<SweetSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        server.memberJoin(session.userId, this)

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    receivedMessage(session.userId, frame.readText(), this)
                }
            }
        } finally {
            server.memberLeft(session.userId, this)
        }
    }

}

private val server = ChatServer()

private suspend fun receivedMessage(id: String, command: String, ws: WebSocketSession) {
    when {
        command.startsWith("/who") -> server.who(id)
        command.startsWith("/user") -> {
            val newName = command.removePrefix("/user").trim()
            when {
                newName.isEmpty() -> server.sendTo(id, "server::help", "/user [newName]")
                newName.length > 50 -> server.sendTo(id, "server::help", "new name is too long: 50 characters limit")
                else -> server.sendTo(id, "server:help", "don't change your name.")
            //else -> server.memberRenamed(id, newName) //don't change username
            }
        }
        command.startsWith("/help") -> server.help(id)
        command.startsWith("/") -> server.sendTo(id, "server::help", "Unknown command ${command.takeWhile { !it.isWhitespace() }}")
        command.startsWith("/bye") -> server.memberLeft(id, ws)//todo...
        else -> server.message(id, command)
    }
}