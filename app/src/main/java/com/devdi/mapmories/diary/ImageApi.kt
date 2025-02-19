package com.devdi.mapmories.diary

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface ImageApi {
    @POST("image")
    suspend fun getUploadUrl(
        @Query("fileName") fileName: String,
        @Query("contentType") contentType: String
    ): Response<ImageUploadResponse>
}

data class ImageUploadResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UploadResult // result를 별도 데이터 클래스로 분리
)

data class UploadResult(
    val url: String // 업로드 URL이 있는 필드
)
