package com.example.zola

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.zola.ui.chat.ChatScreen
import com.example.zola.ui.home.HomeScreen
import com.example.zola.ui.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatApp()
        }
    }
}

@Composable
fun ChatApp() {
    var currentScreen by remember {
        mutableStateOf("login")
    }

    var chatUsername by remember {
        mutableStateOf("")
    }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginClick = { loggedInUser ->
                    currentScreen = "home"
                }
            )
        }
        "home" -> {
            HomeScreen(
                onChatClick = { username ->
                    chatUsername = username
                    currentScreen = "chat"
                },
                onLogoutClick = {
                    currentScreen = "login"
                }
            )
        }
        "chat" -> {
            ChatScreen(
                username = chatUsername,
                onBackClick = {
                    currentScreen = "home"
                }
            )
        }
    }
}
