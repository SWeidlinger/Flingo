package com.flingoapp.flingo.data.network

import com.flingoapp.flingo.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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

        if (BuildConfig.DEBUG) {
            //only add logging interceptor in debug mode
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            retrofitBuilder.client(okHttpClient)
        }

        return retrofitBuilder.build()
    }
}