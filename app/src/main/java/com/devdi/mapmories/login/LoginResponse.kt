package com.devdi.mapmories.login

data class LoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)