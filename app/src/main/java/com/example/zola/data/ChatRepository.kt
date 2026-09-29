package com.example.zola.data

import com.example.zola.data.network.ApiMessage
import com.example.zola.data.network.RetrofitClient
import com.example.zola.data.network.SendMessageRequest
import com.example.zola.ui.chat.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.UUID

class ChatRepository(private val messageDao: MessageDao) {

    private var webSocket: WebSocket? = null

    val messages: Flow<List<Message>> = messageDao.getAllMessages().map { entityList ->
        entityList.map { entity ->
            Message(
                id = entity.id,
                text = entity.text,
                isMine = entity.isMine
            )
        }
    }

    // Kết nối WebSocket
    fun connectWebSocket(currentUser: String) {
        // Đổi http:// thành ws://
        val wsUrl = RetrofitClient.BASE_URL.replace("http", "ws") + "ws/chat"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = RetrofitClient.okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Gửi thông tin định danh lên Server
                webSocket.send("""{"username":"$currentUser"}""")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Nhận được tin nhắn từ người khác thông qua WebSocket
                try {
                    val apiMsg = RetrofitClient.gson.fromJson(text, ApiMessage::class.java)
                    val entity = MessageEntity(
                        id = apiMsg.id,
                        text = apiMsg.text,
                        isMine = (apiMsg.sender == currentUser)
                    )
                    // Cập nhật ngay lập tức vào Room
                    CoroutineScope(Dispatchers.IO).launch {
                        messageDao.insertMessage(entity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    // Gửi tin nhắn qua API REST (Đồng thời lưu vào local)
    suspend fun sendMessage(text: String, sender: String, receiver: String) {
        if (text.isBlank()) return

        val msgId = UUID.randomUUID().toString()

        val entity = MessageEntity(
            id = msgId,
            text = text,
            isMine = true
        )
        messageDao.insertMessage(entity)

        try {
            RetrofitClient.api.sendMessage(
                SendMessageRequest(
                    id = msgId,
                    sender = sender,
                    receiver = receiver,
                    text = text
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Tải toàn bộ lịch sử (Khi vừa mở chat)
    suspend fun fetchMessagesFromServer(sender: String, receiver: String) {
        try {
            val response = RetrofitClient.api.getMessages(sender = sender, receiver = receiver)
            if (response.isSuccessful && response.body()?.messages != null) {
                val serverMessages = response.body()!!.messages!!
                for (apiMsg in serverMessages) {
                    val isMine = (apiMsg.sender == sender)
                    val entity = MessageEntity(
                        id = apiMsg.id,
                        text = apiMsg.text,
                        isMine = isMine
                    )
                    messageDao.insertMessage(entity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun disconnectWebSocket() {
        webSocket?.close(1000, "User left")
        webSocket = null
    }
}
