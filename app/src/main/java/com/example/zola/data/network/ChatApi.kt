package com.example.zola.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApi {

    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/messages")
    suspend fun getMessages(
        @Query("sender") sender: String,
        @Query("receiver") receiver: String
    ): Response<GetMessagesResponse>

    @GET("/api/users")
    suspend fun getUsers(
        @Query("current_user") currentUser: String
    ): Response<UsersResponse>
}
