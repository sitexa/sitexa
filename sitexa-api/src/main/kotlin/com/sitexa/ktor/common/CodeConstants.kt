package com.sitexa.ktor.common

/**
 * Created by open on 06/05/2017.
 *
 */

/**
 * 通用代码. 0-99
 */
object CommonCode {
    val NO = 0
    val YES = 1
}

/**
 * API请求代码. 100-200
 */
object ApiCode {

    val OK = 100
    val ERROR = 101
    val FAILURE = 103
    val SERVER_CONNECTION_ERROR = 104
    val NETWORK_ERROR = 105
}

/**
 * 网络请求代码
 */
object HttpCode {
    val CODE = "code"
    val VALUE = "value"
    val SUCCESS_CODE = "00000"
    val BUSINESS_ERROR_CODE = "10000"
    val USER_NOT_LOGIN = "10043"
}

/**
 * Intent识别代码。201-300
 */
object IntentCode {
    val REQUEST_CODE_CAMERA = 201
    val REQUEST_CODE_ALBUM_CHOOSE_PHOTO = 202
    val REQUEST_CODE_CROP_PHOTO = 203
}

/**
 * 推送代码
 */
object PushCode {
    val PUSH_CODE = "code"
    val PUSH_VALUE = "value"
    val SQUARE_COMMENT_NEW_MESSAGE_CODE = 301
    val GROUP_POST_NEW_MESSAGE_CODE = 302
    val GROUP_COMMENT_NEW_MESSAGE_CODE = 303
    val GROUP_REQUEST_NEW_MESSAGE_CODE = 304
    val BLOG_COMMENT_NEW_MESSAGE_CODE = 305
    val USER_LOGINED_NEW_CODE = 306
    val COMMUNITY_USER_NEW_REQUEST = 307
    val COMMUNITY_USER_REQUEST_AUDITED = 308
    val JOIN_GROUP_REQUEST_MESSAGE_AUDITED_CODE = 309
}


