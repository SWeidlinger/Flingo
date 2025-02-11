package com.flingoapp.flingo.data.network

import com.flingoapp.flingo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

interface RetrofitProvider {
    fun getInstance(baseUrl: String): Retrofit
}

object RetrofitProviderImpl : RetrofitProvider {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    override fun getInstance(baseUrl: String): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
        }

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))

        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        val okHttpClient = okHttpClientBuilder.build()
        return retrofitBuilder.client(okHttpClient).build()
    }
}