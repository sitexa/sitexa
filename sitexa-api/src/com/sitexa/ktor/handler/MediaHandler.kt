package com.sitexa.ktor.handler

import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.uploadDir
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.log
import org.jetbrains.ktor.content.LocalFileContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.application
import java.io.File

/**
 * Created by open on 25/06/2017.
 *
 */

@location("/sweet-medias/{refId}") class GetMedias(val refId: Int)

@location("/media-new") class MediaNew(val refId: Int = 0, val fileName: String = "", val fileType: String? = "unknown", val title: String? = null, val sortOrder: Int? = null)
@location("/media-del") class MediaDel(val id: Int)
@location("/media/{name}/{type}") class MediaView(val name: String, val type: String)
@location("/media/{id}") class MediaData(val id: Int)
@location("/mediasBySweet/{refId}") class MediasBySweet(val refId: Int)

fun Route.mediaHandler(dao: DAOFacade, hashFunction: (String) -> String) {
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
    get<GetMedias> {
        var medias: List<Media> = emptyList()
        try {
            medias = dao.getMedias(it.refId).map { dao.getMedia(it) }.filterNotNull()
        } catch(e: Exception) {
            application.log.error(e)
        }
        call.respond(JsonResponse(medias))
    }
}