package com.sitexa.ktor.model

import java.io.Serializable

/**
 * Created by open on 03/04/2017.
 *
 */

data class User(val userId: String, var mobile: String, var email: String, var displayName: String, var passwordHash: String) : Serializable