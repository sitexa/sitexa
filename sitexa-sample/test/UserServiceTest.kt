import com.sitexa.ktor.model.User
import com.sitexa.ktor.service.UserService

/**
 * Created by open on 07/05/2017.
 */


fun main(vararg: Array<String>) {

    testGetUserPageApiResult()

    testSendVCode()

    testVCode()

    testChangePwd()

    testLogin()

    testRegisterUser()

    testGetUserPageGson()

    testGetUserPageMoshi()
}

fun testSendVCode() {
    println("\n###########testSendVCode")
    val result = UserService().sendVCode("18673107430")
    println("result:$result")
}

fun testVCode() {
    println("\n###########testVCode")
    val test = UserService().testVCode("588495", 1494127055175, "f53a6e52e27b25fd595a6c31bee7c5738e45b296")
    println("test:$test")
}

fun testChangePwd() {
    println("\n###########testChangePwd")
    val result = UserService().changePassword("xnpeng01", "pop008", "pop009")
    println("\nresult:$result")
}

fun testLogin() {
    println("\n###########testLogin")
    val user = UserService().login("xnpeng01", "pop009")
    println("\nlogin:\n$user")

}

fun testRegisterUser() {
    println("\n###########testRegisterUser")
    val user1 = User(userId = "xnpeng01", mobile = "13548599781", email = "xnpeng01@163.com", displayName = "xnpeng01", passwordHash = "pop008")
    val newUser = UserService().register(user1)
    println("\nnweUser:\n$newUser")
}

fun testGetUserPageApiResult() {
    println("\n###########testGetUserPageApiResult")
    val user1 = UserService().getUserPageApiResult("xnpeng")
    user1?.forEach { it -> println("\nuserPageApiResult:$it") }
}

fun testGetUserPageGson() {
    println("\n###########testGetUserPageGson")
    val user2 = UserService().getUserPageGson("xnpeng")
    user2?.forEach { it -> println("\nuserPageGson:$it") }

}

fun testGetUserPageMoshi() {
    println("\n###########testGetUserPageMoshi")
    val user2 = UserService().getUserPageMoshi("xnpeng")
    user2?.forEach { it -> println("\nuserPageMoshi:$it") }
}