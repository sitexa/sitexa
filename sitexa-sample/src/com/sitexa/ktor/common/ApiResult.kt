package com.sitexa.ktor.common

import com.github.salomonbrys.kotson.obj
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.joda.time.DateTime
import java.io.Serializable
import java.lang.reflect.Type

/**
 * Created by open on 06/05/2017.
 *
 */
data class ApiResult(private var code: Int = 0, private var desc: String = "", private var data: String = "") : Serializable {

    constructor(json: String) : this() {
        code = JsonParser().parse(json).obj["code"].asInt
        desc = JsonParser().parse(json).obj["desc"].asString
        data = JsonParser().parse(json).obj["data"].asString
    }


    fun <T> data(aClass: Class<T>): T? {
        if (code == ApiCode.NETWORK_ERROR) return null
        val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                .setLenient()
                .create()
        try {
            return gson.fromJson<T>(data, aClass)
        } catch (e: Exception) {
            println(e.stackTrace)
            return null
        }
    }

    fun <T> dataList(type: Type): T? {
        if (code == ApiCode.NETWORK_ERROR) return null
        val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                .setLenient()
                .create()

        try {
            return gson.fromJson<T>(data, type)
        } catch(e: Exception) {
            println(e.stackTrace)
            return null
        }
    }
}
