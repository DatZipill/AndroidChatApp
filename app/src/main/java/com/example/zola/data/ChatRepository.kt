package com.example.zola.data

import com.example.zola.data.network.ApiMessage
import com.example.zola.data.network.RetrofitClient
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

    // Lấy danh sách tin nhắn phân loại theo từng cuộc trò chuyện
    fun getMessagesForConversation(user1: String, user2: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(user1, user2).map { entityList ->
            entityList.map { entity ->
                Message(
                    id = entity.id,
                    text = entity.text,
                    isMine = (entity.sender == user1)
                )
            }
        }
    }

    // Kết nối WebSocket
    fun connectWebSocket(currentUser: String) {
        val wsUrl = RetrofitClient.BASE_URL.replace("http", "ws") + "ws/chat"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = RetrofitClient.okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send("""{"username":"$currentUser"}""")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val apiMsg = RetrofitClient.gson.fromJson(text, ApiMessage::class.java)
                    val entity = MessageEntity(
                        id = apiMsg.id,
                        sender = apiMsg.sender,
                        receiver = apiMsg.receiver,
                        text = apiMsg.text,
                        isMine = (apiMsg.sender == currentUser)
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        messageDao.insertMessage(entity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    // Gửi tin nhắn FULL-DUPLEX
    suspend fun sendMessage(text: String, sender: String, receiver: String) {
        if (text.isBlank()) return

        val msgId = UUID.randomUUID().toString()

        val entity = MessageEntity(
            id = msgId,
            sender = sender,
            receiver = receiver,
            text = text,
            isMine = true
        )
        messageDao.insertMessage(entity)

        try {
            val apiMsg = ApiMessage(
                id = msgId,
                sender = sender,
                receiver = receiver,
                text = text,
                timestamp = null
            )
            val jsonString = RetrofitClient.gson.toJson(apiMsg)
            webSocket?.send(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Tải lịch sử chat giữa 2 người
    suspend fun fetchMessagesFromServer(sender: String, receiver: String) {
        try {
            val response = RetrofitClient.api.getMessages(sender = sender, receiver = receiver)
            if (response.isSuccessful && response.body()?.messages != null) {
                val serverMessages = response.body()!!.messages!!
                for (apiMsg in serverMessages) {
                    val isMine = (apiMsg.sender == sender)
                    val entity = MessageEntity(
                        id = apiMsg.id,
                        sender = apiMsg.sender,
                        receiver = apiMsg.receiver,
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
