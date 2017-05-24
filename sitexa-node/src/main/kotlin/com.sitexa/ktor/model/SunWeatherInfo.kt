package com.sitexa.ktor.model

data class SunWeatherInfo(val sunInfo: SunInfo, val temperature: Double)

data class SunInfo(val sunrise: String, val sunset: String)
