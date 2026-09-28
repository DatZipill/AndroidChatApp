package com.example.zola.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zola.data.UserPreferences
import com.example.zola.data.network.LoginRequest
import com.example.zola.data.network.RegisterRequest
import com.example.zola.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _loginState = MutableStateFlow<LoginResult>(LoginStateIdle)
    val loginState: StateFlow<LoginResult> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginError("Vui lòng nhập đầy đủ thông tin")
            return
        }

        _loginState.value = LoginLoading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body()?.token != null) {
                    val token = response.body()!!.token!!
                    val user = response.body()!!.username ?: username
                    
                    // Lưu session vào DataStore
                    userPreferences.saveSession(token, user)
                    _loginState.value = LoginSuccess(user)
                } else {
                    val errorMsg = response.body()?.error ?: "Sai tên đăng nhập hoặc mật khẩu"
                    _loginState.value = LoginError(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginError("Không thể kết nối tới Server. Hãy đảm bảo Server đang chạy.")
            }
        }
    }

    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginError("Vui lòng nhập đầy đủ thông tin")
            return
        }

        _loginState.value = LoginLoading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.register(RegisterRequest(username, password))
                if (response.isSuccessful) {
                    // Tự động đăng nhập sau khi đăng ký thành công
                    login(username, password)
                } else {
                    val errorMsg = response.body()?.error ?: "Đăng ký thất bại"
                    _loginState.value = LoginError(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginError("Không thể kết nối tới Server")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginStateIdle
    }
}

// Các trạng thái của màn hình Login
sealed interface LoginResult
object LoginStateIdle : LoginResult
object LoginLoading : LoginResult
data class LoginSuccess(val username: String) : LoginResult
data class LoginError(val message: String) : LoginResult
