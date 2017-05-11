import com.sitexa.ktor.dao.api.FileApiImpl


/**
 * Created by open on 09/05/2017.
 */

fun main(vararg: Array<String>) {
    testUpload()
    //testDownload()
}

fun testUpload() {
    val id = FileApiImpl().upload()

    println(id)
}

fun testDownload() {
    FileApiImpl().download(2)
}
