package com.sitexa.ktor.handler


import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
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
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.joda.time.DateTime

/**
 * Created by open on 03/04/2017.
 *
 */

@Location("/user-info/{userId}") data class UserInfo(val userId: String = "")

@Location("/user/{user}") data class UserPage(val user: String ="")

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
        val post = call.receive<Parameters>()
        val password = post["password"]
        val userId = post["userId"]
        val email = post["email"]
        val mobile = post["mobile"]
        val displayName = post["displayName"]

        var result = ""
        if (password!!.length < 6 || !userNameValid(userId!!) || !emailValid(email!!) || !phoneValid(mobile!!)) {
            result += "UserId:$userId invalid or password invalid or email:$email invalid or mobile:$mobile invalid."
        } else {
            val hash = hashFunction(password)
            val newUser = User(userId, mobile, email, displayName!!, hash)
            try {
                dao.createUser(newUser)
            } catch (e: Throwable) {
                if (dao.user(userId) != null) {
                    result += " User with the ID: $userId is already registered."
                } else if (dao.userByEmail(email) != null) {
                    result += " User with the email: $email is already registered."
                } else if (dao.userByMobile(mobile) != null) {
                    result += " User with the mobile: $mobile is already registered."
                } else {
                    result += " Failed to register user,$e"
                    application.log.error("Failed to register user", e)
                }
            }
        }
        call.respond(mapOf("user" to it, "result" to result))
    }
    post<Login> {
        val post = call.receive<Parameters>()
        val userId = post["userId"]
        val password = post["password"]

        val login = when {
            userId!!.length < 4 -> null
            password!!.length < 6 -> null
            !userNameValid(userId) -> null
            else -> dao.user(userId, hashFunction(password))
        }

        if (login == null) {
            call.respond(mapOf("result" to -1))
        } else {
            call.sessions.set(SweetSession(userId = login.userId))
            call.respond(mapOf("user" to login, "result" to 1))
        }
    }
    get<Logout> {
        call.sessions.clear<SweetSession>()
        call.respond(mapOf("result" to "success"))
    }
    get<UserPage> {
        //val user = call.sessions.get<SweetSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.user)

        if (pageUser == null) {
            call.respond(HttpStatusCode.NotFound.description("User ${it.user} doesn't exist"))
        } else {
            val sweets = dao.userSweets(it.user).map { dao.getSweet(it) }
            call.respond(sweets)
        }
    }
    post<ChangePassword> {
        val post = call.receive<Parameters>()
        val userId = post["userId"]
        val password = post["password"]
        val newPassword = post["newPassword"]

        var result: ApiResult
        val user: User? = dao.user(userId!!, hashFunction(password!!))
        if (user == null) {
            result = ApiResult(code = 0, desc = "用户不存在")
        } else if (newPassword!!.length < 6) {
            result = ApiResult(code = 0, desc = "密码错误")
        } else {
            user.passwordHash = hashFunction(newPassword)
            try {
                dao.updateUser(user)
                result = ApiResult(code = 1, desc = "修改密码成功")
            } catch (e: Exception) {
                result = ApiResult(code = 0, desc = e.message!!)
                application.log.error(e.toString())
            }
        }
        call.respond(result)
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
        call.respond(result)
    }
    post<VCode> {
        val post = call.receive<Parameters>()
        val date = post["date"]!!.toLongOrNull()
        val vcode = post["vcode"]
        val sign = post["sign"]

        val result = if (call.testVCode(date!!, vcode!!, sign!!, hashFunction))
            ApiResult(code = 1) else ApiResult(code = 0)
        call.respond(result)
    }

    get<UserInfo> {
        println("userId:${it.userId}")
        val user: User? = dao.user(it.userId)
        call.respond(user!!)
    }
    get<UserByEmail> {
        val user: User? = dao.userByEmail(it.email)
        call.respond(user!!)
    }
    get<UserByMobile> {
        val user: User? = dao.userByMobile(it.mobile)
        call.respond(user!!)
    }
}


private val userIdPattern = "[a-zA-Z0-9_\\.]+".toRegex()
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)

private val emailPattern = "[a-zA-Z0-9_]+@[a-zA-Z0-9_]+([-.][a-zA-Z0-9_]+)".toRegex()
internal fun emailValid(email: String) = email.matches(emailPattern)

private val phonePattern = "\\d{11}|\\d{7,8}".toRegex()
internal fun phoneValid(phone: String) = phone.matches(phonePattern)