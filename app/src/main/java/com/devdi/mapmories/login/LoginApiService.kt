package com.devdi.mapmories.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("join")  //
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>
}
