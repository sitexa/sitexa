package com.sitexa.ktor

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeDatabase
import com.sitexa.ktor.handler.*
import com.sitexa.ktor.model.User
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.auth.UserHashedTableAuth
import io.ktor.auth.authentication
import io.ktor.auth.basicAuthentication
import io.ktor.content.default
import io.ktor.content.files
import io.ktor.content.static
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.locations.Locations
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.decodeBase64
import io.ktor.util.hex
import org.jetbrains.exposed.sql.Database
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

data class SweetSession(val userId: String)
//data class SweetSession(val userId: String, val appId: String)

class SweetApi : AutoCloseable {

    val hashKey = hex("6819b57a326945c1968f45236589")
    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    lateinit var datasource: HikariDataSource

    fun Application.install() {

        loadConfig(environment)

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
        install(PartialContent)
        install(Locations)

        install(Sessions) {
            cookie<SweetSession>("SESSION") {
                transform(SessionTransportTransformerMessageAuthentication(hashKey))
            }
        }

        install(ContentNegotiation){
            gson {
                registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                setLongSerializationPolicy(LongSerializationPolicy.STRING)
                //setPrettyPrinting()
            }
        }

        val hashFunction = { s: String -> hash(s) }

        authentication { basicAuthentication("ktor") { hashedUserTable.authenticate(it) } }

        install(Routing) {
            userHandler(dao, hashFunction)
            sweetHandler(dao, hashFunction)
            mediaHandler(dao, hashFunction)
            fileHandler(dao, hashFunction)
            TestHandler(dao,hashFunction)
            static {
                files(".")
                default("index.html")
            }
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

    private fun loadConfig(environment: ApplicationEnvironment) {
        val mode = environment.config.property("mode").getString()
        uploadDir = environment.config.property("$mode.dir.uploadDir").getString()
        cacheDir = environment.config.property("$mode.dir.cacheDir").getString()
        val jdbcUrl = environment.config.property("database.url").getString()
        val dbuser = environment.config.property("database.user").getString()
        val dbpwd = environment.config.property("database.password").getString()
        val dbdialect = environment.config.property("database.dialect").getString()

        println(" Environment:\n mode:$mode;\n upload-dir:$uploadDir;\n cache-dir:$cacheDir;\n jdbcUrl=$jdbcUrl;\n dbuser=$dbuser;\n dbpwd=$dbpwd;\n dialect=$dbdialect;\n")
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
