package com.sitexa.ktor.handler

import io.ktor.application.call
import io.ktor.content.resolveResource
import io.ktor.locations.get
import io.ktor.locations.Location
import io.ktor.response.respond
import io.ktor.routing.Route

/**
 * Created by open on 03/04/2017.
 *
 */

@Location("/styles/main.css")
class MainCss

fun Route.staticHandler() {

    get<MainCss> {
        call.respond(call.resolveResource("sweet.css")!!)
    }

}
