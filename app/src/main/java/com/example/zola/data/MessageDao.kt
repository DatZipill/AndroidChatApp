package com.example.zola.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object) là nơi định nghĩa các thao tác với Database (Thêm, Xóa, Sửa, Truy vấn)
@Dao
interface MessageDao {

    // Lấy toàn bộ tin nhắn.
    // Dùng Flow ở đây rất lợi hại: Mỗi khi có tin nhắn mới insert vào database, 
    // Flow sẽ tự động "nhả" ra danh sách mới, ta không cần phải tự gọi hàm query lại.
    @Query("SELECT * FROM messages")
    fun getAllMessages(): Flow<List<MessageEntity>>

    // Hàm này phải có 'suspend' vì insert vào ổ cứng có thể mất thời gian, 
    // phải chạy ngầm (Coroutine) để không làm đơ màn hình.
    @Insert
    suspend fun insertMessage(message: MessageEntity)
}
