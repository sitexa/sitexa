import com.sitexa.ktor.cacheDir
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeNetwork
import java.io.File

/**
 * Created by open on 14/05/2017.
 */


val dao: DAOFacade = DAOFacadeCache(DAOFacadeNetwork(), File(cacheDir, "ehcache"))

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