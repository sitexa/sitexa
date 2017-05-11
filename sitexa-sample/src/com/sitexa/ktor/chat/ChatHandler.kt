package com.sitexa.ktor.chat

import com.sitexa.ktor.Session
import com.sitexa.ktor.handler.Login
import com.sitexa.ktor.redirect
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.resolveResource
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.sessions.sessionOrNull
import org.jetbrains.ktor.websocket.*
import java.time.Duration

/**
 * Created by open on 23/04/2017.
 *
 */

@location("/chat") class Chat

@location("/js/chat.js") class ChatJs

fun Route.chatHandler() {
    webSocket("/ws") {
        val session = call.sessionOrNull<Session>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        pingInterval = Duration.ofMinutes(1)

        server.memberJoin(session.userId, this)

        handle { frame ->
            if (frame is Frame.Text) {
                receivedMessage(session.userId, frame.readText(), this)
            }
        }

        close {
            server.memberLeft(session.userId, this)
        }
    }

    get<Chat> {
        val session = call.sessionOrNull<Session>()
        if (session == null) call.redirect(Login())
        call.respond(FreeMarkerContent("chat/chat.ftl", mapOf("user" to "xnpeng"), "1001"))
    }

    get<ChatJs> {
        call.respond(call.resolveResource("chat/main.js", "templates")!!)
    }
}


private val server = ChatServer()

private suspend fun receivedMessage(id: String, command: String, ws: WebSocket) {
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