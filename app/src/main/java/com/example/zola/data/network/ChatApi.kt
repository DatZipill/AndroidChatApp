package com.example.zola.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Định nghĩa các đường dẫn API mà Android sẽ gọi lên Server
interface ChatApi {

    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
