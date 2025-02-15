package com.devdi.mapmories.settings

data class Friend(
    val id: Long,
    val email: String,
    val name: String,
    val profileImg: String
)

data class FriendSearchResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<Friend>
)

data class FriendRequestBody(
    val toUserId: Long
)

data class FriendRequestResult(
    val id: Long,
    val fromUserName: String,
    val toUserName: String,
    val status: String,
    val createdAt: String
)

data class FriendRequestResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FriendRequestResult
)

data class FriendRequestListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<Friend>
)

data class FriendActionResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FriendRequestResult
)
