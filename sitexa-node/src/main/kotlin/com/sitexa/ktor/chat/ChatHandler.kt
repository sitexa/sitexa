package com.sitexa.ktor.chat

import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.handler.Login
import com.sitexa.ktor.redirect
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.http.content.resolveResource
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.webSocket
import kotlinx.coroutines.experimental.channels.consumeEach

/**
 * Created by open on 23/04/2017.
 *
 */

@Location("/chat")
class Chat

@Location("/js/chat.js")
class ChatJs

fun Route.chatHandler(dao:DAOFacade) {
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
                    receivedMessage(session.userId, frame.readText())
                }
            }
        } finally {
            server.memberLeft(session.userId, this)
        }
    }

    get<Chat> {
        //val session = call.sessions.get<SweetSession>()
        //if (session == null) call.redirect(Login())
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if(user == null) call.redirect(Login())
        call.respond(FreeMarkerContent("chat/chat.ftl", mapOf("user" to user?.displayName), user?.userId))
    }

    get<ChatJs> {
        call.respond(call.resolveResource("chat/main.js", "templates")!!)
    }
}


private val server = ChatServer()

private suspend fun receivedMessage(id: String, command: String) {
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
    //command.startsWith("/bye") -> server.memberLeft(id, ws)//todo...
        else -> server.message(id, command)
    }
}