package com.example.zola.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zola.data.ChatDatabase
import com.example.zola.data.ChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Thay vì kế thừa ViewModel thông thường, ta dùng AndroidViewModel để có thể lấy được biến 'application'
// Biến 'application' (Context) rất cần thiết để khởi tạo ChatDatabase
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    // Khởi tạo Database và Repository
    private val database = ChatDatabase.getDatabase(application)
    private val repository = ChatRepository(database.messageDao())

    // Convert Flow (của Repository) sang StateFlow để Jetpack Compose dễ dàng sử dụng
    val messages: StateFlow<List<Message>> = repository.messages
        .stateIn(
            scope = viewModelScope, // Vòng đời: Hủy luồng này khi ViewModel bị hủy
            started = SharingStarted.WhileSubscribed(5000), // Bắt đầu lắng nghe khi UI hiện, tự ngắt nếu UI ẩn
            initialValue = emptyList() // Giá trị mặc định lúc mới vào màn hình chưa kịp tải DB
        )

    fun sendMessage(text: String) {
        // Gọi hàm của Repository.
        // Vì sendMessage của Repository bây giờ là 'suspend' (làm việc với Database), 
        // ta phải chạy nó trong một Coroutine. viewModelScope lo việc này.
        viewModelScope.launch {
            repository.sendMessage(text)
        }
    }
}
