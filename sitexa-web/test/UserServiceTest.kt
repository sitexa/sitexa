import com.sitexa.ktor.model.User
import com.sitexa.ktor.dao.api.UserApiImpl

/**
 * Created by open on 07/05/2017.
 */


fun main(vararg: Array<String>) {

    //testGetUserPageApiResult()

    //testSendVCode()

    //testVCode()

    //testChangePwd()

    testLogin()

    //testRegisterUser()

    //testGetUserPageGson()

    //testGetUserPageMoshi()

    //testGetUserPage()

    //testGetUserInfo()
}

fun testSendVCode() {
    println("\n###########testSendVCode")
    val result = UserApiImpl().sendVCode("18673107430")
    println("result:$result")
}

fun testVCode() {
    println("\n###########testVCode")
    val test = UserApiImpl().testVCode("588495", 1494127055175, "f53a6e52e27b25fd595a6c31bee7c5738e45b296")
    println("test:$test")
}

fun testChangePwd() {
    println("\n###########testChangePwd")
    val result = UserApiImpl().changePassword("xnpeng01", "pop008", "pop009")
    println("\nresult:$result")
}

fun testLogin() {
    println("\n###########testLogin")
    val user = UserApiImpl().login("xnpeng", "pop007")
    println("\nlogin:\n$user")

}

fun testRegisterUser() {
    println("\n###########testRegisterUser")
    val user1 = User(userId = "xnpeng01", mobile = "13548599781", email = "xnpeng01@163.com", displayName = "xnpeng01", passwordHash = "pop008")
    val newUser = UserApiImpl().register(user1)
    println("\nnweUser:\n$newUser")
}

fun testGetUserPageApiResult() {
    println("\n###########testGetUserPageApiResult")
    val user1 = UserApiImpl().getUserPageApiResult("xnpeng")
    user1?.forEach { it -> println("\nuserPageApiResult:$it") }
}

fun testGetUserPageGson() {
    println("\n###########testGetUserPageGson")
    val user2 = UserApiImpl().getUserPageGson("xnpeng")
    user2?.forEach { it -> println("\nuserPageGson:$it") }

}

fun testGetUserPageMoshi() {
    println("\n###########testGetUserPageMoshi")
    val user2 = UserApiImpl().getUserPageMoshi("xnpeng")
    user2?.forEach { it -> println("\nuserPageMoshi:$it") }
}


fun testGetUserPage() {
    println("\n###########testGetUserPage")
    val user2 = UserApiImpl().getUserPage("xnpeng")
    user2.forEach { it -> println("\nuserPage:$it") }
}

fun testGetUserInfo(){
    println("\n###########testGetUserInfo")
    val userInfo = UserApiImpl().getUserInfo("xnpeng")
    println("\nuserInfo:$userInfo")

}