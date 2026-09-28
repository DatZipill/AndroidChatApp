package com.example.zola.ui.chat

// Đây là Data class định nghĩa cấu trúc của 1 tin nhắn
data class Message(
    val id: String,
    val text: String,
    val isMine: Boolean
)
