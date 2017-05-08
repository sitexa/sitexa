package com.sitexa.ktor.service

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.obj
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.BASE_URL
import com.sitexa.ktor.common.ApiResult
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
    fun singleSweetResponseBody(@Path("id") id: Int): Call<ResponseBody>

    @GET("/sweet/{id}")
    fun singleSweet(@Path("id") id: Int): Call<Sweet>

    /**
     * 该接口转换成功。
     */
    @GET("/sweet-bag/{id}")
    fun sweetBagResponseBody(@Path("id") id: Int): Call<ResponseBody>

    /**
     * 该接口转换不成功。不论是Call<Map<String,Any>>还是Call<Map<String,String>>,
     * 都得不到有效的JsonObject,无法直接转换。
     */
    @GET("/sweet-component/{id}")
    fun sweetComponent(@Path("id") id: Int): Call<ApiResult>
}

class SweetService : ApiService() {
    private val sweetApi = retrofit.create(SweetApi::class.java)

    @Deprecated("use getSweetSingle")
    fun getSingleSweetMoshi(id: Int): Sweet {
        val call = sweetApi.singleSweetResponseBody(id)
        val response = call.execute().body()
        val jsonString = response.string()
        val sweetAdapter = moshi.adapter<Sweet>(Sweet::class.java).lenient()
        return sweetAdapter.fromJson(jsonString)
    }

    @Deprecated("use getSweetSingle")
    fun getSingleSweetGson(id: Int): Sweet {
        val call = sweetApi.singleSweet(id)
        return call.execute().body()
    }

    @Deprecated("use getSweetComponent")
    fun getSweetBagResponseBody(id: Int): Map<String, Any> {
        val call = sweetApi.sweetBagResponseBody(id)
        val response = call.execute().body()
        val jsonString = response.string()

        val sweetJson = JsonParser().parse(jsonString).obj["sweet"]
        val repliesJson = JsonParser().parse(jsonString).obj["replies"]
        val mediaJson = JsonParser().parse(jsonString).obj["medias"]

        val sweet = gson.fromJson<Sweet>(sweetJson)
        val replies = gson.fromJson<List<Sweet>>(repliesJson, object : TypeToken<List<Sweet>>() {}.type)
        val medias = gson.fromJson<List<Media>>(mediaJson, object : TypeToken<List<Media>>() {}.type)

        val result = mapOf("sweet" to sweet, "replies" to replies, "medias" to medias)
        return result
    }

    fun getSweetSingle(id: Int): Sweet {
        val call = sweetApi.singleSweet(id)
        return call.execute().body()
    }

    fun getSweetComponent(id: Int): Map<String, Any> {
        val map = HashMap<String,Any>()
        val call = sweetApi.sweetComponent(id)
        val apiResult = call.execute().body()

        val data = apiResult.data(Map::class.java)

        data!!.forEach { k, v ->
            if (k == "sweet") {
                val sweet = gson.fromJson<Sweet>(v.toString())
                map.put("sweet",sweet)
            } else if (k == "replies") {
                val replies = gson.fromJson<List<Sweet>>(v.toString(), object : TypeToken<List<Sweet>>() {}.type)
                map.put("replies",replies)
            } else if (k == "medias") {
                val medias = gson.fromJson<List<Media>>(v.toString(), object : TypeToken<List<Media>>() {}.type)
                map.put("medias",medias)
            }
        }
        return map
    }

}
