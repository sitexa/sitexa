package com.sitexa.ktor.service

/**
 * Created by open on 04/05/2017.
 *
 */

/**
 * 利用推送服务器向设备推送消息
 */
fun sendMessage(imei: String, text: String) {

}

fun share2Wechat(webchat: String, text: String) {

}

fun share2Weibo(weibo: String, text: String) {

}

fun share2Qq(qq: String, text: String) {

}

fun alipay(account: String, amount: Float) {

}

fun wepay(account: String, amount: Float) {

}

fun loginWechat() {

}

fun loginWeibo() {

}

fun loginQq() {

}


fun main(vararg: Array<String>) {
    val n = createRandomStr(true, 6)
    println(n)
}