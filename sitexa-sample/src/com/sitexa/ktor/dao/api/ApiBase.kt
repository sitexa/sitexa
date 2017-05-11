package com.sitexa.ktor.dao.api

import com.google.gson.GsonBuilder
import com.sitexa.ktor.apiBaseUrl
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.common.JodaMoshiAdapter
import com.squareup.moshi.Moshi
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by open on 09/05/2017.
 *
 */


val HEADERS_JSON = Headers.Builder().add("Accept", "application/json").build()

val HEADERS_MULTIPART = Headers.Builder().add("Content-Type", "application/octet-stream").build()

val headerJsonInterceptor = Interceptor { chain -> chain.proceed(chain.request().newBuilder().headers(HEADERS_JSON).build()) }

val headerMultipartInterceptor = Interceptor { chain -> chain.proceed(chain.request().newBuilder().headers(HEADERS_MULTIPART).build()) }

val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

class HeaderInterceptor(val name: String = "Accept", val value: String = "application/json") : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader(name, value).build()
        return chain.proceed(request)
    }
}

open class ApiService {

    internal val log = LoggerFactory.getLogger(this.javaClass)

    protected val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setPrettyPrinting()
            .create()
    protected val moshi = Moshi.Builder().add(JodaMoshiAdapter()).build()

    val okClient = OkHttpClient().newBuilder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
}