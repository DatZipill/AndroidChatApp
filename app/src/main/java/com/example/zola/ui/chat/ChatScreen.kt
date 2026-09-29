package com.example.zola.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(
    username: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    var messageInput by remember { mutableStateOf("") }
    
    // Khởi tạo chat phiên làm việc
    LaunchedEffect(username) {
        viewModel.startChatWith(username)
    }

    // Quan sát danh sách tin nhắn từ ViewModel
    val messages by viewModel.messages.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding() // Cực kỳ quan trọng: Tự động đẩy khu vực nhập liệu lên TRÊN BÀN PHÍM khi gõ!
    ) {
        // Thanh tiêu đề Top Bar có nút Quay lại (Back Button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("← Quay lại")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        HorizontalDivider()

        // Danh sách tin nhắn
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(text = message.text, isMine = message.isMine)
            }
        }

        // Khu vực nhập tin nhắn
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                    viewModel.sendMessage(messageInput)
                    messageInput = ""
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Gửi")
            }
        }
    }
}

// Component UI dùng chung: MessageBubble
@Composable
fun MessageBubble(
    text: String,
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
