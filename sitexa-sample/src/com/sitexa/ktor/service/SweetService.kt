package com.sitexa.ktor.service

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by open on 04/05/2017.
 *
 */

interface SweetApi {
    @GET("/sweet/{id}") fun singleSweet(@Path("id") id: Int): Call<Sweet>
    @GET("/sweet-component/{id}") fun sweetComponent(@Path("id") id: Int): Call<ApiResult>
    @GET("/sweet-top/{num}") fun topSweet(@Path("num") num: Int): Call<List<Sweet>>
    @GET("/sweet-latest/{num}") fun latestSweet(@Path("num") num: Int): Call<List<Sweet>>
    @GET("/sweet-reply-count/{id}") fun countSweetReplies(@Path("id") id: Int): Call<Int>

    @FormUrlEncoded @POST("/sweet-new") fun createSweet(@Field("user") user: String, @Field("text") text: String, @Field("replyTo") replyTo: Int?): Call<ApiResult>
    @GET("/sweet-del") fun deleteSweet(@Query ("id") id: Int): Call<ApiResult>
    @FormUrlEncoded @POST("/sweet-upd") fun updateSweet(@Field("id") id: Int, @Field("text") text: String): Call<ApiResult>

    @FormUrlEncoded @POST("/media-new") fun createMedia(@Field("refId") refId: Int, @Field("fileName") fileName: String, @Field("fileType") fileType: String?, @Field("title") title: String?, @Field("sortOrder") sortOrder: Int?): Call<ApiResult>
    @GET("/media-del") fun deleteMedia(@Query("id") id: Int): Call<ApiResult>
    @GET("/media/{name}/{type}") fun viewMedia(@Path("name") name: String, @Path("type") type: String): Call<ResponseBody>
    @GET("/media/{id}") fun getMedia(@Path("id") id: Int): Call<Media>
}

class SweetService : ApiService() {
    private val sweetApi = retrofit.create(SweetApi::class.java)

    fun getSweetSingle(id: Int): Sweet = sweetApi.singleSweet(id).execute().body()

    fun getSweetComponent(id: Int): Map<String, Any> {
        val map = HashMap<String, Any>()
        val call = sweetApi.sweetComponent(id)
        val apiResult = call.execute().body()

        val data = apiResult.data(Map::class.java)

        data!!.forEach { k, v ->
            if (k == "sweet") {
                val sweet = gson.fromJson<Sweet>(v.toString())
                map.put("sweet", sweet)
            } else if (k == "replies") {
                val replies = gson.fromJson<List<Sweet>>(v.toString(), object : TypeToken<List<Sweet>>() {}.type)
                map.put("replies", replies)
            } else if (k == "medias") {
                val medias = gson.fromJson<List<Media>>(v.toString(), object : TypeToken<List<Media>>() {}.type)
                map.put("medias", medias)
            }
        }
        return map
    }

    fun getTopSweet(num: Int): List<Sweet> = sweetApi.topSweet(num).execute().body()

    fun getLatestSweet(num: Int): List<Sweet> = sweetApi.latestSweet(num).execute().body()

    fun countReplies(id: Int): Int = sweetApi.countSweetReplies(id).execute().body()


    fun createSweet(user: String, text: String, replyTo: Int?=null): Int {
        var id: Int = -1
        val apiResult = sweetApi.createSweet(user, text, replyTo).execute().body()
        val code = apiResult.code()
        if (code == ApiCode.OK) {
            id = apiResult.data(Int::class.java)!!
        }
        return id
    }

    fun deleteSweet(id: Int): Boolean {
        val apiResult = sweetApi.deleteSweet(id).execute().body()
        return (apiResult.code() == ApiCode.OK)
    }

    fun updateSweet(id: Int, text: String): Boolean {
        val apiResult = sweetApi.updateSweet(id, text).execute().body()
        return (apiResult.code() == ApiCode.OK)
    }


    fun createMedia(refId: Int, fileName: String, fileType: String? = "unknown", title: String? = null, sortOrder: Int? = null): Int {
        var id: Int = -1
        val apiResult = sweetApi.createMedia(refId, fileName, fileType, title, sortOrder).execute().body()
        val code = apiResult.code()
        if (code == ApiCode.OK) {
            id = apiResult.data(Int::class.java)!!
        }
        return id
    }

    fun deleteMedia(id: Int): Boolean {
        val apiResult = sweetApi.deleteMedia(id).execute().body()
        return (apiResult.code() == ApiCode.OK)
    }

    fun viewMedia(name: String, type: String) {
        val call = sweetApi.viewMedia(name, type)
        val response = call.execute().body()
    }

    fun getMedia(id: Int): Media = sweetApi.getMedia(id).execute().body()
}
