package com.example.zola.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    
    val sender: String,
    val receiver: String,
    val text: String,
    val isMine: Boolean
)
