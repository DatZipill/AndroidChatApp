package com.example.zola.ui.chat // CHÚ Ý: Chỉnh lại package name cho đúng chuẩn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Thêm dòng này để lặp danh sách
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Thêm dòng này

@Composable
fun ChatScreen(
    username: String,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel() // Compose sẽ tự động tạo hoặc lấy ViewModel đã có
) {
    var messageInput by remember { mutableStateOf("") }
    
    // Quan sát state từ ViewModel. Mỗi khi _messages.value thay đổi, UI sẽ tự vẽ lại (Recomposition)
    val messages by viewModel.messages.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Thanh tiêu đề
        Text(
            text = username,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Danh sách tin nhắn
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Thay vì code cứng, giờ ta sẽ lặp qua danh sách messages
            items(messages) { message ->
                MessageBubble(text = message.text, isMine = message.isMine)
            }
        }

        // Khu vực nhập tin nhắn
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nhập tin nhắn...") }
            )

            Button(
                onClick = {
                    viewModel.sendMessage(messageInput) // Gửi tin nhắn
                    messageInput = "" // Xóa ô nhập sau khi gửi
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Gửi")
            }
        }
    }
}

// ----------------------------------------------------
// Component UI dùng chung: MessageBubble
// ----------------------------------------------------
@Composable
fun MessageBubble(
    text: String,
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    // Nếu là tin nhắn của mình thì căn lề phải (End), ngược lại căn lề trái (Start)
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    
    // Màu nền tuỳ thuộc vào người gửi
    val backgroundColor = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth() // Chiếm hết chiều ngang để Box con bên trong có thể căn lề (Alignment)
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp) // Bo góc giống Zalo/Messenger
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}