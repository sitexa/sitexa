package com.sitexa.ktor.handler

import com.sitexa.ktor.Session
import com.sitexa.ktor.dao.*
import com.sitexa.ktor.service.SweetService
import com.sitexa.ktor.service.UserService
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.auth.UserIdPrincipal
import org.jetbrains.ktor.auth.authentication
import org.jetbrains.ktor.auth.basicAuthentication
import org.jetbrains.ktor.freemarker.*
import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.sessions.*

/**
 * Created by open on 03/04/2017.
 *
 */


@location("/")
class Index

fun Route.indexHandler(dao: DAOFacade) {
/*
    get<Index> {
        val user = call.sessionOrNull<Session>()?.let { dao.user(it.userId) }
        val top = dao.top(10).map { dao.getSweet(it) }
        val latest = dao.latest(10).map { dao.getSweet(it) }
        val date = System.currentTimeMillis()
        val etagString = date.toString() + "," + user?.userId + "," + top.joinToString { it.id.toString() } + latest.joinToString { it.id.toString() }
        val etag = etagString.hashCode()

        call.respond(FreeMarkerContent("index.ftl", mapOf("top" to top, "latest" to latest, "user" to user), etag.toString()))
    }
*/

    get<Index> {
        val user = call.sessionOrNull<Session>()?.let { UserService().getUserInfo(it.userId) }
        val top = SweetService().getTopSweet(10)
        val latest = SweetService().getLatestSweet(10)
        val date = System.currentTimeMillis()
        val etagString = date.toString() + "," + user?.userId + "," + top.joinToString { it.id.toString() } + latest.joinToString { it.id.toString() }
        val etag = etagString.hashCode()
        call.respond(FreeMarkerContent("index.ftl", mapOf("top" to top, "latest" to latest, "user" to user), etag.toString()))
    }

    route("/*") {
        authentication {
            basicAuthentication("ktor") { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

        get("ui") {
            call.respondText("Success")
        }
    }
}
