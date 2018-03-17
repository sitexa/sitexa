package com.sitexa.ktor.handler

import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.dao.DAOFacade
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions

/**
 * Created by open on 03/04/2017.
 *
 */


@Location("/")
class Index()

fun Route.indexHandler(dao: DAOFacade) {

    get<Index> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val top = dao.top(10).map { dao.getSweet(it) }
        val latest = dao.latest(10).map { dao.getSweet(it) }
        val date = System.currentTimeMillis()
        val etagString = date.toString() + "," + user?.userId + "," + top.joinToString { it.id.toString() } + latest.joinToString { it.id.toString() }
        val etag = etagString.hashCode()

        call.respond(FreeMarkerContent("index.ftl", mapOf("top" to top, "latest" to latest, "user" to user), etag.toString()))
    }

}
