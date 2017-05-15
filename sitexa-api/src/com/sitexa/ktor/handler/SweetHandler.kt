package com.sitexa.ktor.handler

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.uploadDir
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.log
import org.jetbrains.ktor.content.LocalFileContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.application
import org.joda.time.DateTime
import java.io.File

/**
 * Created by open on 10/04/2017.
 *
 */
@location("/sweet-new") class SweetNew(val text: String = "", val user: String = "", val replyTo: Int? = null)

@location("/sweet-del") class SweetDel(val id: Int = -1)
@location("/sweet-upd") class SweetUpd(val id: Int = -1, val text: String = "")

@location("/sweet/{id}") class SweetSingle(val id: Int)
@location("/sweet-component/{id}") class SweetComponent(val id: Int)
@location("/sweet-top/{count}/{page}") class TopSweet(val count: Int = 10,val page: Int = 1)
@location("/sweet-latest/{count}/{page}") class LatestSweet(val count: Int = 10,val page: Int = 1)
@location("/sweet-reply-count/{id}") class CountSweetReplies(val id: Int)
@location("/sweet-replies/{id}") class GetReplies(val id: Int)
@location("/sweet-user/{user}") class UserSweet(val user: String)

@location("/media-new") class MediaNew(val refId: Int = 0, val fileName: String = "", val fileType: String? = "unknown", val title: String? = null, val sortOrder: Int? = null)
@location("/media-del") class MediaDel(val id: Int)
@location("/media/{name}/{type}") class MediaView(val name: String, val type: String)
@location("/media/{id}") class MediaData(val id: Int)
@location("/mediasBySweet/{refId}") class MediasBySweet(val refId: Int)

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
        var top: List<Int> = emptyList()
        try {
            top = dao.topSweets(it.count, it.page)
        } catch (e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(top))
    }
    get<LatestSweet> {
        var latest: List<Int> = emptyList()
        try {
            latest = dao.latestSweets(it.count, it.page)
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
    get<GetReplies> {
        var replies: List<Int> = emptyList()
        try {
            replies = dao.getReplies(it.id)
        } catch(e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(replies))
    }
    get<UserSweet> {
        var sweets: List<Int> = emptyList()
        try {
            sweets = dao.userSweets(it.user)
        } catch (e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(sweets))
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
    get<MediasBySweet> {
        var medias: List<Int> = emptyList()
        try {
            medias = dao.getMedias(it.refId)
        } catch(e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(medias))
    }
}
