package com.devdi.mapmories.settings

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApi {
    @GET("friends/search")
    suspend fun searchFriends(@Query("name") name: String): Response<FriendSearchResponse>

    @POST("friends/sendRequest")
    suspend fun sendFriendRequest(@Query("toUserId") toUserId: Long): Response<FriendRequestResponse>

    @GET("friends/requests")
    suspend fun getFriendRequests(): Response<FriendRequestListResponse>

    @POST("friends/acceptRequest/{fromUserId}")
    suspend fun acceptFriendRequest(@Path("fromUserId") fromUserId: Long): Response<FriendActionResponse>

    @POST("friends/rejectRequest/{fromUserId}")
    suspend fun rejectFriendRequest(@Path("fromUserId") fromUserId: Long): Response<FriendActionResponse>
}

