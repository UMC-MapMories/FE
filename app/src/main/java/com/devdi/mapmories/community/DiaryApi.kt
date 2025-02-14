package com.devdi.mapmories.community

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DiaryApi {
    @GET("diary")
    suspend fun getDiaries(): Response<DiaryListResponse>

    @GET("diary/{diaryId}")
    suspend fun getDiaryDetail(@Path("diaryId") diaryId: Long): Response<DiaryDetailResponse>
}