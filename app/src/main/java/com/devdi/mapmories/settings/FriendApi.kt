package com.devdi.mapmories.settings

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FriendApi {
    @GET("friends/search")
    suspend fun searchFriends(@Query("name") name: String): Response<FriendSearchResponse>

    @POST("friends/sendRequest")
    suspend fun sendFriendRequest(@Body request: FriendRequestBody): Response<FriendRequestResponse>
}
