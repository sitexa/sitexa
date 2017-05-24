package com.sitexa.ktor.dao.api

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.obj
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by open on 24/04/2017.
 *
 */

interface UserApi {
    @GET("/user-info/{userId}") fun userInfo(@Path("userId") userId: String): Call<User>

    @GET("/user/{userId}")
    fun userPage(@Path("userId") userId: String): Call<ResponseBody>

    @GET("/user/{userId}")
    fun userPage2(@Path("userId") userId: String): retrofit2.Call<List<Sweet>>

    @FormUrlEncoded @POST("/login")
    fun login(@Field("userId") userId: String, @Field("password") password: String = ""): Call<ResponseBody>

    @FormUrlEncoded @POST("/register")
    fun register(@Field("userId") userId: String,
                 @Field("password") password: String,
                 @Field("email") email: String,
                 @Field("mobile") mobile: String,
                 @Field("displayName") displayName: String): Call<ResponseBody>

    @FormUrlEncoded @POST("/cpwd")
    fun changePassword(@Field("userId") userId: String,
                       @Field("password") password: String,
                       @Field("newPassword") newPassword: String): Call<ApiResult>

    @GET("/vcode")
    fun sendVCode(@Query("mobile") mobile: String): Call<ApiResult>

    @FormUrlEncoded @POST("/vcode")
    fun testVCode(@Field("vcode") vcode: String, @Field("date") date: Long, @Field("sign") sign: String): Call<ApiResult>

    @GET("/userByMobile/{mobile}") fun getUserByMobile(@Path("mobile") mobile: String): Call<User>
    @GET("/userByEmail/{email}") fun getUserByEmail(@Path("email") email: String): Call<User>
}

class UserService : ApiService() {

    private val userApi: UserApi = retrofit.create(UserApi::class.java)
    private val sweetListType = object : TypeToken<List<Sweet>>() {}.type

    @Deprecated("use getUserPage")
    fun getUserPageApiResult(userId: String): List<Sweet>? {
        val call = userApi.userPage(userId)
        val response = call.execute().body().string()
        val response_apiResult = ApiResult(data = response)
        val response_apiResult_dataList = response_apiResult.data<List<Sweet>>(sweetListType)
        return response_apiResult_dataList
    }

    @Deprecated("use getUserPage")
    fun getUserPageMoshi(userId: String): List<Sweet>? {
        val call = userApi.userPage(userId)
        val response = call.execute().body().string()

        val sweetsAdapter = moshi.adapter<List<Sweet>>(sweetListType).lenient()
        return sweetsAdapter.fromJson(response)
    }

    @Deprecated("use getUserPage")
    fun getUserPageGson(userId: String): List<Sweet>? {
        val call = userApi.userPage2(userId)
        return call.execute().body()
    }

    fun getUserPage(userId: String): List<Sweet> {
        val call = userApi.userPage2(userId)
        return call.execute().body()
    }

    fun register(user: User): User? {
        val call = userApi.register(user.userId, user.passwordHash, user.email, user.mobile, user.displayName)
        val response = call.execute().body().string()

        val result = JsonParser().parse(response).obj["result"].asString
        var newUser: User? = null
        if (result != "") {
            log.error(result)
        } else {
            val jsonUser = JsonParser().parse(response).obj["user"]
            newUser = Gson().fromJson<User>(jsonUser)
        }
        return newUser
    }

    fun login(userId: String, password: String): User? {

        println("UserService.login:user:$userId:$password")

        val call = userApi.login(userId, password)
        val response = call.execute().body().string()

        val result = JsonParser().parse(response).obj["result"].asInt
        if (result == -1) return null

        val jsonUser = JsonParser().parse(response).obj["user"]
        val user = Gson().fromJson<User>(jsonUser)

        return user
    }

    fun loginOrigin(userId: String, password: String): User? {
        /**
         * 用OkHttpClient,手工操作formBody,form item,request,request header,
         * 接收response后，用Gson处理结果，JsonParser操作JsonElement,gson进行类型转换。
         * kotson是对Gson的包装，没有改变任何Gson的功能，只是增加了"语法糖"，让代码写起来更简单。
         */

        val LOGIN_URL = "${com.sitexa.ktor.apiBaseUrl}/login"

        val client = OkHttpClient()
        val formBody = FormBody.Builder().add("userId", userId).add("password", password).build()
        val request = Request.Builder().url(LOGIN_URL).headers(HEADERS_JSON).post(formBody).build()
        val response = client.newCall(request).execute()

        val jsonStr = response.body().string()
        val result = JsonParser().parse(jsonStr).obj["result"].asInt
        if (result == -1) return null

        val jsonUser = JsonParser().parse(jsonStr).obj["user"]
        val user = com.google.gson.GsonBuilder().create().fromJson<User>(jsonUser)

        return user
    }

    fun changePassword(userId: String, password: String, newPassword: String): ApiResult {
        val call = userApi.changePassword(userId, password, newPassword)
        return call.execute().body()
    }

    fun sendVCode(mobile: String): ApiResult = userApi.sendVCode(mobile).execute().body()

    fun testVCode(vcode: String, date: Long, sign: String) = userApi.testVCode(vcode, date, sign).execute().body()

    fun getUserInfo(userId: String): User? = userApi.userInfo(userId).execute().body()

    fun getUserByMobile(mobile: String): User? = userApi.getUserByMobile(mobile).execute().body()

    fun getUserByEmail(email: String): User? = userApi.getUserByEmail(email).execute().body()
}
