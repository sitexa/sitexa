package com.sitexa.ktor.dao

import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import java.io.Closeable

/**
 * Created by open on 19/04/2017.
 *
 */


interface DAOFacade : Closeable {
    fun init()
    fun countReplies(id: Int): Int
    fun createSweet(user: String, text: String, replyTo: Int? = null): Int
    fun deleteSweet(id: Int)
    fun updateSweet(id: Int, text: String)
    fun getSweet(id: Int): Sweet
    fun getReplies(id: Int): List<Int>
    fun userSweets(userId: String): List<Int>

    fun createMedia(refId: Int = -1, fileName: String, fileType: String? = null, title: String? = null, sortOrder: Int = 0): Int
    fun deleteMedia(id: Int)
    fun getMedia(id: Int): Media?
    fun getMedias(refId: Int): List<Int>

    fun login(userId: String, password: String): User?
    fun user(userId: String): User?
    fun userByEmail(email: String): User?
    fun userByMobile(mobile: String): User?
    fun createUser(user: User)
    fun top(count: Int = 10, page: Int = 1): List<Int>
    fun latest(count: Int = 10, page: Int = 1): List<Int>
}