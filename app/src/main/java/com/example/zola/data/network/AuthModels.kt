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

// Model DTO tin nhắn từ Server
data class ApiMessage(
    val id: String,
    val sender: String,
    val receiver: String,
    val text: String,
    val timestamp: String?
)

// Model gửi tin nhắn
data class SendMessageRequest(
    val id: String, // Đã thêm ID để Server lưu
    val sender: String,
    val receiver: String,
    val text: String
)

// Response tin nhắn
data class SendMessageResponse(
    val message: String?,
    val id: String?,
    val sender: String?,
    val receiver: String?,
    val text: String?,
    val error: String?
)

// Response danh sách tin nhắn
data class GetMessagesResponse(
    val messages: List<ApiMessage>?,
    val error: String?
)

// Response danh sách người dùng
data class UsersResponse(
    val users: List<String>?,
    val error: String?
)
