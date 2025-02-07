package com.flingoapp.flingo.data.network.google

import com.flingoapp.flingo.BuildConfig
import com.flingoapp.flingo.data.network.RetrofitProviderImpl
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GoogleAiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}"
    )
    @POST("v1/chat/completions")
    suspend fun getResponse(@Body request: GeminiRequest): GeminiResponse

    companion object {
        private const val BASE_URL = "https://api.openai.com/"

        //should be injected
        private val retrofitInstance = RetrofitProviderImpl.getInstance(BASE_URL)
        private lateinit var service: GoogleAiService

        val instance: GoogleAiService
            get() {
                if (!this::service.isInitialized) {
                    createInstance()
                }

                return service
            }

        private fun createInstance() {
            service = retrofitInstance.create(GoogleAiService::class.java)
        }
    }
}