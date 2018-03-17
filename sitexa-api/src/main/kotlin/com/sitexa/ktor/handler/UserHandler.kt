package com.sitexa.ktor.handler


import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.SweetSession
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.model.User
import com.sitexa.ktor.service.createRandomStr
import com.sitexa.ktor.signVCode
import com.sitexa.ktor.testVCode
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.clear
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.joda.time.DateTime

/**
 * Created by open on 03/04/2017.
 *
 */

@Location("/user-info/{user}") data class UserInfo(val user: String)

@Location("/user/{user}") data class UserPage(val user: String)

@Location("/register")
data class Register(val userId: String = "", val mobile: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@Location("/login")
data class Login(val userId: String = "", val password: String = "", val error: String = "")

@Location("/logout") class Logout

@Location("/cpwd")
data class ChangePassword(val userId: String = "", val password: String = "", val newPassword: String = "")

@Location("/vcode")
data class VCode(val mobile: String = "", val vcode: String = "", val date: Long = 0, val sign: String = "")

@Location("/userByMobile/{mobile}") class UserByMobile(val mobile: String)

@Location("/userByEmail/{email}") class UserByEmail(val email: String)

fun Route.userHandler(dao: DAOFacade, hashFunction: (String) -> String) {
    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

    post<Register> {
        var result = ""
        if (it.password.length < 6 || !userNameValid(it.userId) || !emailValid(it.email) || !phoneValid(it.mobile)) {
            result += "UserId:${it.userId} invalid or password:${it.password} invalid or email:${it.email} invalid or mobile:${it.mobile} invalid."
        } else {
            val hash = hashFunction(it.password)
            val newUser = User(it.userId, it.mobile, it.email, it.displayName, hash)
            try {
                dao.createUser(newUser)
            } catch (e: Throwable) {
                if (dao.user(it.userId) != null) {
                    result += " User with the ID: ${it.userId} is already registered."
                } else if (dao.userByEmail(it.email) != null) {
                    result += " User with the email: ${it.email} is already registered."
                } else if (dao.userByMobile(it.mobile) != null) {
                    result += " User with the mobile: ${it.mobile} is already registered."
                } else {
                    result += " Failed to register user,$e"
                    application.log.error("Failed to register user", e)
                }
            }
        }
        call.respond(JsonResponse(mapOf("user" to it, "result" to result)))
    }
    post<Login> {
        val login = when {
            it.userId.length < 4 -> null
            it.password.length < 6 -> null
            !userNameValid(it.userId) -> null
            else -> dao.user(it.userId, hashFunction(it.password))
        }

        if (login == null) {
            call.respond(JsonResponse(mapOf("result" to -1)))
        } else {
            call.sessions.set(SweetSession(userId = login.userId))
            call.respond(JsonResponse(mapOf("user" to login, "result" to 1)))
        }
    }
    get<Logout> {
        call.sessions.clear<SweetSession>()
        call.respond(JsonResponse(mapOf("result" to "success")))
    }
    get<UserPage> {
        val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.user)

        if (pageUser == null) {
            call.respond(HttpStatusCode.NotFound.description("User ${it.user} doesn't exist"))
        } else {
            val sweets = dao.userSweets(it.user).map { dao.getSweet(it) }
            call.respond(JsonResponse(sweets))
        }
    }
    post<ChangePassword> {
        var result: ApiResult
        val user: User? = dao.user(it.userId, hashFunction(it.password))
        if (user == null) {
            result = ApiResult(code = 0, desc = "用户不存在")
        } else if (it.newPassword.length < 6) {
            result = ApiResult(code = 0, desc = "密码错误")
        } else {
            user.passwordHash = hashFunction(it.newPassword)
            try {
                dao.updateUser(user)
                result = ApiResult(code = 1, desc = "修改密码成功")
            } catch (e: Exception) {
                result = ApiResult(code = 0, desc = e.message!!)
                application.log.error(e.toString())
            }
        }
        call.respond(JsonResponse(result))
    }

    get<VCode> {
        val result: ApiResult
        if (it.mobile != "") {
            val user = dao.userByMobile(it.mobile)
            if (user != null) {
                val vcode = createRandomStr(true, 6)
                println("\nvcode:$vcode")
                //sendSms(it.mobile, vcode) //todo switch it

                val date = System.currentTimeMillis()
                val sign = call.signVCode(date, vcode, hashFunction)
                val data = gson.toJson(mapOf("sign" to sign, "date" to date))
                result = ApiResult(code = 1, desc = "success", data = data)
            } else {
                result = ApiResult(code = 0, desc = "fail", data = "手机号不存在")
            }
        } else {
            result = ApiResult(code = 0, desc = "fail", data = "手机号不存在")
        }
        call.respond(JsonResponse(result))
    }
    post<VCode> {
        val result = if (call.testVCode(it.date, it.vcode, it.sign, hashFunction))
            ApiResult(code = 1) else ApiResult(code = 0)
        call.respond(JsonResponse(result))
    }

    get<UserInfo> {
        val user: User? = dao.user(it.user)
        call.respond(JsonResponse(user!!))
    }
    get<UserByEmail> {
        val user: User? = dao.userByEmail(it.email)
        call.respond(JsonResponse(user!!))
    }
    get<UserByMobile> {
        val user: User? = dao.userByMobile(it.mobile)
        call.respond(JsonResponse(user!!))
    }
}


private val userIdPattern = "[a-zA-Z0-9_\\.]+".toRegex()
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)

private val emailPattern = "[a-zA-Z0-9_]+@[a-zA-Z0-9_]+([-.][a-zA-Z0-9_]+)".toRegex()
internal fun emailValid(email: String) = email.matches(emailPattern)

private val phonePattern = "\\d{11}|\\d{7,8}".toRegex()
internal fun phoneValid(phone: String) = phone.matches(phonePattern)