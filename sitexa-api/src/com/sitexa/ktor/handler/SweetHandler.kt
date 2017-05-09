package com.sitexa.ktor.handler

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.*
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sun.org.apache.bcel.internal.util.InstructionFinder
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.log
import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.content.LocalFileContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.MultiPartData
import org.jetbrains.ktor.request.PartData
import org.jetbrains.ktor.request.isMultipart
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.application
import org.jetbrains.ktor.sessions.sessionOrNull
import org.joda.time.DateTime
import java.io.File

/**
 * Created by open on 10/04/2017.
 *
 */
@location("/sweet-new") data class SweetNew(val text: String = "", val user: String = "", val replyTo: Int? = null)
@location("/sweet-del") data class SweetDel(val id: Int = -1)
@location("/sweet-upd") data class SweetUpd(val id: Int = -1, val text: String = "")

@location("/sweet/{id}") data class SweetSingle(val id: Int)
@location("/sweet-component/{id}") data class SweetComponent(val id: Int)
@location("/sweet-top/{num}") data class TopSweet(val num: Int = 10)
@location("/sweet-latest/{num}") data class LatestSweet(val num: Int = 10)
@location("/sweet-reply-count/{id}") data class CountSweetReplies(val id: Int)

@location("/media-new") data class MediaNew(val refId: Int = 0, val fileName: String = "", val fileType: String? = "unknown", val title: String? = null, val sortOrder: Int? = null)
@location("/media-del") data class MediaDel(val id: Int)
@location("/media/{name}/{type}") data class MediaView(val name: String, val type: String)
@location("/media/{id}") class MediaData(val id: Int)

fun Route.sweetHandler(dao: DAOFacade, hashFunction: (String) -> String) {

    val gson = GsonBuilder().registerTypeAdapter(DateTime::class.java, JodaGsonAdapter()).setLongSerializationPolicy(LongSerializationPolicy.STRING).create()

    post<SweetNew> {
        var apiResult: ApiResult
        try {
            val id = dao.createSweet(it.user, it.text, it.replyTo)
            apiResult = ApiResult(code = ApiCode.OK, desc = "保存成功", data = "" + id)
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = e.message!!)
        }
        call.respond(JsonResponse(apiResult))

    }
    get<SweetDel> {
        var apiResult: ApiResult
        try {
            dao.deleteSweet(it.id)
            apiResult = ApiResult(code = ApiCode.OK, desc = "删除成功")
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "删除失败", data = e.message!!)
        }
        call.respond(JsonResponse(apiResult))
    }
    post<SweetUpd> {
        var apiResult: ApiResult
        try {
            dao.updateSweet(it.id, it.text)
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = "" + it.id)
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = "" + e.message)
        }
        call.respond(JsonResponse(apiResult))
    }

    get<SweetSingle> {
        var sweet: Sweet? = null
        try {
            sweet = dao.getSweet(it.id)
        } catch (e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(sweet!!))
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
            val data = mapOf("sweet" to gson.toJson(sweet), "replies" to gson.toJson(replies), "medias" to gson.toJson(medias))
            val data_json = gson.toJson(data)
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = data_json)
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = "" + e.message)
        }
        call.respond(JsonResponse(apiResult))
    }
    get<TopSweet> {
        var top: List<Sweet> = emptyList()
        try {
            top = dao.topSweets(it.num).map { dao.getSweet(it) }.toList()
        } catch (e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(top))
    }
    get<LatestSweet> {
        var latest: List<Sweet> = emptyList()
        try {
            latest = dao.latestSweets(it.num).map { dao.getSweet(it) }.toList()
        } catch (e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(latest))
    }
    get<CountSweetReplies> {
        var countSweetReplies: Int = 0
        try {
            countSweetReplies = dao.countReplies(it.id)
        } catch (e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(countSweetReplies))
    }

    post<MediaNew> {
        var apiResult: ApiResult
        try {
            val id = dao.createMedia(it.refId, it.fileName, it.fileType, it.title, it.sortOrder)
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = "" + id)
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = e.message!!)
        }
        call.respond(JsonResponse(apiResult))
    }
    get<MediaDel> {
        var apiResult: ApiResult
        try {
            dao.deleteMedia(it.id)
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = "")
        } catch (e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = "" + e.message)
        }
        call.respond(JsonResponse(apiResult))
    }
    get<MediaView> {
        call.respond(LocalFileContent(File(uploadDir + "/" + it.name)))
    }
    get<MediaData> {
        var media: Media? = null
        try {
            media = dao.getMedia(it.id)
        } catch(e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(media!!))
    }

}
