package com.devdi.mapmories.diary

import DiaryRequest
import DiaryResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DiaryApi {
    @POST("diary")
    suspend fun createDiary(@Body diaryRequest: DiaryRequest): DiaryResponse
}