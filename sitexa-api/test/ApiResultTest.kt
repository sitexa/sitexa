import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.google.gson.reflect.TypeToken
import com.sitexa.ktor.JsonResponse
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.common.JodaGsonAdapter
import com.sitexa.ktor.model.Sweet
import com.sitexa.ktor.model.User
import org.joda.time.DateTime
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by open on 07/05/2017.
 */

class ApiResultTest {

    @Test
    fun test() {
        val gson = GsonBuilder()
                .registerTypeAdapter(DateTime::class.java, JodaGsonAdapter())
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create()
        val sign = "3466671c885b03a9366efa41b9ef5687b5f5d966"
        val result = ApiResult(code = 0, desc = "success", data = sign)
        val response = JsonResponse(result)

        val expected1 = JsonResponse(result)
        assertEquals(expected1.data.toString(),response.data.toString())

        val response_json = gson.toJson(response)
        assertEquals("""{"data":{"code":0,"desc":"success","data":"3466671c885b03a9366efa41b9ef5687b5f5d966"}}""",response_json)

        val response_data_json = gson.toJson(response.data)
        assertEquals("""{"code":0,"desc":"success","data":"3466671c885b03a9366efa41b9ef5687b5f5d966"}""",response_data_json)

    }

    @Test
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
        val sweetList = us.data<List<Sweet>>(typeToken)!!
        //sweetList.forEach { it -> println("s:id=${it.id}:$it") }
        assertEquals(3,sweetList.size)
    }

    @Test
    fun test3() {
        val userJson = """
                    {"code":1,
                    "desc":"success",
                    "data":"{'userId':'1','mobile':'18673107430','email':'xnpeng@163.com','displayName':'oscar peng','passwordHash':'111'}"}
                    """
        val ar = ApiResult(userJson)
        val usr = ar.data(User::class.java)!!
        assertEquals("""User(userId=1, mobile=18673107430, email=xnpeng@163.com, displayName=oscar peng, passwordHash=111)""",usr.toString())
    }
}