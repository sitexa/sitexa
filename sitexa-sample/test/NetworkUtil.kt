import com.sitexa.ktor.apiBaseUrl
import com.sitexa.ktor.dao.api.HeaderInterceptor
import com.sitexa.ktor.dao.api.loggingInterceptor
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkUtil {

    private val client = OkHttpClient.Builder()

    init {
        client.authenticator(authenticator)
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(30, TimeUnit.SECONDS)
    }
}

val authenticator = Authenticator { route, response ->
    if (responseCount(response) >= 3) {
        return@Authenticator null
    }
    val credential = Credentials.basic("name", "password")
    response.request().newBuilder().header("Authorization", credential).build()
}

private fun responseCount(response: Response): Int {
    var result = 1
    while ((response.priorResponse()) != null) {
        result++
    }
    return result
}

fun test(){
    val okClient = OkHttpClient().newBuilder()
            .authenticator(authenticator)
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


}