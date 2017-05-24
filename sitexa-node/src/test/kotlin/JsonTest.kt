import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.dao.api.ApiResult
import com.sitexa.ktor.model.Sweet
import org.joda.time.DateTime

/**
 * Created by open on 07/05/2017.
 */

val gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
        .setLenient()
        .create()

val apiResult = ApiResult(code = 0, desc = "", data = """{"sweet":{"id":9,"userId":"xnpeng","text":"Note that\u0027s the method \"annotated\" with \n\n@FromJson does not need to $ take a \nString as an argument.","date":1492000948000},"replies":[{"id":17,"userId":"xnpeng","text":"可以回复了。\r\n\r\n什么时候可以上图片？\r\n什么时候可以上视频？","date":1492010593000,"replyTo":9},{"id":16,"userId":"xnpeng","text":"中国人民解放军。\r\n中华人民共和国。\r\n\r\n五笔字型，打字方法。","date":1492009738000,"replyTo":9},{"id":14,"userId":"xnpeng","text":"西望夏口，东望武昌，山川相缪，郁乎苍苍。","date":1492009645000,"replyTo":9},{"id":11,"userId":"xnpeng","text":"唯江上之清风，与山间之明月，耳遇之则为声，目遇之则成色，而吾与汝之所共适。","date":1492005692000,"replyTo":9}],"medias":[{"id":1,"refId":9,"fileName":"mov_bbb.mp4","fileType":"video","title":"movie","sortOrder":0}]}""")

val jsonString = """ {"code":0,"desc":"","data":"{\"sweet\":{\"id\":9,\"userId\":\"xnpeng\",\"text\":\"Note that\\u0027s the method \\\"annotated\\\" with \\n\\n@FromJson does not need to $ take a \\nString as an argument.\",\"date\":1492000948000},\"replies\":[{\"id\":17,\"userId\":\"xnpeng\",\"text\":\"可以回复了。\\r\\n\\r\\n什么时候可以上图片？\\r\\n什么时候可以上视频？\",\"date\":1492010593000,\"replyTo\":9},{\"id\":16,\"userId\":\"xnpeng\",\"text\":\"中国人民解放军。\\r\\n中华人民共和国。\\r\\n\\r\\n五笔字型，打字方法。\",\"date\":1492009738000,\"replyTo\":9},{\"id\":14,\"userId\":\"xnpeng\",\"text\":\"西望夏口，东望武昌，山川相缪，郁乎苍苍。\",\"date\":1492009645000,\"replyTo\":9},{\"id\":11,\"userId\":\"xnpeng\",\"text\":\"唯江上之清风，与山间之明月，耳遇之则为声，目遇之则成色，而吾与汝之所共适。\",\"date\":1492005692000,\"replyTo\":9}],\"medias\":[{\"id\":1,\"refId\":9,\"fileName\":\"mov_bbb.mp4\",\"fileType\":\"video\",\"title\":\"movie\",\"sortOrder\":0}]}"} """

val jsonElementString = """{"sweet":{"id":9,"userId":"xnpeng","text":"Note that\u0027s the method \"annotated\" with \n\n@FromJson does not need to $ take a \nString as an argument.","date":1492000948000},"replies":[{"id":17,"userId":"xnpeng","text":"可以回复了。\r\n\r\n什么时候可以上图片？\r\n什么时候可以上视频？","date":1492010593000,"replyTo":9},{"id":16,"userId":"xnpeng","text":"中国人民解放军。\r\n中华人民共和国。\r\n\r\n五笔字型，打字方法。","date":1492009738000,"replyTo":9},{"id":14,"userId":"xnpeng","text":"西望夏口，东望武昌，山川相缪，郁乎苍苍。","date":1492009645000,"replyTo":9},{"id":11,"userId":"xnpeng","text":"唯江上之清风，与山间之明月，耳遇之则为声，目遇之则成色，而吾与汝之所共适。","date":1492005692000,"replyTo":9}],"medias":[{"id":1,"refId":9,"fileName":"mov_bbb.mp4","fileType":"video","title":"movie","sortOrder":0}]}"""

val arrayString = """ [{"id":17,"userId":"xnpeng","text":"可以回复了。\r\n\r\n什么时候可以上图片？\r\n什么时候可以上视频？","date":1492010593000,"replyTo":9},{"id":16,"userId":"xnpeng","text":"中国人民解放军。\r\n中华人民共和国。\r\n\r\n五笔字型，打字方法。","date":1492009738000,"replyTo":9},{"id":14,"userId":"xnpeng","text":"西望夏口，东望武昌，山川相缪，郁乎苍苍。","date":1492009645000,"replyTo":9},{"id":11,"userId":"xnpeng","text":"唯江上之清风，与山间之明月，耳遇之则为声，目遇之则成色，而吾与汝之所共适。","date":1492005692000,"replyTo":9}] """

fun testObject2Json() {
    println("\n#########################testObject2Json")
    val res_json = gson.toJson(apiResult)
    println("\nres_json:$res_json")
}

fun testJson2Object() {
    println("\n#########################testJson2Object")
    val res_obj = gson.fromJson(jsonString, ApiResult::class.java)
    println("\nres_obj:$res_obj")
}

fun testObject2JsonTree() {
    println("\n#########################testObject2JsonTree")

    val res_obj = gson.toJsonTree(apiResult).asJsonObject
    println("\nres_obj:$res_obj")
}

fun testJsonElementString2Json() {
    println("\n#########################testJsonElementString2Json")
    val res_str = gson.toJson(jsonElementString)
    println("\nres_str:$res_str")
}

fun testJson2Map() {
    println("\n#########################testJson2Map")

    //println("\njsonElementString:$jsonElementString")

    val res_map = gson.fromJson<Map<String, Any>>(jsonElementString, Map::class.java)
    //println("\nres_map:$res_map")

    res_map.forEach { k, v ->
        println("\nk:v=$k:$v")
        if (k == "sweet") {
            val field_map = v as Map<*, *>
            println("\nfield_map:$field_map")
            field_map.forEach { k1, v1 ->
                println("\n$k1=$v1")
            }
        }
    }
}

fun testJson2Array() {
    println("\n#########################testJson2Array")
    val res_arr = gson.fromJson<List<Sweet>>(arrayString, object : TypeToken<List<Sweet>>() {}.type)
    println("\nres_arr:$res_arr")

    println("\nres_list")
    res_arr.forEach { println(it) }
}

fun main(vararg: Array<String>) {

    //testObject2Json()

    //testJson2Object()

    //testObject2JsonTree()

    //testMapString2Json()

    testJson2Map()

    //testJson2Array()
}