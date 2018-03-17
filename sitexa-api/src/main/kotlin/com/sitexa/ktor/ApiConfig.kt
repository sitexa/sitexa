package com.sitexa.ktor

/**
 * Created by open on 18/04/2017.
 *
 */

var uploadDir = "/Users/open/IdeaProjects/sitexa/uploads"

val dbConfig = mapOf("driver" to "org.mariadb.jdbc.Driver",
        "url" to "jdbc:mysql://192.168.2.108:3306/sitexa",
        "user" to "root",
        "password" to "pop007",
        "pool" to 20,
        "autoCommit" to false,
        "dialect" to "MysqlDialect")

var cacheDir = "target/apidb"



