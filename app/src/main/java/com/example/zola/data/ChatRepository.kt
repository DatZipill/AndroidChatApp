package com.example.zola.data

import com.example.zola.ui.chat.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

// Bây giờ Repository cần nhận MessageDao vào để làm việc
class ChatRepository(private val messageDao: MessageDao) {

    // messages bây giờ KHÔNG phải là biến StateFlow tĩnh nữa.
    // Nó lấy trực tiếp Flow từ DAO, sau đó dùng map{} để biến đổi MessageEntity (của Room)
    // thành Message (của UI) để UI hiển thị.
    val messages: Flow<List<Message>> = messageDao.getAllMessages().map { entityList ->
        entityList.map { entity ->
            Message(
                id = entity.id,
                text = entity.text,
                isMine = entity.isMine
            )
        }
    }

    suspend fun sendMessage(text: String) {
        if (text.isBlank()) return

        // 1. Tạo MessageEntity
        val entity = MessageEntity(
            id = UUID.randomUUID().toString(),
            text = text,
            isMine = true
        )

        // 2. Lưu xuống Database. Vì là suspend function nên nó sẽ chờ tới khi lưu xong mới đi tiếp
        messageDao.insertMessage(entity)
    }
}
