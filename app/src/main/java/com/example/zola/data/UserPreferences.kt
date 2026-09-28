package com.example.zola.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Khởi tạo DataStore tên là "user_prefs"
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_TOKEN = stringPreferencesKey("user_token")
        val KEY_USERNAME = stringPreferencesKey("username")
    }

    // Đọc token từ DataStore dưới dạng Flow
    val userToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_TOKEN]
    }

    // Đọc username từ DataStore
    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USERNAME]
    }

    // Lưu phiên làm việc (Token & Username) xuống đĩa
    suspend fun saveSession(token: String, username: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            preferences[KEY_USERNAME] = username
        }
    }

    // Đăng xuất (xóa Token & Username)
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
