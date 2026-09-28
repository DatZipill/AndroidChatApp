package com.example.zola.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Đánh dấu đây là một Entity của Room.
// Nó sẽ tạo ra một bảng tên là "messages" trong cơ sở dữ liệu SQLite
@Entity(tableName = "messages")
data class MessageEntity(
    // Đây là Khóa chính (Primary Key). Phải có cái này để SQLite phân biệt các hàng (rows)
    @PrimaryKey
    val id: String,
    
    val text: String,
    val isMine: Boolean
)
