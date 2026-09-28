package com.example.chatapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ChatPreview(
    val username: String,
    val lastMessage: String
)

@Composable
fun HomeScreen(
    onChatClick: (String) -> Unit, // Thêm callback này để báo cho MainActivity biết người dùng đã bấm vào chat
    modifier: Modifier = Modifier
) {

    val chats = listOf(
        ChatPreview(
            username = "Bob",
            lastMessage = "Hello!"
        ),
        ChatPreview(
            username = "Charlie",
            lastMessage = "Hi!"
        )
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Text(
            text = "Tin nhắn",
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {

            items(chats) { chat ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onChatClick(chat.username) // Gọi callback và truyền tên người dùng
                        }
                        .padding(16.dp)
                ) {

                    Text(
                        text = chat.username
                    )

                    Text(
                        text = chat.lastMessage
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(onChatClick = {})
}