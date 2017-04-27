package com.sitexa.ktor.service

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.obj
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import com.squareup.moshi.Moshi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Created by open on 24/04/2017.
 *
 */

val BASE_URL = "http://localhost:8080"
val HEADERS = Headers.Builder().add("Accept", "application/json").build()

val headerInterceptor = Interceptor { chain ->
    chain.proceed(chain.request().newBuilder().addHeader("Accept", "application/json").build())
}
val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

class HeaderInterceptor(val name: String = "Accept", val value: String = "application/json") : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader(name, value).build()
        return chain.proceed(request)
    }
}

interface UserApi {
    @GET("/user/{userId}")
    fun userPage(@Path("userId") userId: String): Call<ResponseBody>

    @GET("/user/{userId}")
    fun userPage2(@Path("userId") userId:String):Call<List<Sweet>>

    @GET("/sweet/{id}")
    fun sweet(@Path("id") id: Int): Call<ResponseBody>

    @GET("/sweet/{id}")
    fun sweet2(@Path("id") id: Int): Call<Sweet>



}

fun login(userId: String, password: String): User? {
    /**
     * 用OkHttpClient,手工操作formBody,form item,request,request header,
     * 接收response后，用Gson处理结果，JsonParser操作JsonElement,gson进行类型转换。
     * kotson是对Gson的包装，没有改变任何Gson的功能，只是增加了"语法糖"，让代码写起来更简单。
     */

    val LOGIN_URL = "$BASE_URL/login"

    val client = OkHttpClient()
    val formBody = FormBody.Builder().add("userId", userId).add("password", password).build()
    val request = Request.Builder().url(LOGIN_URL).headers(HEADERS).post(formBody).build()
    val response = client.newCall(request).execute()

    val jsonStr = response.body().string()
    val result = JsonParser().parse(jsonStr).obj["result"].asInt
    if (result == -1) return null

    val jsonUser = JsonParser().parse(jsonStr).obj["user"]
    val user = GsonBuilder().create().fromJson<User>(jsonUser)

    return user
}

fun getSweet(id: Int): Sweet {
    /**
     * 使用GsonConverterFactory将ResponseBody转换为JsonObject。
     * 使用Moshi进行Json与对象的转换。Moshi的Adapter写法比Gson的TypeAdapter更简单灵活.
     * JodaTimeAdapter处理JsonElement与时间的转换，sweetAdapter处理JsonObject与对象的转换。
     */

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(headerInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            //ResponseBody中是JsonObject的封装。
            //此处【可以进行／不进行】JsonObject到Json串的转换，在后面的代码中
            //ResponseBody.string()都能得到合法的Json串。
            //.addConverterFactory(GsonConverterFactory.create())
            .build()

    val sitexaApi = retrofit.create(UserApi::class.java)

    val call = sitexaApi.sweet(id)
    val response = call.execute()
    val jsonString = (response.body()).string()

    val moshi = Moshi.Builder().add(JodaTimeAdapter()).build()
    val sweetAdapter = moshi.adapter<Sweet>(Sweet::class.java).lenient()

    return sweetAdapter.fromJson(jsonString)
}

fun getUserPage(userId: String): List<Sweet> {

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(HeaderInterceptor())
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            //ResponseBody中是JsonArray的封装。
            //此处不需要进行JsonArray到Json串的转换，在后面的代码中
            //ResponseBody.string()才能得到合法的Json串。
            //.addConverterFactory(GsonConverterFactory.create())
            .build()

    val sitexaApi = retrofit.create(UserApi::class.java)
    val call = sitexaApi.userPage(userId)

    val response = call.execute()
    //ResponseBody是JsonArray(元素是Sweet)的封装，.string()是合法的JsonObject。
    //getSweet中
    val jsonString = (response.body() as ResponseBody).string()

    //JsonElement Adapter: JsonElement -> DateTime
    val moshi = Moshi.Builder().add(JodaTimeAdapter()).build()
    val typeToken = object : TypeToken<List<Sweet>>() {}.type
    //JsonObject Adapter: JsonObject -> List<Sweet>
    val sweetsAdapter = moshi.adapter<List<Sweet>>(typeToken).lenient()

    return sweetsAdapter.fromJson(jsonString)
}

fun getSweet2(id:Int):Sweet{

    val gson = GsonBuilder().
            registerTypeAdapter(DateTime::class.java, JodaTypeAdapter())
            .create()

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(headerInterceptor)
            //.addInterceptor(loggingInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    val sitexaApi = retrofit.create(UserApi::class.java)
    val call = sitexaApi.sweet2(id)

    return call.execute().body()
}

fun getUserPage2(userId: String): List<Sweet> {

    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaTypeAdapter())
            .create()

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    val sitexaApi = retrofit.create(UserApi::class.java)
    val call = sitexaApi.userPage2(userId)
    return  call.execute().body()
}

fun main(vararg: Array<String>) {
    //val user = login("xnpeng", "pop007")
    //println(user)


    val sweet = getSweet2(9)
    println(sweet)

    //val sweets = getUserPage2("xnpeng")
    //sweets.forEach { item -> println(item) }
}

