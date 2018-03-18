import com.github.salomonbrys.kotson.obj
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.JodaGsonAdapter
import org.joda.time.DateTime
import java.io.Serializable
import java.lang.reflect.Type

/**
 * Created by open on 08/05/2017.
 *
 */


data class ApiResponse(private var code: Int = 0, private var desc: String = "", private var data: Any = Unit) : Serializable {

    constructor(json: String) : this() {
        code = JsonParser().parse(json).obj["code"].asInt
        desc = JsonParser().parse(json).obj["desc"].asString
        data = JsonParser().parse(json).obj["data"].asString
    }

    fun code()=this.code
    fun desc()=this.desc
    fun data()=this.data

    fun <T> data(aClass: Class<T>): T? {
        if (code == ApiCode.NETWORK_ERROR) return null
        val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                .setLenient()
                .create()
        try {
            return gson.fromJson<T>(data.toString(), aClass)
        } catch (e: Exception) {
            println(e.stackTrace)
            return null
        }
    }

    fun <T> data(type: Type): T? {
        if (code == ApiCode.NETWORK_ERROR) return null
        val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                .setLenient()
                .create()

        try {
            return gson.fromJson<T>(data.toString(), type)
        } catch(e: Exception) {
            println(e.stackTrace)
            return null
        }
    }
}
