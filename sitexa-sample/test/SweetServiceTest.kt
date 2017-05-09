import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.service.SweetService

/**
 * Created by open on 07/05/2017.
 */

fun main(vararg: Array<String>) {
    //getSingleSweet()
    //getSweetComponent()
    //testGetTopSweet()
    //testGetLatestSweet()
    //testCountReplies()

    //testCreateSweet()
    //testUpdateSweet()
    //testDeleteSweet()

    //testCreateMedia()
    //testDeleteMedia()
    //testGetMedia()
}

fun getSingleSweet() {

    println("\n#################getSingleSweet")

    val sweetSingle = SweetService().getSweetSingle(9)
    println("\nsweetSingle:$sweetSingle")
}

fun getSweetComponent() {
    println("\n#################getSweetComponent")

    val sweetComponent = SweetService().getSweetComponent(9)

    val sweet = sweetComponent["sweet"] as Sweet
    println("\nsweet:$sweet")

    val replies = sweetComponent["replies"] as List<*>
    replies.forEach { println("reply:$it") }

    val medias = sweetComponent["medias"] as List<*>
    medias.forEach { println("media:$it") }

}

fun testGetTopSweet() {
    println("\n#################testGetTopSweet")
    val sweets = SweetService().getTopSweet(10)
    sweets.forEach { println(it) }
}

fun testGetLatestSweet() {
    println("\n#################testGetLatestSweet")
    val sweets = SweetService().getLatestSweet(10)
    sweets.forEach { println(it) }
}

fun testCountReplies(){
    println("\n#################testCountReplies")
    val count = SweetService().countReplies(90)
    println("\ncount:$count")
}

fun testCreateSweet(){
    println("\n#################testCreateSweet")

    val user ="xnpeng"
    val text = "test create sweet again"
    val replyTo = 9

    val id = SweetService().createSweet(user,text)
    println("\nid:$id")
}

fun testUpdateSweet(){
    println("\n#################testUpdateSweet")
    val id = 23
    val text = "test create reply to 9"

    val res = SweetService().updateSweet(id,text)
    println("\nresult:$res")
}

fun testDeleteSweet(){
    println("\n#################testDeleteSweet")
    val res = SweetService().deleteSweet(24)
    println("\nresult:$res")
}

fun testCreateMedia(){
    println("\n#################testCreateMedia")
    val refId = 9
    val fileName = ""
    val fileType = ""
    val title = null
    val sortOrder = null

    val id = SweetService().createMedia(refId,fileName,fileType)
    println("\nid:$id")
}

fun testDeleteMedia(){
    println("\n#################testDeleteMedia")
    val res = SweetService().deleteMedia(5)
    println("\nres:$res")
}

fun testGetMedia(){
    println("\n#################testGetMedia")
    val media = SweetService().getMedia(4)
    println("\nmedia:$media")
}

fun testViewMedia(){

}