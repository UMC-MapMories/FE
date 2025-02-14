package com.devdi.mapmories

import android.content.Context
import com.devdi.mapmories.community.DiaryApi
import com.devdi.mapmories.login.LoginApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://43.201.246.74:8080/"

    private val authInterceptor: AuthInterceptor by lazy {
        // Application의 onCreate 이후에 lazy 초기화됨
        AuthInterceptor(
            MapMoriesApplication.instance.getSharedPreferences("auth", Context.MODE_PRIVATE)
        )
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)      // 토큰 헤더 추가 인터셉터
        .addInterceptor(loggingInterceptor) // 네트워크 요청/응답 로그 추가
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val api: LoginApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LoginApiService::class.java)
    }

    val loginApi: LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }

    val diaryApi: DiaryApi by lazy {
        retrofit.create(DiaryApi::class.java)
    }
}
