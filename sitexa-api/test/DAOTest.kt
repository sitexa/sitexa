import com.sitexa.ktor.cacheDir
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeDatabase
import com.sitexa.ktor.hashKey
import org.jetbrains.exposed.sql.Database
import java.io.File
import javax.crypto.spec.SecretKeySpec

/**
 * Created by open on 14/05/2017.
 */

val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
val dao: DAOFacade = DAOFacadeCache(DAOFacadeDatabase(Database.connect(datasource)), File(cacheDir, "ehcache"))

fun main(vararg: Array<String>) {
    testTopSweets()
    testLatestSweets()
}

fun testTopSweets() {
    println("\n====================testTopSweets")
    val s = dao.topSweets(10, 1)
    s.forEach {
        println("it=$it")
    }
}


fun testLatestSweets() {
    println("\n====================testLatestSweets")
    val s = dao.latestSweets(10, 1)
    s.forEach {
        println("it=$it")
    }
}