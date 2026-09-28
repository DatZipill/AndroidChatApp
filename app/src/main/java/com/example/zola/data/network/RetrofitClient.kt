package com.example.zola.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Sử dụng http://127.0.0.1:5000/ nhờ adb reverse tcp:5000 tcp:5000
    private const val BASE_URL = "http://127.0.0.1:5000/"

    val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Tự động chuyển đổi JSON <-> Data Class Kotlin
            .build()
            .create(ChatApi::class.java)
    }
}
