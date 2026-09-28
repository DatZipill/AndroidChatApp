package com.example.zola.data.network

// Model gửi lên cho API đăng nhập
data class LoginRequest(
    val username: String,
    val password: String
)

// Model phản hồi từ API đăng nhập
data class LoginResponse(
    val message: String?,
    val token: String?,
    val username: String?,
    val error: String?
)

// Model gửi lên cho API đăng ký
data class RegisterRequest(
    val username: String,
    val password: String
)

// Model phản hồi từ API đăng ký
data class RegisterResponse(
    val message: String?,
    val error: String?
)
