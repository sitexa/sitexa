package com.sitexa.ktor.handler

import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.routing.Route
import java.io.File

/**
 * Created by open on 03/04/2017.
 *
 */

val basedir = File("public")

fun Route.staticHandler() {

    static("public") {
        files(basedir)
        default("index.html")
    }
}
