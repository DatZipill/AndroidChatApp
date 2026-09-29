package com.example.zola.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onChatClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val users by viewModel.users.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Thanh thông tin User + Nút Đăng xuất
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Xin chào,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = currentUsername,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.logout(onSuccess = onLogoutClick)
                }
            ) {
                Text("Đăng xuất")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

        Text(
            text = "Danh sách người dùng",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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
