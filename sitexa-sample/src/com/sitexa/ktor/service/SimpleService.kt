package com.sitexa.ktor.service

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

import java.io.IOException

/**
 * Created by open on 24/04/2017.

 */

object SimpleService {
    val API_URL = "https://api.github.com"

    data class Contributor(val login: String, val contributions: Int)

    interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        fun contributors(@Path("owner") owner: String, @Path("repo") repo: String): Call<List<Contributor>>
    }

    @Throws(IOException::class)
    @JvmStatic fun main(args: Array<String>) {
        val okClient = OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build()
        // Create a very simple REST adapter which points the GitHub API.
        val retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        // Create an instance of our GitHub API interface.
        val github = retrofit.create(GitHub::class.java)

        // Create a call instance for looking up Retrofit contributors.
        val call = github.contributors("square", "retrofit")

        // Fetch and print a list of the contributors to the library.
        val contributors = call.execute().body()

        for (contributor in contributors) {
            println(contributor.login + " (" + contributor.contributions + ")")
            println(contributor)
        }
    }
}