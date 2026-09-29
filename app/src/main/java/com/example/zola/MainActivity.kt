package com.example.zola

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.zola.ui.home.HomeScreen
import com.example.zola.ui.chat.ChatScreen
import com.example.zola.ui.login.LoginScreen
import com.example.zola.ui.theme.ZolaTheme

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
    
    // We also need to remember the username of the person we are chatting with
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
                }
            )
        }
        "chat" -> {
            ChatScreen(
                username = chatUsername
            )
        }
    }
}
