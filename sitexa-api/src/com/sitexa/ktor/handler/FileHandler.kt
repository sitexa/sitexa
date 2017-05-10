package com.sitexa.ktor.handler

import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.uploadDir
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.content.LocalFileContent
import org.jetbrains.ktor.content.fromFilePath
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.MultiPartData
import org.jetbrains.ktor.request.PartData
import org.jetbrains.ktor.request.isMultipart
import org.jetbrains.ktor.routing.Route
import java.io.File

/**
 * Created by open on 09/05/2017.
 *
 */

@location("/upload") class Upload

@location("/download") class Download(val id: Int)

fun Route.fileHandler(dao: DAOFacade, hashFunction: (String) -> String) {

    post<Upload> {

        var id: Int? = null
        var refId: Int? = null
        var title: String? = null
        var sortOrder: Int? = null
        var fileName: String? = null
        var fileType: String? = null

        var apiResult: ApiResult

        try {
            val multipart = call.request.receive<MultiPartData>()
            if (call.request.isMultipart()) {
                multipart.parts.forEach { part ->
                    if (part is PartData.FormItem) {
                        if (part.partName == "refId") {
                            refId = part.value.toInt()
                        } else if (part.partName == "title") {
                            title = part.value
                        } else if (part.partName == "sortOrder") {
                            sortOrder = part.value.toInt()
                        }
                    } else if (part is PartData.FileItem) {
                        val ext = File(part.originalFileName).extension
                        val file = File(uploadDir, "${System.currentTimeMillis()}.$ext")
                        part.streamProvider().use { instream ->
                            file.outputStream().buffered().use { outstream ->
                                instream.copyTo(outstream)
                            }
                        }
                        fileName = file.name
                        fileType = ContentType.fromFilePath(file.path).firstOrNull()?.contentType
                    }
                    part.dispose()
                }
            }
            id = (fileName != null).let { dao.createMedia(refId, fileName!!, fileType, title, sortOrder) }
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = "$id")
        } catch(e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = e.message!!)
        }
        call.respond(JsonResponse(apiResult))
    }

    get<Download> {
        val media = dao.getMedia(it.id)
        call.respond(LocalFileContent(File(uploadDir + "/" + media!!.fileName)))
    }
}