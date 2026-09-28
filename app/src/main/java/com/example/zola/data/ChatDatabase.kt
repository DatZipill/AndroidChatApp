package com.example.zola.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Định nghĩa Database chứa bảng MessageEntity, phiên bản 1
@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        // Hàm này đảm bảo chỉ có duy nhất 1 kết nối tới Database trong toàn bộ app (Singleton)
        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database" // Tên file được lưu trên điện thoại
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
