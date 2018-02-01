package com.sitexa.ktor.dao.api


import com.sitexa.ktor.dao.PublishedDate
import com.sitexa.ktor.dao.Result
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by open on 11/06/2017.
 */

interface GankApi {

    @GET("api/data/{type}/{pageSize}/{pageNumber}")
    fun getData(@Path("type") type: String,
                @Path("pageSize") pageSize: Int,
                @Path("pageNumber") pageNumber: Int): Observable<Result>

    @GET("api/day/{year}/{month}/{day}")
    fun getDataByDate(@Path("year") year: String,
                      @Path("month") month: String,
                      @Path("day") day: String)

    @GET("api/day/{date}")
    fun getDataByDate(@Path("date") date: String): Observable<ResponseBody>

    @GET("history")
    fun getHistory(): Observable<ResponseBody>

    @GET("api/day/history")
    fun getPublishedDate(): Observable<PublishedDate>


    companion object Factory {
        fun create(): GankApi {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://gank.io/")
                    .build()

            return retrofit.create(GankApi::class.java)
        }
    }
}