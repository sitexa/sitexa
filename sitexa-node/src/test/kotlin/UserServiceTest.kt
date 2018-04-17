import com.sitexa.ktor.dao.api.UserService
import com.sitexa.ktor.model.User

/**
 * Created by open on 07/05/2017.
 */


fun main(vararg: Array<String>) {

    //testGetUserPageApiResult()

    //testSendVCode()

    //testVCode()

    //testChangePwd()

    //testLogin()

    //testRegisterUser()

    //testGetUserPageGson()

    //testGetUserPageMoshi()

    //testGetUserPage()

    testGetUserInfo()
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
    val user = UserService().login("xnpeng", "pop007")
    println("\nlogin:\n$user")

}

fun testRegisterUser() {
    println("\n###########testRegisterUser")
    val user1 = User(userId = "xnpeng01", mobile = "13548599781", email = "xnpeng01@163.com", displayName = "xnpeng01", passwordHash = "pop008")
    val newUser = UserService().register(user1)
    println("\nnweUser:\n$newUser")
}

fun testGetUserPage() {
    println("\n###########testGetUserPage")
    val user2 = UserService().getUserPage("xnpeng")
    user2.forEach { it -> println("\nuserPage:$it") }
}

fun testGetUserInfo(){
    println("\n###########testGetUserInfo")
    val userInfo = UserService().getUserInfo("xnpeng")
    println("\nuserInfo:$userInfo")

}