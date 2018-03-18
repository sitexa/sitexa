package com.sitexa.ktor


import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.chat.chatHandler
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeNetwork
import com.sitexa.ktor.handler.indexHandler
import com.sitexa.ktor.handler.staticHandler
import com.sitexa.ktor.handler.sweetHandler
import com.sitexa.ktor.handler.userHandler
import com.sitexa.ktor.model.User
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.hex
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

data class SweetSession(val userId: String)

class SweetWeb : AutoCloseable {

    val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

    val dao: DAOFacade = DAOFacadeCache(DAOFacadeNetwork(), File(cacheDir, "ehcache"))

    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    fun Application.install() {
        dao.init()
        install(DefaultHeaders)
        install(CallLogging)
        install(ConditionalHeaders)
        install(PartialContentSupport)
        install(Locations)
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(SweetWeb::class.java.classLoader, "templates")
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

        intercept(ApplicationCallPipeline.Infrastructure) {
            //for chat
            if (call.sessions.get<SweetSession>() == null) {
                //call.sessions.set(SweetSession(nextNonce()))
                //call.redirect(Login())
            }
        }

        install(Routing) {
            staticHandler()
            indexHandler(dao)
            userHandler(dao, hashFunction)
            sweetHandler(dao, hashFunction)
            chatHandler()
        }
    }

    override fun close() {
    }

    fun hash(password: String): String {
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
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

