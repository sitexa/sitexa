package com.sitexa.ktor.dao

import com.sitexa.ktor.dao.api.ApiResult
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Site
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import org.ehcache.CacheManagerBuilder
import org.ehcache.config.CacheConfigurationBuilder
import org.ehcache.config.ResourcePoolsBuilder
import org.ehcache.config.persistence.CacheManagerPersistenceConfiguration
import org.ehcache.config.units.EntryUnit
import org.ehcache.config.units.MemoryUnit
import java.io.File
import java.math.BigDecimal


/**
 * Created by open on 03/04/2017.
 *
 */


class DAOFacadeCache(val delegate: DAOFacade, val storagePath: File) : DAOFacade {
    val cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .with(CacheManagerPersistenceConfiguration(storagePath))
            .withCache("sweetsCache",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder<Int, Sweet>()
                            .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
                                    .heap(1000, EntryUnit.ENTRIES)
                                    .offheap(10, MemoryUnit.MB)
                                    .disk(100, MemoryUnit.MB, true)
                            )
                            .buildConfig(Int::class.javaObjectType, Sweet::class.java))
            .withCache("usersCache",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder<String, User>()
                            .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
                                    .heap(1000, EntryUnit.ENTRIES)
                                    .offheap(10, MemoryUnit.MB)
                                    .disk(100, MemoryUnit.MB, true)
                            )
                            .buildConfig(String::class.java, User::class.java))
            .withCache("mediasCache",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder<Int, Media>()
                            .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
                                    .heap(1000, EntryUnit.ENTRIES)
                                    .offheap(10, MemoryUnit.MB)
                                    .disk(100, MemoryUnit.MB, true)
                            )
                            .buildConfig(Int::class.javaObjectType, Media::class.java))
            .withCache("sitesCache",
                    CacheConfigurationBuilder.newCacheConfigurationBuilder<Int, Site>()
                            .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
                                    .heap(1000, EntryUnit.ENTRIES)
                                    .offheap(10, MemoryUnit.MB)
                                    .disk(100, MemoryUnit.MB, true)
                            )
                            .buildConfig(Int::class.javaObjectType, Site::class.java))
            .build(true)

    val sweetsCache = cacheManager.getCache("sweetsCache", Int::class.javaObjectType, Sweet::class.java)

    val usersCache = cacheManager.getCache("usersCache", String::class.java, User::class.java)

    val mediasCache = cacheManager.getCache("mediasCache", Int::class.javaObjectType, Media::class.java)

    val sitesCache = cacheManager.getCache("sitesCache", Int::class.javaObjectType, Site::class.java)

    override fun init() {
        delegate.init()
    }

    override fun countReplies(id: Int): Int {
        return delegate.countReplies(id)
    }

    override fun createSweet(user: String, text: String, replyTo: Int?): Int {
        val id = delegate.createSweet(user, text, replyTo)
        val sweet = delegate.getSweet(id)
        sweetsCache.put(id, sweet)
        return id
    }

    override fun deleteSweet(id: Int) {
        delegate.deleteSweet(id)
        sweetsCache.remove(id)
    }

    override fun updateSweet(id: Int, text: String) {
        delegate.updateSweet(id, text)
        sweetsCache.remove(id)
    }

    override fun getSweet(id: Int): Sweet {
        val cached = sweetsCache.get(id)
        if (cached != null) {
            return cached
        }

        val sweet = delegate.getSweet(id)
        sweetsCache.put(id, sweet)

        return sweet
    }

    override fun getReplies(id: Int): List<Sweet> {
        return delegate.getReplies(id)
    }

    override fun userSweets(userId: String): List<Int> {
        return delegate.userSweets(userId)
    }

    override fun createMedia(refId: Int, fileName: String, fileType: String?, title: String?, sortOrder: Int): Int {
        val id = delegate.createMedia(refId, fileName, fileType, title, sortOrder)
        val media = Media(id, refId, fileName, fileType, title, sortOrder)
        mediasCache.put(id, media)
        return id
    }

    override fun deleteMedia(id: Int) {
        delegate.deleteMedia(id)
        mediasCache.remove(id)
    }

    override fun getMedia(id: Int): Media? {
        val cached = mediasCache.get(id)
        if (cached != null) return cached

        val media = delegate.getMedia(id)
        mediasCache.put(id, media)
        return media
    }

    override fun getMedias(refId: Int): List<Int> {
        return delegate.getMedias(refId)
    }

    override fun login(userId: String, password: String): User? {
        val cached = usersCache.get(userId)
        val user = if (cached == null) {
            val dbUser = delegate.login(userId, password)
            if (dbUser != null) {
                usersCache.put(userId, dbUser)
            }
            dbUser
        } else {
            cached
        }

        return user
    }

    override fun user(userId: String): User? {
        val cached = usersCache.get(userId)
        val user = if (cached == null) {
            val dbUser = delegate.user(userId)
            if (dbUser != null) {
                usersCache.put(userId, dbUser)
            }
            dbUser
        } else {
            cached
        }

        return user
    }

    override fun userByMobile(mobile: String): User? {
        return delegate.userByMobile(mobile)
    }

    override fun userByEmail(email: String): User? {
        return delegate.userByEmail(email)
    }

    override fun createUser(user: User) {
        if (usersCache.get(user.userId) != null) {
            throw IllegalStateException("User already exist")
        }

        delegate.createUser(user)
        usersCache.put(user.userId, user)
    }

    override fun top(count: Int, page: Int): List<Int> {
        return delegate.top(count, page)
    }

    override fun latest(count: Int, page: Int): List<Int> {
        return delegate.latest(count, page)
    }

    override fun close() {
        try {
            delegate.close()
        } finally {
            cacheManager.close()
        }
    }

    override fun site(id: Int): Site? {
        val cached = sitesCache.get(id)

        return if (cached == null) {
            val dbSite = delegate.site(id)
            if (dbSite != null) {
                sitesCache.put(id, dbSite)
            }
            dbSite
        } else {
            cached
        }
    }

    override fun siteByCode(code: Int): Site? {
        return delegate.siteByCode(code)
    }

    override fun childrenById(id: Int): List<Site>? {
        return delegate.childrenById(id)
    }

    override fun childrenByCode(code: Int): List<Site>? {
        return delegate.childrenByCode(code)
    }

    override fun sitesByLevel(level: Int): List<Site>? {
        return delegate.sitesByLevel(level)
    }

    override fun updateLatLng(id: Int, lat: BigDecimal?, lng: BigDecimal?): ApiResult {
        return delegate.updateLatLng(id, lat, lng)
    }
}
