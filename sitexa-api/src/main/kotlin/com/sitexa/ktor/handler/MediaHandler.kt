package com.sitexa.ktor.handler

import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.uploadDir
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.content.LocalFileContent
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Route
import java.io.File

/**
 * Created by open on 25/06/2017.
 *
 */

@Location("/sweet-medias/{refId}") class GetMedias(val refId: Int)

@Location("/media-new") class MediaNew(val refId: Int = 0, val fileName: String = "", val fileType: String? = "unknown", val title: String? = null, val sortOrder: Int? = null)
@Location("/media-del") class MediaDel(val id: Int)
@Location("/media/{name}/{type}") class MediaView(val name: String, val type: String)
@Location("/media/{id}") class MediaData(val id: Int)
@Location("/mediasBySweet/{refId}") class MediasBySweet(val refId: Int)

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
            application.log.error(e.toString())
        }
        call.respond(JsonResponse(media!!))
    }
    get<MediasBySweet> {
        var medias: List<Int> = emptyList()
        try {
            medias = dao.getMedias(it.refId)
        } catch(e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(JsonResponse(medias))
    }
    get<GetMedias> {
        var medias: List<Media> = emptyList()
        try {
            medias = dao.getMedias(it.refId).map { dao.getMedia(it) }.filterNotNull()
        } catch(e: Exception) {
            application.log.error(e.toString())
        }
        call.respond(JsonResponse(medias))
    }
}