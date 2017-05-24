package com.sitexa.ktor.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sitexa.ktor.dao.api.SunInfoService
import com.sitexa.ktor.dao.api.TemperatureService
import com.sitexa.ktor.model.SunWeatherInfo
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Route

/**
 * Created by open on 23/05/2017.
 *
 */

@location("/weather") class Weather(val lat: Double, val lng: Double)

fun Route.weatherHandler() {
    get<Weather> {
        val sunInfo = SunInfoService().getSunInfo(it.lat, it.lng)
        val tempInfo = TemperatureService().getTemperature(it.lat, it.lng)
        val info = SunWeatherInfo(sunInfo, tempInfo)
        val jsonResult = jacksonObjectMapper().writeValueAsString(info)
        call.respondText(jsonResult)
    }
}
