package com.sitexa.ktor.dao.api

import com.github.salomonbrys.kotson.double
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.sitexa.ktor.common.JodaGsonAdapter
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by open on 23/05/2017.
 *
 */


data class SunWeatherInfo(val sunInfo: SunInfo, val temperature: Double)

data class SunInfo(val sunrise: String, val sunset: String)

interface SunInfoApi {
    @GET("/json") fun
            getSunInfo(@Query("lat") lat: Double,
                       @Query("lng") lng: Double,
                       @Query("formatted") formatted: Int = 0): Call<ResponseBody>
}

interface TemperatureApi {
    @GET("/data/2.5/weather")
    fun getTemperature(@Query("lat") lat: Double,
                       @Query("lon") lng: Double,
                       @Query("appid") appid: String = "d06f9fa75ebe72262aa71dc6c1dcd118",
                       @Query("units") units: String = "metric"): Call<ResponseBody>
}

class SunInfoService {
    private val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setPrettyPrinting()
            .create()
    private val baseUrl = "http://api.sunrise-sunset.org"
    private val okClient = OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()
    private val retrofit = Retrofit.Builder().client(okClient).baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).build()

    private val sunInfoApi = retrofit.create(SunInfoApi::class.java)

    fun getSunInfo(lat: Double, lng: Double): SunInfo {
        val jsonStr = sunInfoApi.getSunInfo(lat, lng).execute().body().string()
        val json = JsonParser().parse(jsonStr).obj
        val sunrise = json["results"]["sunrise"].string
        val sunset = json["results"]["sunset"].string
        val sunriseTime = DateTime.parse(sunrise)
        val sunsetTime = DateTime.parse(sunset)
        val formatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(DateTimeZone.forID("Asia/Chongqing"))
        return SunInfo(formatter.print(sunriseTime), formatter.print(sunsetTime))
    }

}

class TemperatureService {
    private val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setPrettyPrinting()
            .create()
    private val baseUrl = "http://api.openweathermap.org"
    private val okClient = OkHttpClient().newBuilder().addInterceptor(loggingInterceptor).build()
    private val retrofit = Retrofit.Builder().client(okClient).baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).build()

    private val temperatureApi = retrofit.create(TemperatureApi::class.java)

    fun getTemperature(lat: Double, lng: Double): Double {
        val jsonStr = temperatureApi.getTemperature(lat, lng).execute().body().string()
        val json = JsonParser().parse(jsonStr).obj
        return json["main"]["temp"].double
    }
}
