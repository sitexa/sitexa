package com.sitexa.ktor.service

import com.sitexa.ktor.BASE_URL
import com.sitexa.ktor.common.ApiCode
import com.sitexa.ktor.common.ApiResult
import com.sitexa.ktor.uploadDir
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File


/**
 * Created by open on 09/05/2017.
 *
 */

interface FileApi {

    @Multipart @POST("/upload")
    fun upload(@Part("refId") refId: RequestBody,
               @Part("title") title: RequestBody,
               @Part("sortOrder") sortOrder: RequestBody,
               @Part file: MultipartBody.Part): Call<ApiResult>

    @GET("/download")
    fun download(@Query("id") id: Int): Call<ResponseBody>
}

class FileService {

    private val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(headerJsonInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    private val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val fileApi = retrofit.create(FileApi::class.java)

    fun upload(): Int {
        val refId: Int = 9
        val title = "my file"
        val sortOrder = 0

        val refIdPart = RequestBody.create(MultipartBody.FORM, "$refId")
        val titlePart = RequestBody.create(MultipartBody.FORM, title)
        val sortOrderPart = RequestBody.create(MultipartBody.FORM, "$sortOrder")

        val file = File(uploadDir, "horse.ogg")
        val filebody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val filePart = MultipartBody.Part.createFormData("file", file.name, filebody)

        val call = fileApi.upload(refIdPart, titlePart, sortOrderPart, filePart)
        val response = call.execute().body()

        return if (response.code() == ApiCode.OK) response.data().toInt() else -1
    }

    fun download(id: Int) {

        val call = fileApi.download(id)
        val response = call.execute().body()
        val instream = response.byteStream()
        val file = File(uploadDir, "download.ogg")
        val outstream = file.outputStream()
        file.outputStream().buffered().use {
            instream.copyTo(outstream)
        }

    }
}
