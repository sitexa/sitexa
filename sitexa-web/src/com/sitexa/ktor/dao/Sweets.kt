package com.sitexa.ktor.dao

import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

/**
 * Created by open on 03/04/2017.
 *
 */


object Sweets : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val user = varchar("user_id", 20).index()
    val date = datetime("date").default(DateTime.now())
    val replyTo = integer("reply_to").index().nullable()
    val directReplyTo = integer("direct_reply_to").index().nullable()
    val text = varchar("text", 1024)
}