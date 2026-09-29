package com.example.zola.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Định nghĩa các đường dẫn API mà Android sẽ gọi lên Server
interface ChatApi {

    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/send_message")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<SendMessageResponse>

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
