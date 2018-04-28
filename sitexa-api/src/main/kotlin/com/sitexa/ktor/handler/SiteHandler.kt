package com.sitexa.ktor.handler

import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.dao.DAOFacade
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
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
data class SiteLatLng(val id: Int = 0, val lat: BigDecimal? = null, val lng: BigDecimal? = null)


fun Route.siteHandler(dao: DAOFacade) {

    get<SiteById> {
        val site = dao.site(it.id) ?: ""
        call.respond(site)
    }

    get<SiteByCode> {
        val site = dao.siteByCode(it.code) ?: ""
        call.respond(site)
    }

    get<ChildrenById> {
        val sites = dao.childrenById(it.id) ?: ""
        call.respond(sites)
    }

    get<ChildrenByCode> {
        val sites = dao.childrenByCode(it.code) ?: ""
        call.respond(sites)
    }

    get<SiteByLevel> {
        val sites = dao.sitesByLevel(it.level) ?: ""
        call.respond(sites)
    }

    post<SiteLatLng> {
        val post = call.receive<Parameters>()
        val id = post["id"]?.toIntOrNull()
        val lat = post["lat"]?.toBigDecimalOrNull()
        val lng = post["lng"]?.toBigDecimalOrNull()

        println("SiteLatLng:$id,$lat,$lng")

        val apiResult = if (id == null || lat == null || lng == null) {
            ApiResult(code = ApiCode.FAILURE, desc = "parameter null error")
        } else {
            try {
                dao.updateLatLng(id, lat, lng)
                ApiResult(code = ApiCode.OK, desc = "success", data = "" + id)
            } catch (e: Exception) {
                ApiResult(code = ApiCode.FAILURE, desc = e.toString())
            }
        }
        call.respond(apiResult)
    }
}