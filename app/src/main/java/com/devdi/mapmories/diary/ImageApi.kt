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
    val result: Map<String, String> // 예: {"additionalProp1": "https://upload.url/..." }
)