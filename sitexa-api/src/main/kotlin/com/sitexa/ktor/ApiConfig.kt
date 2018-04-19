package com.sitexa.ktor

import com.zaxxer.hikari.HikariDataSource

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

var datasource = HikariDataSource().apply {
    maximumPoolSize = (dbConfig["pool"] as Int?)!!
    driverClassName = dbConfig["driver"] as String
    jdbcUrl = dbConfig["url"] as String
    isAutoCommit = dbConfig["autoCommit"] as Boolean
    addDataSourceProperty("user", dbConfig["user"] as String)
    addDataSourceProperty("password", dbConfig["password"] as String)
    addDataSourceProperty("dialect", dbConfig["dialect"] as String)
}
