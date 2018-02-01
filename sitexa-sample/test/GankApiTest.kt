import com.sitexa.ktor.dao.PublishedDate
import com.sitexa.ktor.dao.api.GankApi

/**
 * Created by open on 11/06/2017.
 */


fun main(args: Array<String>) {
    testApi()
}

fun testApi() {
    val api = GankApi.create()
    api.getPublishedDate().subscribe({ result ->
        parseResult(result)
    }, { error ->
        parseError(error)

    }, {
        parseComplete()
    })

}

private fun parseResult(result: PublishedDate) {
    val err = result.error
    val res: List<String> = result.results
    println("err:$err")
    println("res:$res")

}

private fun parseError(error: Throwable) {
    println(error.message)
}

private fun parseComplete() {
    println("completed")
}