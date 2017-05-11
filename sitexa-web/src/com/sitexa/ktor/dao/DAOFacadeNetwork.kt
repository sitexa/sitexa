package com.sitexa.ktor.dao

import com.sitexa.ktor.dao.api.SweetApiImpl
import com.sitexa.ktor.dao.api.UserApiImpl
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User


class DAOFacadeNetwork : DAOFacade {

    override fun init() {
    }

    override fun countReplies(id: Int): Int = SweetApiImpl().countReplies(id)

    override fun createSweet(user: String, text: String, replyTo: Int?): Int = SweetApiImpl().createSweet(user, text, replyTo)

    override fun deleteSweet(id: Int) {
        SweetApiImpl().deleteSweet(id)
    }

    override fun updateSweet(id: Int, text: String) {
        SweetApiImpl().updateSweet(id, text)
    }

    override fun getSweet(id: Int): Sweet = SweetApiImpl().getSweetSingle(id)

    override fun getReplies(id: Int): List<Int> = SweetApiImpl().getReplies(id)

    override fun userSweets(userId: String): List<Int> = SweetApiImpl().getUserSweets(userId)

    override fun createMedia(refId: Int, fileName: String, fileType: String?, title: String?, sortOrder: Int): Int
            = SweetApiImpl().createMedia(refId, fileName, fileType, title, sortOrder)

    override fun deleteMedia(id: Int) {
        SweetApiImpl().deleteMedia(id)
    }

    override fun getMedia(id: Int): Media? = SweetApiImpl().getMedia(id)

    override fun getMedias(refId: Int): List<Int> = SweetApiImpl().getMediasBySweet(refId)

    override fun login(userId: String, password: String): User? = UserApiImpl().login(userId, password)

    override fun user(userId: String): User? = UserApiImpl().getUserInfo(userId)

    override fun userByMobile(mobile: String): User? = UserApiImpl().getUserByMobile(mobile)

    override fun userByEmail(email: String): User? = UserApiImpl().getUserByEmail(email)

    override fun createUser(user: User) {
        UserApiImpl().register(user)
    }

    override fun top(count: Int): List<Int> = SweetApiImpl().getTopSweet(count)

    override fun latest(count: Int): List<Int> = SweetApiImpl().getLatestSweet(count)

    override fun close() {
    }
}
