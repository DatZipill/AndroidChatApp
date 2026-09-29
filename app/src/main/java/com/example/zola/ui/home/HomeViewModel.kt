package com.example.zola.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zola.data.UserPreferences
import com.example.zola.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users.asStateFlow()

    private val _currentUsername = MutableStateFlow("")
    val currentUsername: StateFlow<String> = _currentUsername.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.username.collect { name ->
                if (!name.isNullOrBlank()) {
                    _currentUsername.value = name
                    fetchUsers(name)
                }
            }
        }
    }

    // Lấy danh sách người dùng từ Server
    fun fetchUsers(currentUser: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getUsers(currentUser)
                if (response.isSuccessful && response.body()?.users != null) {
                    _users.value = response.body()!!.users!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
