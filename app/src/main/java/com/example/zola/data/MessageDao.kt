package com.example.zola.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // CHỈ lấy các tin nhắn thuộc về cuộc trò chuyện giữa user1 và user2
    @Query("""
        SELECT * FROM messages 
        WHERE (sender = :user1 AND receiver = :user2) 
           OR (sender = :user2 AND receiver = :user1)
    """)
    fun getMessagesForConversation(user1: String, user2: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
}
