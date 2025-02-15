package com.devdi.mapmories.login

data class LogoutResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: String
)