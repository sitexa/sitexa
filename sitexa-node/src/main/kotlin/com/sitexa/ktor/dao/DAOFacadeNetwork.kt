package com.sitexa.ktor.dao

import com.sitexa.ktor.dao.api.SiteService
import com.sitexa.ktor.dao.api.SweetService
import com.sitexa.ktor.dao.api.UserService
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Site
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import java.math.BigDecimal


class DAOFacadeNetwork : DAOFacade {

    override fun init() {
    }

    override fun countReplies(id: Int): Int = SweetService().countReplies(id)

    override fun createSweet(user: String, text: String, replyTo: Int?): Int = SweetService().createSweet(user, text, replyTo)

    override fun deleteSweet(id: Int) {
        SweetService().deleteSweet(id)
    }

    override fun updateSweet(id: Int, text: String) {
        SweetService().updateSweet(id, text)
    }

    override fun getSweet(id: Int): Sweet = SweetService().getSweetSingle(id)

    override fun getReplies(id: Int): List<Sweet> = SweetService().getReplies(id)

    override fun userSweets(userId: String): List<Int> = SweetService().getUserSweets(userId)

    override fun createMedia(refId: Int, fileName: String, fileType: String?, title: String?, sortOrder: Int): Int
            = SweetService().createMedia(refId, fileName, fileType, title, sortOrder)

    override fun deleteMedia(id: Int) {
        SweetService().deleteMedia(id)
    }

    override fun getMedia(id: Int): Media? = SweetService().getMedia(id)

    override fun getMedias(refId: Int): List<Int> = SweetService().getMediasBySweet(refId)

    override fun login(userId: String, password: String): User? = UserService().login(userId, password)

    override fun user(userId: String): User? = UserService().getUserInfo(userId)

    override fun userByMobile(mobile: String): User? = UserService().getUserByMobile(mobile)

    override fun userByEmail(email: String): User? = UserService().getUserByEmail(email)

    override fun createUser(user: User) {
        UserService().register(user)
    }

    override fun top(count: Int, page: Int): List<Int> = SweetService().getTopSweet(count, page)

    override fun latest(count: Int, page: Int): List<Int> = SweetService().getLatestSweet(count, page)

    override fun close() {
    }

    override fun site(id: Int): Site? = SiteService().site(id)

    override fun siteByCode(code: Int): Site?=SiteService().siteByCode(code)

    override fun childrenById(id: Int): List<Site>? = SiteService().childrenById(id)

    override fun childrenByCode(code: Int): List<Site>? = SiteService().childrenByCode(code)

    override fun sitesByLevel(level: Int): List<Site>? = SiteService().siteByLevel(level)

    override fun updateLatLng(id: Int, lat: BigDecimal?, lng: BigDecimal?) = SiteService().updateSiteLatLng(id,lat,lng)
}
