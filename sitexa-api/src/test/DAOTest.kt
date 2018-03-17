import com.sitexa.ktor.cacheDir
import com.sitexa.ktor.dao.DAOFacade
import com.sitexa.ktor.dao.DAOFacadeCache
import com.sitexa.ktor.dao.DAOFacadeDatabase
import org.jetbrains.exposed.sql.Database
import org.junit.Test
import java.io.File

/**
 * Created by open on 14/05/2017.
 */

class DAOTest {


    @Test
    fun testTopSweets() {
        val dao: DAOFacade = DAOFacadeCache(DAOFacadeDatabase(Database.connect(datasource)), File(cacheDir, "ehcache"))
        val s = dao.topSweets(10, 1)
        assert(s.isNotEmpty())
    }

    @Test
    fun testLatestSweets() {
        val dao: DAOFacade = DAOFacadeCache(DAOFacadeDatabase(Database.connect(datasource)), File(cacheDir, "ehcache"))
        val s = dao.latestSweets(10, 1)
        assert(s.isNotEmpty())
    }
}