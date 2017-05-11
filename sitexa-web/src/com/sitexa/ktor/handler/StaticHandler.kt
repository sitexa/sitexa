package com.sitexa.ktor.handler

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.resolveResource
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.routing.Route

/**
 * Created by open on 03/04/2017.
 *
 */

@location("/styles/main.css")
class MainCss

fun Route.staticHandler() {

    get<MainCss> {
        call.respond(call.resolveResource("sweet.css")!!)
    }

}
