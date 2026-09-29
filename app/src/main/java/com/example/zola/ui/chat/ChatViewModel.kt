package com.example.zola.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zola.data.ChatDatabase
import com.example.zola.data.ChatRepository
import com.example.zola.data.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ChatDatabase.getDatabase(application)
    private val repository = ChatRepository(database.messageDao())
    private val userPreferences = UserPreferences(application)

    val messages: StateFlow<List<Message>> = repository.messages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var currentUser = "alice"
    private var chatPartner = ""

    init {
        viewModelScope.launch {
            userPreferences.username.collect { name ->
                if (!name.isNullOrBlank()) {
                    currentUser = name
                }
            }
        }
    }

    // Thiết lập người chat cùng và kết nối WebSocket để chat thời gian thực (Real-time)
    fun startChatWith(partner: String) {
        chatPartner = partner
        
        // Kéo lịch sử chat về
        viewModelScope.launch {
            repository.fetchMessagesFromServer(currentUser, chatPartner)
        }
        
        // Mở đường truyền WebSocket
        repository.connectWebSocket(currentUser)
    }

    // Gửi tin nhắn qua REST API (Server sẽ báo lại qua WebSocket cho người kia)
    fun sendMessage(text: String) {
        if (text.isBlank() || chatPartner.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(text, currentUser, chatPartner)
        }
    }

    override fun onCleared() {
        // Ngắt kết nối WebSocket khi thoát phòng chat
        repository.disconnectWebSocket()
    }
}
