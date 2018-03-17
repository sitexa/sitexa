package com.sitexa.ktor.service

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


/**
 * Created by open on 05/05/2017.
 *
 */

/**
 * 简单邮件（不带附件的邮件）发送器
 */
fun sendTextMail(mailInfo: MailSenderInfo): Boolean {
    // 判断是否需要身份认证
    var authenticator: MyAuthenticator? = null
    val pro = mailInfo.properties
    if (mailInfo.isValidate) {
        // 如果需要身份认证，则创建一个密码验证器
        authenticator = MyAuthenticator(mailInfo.userName, mailInfo.password)
    }
    // 根据邮件会话属性和密码验证器构造一个发送邮件的session
    val sendMailSession = Session.getDefaultInstance(pro, authenticator)
    try {
        // 根据session创建一个邮件消息
        val mailMessage = MimeMessage(sendMailSession)
        // 创建邮件发送者地址
        val from = InternetAddress(mailInfo.fromAddress)
        // 设置邮件消息的发送者
        mailMessage.sender = from
        // 创建邮件的接收者地址，并设置到邮件消息中
        val to = InternetAddress(mailInfo.toAddress)
        mailMessage.setRecipient(Message.RecipientType.TO, to)
        // 设置邮件消息的主题
        mailMessage.subject = mailInfo.subject
        // 设置邮件消息发送的时间
        mailMessage.sentDate = Date()
        // 设置邮件消息的主要内容
        val mailContent = mailInfo.content
        mailMessage.setText(mailContent)
        // 发送邮件
        Transport.send(mailMessage)
        return true
    } catch (ex: MessagingException) {
        ex.printStackTrace()
        return false
    }
}

/**
 * 以HTML格式发送邮件
 * @param mailInfo 待发送的邮件信息
 */
fun sendHtmlMail(mailInfo: MailSenderInfo): Boolean {
    // 判断是否需要身份认证
    var authenticator: MyAuthenticator? = null
    val pro = mailInfo.properties
    //如果需要身份认证，则创建一个密码验证器
    if (mailInfo.isValidate) {
        authenticator = MyAuthenticator(mailInfo.userName, mailInfo.password)
    }
    // 根据邮件会话属性和密码验证器构造一个发送邮件的session
    val sendMailSession = Session.getDefaultInstance(pro, authenticator)
    try {
        // 根据session创建一个邮件消息
        val mailMessage = MimeMessage(sendMailSession)
        // 创建邮件发送者地址
        val from = InternetAddress(mailInfo.fromAddress)
        // 设置邮件消息的发送者
        mailMessage.setFrom(from)
        // 创建邮件的接收者地址，并设置到邮件消息中
        val to = InternetAddress(mailInfo.toAddress)
        // Message.RecipientType.TO属性表示接收者的类型为TO
        mailMessage.setRecipient(Message.RecipientType.TO, to)
        // 设置邮件消息的主题
        mailMessage.subject = mailInfo.subject
        // 设置邮件消息发送的时间
        mailMessage.sentDate = Date()
        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
        val mainPart = MimeMultipart()
        // 创建一个包含HTML内容的MimeBodyPart
        val html = MimeBodyPart()
        // 设置HTML内容
        html.setContent(mailInfo.content, "text/html; charset=utf-8")
        mainPart.addBodyPart(html)
        // 将MiniMultipart对象设置为邮件内容
        mailMessage.setContent(mainPart)
        // 发送邮件
        Transport.send(mailMessage)
        return true
    } catch (ex: MessagingException) {
        ex.printStackTrace()
    }

    return false
}


class MyAuthenticator(internal var userName: String, internal var password: String)
    : Authenticator() {

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(userName, password)
    }
}

/**
 * 发送邮件需要使用的基本信息
 */
class MailSenderInfo {
    // 发送邮件的服务器的IP和端口
    lateinit var mailServerHost: String
    lateinit var mailServerPort: String
    // 邮件发送者的地址
    lateinit var fromAddress: String
    // 邮件接收者的地址
    lateinit var toAddress: String
    // 登陆邮件发送服务器的用户名和密码
    lateinit var userName: String
    lateinit var password: String
    // 是否需要身份验证
    var isValidate = true
    // 邮件主题
    lateinit var subject: String
    // 邮件的文本内容
    lateinit var content: String
    // 邮件附件的文件名
    var attachFileNames: Array<String>? = null
    /**
     * 获得邮件会话属性
     */
    val properties: Properties
        get() {
            val p = Properties()
            p.put("mail.smtp.host", this.mailServerHost)
            p.put("mail.smtp.port", this.mailServerPort)
            p.put("mail.smtp.auth", if (isValidate) "true" else "false")
            return p
        }
}

fun main(vararg: Array<String>) {
    //这个类主要是设置邮件
    val mailInfo = MailSenderInfo()
    mailInfo.mailServerHost = "smtp.163.com"
    mailInfo.mailServerPort = "25"
    mailInfo.isValidate = true
    mailInfo.userName = "xnpeng@163.com"
    mailInfo.password = "pop007"//授权码
    mailInfo.fromAddress = "xnpeng@163.com"
    mailInfo.toAddress = "xnpeng@163.com"
    mailInfo.subject = "设置邮箱标题:from sitexa.com"
    mailInfo.content = "设置邮箱内容:sitexa.com"
    //这个类主要来发送邮件
    sendTextMail(mailInfo)//发送文体格式
    sendHtmlMail(mailInfo)//发送html格式
}