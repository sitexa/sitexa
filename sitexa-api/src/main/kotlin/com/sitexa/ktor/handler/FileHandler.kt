package com.sitexa.ktor.handler

import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.uploadDir
import io.ktor.application.call
import io.ktor.content.LocalFileContent
import io.ktor.content.PartData
import io.ktor.content.forEachPart
import io.ktor.http.ContentType
import io.ktor.http.fromFilePath
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import java.io.File

/**
 * Created by open on 09/05/2017.
 *
 */

@Location("/upload") class Upload

@Location("/download") class Download(val id: Int)

fun Route.fileHandler(dao: DAOFacade, hashFunction: (String) -> String) {

    post<Upload> {

        var id: Int?
        var refId: Int? = null
        var title: String? = null
        var sortOrder: Int? = null
        var fileName: String? = null
        var fileType: String? = null

        var apiResult: ApiResult

        try {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when(part){
                    is PartData.FormItem ->{
                        if (part.partName == "refId") {
                            refId = part.value.toInt()
                        } else if (part.partName == "title") {
                            title = part.value
                        } else if (part.partName == "sortOrder") {
                            sortOrder = part.value.toInt()
                        }
                    }
                    is PartData.FileItem ->{
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
                }
                part.dispose()
            }
            id = (fileName != null).let { dao.createMedia(refId, fileName!!, fileType, title, sortOrder) }
            apiResult = ApiResult(code = ApiCode.OK, desc = "success", data = "$id")
        } catch(e: Exception) {
            apiResult = ApiResult(code = ApiCode.ERROR, desc = "fail", data = e.message!!)
        }
        call.respond(apiResult)
    }

    get<Download> {
        val media = dao.getMedia(it.id)
        call.respond(LocalFileContent(File(uploadDir + "/" + media!!.fileName)))
    }
}