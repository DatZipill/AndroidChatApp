package com.example.zola.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zola.data.ChatDatabase
import com.example.zola.data.ChatRepository
import com.example.zola.data.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ChatDatabase.getDatabase(application)
    private val repository = ChatRepository(database.messageDao())
    private val userPreferences = UserPreferences(application)

    private val _chatPartner = MutableStateFlow("")
    private var currentUser = "alice"

    // Lọc danh sách tin nhắn CHỈ thuộc về cuộc trò chuyện giữa currentUser và chatPartner
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<Message>> = _chatPartner
        .flatMapLatest { partner ->
            if (partner.isBlank()) flowOf(emptyList())
            else repository.getMessagesForConversation(currentUser, partner)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            userPreferences.username.collect { name ->
                if (!name.isNullOrBlank()) {
                    currentUser = name
                }
            }
        }
    }

    // Khởi tạo phòng chat cho đúng đối tượng
    fun startChatWith(partner: String) {
        _chatPartner.value = partner

        viewModelScope.launch {
            repository.fetchMessagesFromServer(currentUser, partner)
        }

        repository.connectWebSocket(currentUser)
    }

    // Gửi tin nhắn
    fun sendMessage(text: String) {
        val partner = _chatPartner.value
        if (text.isBlank() || partner.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(text, currentUser, partner)
        }
    }

    override fun onCleared() {
        repository.disconnectWebSocket()
    }
}
