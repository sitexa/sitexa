import com.github.salomonbrys.kotson.fromJson
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.dao.api.ApiResult
import com.sitexa.ktor.model.Media
import com.sitexa.ktor.model.Sweet
import org.joda.time.DateTime

/**
 * Created by open on 08/05/2017.
 */

fun main(vararg: Array<String>) {
    //testStringData()

    //testObjData()

    //testMapData()

    //testJsonData()

    testDeepJsonData()
}

fun testStringData() {
    val apiResult = ApiResult(code = 1, desc = "success", data = "Simple string data")
    println("\napiResult:$apiResult")

    val json1 = gson.toJson(apiResult)
    println("\njson1:$json1")

    val obj1 = gson.fromJson<ApiResult>(json1)
    println("\nobj1:$obj1")


}

fun testObjData() {
    val sweet = Sweet(id = 1, userId = "xnpeng", text = "this is a sweet", date = DateTime.now(), replyTo = null)
    val apiResponse = ApiResponse(code = 1, desc = "object data", data = sweet)
    println("\napiResponse:$apiResponse")

    val json = gson.toJson(apiResponse)
    println("\njson:$json")

    val obj = gson.fromJson<ApiResponse>(json)
    println("\nobj:$obj")
}

fun testMapData() {
    println("\n#########################testMapData")
    val sweet = Sweet(id = 1, userId = "xnpeng", text = "this is a sweet", date = DateTime.now(), replyTo = null)
    val reply1 = Sweet(id = 2, userId = "xnpeng", text = "this is a reply", date = DateTime.now(), replyTo = 1)
    val reply2 = Sweet(id = 3, userId = "xnpeng", text = "this is a reply 2", date = DateTime.now(), replyTo = 1)
    val replies = listOf(reply1, reply2)
    val media = Media(id = 1, refId = 1, fileName = "media1", fileType = "video", title = "video title", sortOrder = 0)
    val medias = listOf(media)

    val data = mapOf("sweet" to sweet, "replies" to replies, "medias" to medias)

    val apiResponse = ApiResponse(code = 1, desc = "map data", data = data)
    println("\napiResponse:$apiResponse")

    val json = gson.toJson(apiResponse)
    println("\njson:$json")

    val obj = gson.fromJson<ApiResponse>(json)
    println("\nobj:$obj")
}

fun testJsonData() {
    println("\n#########################testJsonData")
    val sweet = Sweet(id = 1, userId = "xnpeng", text = "this is a sweet", date = DateTime.now(), replyTo = null)
    val reply1 = Sweet(id = 2, userId = "xnpeng", text = "this is a reply for sweet=1", date = DateTime.now(), replyTo = 1)
    val reply2 = Sweet(id = 3, userId = "xnpeng", text = "this is a reply for \"sweet's id%1 \"", date = DateTime.now(), replyTo = 1)
    val replies = listOf(reply1, reply2)
    val media = Media(id = 1, refId = 1, fileName = "media1", fileType = "video", title = "video title", sortOrder = 0)
    val medias = listOf(media)

    val data = mapOf("sweet" to sweet, "replies" to replies, "medias" to medias)
    val json_data = gson.toJson(data)

    val apiResult = ApiResult(code = 1, desc = "json data", data = json_data)
    println("\napiResult:$apiResult")

    val json = gson.toJson(apiResult)
    println("\njson:$json")

    val obj = gson.fromJson<ApiResult>(json)
    println("\nobj:$obj")

    val obj_data = obj.data
    println("\nobj_data:$obj_data")

    val obj_data_map = obj.data(Map::class.java)
    println("\nobj_data_map:$obj_data_map")

    val obj_data_list = obj.data<Map<String, Any>>(object : TypeToken<Map<String, Any>>() {}.type)
    println("\nobj_data_list:$obj_data_list")
}

fun testDeepJsonData() {
    println("\n#########################testDeepJsonData")
    val sweet = Sweet(id = 1, userId = "xnpeng", text = "this is a sweet", date = DateTime.now(), replyTo = null)
    val sweetJson = gson.toJson(sweet)

    val reply1 = Sweet(id = 2, userId = "xnpeng", text = "this is a reply for sweet=1", date = DateTime.now(), replyTo = 1)
    val reply2 = Sweet(id = 3, userId = "xnpeng", text = "this is a reply for \"sweet's id%1 \"", date = DateTime.now(), replyTo = 1)
    val replies2 = listOf(reply1, reply2)
    val replies2Json = gson.toJson(replies2)

    val media = Media(id = 1, refId = 1, fileName = "media1", fileType = "video", title = "video title", sortOrder = 0)
    val medias2 = listOf(media)
    val medias2Json = gson.toJson(medias2)

    val map = mapOf("sweet" to sweetJson, "replies" to replies2Json, "medias" to medias2Json)
    val mapJson = gson.toJson(map)

    val apiResult = ApiResult(code = 100, desc = "deep json data", data = mapJson)
    println("\napiResult:$apiResult")

    val resJson = gson.toJson(apiResult)
    println("\nresJson:$resJson")

    val resData = apiResult.data
    println("\nresData:$resData")

    val resDataMap = apiResult.data(Map::class.java)
    println("\nresDataMap:$resDataMap")

    resDataMap!!.forEach { k, v ->
        if (k == "sweet") {
            val resSweet = gson.fromJson<Sweet>(v.toString())
            println("\nresSweet:$resSweet")
        } else if (k == "replies") {
            val resReplies = gson.fromJson<List<Sweet>>(v.toString(), object : TypeToken<List<Sweet>>() {}.type)
            println("\nresReplies:$resReplies")
            resReplies.forEach { it ->
                println("\nreply:$it")
            }
        } else if (k == "medias") {
            val resMedias = gson.fromJson<List<Media>>(v.toString(), object : TypeToken<List<Media>>() {}.type)
            println("\nres_medias:$resMedias")
            resMedias.forEach { it ->
                println("\nmedia:$it")
            }
        }
    }

}