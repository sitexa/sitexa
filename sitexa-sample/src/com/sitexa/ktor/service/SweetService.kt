package com.sitexa.ktor.service

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.obj
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.common.JodaMoshiAdapter
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by open on 04/05/2017.
 *
 */

interface SweetApi {
    @GET("/sweet/{id}")
    fun singleSweet(@Path("id") id: Int): Call<ResponseBody>

    @GET("/sweet/{id}")
    fun singleSweet2(@Path("id") id: Int): Call<Sweet>

    /**
     * 该接口转换成功。
     */
    @GET("/sweet-bag/{id}")
    fun sweetBag(@Path("id") id: Int): Call<ResponseBody>

    /**
     * 该接口转换不成功。不论是Call<Map<String,Any>>还是Call<Map<String,String>>,
     * 都得不到有效的JsonObject,无法直接转换。
     */
    @GET("/sweet-bag/{id}")
    fun sweetBag2(@Path("id") id: Int): Call<Map<String, String>>
}


fun getSingleSweet(id: Int): Sweet {
    /**
     * 使用GsonConverterFactory将ResponseBody转换为JsonObject。
     * 使用Moshi进行Json与对象的转换。Moshi的Adapter写法比Gson的TypeAdapter更简单灵活.
     * JodaTimeAdapter处理JsonElement与时间的转换，sweetAdapter处理JsonObject与对象的转换。
     */

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(headerInterceptor)
            //.addInterceptor(loggingInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            //ResponseBody中是JsonObject的封装。
            //此处【可以进行／不进行】JsonObject到Json串的转换，在后面的代码中
            //ResponseBody.string()都能得到合法的Json串。
            //.addConverterFactory(GsonConverterFactory.create())
            .build()

    val sweetApi = retrofit.create(SweetApi::class.java)

    val call = sweetApi.singleSweet(id)
    val response = call.execute()
    val jsonString = (response.body()).string()

    val moshi = Moshi.Builder().add(JodaMoshiAdapter()).build()
    val sweetAdapter = moshi.adapter<Sweet>(Sweet::class.java).lenient()

    return sweetAdapter.fromJson(jsonString)
}

fun getSingleSweet2(id: Int): Sweet {

    val gson = GsonBuilder().
            registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
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

    val sweetApi = retrofit.create(SweetApi::class.java)
    val call = sweetApi.singleSweet2(id)

    return call.execute().body()
}

fun getSweetBag(id: Int): Map<String, Any> {

    val gson = GsonBuilder().
            registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .create()

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(headerInterceptor)
            //.addInterceptor(loggingInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val sweetApi = retrofit.create(SweetApi::class.java)

    val call = sweetApi.sweetBag(id)
    val response = call.execute()
    val jsonString = (response.body()).string()

    val sweetJson = JsonParser().parse(jsonString).obj["sweet"]
    val repliesJson = JsonParser().parse(jsonString).obj["replies"]
    val mediaJson = JsonParser().parse(jsonString).obj["medias"]

    val sweet = gson.fromJson<Sweet>(sweetJson)
    val replies = gson.fromJson<List<Sweet>>(repliesJson.toString(), object : TypeToken<List<Sweet>>() {}.type)
    val medias = gson.fromJson<List<Media>>(mediaJson.toString(), object : TypeToken<List<Media>>() {}.type)

    val result = mapOf("sweet" to sweet, "replies" to replies, "medias" to medias)
    return result
}

@Deprecated("")
fun getSweetBag2(id: Int): Map<String, Any> {

    val sweetType = object : TypeToken<Sweet>() {}.type
    val listSweetType = object : TypeToken<List<Sweet>>() {}.type
    val listMediaType = object : TypeToken<List<Media>>() {}.type

    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
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

    val sweetApi = retrofit.create(SweetApi::class.java)

    val call = sweetApi.sweetBag2(id)
    val response = call.execute()
    val map = response.body()
    //println("\nmap:$map")


    val sweetJson = map["sweet"]
    val repliesJson = map["replies"]
    val mediaJson = map["medias"]

    //println("\nsweetJson:$sweetJson")
    //println("\nrepliesJson:$repliesJson")
    //println("\nmediaJson:$mediaJson")

    val sweet = gson.fromJson<Sweet>(sweetJson, sweetType)
    val replies = gson.fromJson<List<Sweet>>(repliesJson, listSweetType)
    val medias = gson.fromJson<List<Media>>(mediaJson, listMediaType)

    val result = mapOf("sweet" to sweet, "replies" to replies, "medias" to medias)

    return result
}

fun main(vararg: Array<String>) {
    val sweet1 = getSingleSweet(9)
    println("\nsweet1:\n$sweet1")

    val sweet2 = getSingleSweet2(9)
    println("\nsweet2:\n$sweet2")

    val sweet3 = getSweetBag(9)
    println("\nsweet3:\n$sweet3")

    val sweet = getSweetBag(9)
    val s = sweet["sweet"] as Sweet
    val r = sweet["replies"] as List<*>
    val m = sweet["medias"] as List<*>

    println("\nsweet:$s")
    println("\nrs:$r")
    r.forEach { it -> println("r:${it as Sweet}") }
    m.forEach { it -> println("m:${it as Media}") }

    //val sweet4 = getSweetBag2(9)
    //println("\nsweet4:\n$sweet4")
}