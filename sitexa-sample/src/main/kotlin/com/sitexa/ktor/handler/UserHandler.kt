package com.sitexa.ktor.handler


import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.User
import com.sitexa.ktor.redirect
import io.ktor.application.*
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.*

/**
 * Created by open on 03/04/2017.
 *
 */


@Location("/user/{user}")
data class UserPage(val user: String)

@Location("/register")
data class Register(val userId: String = "", val mobile: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@Location("/login")
data class Login(val userId: String = "", val password: String = "", val error: String = "")

@Location("/logout")
class Logout

fun Route.userHandler(dao: DAOFacade, hashFunction: (String) -> String) {
    post<Register> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user != null) {
            call.redirect(UserPage(user.userId))
        } else {

            println("userId:${it.userId},email:${it.email},displayName:${it.displayName},password:${it.password}")
            if (it.password.length < 6) {
                call.redirect(it.copy(error = "Password should be at least 6 characters long", password = ""))
            } else if (it.userId.length < 4) {
                call.redirect(it.copy(error = "Login should be at least 4 characters long", password = ""))
            } else if (!userNameValid(it.userId)) {
                call.redirect(it.copy(error = "Login should be consists of digits, letters, dots or underscores", password = ""))
            } else if (dao.user(it.userId) != null) {
                call.redirect(it.copy(error = "User with the following login is already registered", password = ""))
            } else {
                val hash = hashFunction(it.password)
                val newUser = User(it.userId, it.mobile, it.email, it.displayName, hash)

                try {
                    dao.createUser(newUser)
                } catch (e: Throwable) {
                    if (dao.user(it.userId) != null) {
                        call.redirect(it.copy(error = "User with the following login is already registered", password = ""))
                    } else if (dao.userByEmail(it.email) != null) {
                        call.redirect(it.copy(error = "User with the following email ${it.email} is already registered", password = ""))
                    } else if (dao.userByMobile(it.mobile) != null) {
                        call.redirect(it.copy(error = "User with the following mobile ${it.mobile} is already registered", password = ""))
                    } else {
                        application.log.error("Failed to register user", e)
                        call.redirect(it.copy(error = "Failed to register", password = ""))
                    }
                }

                call.sessions.set(SweetSession(newUser.userId))
                call.redirect(UserPage(newUser.userId))
            }
        }
    }
    get<Register> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        if (user != null) {
            call.redirect(UserPage(user.userId))
        } else {
            call.respond(FreeMarkerContent("register.ftl", mapOf("pageUser" to User(it.userId, it.mobile, it.email, it.displayName, ""), "error" to it.error), ""))
        }
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
        println("\nuserHandler:user:${it.userId}:${it.password}")
        val login = when {
            it.userId.length < 4 -> null
            it.password.length < 6 -> null
            !userNameValid(it.userId) -> null
            else -> dao.login(it.userId, it.password)
        }

        if (login == null) {
            call.redirect(it.copy(password = "", error = "Invalid username or password"))
        } else {
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

        if (pageUser == null) {
            call.respond(HttpStatusCode.NotFound.description("User ${it.user} doesn't exist"))
        } else {
            val sweets = dao.userSweets(it.user).map { dao.getSweet(it) }
            val etag = (user?.userId ?: "") + "_" + sweets.map { it.text.hashCode() }.hashCode().toString()

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
