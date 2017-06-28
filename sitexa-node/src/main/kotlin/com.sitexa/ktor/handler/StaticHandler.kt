package com.sitexa.ktor.handler

import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.routing.Route

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
