import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.service.SweetService

/**
 * Created by open on 07/05/2017.
 */

fun main(vararg: Array<String>) {
    //getSingleSweet()

    getSweetComponent()

    //getSweetBag()
}

fun getSingleSweet() {

    println("\n#################getSingleSweet")

    val sweetSingle = SweetService().getSweetSingle(9)
    println("\nsweetSingle:$sweetSingle")

    //val sweetMoshi = SweetService().getSingleSweetMoshi(9)
    //println("\nsweetMoshi:\n$sweetMoshi")

    //val sweetGson = SweetService().getSingleSweetGson(9)
    //println("\nsweetGson:\n$sweetGson")

}

fun getSweetBag() {

    println("\n#################getSweetBag")

    val sweet = SweetService().getSweetBagResponseBody(9)
    val s = sweet["sweet"] as Sweet
    val r = sweet["replies"] as List<*>
    val m = sweet["medias"] as List<*>

    println("\nsweet:$s")
    println("\nrs:$r")
    r.forEach { it -> println("r:${it as Sweet}") }
    m.forEach { it -> println("m:${it as Media}") }

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