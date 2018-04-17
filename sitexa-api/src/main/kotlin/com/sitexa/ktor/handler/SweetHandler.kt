package com.sitexa.ktor.handler

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import org.joda.time.DateTime

/**
 * Created by open on 10/04/2017.
 *
 */
@Location("/sweet-new")
class SweetNew(val text: String = "", val user: String = "", val replyTo: Int? = null)

@Location("/sweet-del")
class SweetDel(val id: Int = -1)

@Location("/sweet-upd")
class SweetUpd(val id: Int = -1, val text: String = "")

@Location("/sweet/{id}")
class SweetSingle(val id: Int)

@Location("/sweet-component/{id}")
class SweetComponent(val id: Int)

@Location("/sweet-top/{count}/{page}")
class TopSweet(val count: Int = 10, val page: Int = 1)

@Location("/sweet-latest/{count}/{page}")
class LatestSweet(val count: Int = 10, val page: Int = 1)

@Location("/sweet-reply-count/{id}")
class CountSweetReplies(val id: Int)

@Location("/sweet-replies/{id}")
class GetReplies(val id: Int)

@Location("/sweet-user/{user}")
class UserSweet(val user: String)

@Location("/top/{count}/{page}")
class Top(val count: Int = 10, val page: Int = 1)

@Location("/latest/{count}/{page}")
class Latest(val count: Int = 10, val page: Int = 1)


fun Route.sweetHandler(dao: DAOFacade, hashFunction: (String) -> String) {

    val gson = GsonBuilder().registerTypeAdapter(DateTime::class.java, JodaGsonAdapter()).setLongSerializationPolicy(LongSerializationPolicy.STRING).create()

    post<SweetNew> {
        var apiResult: ApiResult

        val post = call.receive<Parameters>()
        val user = post["user"]
        val text = post["text"]
        val replyTo = post["replyTo"]?.toIntOrNull()

        if (user.isNullOrEmpty() || text.isNullOrEmpty()) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "user or text cannot be empty")
        } else {
            try {
                val id = dao.createSweet(user!!, text!!, replyTo)
                apiResult = ApiResult(code = ApiCode.OK, desc = "保存成功", data = id)
            } catch (e: Exception) {
                apiResult = ApiResult(code = ApiCode.ERROR, desc = e.message!!)
            }
        }
        call.respond(apiResult)
    }

    get<SweetDel> {
        var apiResult: ApiResult
        try {
            dao.deleteSweet(it.id)
            apiResult = ApiResult(code = ApiCode.OK, desc = "删除成功")
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "删除失败", data = e.message!!)
        }
        call.respond(apiResult)
    }
    post<SweetUpd> {
        var apiResult: ApiResult

        val post = call.receive<Parameters>()
        val id = post["id"]?.toIntOrNull()
        val text = post["text"]

        apiResult = if (id == null || text.isNullOrEmpty()) {
            ApiResult(code = ApiCode.ERROR, desc = "id or text cannot be empty")
        } else {
            try {
                dao.updateSweet(id, text!!)
                ApiResult(code = ApiCode.OK, desc = "success", data = "" + it.id)
            } catch (e: Exception) {
                ApiResult(code = ApiCode.ERROR, desc = "fail", data = "" + e.message)
            }
        }
        call.respond(apiResult)
    }

    get<SweetSingle> {
        var sweet: Sweet? = null
        try {
            sweet = dao.getSweet(it.id)
            //println("Sweet:=$sweet")
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(sweet!!)
    }
    get<SweetComponent> {
        var apiResult: ApiResult
        try {
            val sweet = dao.getSweet(it.id)
            val replies = dao.getReplies(it.id).map { dao.getSweet(it) }
            val medias = dao.getMedias(sweet.id).map {
                val med = dao.getMedia(it)
                Media(med!!.id, med.refId, med.fileName, med.fileType, med.title, med.sortOrder)
            }.toList()
            val dataMap = mapOf("sweet" to gson.toJson(sweet), "replies" to gson.toJson(replies), "medias" to gson.toJson(medias))
            val dataMapJson = gson.toJson(dataMap)
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = dataMapJson)
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = "" + e.message)
        }
        call.respond(apiResult)
    }
    get<TopSweet> {
        var top: List<Int> = emptyList()
        try {
            top = dao.topSweets(it.count, it.page)
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(top)
    }
    get<Top> {
        var top: List<Sweet> = emptyList()
        try {
            top = dao.top(it.count, it.page)
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(top)
    }
    get<LatestSweet> {
        var latest: List<Int> = emptyList()
        try {
            latest = dao.latestSweets(it.count, it.page)
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(latest)
    }
    get<Latest> {
        var latest: List<Sweet> = emptyList()
        try {
            latest = dao.latest(it.count, it.page)
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(latest)
    }
    get<CountSweetReplies> {
        var countSweetReplies: Int = 0
        try {
            countSweetReplies = dao.countReplies(it.id)
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(countSweetReplies)
    }
    get<GetReplies> {
        var replies: List<Sweet> = emptyList()
        try {
            val ids = dao.getReplies(it.id)
            replies = ids.map { dao.getSweet(it) }.toList()
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(replies)
    }
    get<UserSweet> {
        var sweets: List<Int> = emptyList()
        try {
            sweets = dao.userSweets(it.user)
        } catch (e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(sweets)
    }

}
