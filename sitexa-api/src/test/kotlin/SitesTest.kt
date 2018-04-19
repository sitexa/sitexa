import java.math.BigDecimal

fun main(args: Array<String>) {
    testGetSite(1)
    testSiteByCode(11)
    testChildrenById(2)
    testSitesByLevel(1)
    testSiteLatLng(338, BigDecimal(117.20),BigDecimal(39.13))
}

fun testGetSite(id: Int) {
    val s = dao.site(id)
    println("site:=$s")
}

fun testSiteByCode(code: Int) {
    val s = dao.siteByCode(code)
    println("site:$s")
}

fun testChildrenById(id: Int) {
    val l = dao.childrenById(id)
    l!!.forEach {
        println(it)
    }
}

fun testSitesByLevel(level: Int) {
    val l = dao.sitesByLevel(level)
    l!!.forEach {
        println(it)
    }
}

fun testSiteLatLng(id:Int,lat:BigDecimal?,lng:BigDecimal?){
    dao.updateLatLng(id,lat,lng)
}