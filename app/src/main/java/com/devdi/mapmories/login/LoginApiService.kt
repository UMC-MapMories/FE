package com.devdi.mapmories.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface LoginApiService {
    @POST("join")  //
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: ProfileRequest): Response<ProfileResponse>

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<Void>

    @POST("login/google")
    @Headers("Content-Type: application/json")
    suspend fun loginWithGoogle(
        @Header("X-Google-Authorization") token: String
    ): Response<LoginResponse>
}

