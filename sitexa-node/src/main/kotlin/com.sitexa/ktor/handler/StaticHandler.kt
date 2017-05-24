package com.sitexa.ktor.handler

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.resolveResource
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.routing.Route
import java.io.File

/**
 * Created by open on 03/04/2017.
 *
 */

@location("/styles/main.css") class MainCss

@location("/node/styles.css") class NodeCss

@location("/node/bundle.js") class BundleJs

val basedir = File("public")

fun Route.staticHandler() {

    static("public") {
        files(basedir)
        default("index.html")
    }

    get<MainCss> {
        call.respond(call.resolveResource("sweet.css")!!)
    }

    get<NodeCss> {
        call.respond(call.resolveResource("/node/styles.css")!!)
    }

    get<BundleJs> {
        call.respond(call.resolveResource("/node/bundle.js")!!)
    }

}
