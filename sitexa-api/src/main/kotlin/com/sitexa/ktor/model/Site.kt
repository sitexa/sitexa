package com.sitexa.ktor.model

import java.io.Serializable
import java.math.BigDecimal

data class Site(val id: Int, val code: Int, val parentId: Int, val name: String, val level: Int, var lat: BigDecimal? = null, var lng: BigDecimal? = null) : Serializable