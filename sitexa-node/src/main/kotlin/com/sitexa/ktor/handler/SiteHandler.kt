package com.sitexa.ktor.handler

import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.redirect
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import java.math.BigDecimal

@Location("/site/{id}")
data class SiteById(val id: Int)

@Location("/siteByCode/{code}")
data class SiteByCode(val code: Int)

@Location("/childrenById/{id}")
data class ChildrenById(val id: Int)

@Location("/childrenByCode/{code}")
data class ChildrenByCode(val code: Int)

@Location("/siteByLevel/{level}")
data class SiteByLevel(val level: Int)

@Location("/updateSiteLatLng")
data class SiteLatLng(val id: Int, val lat: BigDecimal? = null, val lng: BigDecimal? = null)

fun Route.siteHandler(dao: DAOFacade) {

    get<SiteById> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            val site = dao.site(it.id)
            call.respond(FreeMarkerContent("site/site-view.ftl", mapOf("site" to site)))
        }
    }

    get<SiteByCode> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            val site = dao.siteByCode(it.code)
            call.respond(FreeMarkerContent("site/site-view.ftl", mapOf("site" to site)))
        }
    }

    get<SiteByLevel> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            val sites = dao.sitesByLevel(it.level)
            call.respond(FreeMarkerContent("site/site-list.ftl", mapOf("sites" to sites)))
        }
    }

    get<ChildrenById> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            val sites = dao.childrenById(it.id)
            call.respond(FreeMarkerContent("site/site-list.ftl", mapOf("sites" to sites)))
        }
    }

    get<ChildrenByCode> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            val sites = dao.childrenByCode(it.code)
            call.respond(FreeMarkerContent("site/site-list.ftl", mapOf("sites" to sites)))
        }
    }

    get<SiteLatLng> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            call.respond(FreeMarkerContent("site/site-update.ftl", mapOf("user" to user)))
        }
    }

    post<SiteLatLng> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            val post = call.receive<Parameters>()
            val id = post["id"]!!.toIntOrNull() ?: return@post call.redirect(it)
            val lat = post["lat"]!!.toBigDecimalOrNull() ?: return@post call.redirect(it)
            val lng = post["lng"]!!.toBigDecimalOrNull() ?: return@post call.redirect(it)

            val apiResult = dao.updateLatLng(id, lat, lng)
            if (apiResult.code == ApiCode.OK) {
                call.redirect(SiteById(id))
            } else {
                return@post call.redirect(it)
            }
        }
    }

}