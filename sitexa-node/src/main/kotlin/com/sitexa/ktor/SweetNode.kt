package com.sitexa.ktor

import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.chat.chatHandler
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeNetwork
import com.sitexa.ktor.handler.*
import com.sitexa.ktor.model.User
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.locations.Locations
import io.ktor.locations.locations
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.hex
import io.ktor.websocket.WebSockets
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


data class SweetSession(val userId: String)

class SweetNode : AutoCloseable {

    val hashKey = hex("6819b57a326945c1968f45236589")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

    lateinit var dao: DAOFacade

    fun Application.install() {

        loadConfig(environment)
        dao = DAOFacadeCache(DAOFacadeNetwork(), File(cacheDir, "ehcache"))
        dao.init()
        environment.monitor.subscribe(ApplicationStopped) { dao.close() }

        install(DefaultHeaders)
        install(CallLogging)
        install(ConditionalHeaders)
        install(PartialContent)
        install(Locations)
        install(WebSockets)
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(SweetNode::class.java.classLoader, "templates")
        }

        install(Sessions) {
            cookie<SweetSession>("SESSION") {
                transform(SessionTransportTransformerMessageAuthentication(hashKey))
            }
        }

        install(ContentNegotiation){
            gson {
                registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                setLongSerializationPolicy(LongSerializationPolicy.STRING)
                setPrettyPrinting()
            }
        }

        val hashFunction = { s: String -> hash(s) }

        install(Routing) {
            staticHandler()
            indexHandler(dao)
            userHandler(dao, hashFunction)
            sweetHandler(dao, hashFunction)
            chatHandler(dao)
            weatherHandler()
        }
    }

    override fun close() {
    }

    private fun hash(password: String): String {
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }

    private fun loadConfig(environment: ApplicationEnvironment) {
        val mode = environment.config.property("mode").getString()
        apiBaseUrl = environment.config.property("$mode.key.apiBaseUrl").getString()
        uploadDir = environment.config.property("$mode.dir.uploadDir").getString()
        cacheDir = environment.config.property("$mode.dir.cacheDir").getString()
        AppId = environment.config.property("$mode.key.AppId").getString()
        AppKey = environment.config.property("$mode.key.AppKey").getString()

        println("Environment:\n mode:$mode;\n upload-dir:$uploadDir;\n cache-dir:$cacheDir;\n api-base-url:$apiBaseUrl;\n app-id:$AppId;\n app-key:$AppKey")
    }

}

suspend fun ApplicationCall.redirect(location: Any) {
    val host = request.host() ?: "localhost"
    val portSpec = request.port().let { if (it == 80) "" else ":$it" }
    val address = host + portSpec

    respondRedirect("http://$address${application.locations.href(location)}")
}

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) = hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) = securityCode(date, user, hashFunction) == code && (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

