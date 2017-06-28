package com.sitexa.ktor


import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.chat.chatHandler
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeNetwork
import com.sitexa.ktor.handler.*
import com.sitexa.ktor.model.User
import freemarker.cache.ClassTemplateLoader
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.content.TextContent
import org.jetbrains.ktor.features.ConditionalHeaders
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.features.PartialContentSupport
import org.jetbrains.ktor.freemarker.FreeMarker
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.request.acceptItems
import org.jetbrains.ktor.request.header
import org.jetbrains.ktor.request.host
import org.jetbrains.ktor.request.port
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.sessions.*
import org.jetbrains.ktor.transform.transform
import org.jetbrains.ktor.util.hex
import org.joda.time.DateTime
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Created by open on 05/04/2017.
 *
 */


class JsonResponse(val data: Any)

data class Session(val userId: String)

class SweetNode : AutoCloseable {

    val hashKey = hex("6819b57a326945c1968f45236589")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

    lateinit var dao: DAOFacade

    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    fun Application.install() {

        loadConfig(environment)
        dao = DAOFacadeCache(DAOFacadeNetwork(), File(cacheDir, "ehcache"))
        dao.init()

        install(DefaultHeaders)
        install(CallLogging)
        install(ConditionalHeaders)
        install(PartialContentSupport)
        install(Locations)
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(SweetNode::class.java.classLoader, "templates")
        }

        withSessions<Session> {
            withCookieByValue {
                settings = SessionCookiesSettings(transformers = listOf(SessionCookieTransformerMessageAuthentication(hashKey)))
            }
        }

        val hashFunction = { s: String -> hash(s) }

        intercept(ApplicationCallPipeline.Infrastructure) { call ->
            if (call.request.acceptItems().any { it.value == "application/json" }) {
                call.transform.register<JsonResponse> { value ->
                    TextContent(gson.toJson(value.data), ContentType.Application.Json)
                }
            }
            //for chat
            if (call.sessionOrNull<Session>() == null) {
                //call.session(Session(nextNonce()))
                //call.redirect(Login())
            }
        }

        install(Routing) {
            staticHandler()
            indexHandler(dao)
            userHandler(dao, hashFunction)
            sweetHandler(dao, hashFunction)
            chatHandler()
            weatherHandler()
        }
    }

    override fun close() {
    }

    fun hash(password: String): String {
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }

    private fun loadConfig(environment: ApplicationEnvironment) {
        uploadDir = environment.config.property("dir.uploadDir").getString()
        cacheDir = environment.config.property("dir.cacheDir").getString()
        apiBaseUrl = environment.config.property("key.apiBaseUrl").getString()
        AppId = environment.config.property("key.AppId").getString()
        AppKey = environment.config.property("key.AppKey").getString()

        println("Environment:\n upload-dir:$uploadDir;\n cache-dir:$cacheDir;\n api-base-url:$apiBaseUrl;\n app-id:$AppId;\n app-key:$AppKey")
    }

}

suspend fun ApplicationCall.redirect(location: Any) {
    val host = request.host() ?: "localhost"
    val portSpec = request.port().let { if (it == 80) "" else ":$it" }
    val address = host + portSpec

    respondRedirect("http://$address${application.feature(Locations).href(location)}")
}

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
        hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
        securityCode(date, user, hashFunction) == code
                && (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

