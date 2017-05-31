package com.sitexa.ktor.handler

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.*
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.routing.Route
import java.io.File

/**
 * Created by open on 03/04/2017.
 *
 */

@location("/styles/main.css")
class MainCss

val basedir = File("public")

fun Route.staticHandler() {

    get<MainCss> {
        call.respond(call.resolveResource("sweet.css")!!)
    }


/*
    static("public") {
        files(basedir)
        default("index.html")
    }
*/
    static {
        staticRootFolder = basedir
        default("index.html")
        files(".")
    }

}
