package com.sitexa.ktor.handler

import com.sitexa.ktor.*
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.Media
import io.ktor.application.*
import io.ktor.content.*
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*
import java.io.File

/**
 * Created by open on 10/04/2017.
 *
 */

@Location("/upload")
class Upload()

@Location("/media/{name}/{type}")
data class MediaView(val name: String, val type: String)

@Location("/sweet/{id}")
data class SweetView(val id: Int)

@Location("/sweet-new")
data class SweetNew(val text: String = "", val date: Long = System.currentTimeMillis(), val code: String = "")

//set default values to the data class!!!
@Location("/sweet-del")
data class SweetDel(val id: Int = 0, val date: Long = 0L, val code: String = "")

@Location("/sweet-upd")
data class SweetUpd(val id: Int = 0, val text: String = "", val date: Long = 0L, val code: String = "")

@Location("/sweet-reply")
data class SweetReply(val replyTo: Int = 0, val text: String = "", val date: Long = 0L, val code: String = "")

fun Route.sweetHandler(dao: DAOFacade, hashFunction: (String) -> String) {
    get<SweetNew> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }

        if (user == null) {
            call.redirect(Login())
        } else {
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)

            call.respond(FreeMarkerContent("sweet-new.ftl", mapOf("user" to user, "date" to date, "code" to code), user.userId))
        }
    }
    post<SweetNew> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            var date: Long = 0L
            var code: String = ""
            var text: String = ""
            var fileName: String = ""
            var fileType: String? = "unknown"

            val multipart = call.receiveMultipart()

            if (call.request.isMultipart()) {
                multipart.forEachPart { part ->
                    if (part is PartData.FormItem) {
                        if (part.partName == "date") {
                            date = part.value.toLong()
                        } else if (part.partName == "code") {
                            code = part.value
                        } else if (part.partName == "text") {
                            text = part.value
                        }
                    } else if (part is PartData.FileItem) {
                        val ext = File(part.originalFileName).extension
                        val file = File(uploadDir, "${System.currentTimeMillis()}${user.userId.hashCode()}.$ext")
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

            if (!call.verifyCode(date, user, code, hashFunction)) {
                call.redirect(Index())
            } else {
                val id = dao.createSweet(user.userId, text)
                val medId = dao.createMedia(id, fileName, fileType)
                call.redirect(SweetView(id))
            }
        }
    }
    post<SweetDel> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val sweet = dao.getSweet(it.id)

        if (user == null || sweet.userId != user.userId || !call.verifyCode(it.date, user, it.code, hashFunction)) {
            call.redirect(SweetView(it.id))
        } else {
            dao.deleteSweet(it.id)
            call.redirect(Index())
        }
    }
    get<SweetView> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val sweet = dao.getSweet(it.id)
        val replies = dao.getReplies(it.id).map { dao.getSweet(it) }
        val date = System.currentTimeMillis()
        val code = if (user != null) call.securityCode(date, user, hashFunction) else null
        val etagString = date.toString() + "," + user?.userId + "," + sweet.id.toString()
        val etag = etagString.hashCode()
        val medias = dao.getMedias(sweet.id).map {
            val med = dao.getMedia(it)
            Media(med!!.id, med.refId, med.fileName, med.fileType, med.title, med.sortOrder)
        }.toList()

        call.respond(FreeMarkerContent("sweet-view.ftl", mapOf("user" to user, "sweet" to sweet, "replies" to replies, "date" to date, "code" to code, "medias" to medias), etag.toString()))
    }
    post<Upload> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null) {
            call.redirect(Login())
        } else {
            var refId: Int = 0
            var fileName: String = ""
            var fileType: String? = ""
            var title: String? = ""
            var sortOrder: Int = 0

            val multipart = call.receiveMultipart()

            if (call.request.isMultipart()) {
                multipart.forEachPart { part ->
                    if (part is PartData.FormItem) {
                        if (part.partName == "refId") {
                            refId = part.value.toInt()
                        } else if (part.partName == "fileName") {
                            fileName = part.value
                        } else if (part.partName == "title") {
                            title = part.value
                        } else if (part.partName == "sortOrder") {
                            sortOrder = part.value.toInt()
                        }
                    } else if (part is PartData.FileItem) {
                        val ext = File(part.originalFileName).extension
                        val file = File(uploadDir, "${System.currentTimeMillis()}${user.hashCode()}.$ext")
                        part.streamProvider().use { instream ->
                            file.outputStream().buffered().use { outstream ->
                                instream.copyTo(outstream)
                            }
                        }
                        fileName = file.name
                        fileType = part.contentType?.contentType
                    }
                    part.dispose()
                }
            }

            val id = dao.createMedia(refId, fileName, fileType, title, sortOrder)
            call.respond(JsonResponse(Media(id, refId, fileName, fileType, title, sortOrder)))
        }
    }
    get<MediaView> {
        call.respond(LocalFileContent(File(uploadDir + "/" + it.name)))
    }
    get<SweetUpd> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val sweet = dao.getSweet(it.id)
        val date = System.currentTimeMillis()
        if (user != null && sweet.userId == user.userId) {
            val code = call.securityCode(date, user, hashFunction)
            val etagString = date.toString() + "," + user.userId + "," + sweet.id.toString()
            val etag = etagString.hashCode()
            call.respond(FreeMarkerContent("sweet-upd.ftl", mapOf("user" to user, "sweet" to sweet, "date" to date, "code" to code), etag.toString()))
        } else {
            call.redirect(SweetView(it.id))
        }
    }
    post<SweetUpd> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user == null || !call.verifyCode(it.date, user, it.code, hashFunction)) {
            call.redirect(Login())
        } else {
            dao.updateSweet(it.id, it.text)
            call.redirect(SweetView(it.id))
        }
    }
    get<SweetReply> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val sweet = dao.getSweet(it.replyTo)
        val date = System.currentTimeMillis()
        if (user != null) {
            val code = call.securityCode(date, user, hashFunction)
            val etagString = date.toString() + "," + user.userId + "," + sweet.id.toString()
            val etag = etagString.hashCode()
            call.respond(FreeMarkerContent("sweet-reply.ftl", mapOf("user" to user, "sweet" to sweet, "date" to date, "code" to code), etag.toString()))
        } else {
            call.redirect(SweetView(it.replyTo))
        }
    }
    post<SweetReply> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }

        println("Route.replySweet.post:user=${user?.userId},code=${it.code},replyTo=${it.replyTo},text=${it.text}")

        if (user == null || !call.verifyCode(it.date, user, it.code, hashFunction)) {
            call.redirect(Login())
        } else {
            val id = dao.createSweet(user.userId, it.text, it.replyTo)
            call.redirect(SweetView(id))
        }
    }
}
