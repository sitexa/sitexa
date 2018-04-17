package com.sitexa.ktor.handler


import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.User
import com.sitexa.ktor.redirect
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

/**
 * Created by open on 03/04/2017.
 *
 */


@Location("/user/{user}")
data class UserPage(val user: String)

@Location("/register")
data class Register(val userId: String = "", val mobile: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@Location("/login")
data class Login(val userId: String = "", val error: String = "")

@Location("/logout")
class Logout

fun Route.userHandler(dao: DAOFacade, hashFunction: (String) -> String) {

    post<Register> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user != null) call.redirect(UserPage(user.userId)) else {
            val post = call.receive<Parameters>()
            val userId = post["userId"]
            val email = post["email"]
            val displayName = post["displayName"]
            val password = post["password"]
            val mobile = post["mobile"]

            if (password!!.length < 6) {
                call.redirect(it.copy(error = "Password should be at least 6 characters long", password = ""))
            } else if (userId!!.length < 4) {
                call.redirect(it.copy(error = "Login should be at least 4 characters long", userId = ""))
            } else if (!userNameValid(userId)) {
                call.redirect(it.copy(error = "Login should be consists of digits, letters, dots or underscores", userId = ""))
            } else if (dao.user(userId) != null) {
                call.redirect(it.copy(error = "User with the following login is already registered", userId = ""))
            } else if (!emailValid(email!!)) {
                call.redirect(it.copy(error = "Email not valid", email = ""))
            } else {
                val hash = hashFunction(password)
                val newUser = User(userId, mobile!!, email, displayName!!, hash)

                try {
                    dao.createUser(newUser)
                } catch (e: Throwable) {
                    when {
                        dao.user(userId) != null -> call.redirect(it.copy(error = "User with the following login is already registered", password = ""))
                        dao.userByEmail(email) != null -> call.redirect(it.copy(error = "User with the following email $email is already registered", password = ""))
                        dao.userByMobile(mobile) != null -> call.redirect(it.copy(error = "User with the following mobile $mobile is already registered", password = ""))
                        else -> {
                            application.log.error("Failed to register user", e)
                            call.redirect(it.copy(error = "Failed to register", password = ""))
                        }
                    }
                }

                call.sessions.set(SweetSession(newUser.userId))
                call.redirect(UserPage(newUser.userId))
            }
        }
    }

    get<Register> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user != null) call.redirect(UserPage(user.userId))
        else call.respond(FreeMarkerContent("register.ftl", mapOf("pageUser" to User(it.userId, it.mobile, it.email, it.displayName, ""), "error" to it.error), ""))
    }

    get<Login> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user != null) {
            call.redirect(UserPage(user.userId))
        } else {
            call.respond(FreeMarkerContent("login.ftl", mapOf("userId" to it.userId, "error" to it.error), ""))
        }
    }

    post<Login> {

        val post = call.receive<Parameters>()
        val userId = post["userId"] ?: return@post call.redirect(it)
        val password = post["password"] ?: return@post call.redirect(it)

        val error = Login(userId)

        val login = when {
            userId.length < 4 -> null
            password.length < 6 -> null
            !userNameValid(userId) -> null
            else -> dao.login(userId, password)
        }

        if (login == null) call.redirect(error.copy(error = "Invalid username or password"))
        else {
            call.sessions.set(SweetSession(login.userId))
            call.redirect(UserPage(login.userId))
        }
    }

    get<Logout> {
        call.sessions.clear<SweetSession>()
        call.redirect(Index())
    }

    get<UserPage> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.user)

        if (pageUser == null) call.respond(HttpStatusCode.NotFound.description("User ${it.user} doesn't exist"))
        else {
            val sweets = dao.userSweets(it.user).map { dao.getSweet(it) }
            val etag = (user?.userId ?: "") + "_" + sweets.map { it.text.hashCode() }.hashCode().toString()

            val s = call.sessions.get<SweetSession>()
            println("UserPage==:$s,${s?.userId}")

            call.respond(FreeMarkerContent("user.ftl", mapOf("user" to user, "pageUser" to pageUser, "sweets" to sweets), etag))
        }
    }
}


private val userIdPattern = "[a-zA-Z0-9_\\.]+".toRegex()
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)

private val emailPattern = "[a-zA-Z0-9_]+@[a-zA-Z0-9_]+([-.][a-zA-Z0-9_]+)".toRegex()
internal fun emailValid(email: String) = email.matches(emailPattern)

private val phonePattern = "\\d{11}|\\d{7,8}".toRegex()
internal fun phoneValid(phone: String) = phone.matches(phonePattern)
