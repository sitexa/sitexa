package com.sitexa.ktor.dao

import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import org.joda.time.DateTime
import java.io.Closeable

/**
 * Created by open on 19/04/2017.
 *
 */


interface DAOFacade : Closeable {
    fun init()
    fun countReplies(id: Int): Int
    fun createSweet(user: String, text: String, replyTo: Int? = null, date: DateTime = DateTime.now()): Int
    fun deleteSweet(id: Int)
    fun updateSweet(id: Int, text: String)
    fun getSweet(id: Int): Sweet
    fun getReplies(id: Int): List<Int>
    fun userSweets(userId: String): List<Int>
    fun topSweets(count: Int = 10, page: Int = 0): List<Int>
    fun latestSweets(count: Int = 10, page: Int = 0): List<Int>
    fun top(count: Int = 10, page: Int = 0): List<Sweet>
    fun latest(count: Int = 10, page: Int = 0): List<Sweet>

    fun createMedia(refId: Int? = -1, fileName: String, fileType: String? = "unknown", title: String? = null, sortOrder: Int? = null): Int
    fun deleteMedia(id: Int)
    fun getMedia(id: Int): Media?
    fun getMedias(refId: Int): List<Int>

    fun user(userId: String, hash: String? = null): User?
    fun userByEmail(email: String): User?
    fun userByMobile(mobile: String): User?
    fun createUser(user: User)
    fun updateUser(user: User)
}