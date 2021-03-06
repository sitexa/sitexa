package com.sitexa.ktor.common

import com.google.gson.GsonBuilder
import org.joda.time.DateTime
import java.io.Serializable
import java.lang.reflect.Type

/**
 * Created by open on 06/05/2017.
 */

data class ApiResult(val code: Int = 0, val desc: String = "", val data: Any? = null) : Serializable

fun <T> ApiResult.data(aClass: Class<T>): T? {
    if (code == ApiCode.ERROR) return null
    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLenient()
            .create()
    return try {
        gson.fromJson<T>(data.toString(), aClass)
    } catch (e: Exception) {
        null
    }
}

fun <T> ApiResult.data(type: Type): T? {
    if (code == ApiCode.ERROR) return null
    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLenient()
            .create()
    return try {
        gson.fromJson<T>(data.toString(), type)
    } catch (e: Exception) {
        null
    }
}