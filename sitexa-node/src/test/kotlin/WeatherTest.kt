import com.github.kittinunf.fuel.httpGet
import com.github.salomonbrys.kotson.double
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonParser
import com.sitexa.ktor.dao.api.SunInfo
import com.sitexa.ktor.dao.api.SunInfoService
import com.sitexa.ktor.dao.api.TemperatureService
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.lang.Exception
import java.nio.charset.Charset

/**
 * Created by open on 23/05/2017.
 */


fun main(vararg: Array<String>) {
    val lat = 28.1791667
    val lng = 113.1136111

    val info = SunInfoService().getSunInfo(lat, lng)
    println("info:$info")

    val temp = TemperatureService().getTemperature(lat, lng)
    println("temp:$temp")
}

fun testjson() {

    val info1 = """{"results":{"sunrise":"2017-05-22T21:33:24+00:00","sunset":"2017-05-23T11:15:15+00:00","solar_noon":"2017-05-23T04:24:19+00:00","day_length":49311,"civil_twilight_begin":"2017-05-22T21:07:26+00:00","civil_twilight_end":"2017-05-23T11:41:13+00:00","nautical_twilight_begin":"2017-05-22T20:36:09+00:00","nautical_twilight_end":"2017-05-23T12:12:29+00:00","astronomical_twilight_begin":"2017-05-22T20:03:18+00:00","astronomical_twilight_end":"2017-05-23T12:45:21+00:00"},"status":"OK"}"""
    val temp1 = """{"coord":{"lon":113.11,"lat":28.18},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"base":"stations","main":{"temp":21,"pressure":1010,"humidity":77,"temp_min":21,"temp_max":21},"visibility":10000,"wind":{"speed":10,"deg":350},"clouds":{"all":90},"dt":1495519200,"sys":{"type":1,"id":7415,"message":0.0052,"country":"CN","sunrise":1495488821,"sunset":1495538115},"id":1804526,"name":"Langli","cod":200}"""

    val jsonInfo = JsonParser().parse(info1).obj
    val sunrise = jsonInfo["results"]["sunrise"].string
    val sunset = jsonInfo["results"]["sunset"].string

    println("sunrise:$sunrise;sunset:$sunset")

    val jsonTemp = JsonParser().parse(temp1).obj
    val temp2 = jsonTemp["main"]["temp"].double

    println("temp:$temp2")
}

fun getSunInfo(lat: Double, lon: Double): Promise<SunInfo, Exception> = task {
    val url = "http://api.sunrise-sunset.org/json?lat=$lat&lng=$lon&formatted=0"
    val (request, response, result) = url.httpGet().responseString()
    val jsonStr = String(response.data, Charset.forName("UTF-8"))
    val json = JsonParser().parse(jsonStr).obj
    val sunrise = json["results"]["sunrise"].string
    val sunset = json["results"]["sunset"].string
    val sunriseTime = DateTime.parse(sunrise)
    val sunsetTime = DateTime.parse(sunset)
    val formatter = DateTimeFormat.forPattern("HH:mm:ss").withZone(DateTimeZone.forID("Asia/Chongqing"))
    SunInfo(formatter.print(sunriseTime), formatter.print(sunsetTime))
}


fun getTemperature(lat: Double, lon: Double): Promise<Double, Exception> = task {
    val url = "http://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=d06f9fa75ebe72262aa71dc6c1dcd118&units=metric"
    val (request, response, result) = url.httpGet().responseString()
    val jsonStr = String(response.data, Charset.forName("UTF-8"))
    val json = JsonParser().parse(jsonStr).obj
    json["main"]["temp"].double
}
