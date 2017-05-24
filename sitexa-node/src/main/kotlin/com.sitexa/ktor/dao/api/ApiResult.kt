package com.sitexa.ktor.dao.api

import com.github.salomonbrys.kotson.obj
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.JodaGsonAdapter
import org.joda.time.DateTime
import java.io.Serializable
import java.lang.reflect.Type

/**
 * Created by open on 06/05/2017.
 * data 1 : String
 * data 2 : Class -> Json
 * data 3 : Map<String,String> -> Json, value -> Json. 如何系列化，就如何反系列化。
 */
data class ApiResult(private var code: Int = 0, private var desc: String = "", private var data: String = "") : Serializable {

    constructor(json: String) : this() {
        code = JsonParser().parse(json).obj["code"].asInt
        desc = JsonParser().parse(json).obj["desc"].asString
        data = JsonParser().parse(json).obj["data"].asString
    }

    fun code() = this.code
    fun desc() = this.desc
    fun data() = this.data

    fun <T> data(aClass: Class<T>): T? {
        if (code == ApiCode.ERROR) return null
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

    fun <T> data(type: Type): T? {
        if (code == ApiCode.ERROR) return null
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
