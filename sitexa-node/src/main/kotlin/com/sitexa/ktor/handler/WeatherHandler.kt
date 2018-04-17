package com.sitexa.ktor.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sitexa.ktor.dao.api.SunInfoService
import com.sitexa.ktor.dao.api.SunWeatherInfo
import com.sitexa.ktor.dao.api.TemperatureService
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respondText
import io.ktor.routing.Route

/**
 * Created by open on 23/05/2017.
 *
 */

@Location("/weather")
class Weather(val lat: Double = 0.0, val lng: Double = 0.0)

fun Route.weatherHandler() {
    get<Weather> {
        val sunInfo = SunInfoService().getSunInfo(it.lat, it.lng)
        val tempInfo = TemperatureService().getTemperature(it.lat, it.lng)
        val info = SunWeatherInfo(sunInfo, tempInfo)
        val jsonResult = jacksonObjectMapper().writeValueAsString(info)
        call.respondText(jsonResult)
    }
}
