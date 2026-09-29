package com.example.zola.data.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val BASE_URL = "http://10.1.110.90:5000/"

    val okHttpClient = OkHttpClient.Builder().build()
    val gson = Gson()

    val api: ChatApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Dùng chung client cho cả REST API và WebSocket
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ChatApi::class.java)
    }
}
