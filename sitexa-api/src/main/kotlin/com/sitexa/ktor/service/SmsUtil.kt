package com.sitexa.ktor.service

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.NameValuePair
import org.apache.commons.httpclient.methods.PostMethod
import org.slf4j.LoggerFactory

/**
 * Created by open on 05/05/2017.
 *
 */

private val log = LoggerFactory.getLogger("SmsUtil")
private val SMS_URL = "http://utf8.sms.webchinese.cn/"
private val SMS_UID = "sitexa"
private val SMS_KEY = "1ddac3bbce3249bc4a8d"



fun sendSms(mobileNo: String, smsText: String = "验证码:8888"): String {
    val client = HttpClient()
    val post = PostMethod(SMS_URL)
    post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")//在头文件中设置转码

    //val sms_key_md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(SMS_KEY)

    val data = arrayOf(NameValuePair("Uid", SMS_UID), NameValuePair("Key", SMS_KEY), NameValuePair("smsMob", mobileNo), NameValuePair("smsText", smsText))
    post.setRequestBody(data)

    client.executeMethod(post)
    val statusCode = post.statusCode
    val result = post.responseBodyAsString
    post.releaseConnection()

    log.info("sendSMS:$mobileNo,$smsText.statusCode:$statusCode,result:$result")

    return result
}


/**
 * Create a fixed length random String
 * @param onlyNumber is only number
 * @param length
 * @return string
 */
fun createRandomStr(onlyNumber: Boolean, length: Int): String {
    val resultStr = StringBuffer()
    val strTable = if (onlyNumber) "1234567890" else "23456789abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
    val len = strTable.length

    for (i in 0 until length) {
        val dblR = Math.random() * len
        val intR = Math.floor(dblR).toInt()
        resultStr.append(strTable[intR])
    }
    return resultStr.toString()
}


fun main(args: Array<String>) {
    sendSms("18673107430", "验证码:8888")
}
