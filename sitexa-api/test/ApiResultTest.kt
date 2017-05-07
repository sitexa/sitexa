import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import org.joda.time.DateTime

/**
 * Created by open on 07/05/2017.
 */

fun main(vararg: Array<String>) {
    test()
    test2()
    test3()
}


fun test() {
    val gson = GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()
    val sign = "3466671c885b03a9366efa41b9ef5687b5f5d966"
    val result = ApiResult(code = 0, desc = "success", data = sign)
    val response = JsonResponse(result)
    println("\nresponse=$response:\tresponse.data=${response.data}")

    val response_json = gson.toJson(response)
    println("\nresponse_json:$response_json")

    val response_data_json = gson.toJson(response.data)
    println("\nresponse_data_json:$response_data_json")

}

fun test2() {
    val json = """
    {"code":1,
    "desc":"success",
    "data":
    '[
    {"id":21,"userId":"xnpeng","text":"upload a picture ,save path/name to database, and get from database.","date":1492333830000},
    {"id":19,"userId":"xnpeng","text":"How to read and save uploaded file?","date":1492167244000},
    {"id":9,"userId":"xnpeng","text":"Note that the method annotated FromJson does not need to take a String as an argument.","date":1492000948000}
    ]'
    }
    """
    val us = ApiResult(json)
    val typeToken = object : TypeToken<List<Sweet>>() {}.type
    val sweetList = us.dataList<List<Sweet>>(typeToken)!!

    println()
    sweetList.forEach { it -> println("s:id=${it.id}:$it") }
}

fun test3(){
    val userJson = """
                    {"code":1,
                    "desc":"success",
                    "data":"{'userId':'1','mobile':'18673107430','email':'xnpeng@163.com','displayName':'oscar peng','passwordHash':'111'}"}
                    """
    val ar = ApiResult(userJson)
    val usr = ar.data(User::class.java)!!
    println("\nusr:userId=${usr.userId}:$usr")
}