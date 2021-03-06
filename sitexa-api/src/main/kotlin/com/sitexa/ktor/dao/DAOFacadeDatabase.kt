package com.sitexa.ktor.dao

import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Site
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.math.BigDecimal


class DAOFacadeDatabase : DAOFacade {

    override fun init() {
        transaction {
            create(Users, Sweets, Medias, Sites)
        }
    }

    override fun countReplies(id: Int): Int {
        return transaction {
            Sweets.slice(Sweets.id.count()).select {
                Sweets.replyTo.eq(id)
            }.single()[Sweets.id.count()]
        }
    }

    override fun createSweet(user: String, text: String, replyTo: Int?, date: DateTime): Int {
        return transaction {
            Sweets.insert {
                it[Sweets.user] = user
                it[Sweets.date] = date
                it[Sweets.replyTo] = replyTo
                it[Sweets.text] = text
            } get Sweets.id
        }
    }

    override fun deleteSweet(id: Int) {
        transaction {
            Sweets.deleteWhere { Sweets.id.eq(id) }
        }
    }

    override fun updateSweet(id: Int, text: String) {
        transaction {
            Sweets.update({ Sweets.id eq id }) {
                it[Sweets.text] = text
            }
        }
    }

    override fun getSweet(id: Int) = transaction {
        val row = Sweets.select { Sweets.id.eq(id) }.single()
        Sweet(id, row[Sweets.user], row[Sweets.text], row[Sweets.date], row[Sweets.replyTo])
    }

    override fun getReplies(id: Int) = transaction {
        Sweets.slice(Sweets.id).select { Sweets.replyTo.eq(id) }
                .orderBy(Sweets.date, false).limit(100).map { it[Sweets.id] }
    }

    override fun userSweets(userId: String) = transaction {
        Sweets.slice(Sweets.id).select { Sweets.user.eq(userId) and Sweets.replyTo.isNull() }
                .orderBy(Sweets.date, false).limit(100).map { it[Sweets.id] }
    }

    override fun createMedia(refId: Int?, fileName: String, fileType: String?, title: String?, sortOrder: Int?): Int {
        return transaction {
            Medias.insert {
                it[Medias.refId] = refId
                it[Medias.fileName] = fileName
                it[Medias.fileType] = fileType
                it[Medias.title] = title
                it[Medias.sortOrder] = sortOrder
            } get Medias.id
        }
    }

    override fun deleteMedia(id: Int) {
        transaction {
            Medias.deleteWhere { Medias.id.eq(id) }
        }
    }

    override fun getMedia(id: Int) = transaction {
        Medias.select { Medias.id.eq(id) }
                .mapNotNull { row -> Media(row[Medias.id], row[Medias.refId], row[Medias.fileName], row[Medias.fileType], row[Medias.title], row[Medias.sortOrder]) }
                .singleOrNull()
    }

    override fun getMedias(refId: Int) = transaction {
        Medias.slice(Medias.id).select { Medias.refId.eq(refId) }
                .orderBy(Medias.sortOrder, false)
                .limit(100)
                .map { it[Medias.id] }
    }


    override fun user(userId: String, hash: String?) = transaction {
        Users.select { Users.id.eq(userId) }
                .mapNotNull {
                    if (hash == null || it[Users.passwordHash] == hash) {
                        User(userId, it[Users.mobile], it[Users.email], it[Users.displayName], it[Users.passwordHash])
                    } else {
                        null
                    }
                }
                .singleOrNull()
    }

    override fun userByMobile(mobile: String) = transaction {
        Users.select { Users.mobile.eq(mobile) }
                .map { User(it[Users.id], mobile, it[Users.email], it[Users.displayName], it[Users.passwordHash]) }.singleOrNull()
    }

    override fun userByEmail(email: String) = transaction {
        Users.select { Users.email.eq(email) }
                .map { User(it[Users.id], it[Users.mobile], email, it[Users.displayName], it[Users.passwordHash]) }.singleOrNull()
    }

    override fun createUser(user: User) = transaction {
        Users.insert {
            it[Users.id] = user.userId
            it[Users.displayName] = user.displayName
            it[Users.email] = user.email
            it[Users.mobile] = user.mobile
            it[Users.passwordHash] = user.passwordHash
        }
        Unit
    }

    override fun updateUser(user: User) {
        transaction {
            Users.update({ Users.id eq user.userId }) {
                it[Users.displayName] = user.displayName
                it[Users.email] = user.email
                it[Users.mobile] = user.mobile
                it[Users.passwordHash] = user.passwordHash
            }
            Unit
        }
    }

    override fun topSweets(count: Int, page: Int): List<Int> = transaction {
        // note: in a real application you shouldn't do it like this
        //   as it may cause database outages on big data
        //   so this implementation is just for demo purposes
        val start = (page - 1) * count
        val k2 = Sweets.alias("k2")
        Sweets.join(k2, JoinType.LEFT, Sweets.id, k2[Sweets.replyTo])
                .slice(Sweets.id, k2[Sweets.id].count())
                .selectAll()
                .groupBy(Sweets.id)
                .orderBy(k2[Sweets.id].count(), isAsc = false)
                .having { k2[Sweets.id].count().greater(0) }
                .limit(count, start)
                .map { it[Sweets.id] }
    }

    override fun top(count: Int, page: Int): List<Sweet> = topSweets(count, page).map { getSweet(it) }

    override fun latest(count: Int, page: Int): List<Sweet> = latestSweets(count, page).map { getSweet(it) }

    override fun latestSweets(count: Int, page: Int) = transaction {
        val start = (page - 1) * count
        Sweets.slice(Sweets.id).select { Sweets.replyTo.isNull() }
                .orderBy(Sweets.date, false)
                .limit(count, start).map { it[Sweets.id] }
    }

    fun latest2(count: Int): List<Int> = transaction {
        var attempt = 0
        var allCount: Int? = null

        for (minutes in generateSequence(2) { it * it }) {
            attempt++

            val dt = DateTime.now().minusMinutes(minutes)

            val all = Sweets.slice(Sweets.id)
                    .select { Sweets.date.greater(dt) and Sweets.replyTo.isNull() }
                    .orderBy(Sweets.date, false)
                    .limit(count)
                    .map { it[Sweets.id] }

            if (all.size >= count) {
                return@transaction all
            }
            if (attempt > 10 && allCount == null) {
                allCount = Sweets.slice(Sweets.id.count()).selectAll().count()
                if (allCount <= count) {
                    return@transaction Sweets.slice(Sweets.id).selectAll().map { it[Sweets.id] }
                }
            }
        }

        emptyList()
    }

    override fun close() {
    }

    override fun site(id: Int): Site? = transaction {
        Sites.select { Sites.id.eq(id) }
                .mapNotNull { Site(id, it[Sites.code], it[Sites.parentId], it[Sites.name], it[Sites.level], it[Sites.lat], it[Sites.lng]) }
                .singleOrNull()
    }

    override fun siteByCode(code: Int): Site? = transaction {
        Sites.select { Sites.code.eq(code) }
                .mapNotNull { Site(it[Sites.id], it[Sites.code], it[Sites.parentId], it[Sites.name], it[Sites.level], it[Sites.lat], it[Sites.lng]) }
                .singleOrNull()
    }

    override fun childrenById(id: Int): List<Site>? = transaction {
        val s = site(id)
        childrenByCode(s!!.code)
    }

    override fun childrenByCode(code: Int): List<Site>? = transaction {
        Sites.select { Sites.parentId.eq(code) }
                .mapNotNull { Site(it[Sites.id], it[Sites.code], it[Sites.parentId], it[Sites.name], it[Sites.level], it[Sites.lat], it[Sites.lng]) }
                .toList()
    }

    override fun sitesByLevel(level: Int): List<Site>? = transaction {
        Sites.select { Sites.level.eq(level) }
                .mapNotNull { Site(it[Sites.id], it[Sites.code], it[Sites.parentId], it[Sites.name], it[Sites.level], it[Sites.lat], it[Sites.lng]) }
                .toList()
    }

    override fun updateLatLng(id: Int, lat: BigDecimal?, lng: BigDecimal?) = transaction {
        Sites.update({ Sites.id.eq(id) }) {
            it[Sites.lat] = lat
            it[Sites.lng] = lng
        }
        Unit
    }
}
