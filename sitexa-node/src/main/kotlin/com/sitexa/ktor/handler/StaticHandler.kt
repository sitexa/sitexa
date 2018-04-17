package com.sitexa.ktor.handler

import io.ktor.content.default
import io.ktor.content.files
import io.ktor.content.static
import io.ktor.routing.Route

/**
 * Created by open on 03/04/2017.
 *
 */

fun Route.staticHandler() {
    static("public") {
        files("web")
        default("index.html")
    }
}
