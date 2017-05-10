import com.sitexa.ktor.service.FileService


/**
 * Created by open on 09/05/2017.
 */

fun main(vararg: Array<String>) {
    testUpload()
    //testDownload()
}

fun testUpload() {
    val id = FileService().upload()

    println(id)
}

fun testDownload() {
    FileService().download(2)
}
