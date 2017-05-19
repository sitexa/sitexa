import okhttp3.*
import org.jsoup.Jsoup
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by open on 14/05/2017.
 */


class Stock(val title: String, val links: List<String>)


interface StockService {
    @GET operator fun get(@Url url: HttpUrl): Call<Stock>
}


class StockAdapter : Converter<ResponseBody, Stock> {

    override fun convert(responseBody: ResponseBody): Stock {
        val document = Jsoup.parse(responseBody.string())
        val links = document.select("a[href]").map { it.attr("href") }
        return Stock(document.title(), Collections.unmodifiableList(links))
    }

    companion object {
        val FACTORY: Converter.Factory = object : Converter.Factory() {
            override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
                if (type === Stock::class.java) return StockAdapter()
                return null
            }
        }
    }
}


class StockCrawler(private val stockService: StockService) {
    private val fetchedUrls = Collections.synchronizedSet(LinkedHashSet<HttpUrl>())
    private val hostnames = ConcurrentHashMap<String, AtomicInteger>()

    private val file = File("crawler-out.txt")
    private val fos = FileOutputStream(file)
    private val ps = PrintStream(fos)

    fun crawlStock(url: HttpUrl) {
        // Skip hosts that we've visited many times.
        var hostnameCount = AtomicInteger()
        val previous = hostnames.putIfAbsent(url.host(), hostnameCount)
        if (previous != null) hostnameCount = previous
        if (hostnameCount.incrementAndGet() > 100) return

        // Asynchronously visit URL.
        stockService[url].enqueue(object : Callback<Stock> {
            override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                if (!response.isSuccessful) {
                    println(call.request().url().toString() + ": failed: " + response.code())
                    return
                }

                // Print this page's URL and title.
                val stock = response.body()
                val base = response.raw().request().url()
                ps.println(base.toString() + ": " + stock.title)

                // Enqueue its links for visiting.
                stock.links.map { base.resolve(it) }.filter { it != null && fetchedUrls.add(it) }.forEach { crawlStock(it) }
            }

            override fun onFailure(call: Call<Stock>, t: Throwable) {
                println(call.request().url().toString() + ": failed: " + t)
            }
        })
    }
}


fun main(args: Array<String>) {
    val dispatcher = Dispatcher(Executors.newFixedThreadPool(20))
    dispatcher.maxRequests = 20
    dispatcher.maxRequestsPerHost = 1

    val okHttpClient = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool(100, 30, TimeUnit.SECONDS))
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(HttpUrl.parse("http://localhost:8080"))
            .addConverterFactory(PageAdapter.FACTORY)
            .client(okHttpClient)
            .build()

    val pageService = retrofit.create(PageService::class.java)

    val crawler = Crawler(pageService)
    crawler.crawlPage(HttpUrl.parse("http://localhost:8080/"))
}