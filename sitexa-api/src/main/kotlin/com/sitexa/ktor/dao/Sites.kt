package com.sitexa.ktor.dao

import org.jetbrains.exposed.sql.Table

object Sites: Table(){
    val id = integer("id").primaryKey()
    val code = integer("code").uniqueIndex()
    val parentId = integer("parent_id")
    val name = varchar("name",200)
    val level = integer("level")
    val lat = decimal("lat",10,6)
    val lng = decimal("lng",10,6)
}