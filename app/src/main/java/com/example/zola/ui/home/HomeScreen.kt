package com.example.zola.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val users by viewModel.users.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Xin chào, $currentUsername",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Danh sách người dùng",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (users.isEmpty()) {
            Text(
                text = "Chưa có người dùng nào khác. Hãy đăng ký thêm tài khoản trên thiết bị hoặc máy ảo khác!",
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { username ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChatClick(username) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = username,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Nhắn tin ➔",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
