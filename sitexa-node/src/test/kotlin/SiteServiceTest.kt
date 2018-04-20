import com.sitexa.ktor.dao.api.SiteService
import java.math.BigDecimal

fun main(args: Array<String>) {
    //getSiteById(1)
    //getSiteByCode(11)
    //getSiteByLevel(4)
    //getChildrenByCode(43)
    //getChildrenById(25579)
    updateSiteLatLng(25579, BigDecimal(28.21), BigDecimal(113.001))
}

fun getSiteById(id: Int) {
    val site = SiteService().site(id)
    println("site:$site")
}

fun getSiteByCode(code: Int) {
    val site = SiteService().siteByCode(code)
    println("site:$site")
}

fun getSiteByLevel(level: Int) {
    val sites = SiteService().siteByLevel(level)
    println("site-by-level::${sites.size}")
}

fun getChildrenByCode(code: Int) {
    val sites = SiteService().childrenByCode(code)
    println("children-by-code:${sites.size};$sites")
}

fun getChildrenById(id: Int) {
    val sites = SiteService().childrenById(id)
    println("children-by-id:${sites.size};$sites")
}

fun updateSiteLatLng(id: Int, lat: BigDecimal?, lng: BigDecimal?) {
    val result = SiteService().updateSiteLatLng(id, lat, lng)
    println("updateSiteLatLng:$result")
}