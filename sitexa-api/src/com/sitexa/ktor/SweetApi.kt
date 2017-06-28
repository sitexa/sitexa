package com.sitexa.ktor

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeDatabase
import com.sitexa.ktor.handler.fileHandler
import com.sitexa.ktor.handler.mediaHandler
import com.sitexa.ktor.handler.sweetHandler
import com.sitexa.ktor.handler.userHandler
import com.sitexa.ktor.model.User
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.auth.UserHashedTableAuth
import org.jetbrains.ktor.auth.authentication
import org.jetbrains.ktor.auth.basicAuthentication
import org.jetbrains.ktor.content.TextContent
import org.jetbrains.ktor.features.ConditionalHeaders
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.features.PartialContentSupport
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
import org.jetbrains.ktor.sessions.SessionCookieTransformerMessageAuthentication
import org.jetbrains.ktor.sessions.SessionCookiesSettings
import org.jetbrains.ktor.sessions.withCookieByValue
import org.jetbrains.ktor.sessions.withSessions
import org.jetbrains.ktor.transform.transform
import org.jetbrains.ktor.util.decodeBase64
import org.jetbrains.ktor.util.hex
import org.joda.time.DateTime
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Created by open on 19/04/2017.
 *
 */

class JsonResponse(val data: Any)

data class Session(val userId: String)
//data class Session(val userId: String, val appId: String)

class SweetApi : AutoCloseable {

    val hashKey = hex("6819b57a326945c1968f45236589")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    lateinit var datasource: HikariDataSource

    fun Application.install() {

        uploadDir = environment.config.property("dir.uploadDir").getString()
        cacheDir = environment.config.property("dir.cacheDir").getString()

        datasource = HikariDataSource().apply {
            maximumPoolSize = environment.config.property("database.poolSize").getString().toInt()
            driverClassName = environment.config.property("database.driverClass").getString()
            jdbcUrl = environment.config.property("database.url").getString()
            isAutoCommit = environment.config.property("database.autoCommit").getString().toBoolean()
            addDataSourceProperty("user", environment.config.property("database.user").getString())
            addDataSourceProperty("password", environment.config.property("database.password").getString())
            addDataSourceProperty("dialect", environment.config.property("database.dialect").getString())
        }

        val dao: DAOFacade = DAOFacadeCache(DAOFacadeDatabase(Database.connect(datasource)), File(cacheDir, "ehcache"))

        dao.init()
        install(DefaultHeaders)
        install(CallLogging)
        install(ConditionalHeaders)
        install(PartialContentSupport)
        install(Locations)

        withSessions<Session> {
            withCookieByValue {
                settings = SessionCookiesSettings(transformers = listOf(SessionCookieTransformerMessageAuthentication(hashKey)))
            }
        }

        val hashFunction = { s: String -> hash(s) }

        intercept(ApplicationCallPipeline.Infrastructure) { call ->
            if (call.request.acceptItems().any { it.value == "application/json" }) {
                call.transform.register<JsonResponse> { value ->
                    log.debug(value.data.toString())
                    TextContent(gson.toJson(value.data), ContentType.Application.Json)
                }
            }
        }

        authentication { basicAuthentication("ktor") { hashedUserTable.authenticate(it) } }

        install(Routing) {
            userHandler(dao, hashFunction)
            sweetHandler(dao, hashFunction)
            mediaHandler(dao, hashFunction)
            fileHandler(dao, hashFunction)
        }

    }

    override fun close() {
        datasource.close()
    }

    fun hash(password: String): String {
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }

}

//todo:this function does not work. why?
suspend fun ApplicationCall.respondJson(data: Any) = respond(JsonResponse(data))

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


fun ApplicationCall.signVCode(date: Long, vcode: String, hashFunction: (String) -> String) =
        hashFunction("$date:$vcode:${request.host()}:${refererHost()}")

fun ApplicationCall.testVCode(date: Long, vcode: String, sign: String, hashFunction: (String) -> String) =
        signVCode(date, vcode, hashFunction) == sign
                && (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES) }


val hashedUserTable = UserHashedTableAuth(table = mapOf(
        "test" to decodeBase64("VltM4nfheqcJSyH887H+4NEOm2tDuKCl83p5axYXlF0=") // sha256 for "test"
))