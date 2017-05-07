package com.sitexa.ktor.model

import org.joda.time.DateTime
import java.io.Serializable

/**
 * Created by open on 03/04/2017.
 *
 */

data class Sweet(val id: Int, val userId: String, val text: String, val date: DateTime, val replyTo: Int?) : Serializable
