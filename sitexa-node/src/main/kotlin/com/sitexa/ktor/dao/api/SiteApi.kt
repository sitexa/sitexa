package com.sitexa.ktor.dao.api

import com.sitexa.ktor.model.Site
import retrofit2.Call
import retrofit2.http.*
import java.math.BigDecimal

interface SiteApi {

    @GET("/site/{id}")
    fun site(@Path("id") id: Int): Call<Site>

    @GET("/siteByCode/{code}")
    fun siteByCode(@Path("code") code: Int): Call<Site>

    @GET("/childrenById/{id}")
    fun childrenById(@Path("id") id: Int): Call<List<Site>>

    @GET("/childrenByCode/{code}")
    fun childrenByCode(@Path("code") code: Int): Call<List<Site>>

    @GET("/siteByLevel/{level}")
    fun siteByLevel(@Path("level") level: Int): Call<List<Site>>

    @FormUrlEncoded
    @POST("/updateSiteLatLng")
    fun updateSiteLatLng(@Field("id") id: Int, @Field("lat") lat: BigDecimal?, @Field("lng") lng: BigDecimal?): Call<ApiResult>

}

class SiteService : ApiService() {

    private val siteApi = retrofit.create(SiteApi::class.java)

    fun site(id: Int): Site = siteApi.site(id).execute().body()

    fun siteByCode(code: Int): Site = siteApi.siteByCode(code).execute().body()

    fun childrenById(id: Int): List<Site> = siteApi.childrenById(id).execute().body()

    fun childrenByCode(code: Int): List<Site> = siteApi.childrenByCode(code).execute().body()

    fun siteByLevel(level: Int): List<Site> = siteApi.siteByLevel(level).execute().body()

    fun updateSiteLatLng(id: Int, lat: BigDecimal?, lng: BigDecimal?): ApiResult = siteApi.updateSiteLatLng(id, lat, lng).execute().body()
}