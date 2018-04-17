package com.sitexa.ktor.common

/**
 * Created by open on 06/05/2017.
 *
 */


/**
 * 通用代码. 0-99
 */
object CommonCode {
    /**
     * 否
     */
    val NO = 0

    /**
     * 是
     */
    val YES = 1
}


/**
 * API请求代码. 100-200
 */
object ApiCode {

    /**
     * 成功
     */
    val OK = 100

    /**
     * 业务异常(非正常，不可预计)
     */
    val ERROR = 101

    /**
     * 业务失败(业务正常，但是验证错误)
     */
    val FAILURE = 103

    /**
     * 服务器链接异常
     */
    val SERVER_CONNECTION_ERROR = 104

    /**
     * 网络不可用
     */
    val NETWORK_ERROR = 105
}

/**
 * 网络请求代码
 */
object HttpCode {

    /**
     * 代码
     */
    val CODE = "code"

    /**
     * 值
     */
    val VALUE = "value"

    /**
     * 请求成功状态码
     */
    val SUCCESS_CODE = "00000"

    /**
     * 业务处理异常状态码
     */
    val BUSINESS_ERROR_CODE = "10000"

    /**
     * 用户没有登陆
     */
    val USER_NOT_LOGIN = "10043"
}


/**
 * Intent识别代码。201-300
 */
object IntentCode {
    /**
     * 请求相机拍摄
     */
    val REQUEST_CODE_CAMERA = 201

    /**
     * 请求相册选择图片
     */
    val REQUEST_CODE_ALBUM_CHOOSE_PHOTO = 202

    /**
     * 请求剪裁图片
     */
    val REQUEST_CODE_CROP_PHOTO = 203
}


/**
 * 推送代码
 */
object PushCode {

    /**
     * 推送代码
     */
    val PUSH_CODE = "code"

    /**
     * 推送值
     */
    val PUSH_VALUE = "value"

    /**
     * 广场评论新消息推送
     */
    val SQUARE_COMMENT_NEW_MESSAGE_CODE = 301

    /**
     * 部落新帖子推送
     */
    val GROUP_POST_NEW_MESSAGE_CODE = 302

    /**
     * 部落评论新消息推送
     */
    val GROUP_COMMENT_NEW_MESSAGE_CODE = 303

    /**
     * 部落请求验证新消息推送
     */
    val GROUP_REQUEST_NEW_MESSAGE_CODE = 304

    /**
     * 个人空间评论新消息推送
     */
    val BLOG_COMMENT_NEW_MESSAGE_CODE = 305

    /**
     * 异地登陆消息推送
     */
    val USER_LOGINED_NEW_CODE = 306


    /**
     * 用户申请变更主社区。消息推送给管理员
     */
    val COMMUNITY_USER_NEW_REQUEST = 307

    /**
     * 社区用户申请请求已经被审核，消息推送给用户
     */
    val COMMUNITY_USER_REQUEST_AUDITED = 308

    /**
     * 用户申请加入部落消息被审核推送。审核后，消息推送给申请的用户
     */
    val JOIN_GROUP_REQUEST_MESSAGE_AUDITED_CODE = 309
}


