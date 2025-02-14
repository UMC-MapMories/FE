package com.devdi.mapmories.settings

data class UserProfileResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: UserProfile
)

data class UserProfile(
    val name: String?="내용 없음",
    val profileImg: String
)
