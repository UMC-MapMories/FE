package com.devdi.mapmories.network

import android.content.SharedPreferences
import okhttp3.Interceptor

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        // 로그인 엔드포인트는 토큰을 추가하지 않음
        if (request.url.encodedPath.contains("/login")) {
            return chain.proceed(request)
        }

        val token = sharedPreferences.getString("accessToken", null)
        val newRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", token)
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}