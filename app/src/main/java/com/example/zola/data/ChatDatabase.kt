package com.example.zola.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Tăng phiên bản Database lên version 2 do thêm cột sender và receiver
@Database(entities = [MessageEntity::class], version = 2, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                )
                .fallbackToDestructiveMigration() // Xóa dữ liệu bảng cũ bị lệch cấu trúc và tạo mới
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
