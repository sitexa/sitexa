import com.sitexa.ktor.cacheDir
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeDatabase
import com.sitexa.ktor.datasource
import org.jetbrains.exposed.sql.Database
import java.io.File

/**
 * Created by open on 14/05/2017.
 */

val db = Database.connect(datasource)
val dao: DAOFacade = DAOFacadeCache(DAOFacadeDatabase(), File(cacheDir, "ehcache"))

fun main(vararg: Array<String>) {
    testTop()
}

fun testTop() {
    println("\n==========testTop")
    val s = dao.top(1, 3)
    s.forEach {
        println("it:$it")
    }
}