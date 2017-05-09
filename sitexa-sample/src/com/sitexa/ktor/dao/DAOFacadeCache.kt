package com.sitexa.ktor.dao

import com.sitexa.ktor.model.*
import org.ehcache.*
import org.ehcache.config.*
import org.ehcache.config.persistence.*
import org.ehcache.config.units.*
import org.joda.time.*
import java.io.*


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
            .build(true)

    val sweetsCache = cacheManager.getCache("sweetsCache", Int::class.javaObjectType, Sweet::class.java)

    val usersCache = cacheManager.getCache("usersCache", String::class.java, User::class.java)

    val mediasCache = cacheManager.getCache("mediasCache", Int::class.javaObjectType, Media::class.java)

    override fun init() {
        delegate.init()
    }

    override fun countReplies(id: Int): Int {
        return delegate.countReplies(id)
    }

    override fun createSweet(user: String, text: String,replyTo:Int?): Int {
        val id = delegate.createSweet(user, text,replyTo)
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

    override fun getReplies(id: Int): List<Int> {
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

    override fun user(userId: String, hash: String?): User? {
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

        return when {
            user == null -> null
            hash == null -> user
            user.passwordHash == hash -> user
            else -> null
        }
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

    override fun top(count: Int): List<Int> {
        return delegate.top(count)
    }

    override fun latest(count: Int): List<Int> {
        return delegate.latest(count)
    }

    override fun close() {
        try {
            delegate.close()
        } finally {
            cacheManager.close()
        }
    }
}
